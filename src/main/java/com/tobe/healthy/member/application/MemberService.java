package com.tobe.healthy.member.application;

import static com.tobe.healthy.config.error.ErrorCode.MAIL_AUTH_CODE_NOT_VALID;
import static com.tobe.healthy.config.error.ErrorCode.MAIL_SEND_ERROR;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_EMAIL_DUPLICATION;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_ID_DUPLICATION;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_NOT_VALID;
import static com.tobe.healthy.member.domain.entity.Oauth.CLIENT_ID;
import static com.tobe.healthy.member.domain.entity.Oauth.CLIENT_SECRET;
import static com.tobe.healthy.member.domain.entity.Oauth.GRANT_TYPE;
import static com.tobe.healthy.member.domain.entity.Oauth.KAKAO_TOKEN_URL;
import static com.tobe.healthy.member.domain.entity.Oauth.REDIRECT_URL;
import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.util.StringUtils.cleanPath;

import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.security.JwtTokenGenerator;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand;
import com.tobe.healthy.member.domain.dto.in.MemberFindPWCommand;
import com.tobe.healthy.member.domain.dto.in.MemberJoinCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo.KakaoUserInfo;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo.NaverUserInfo;
import com.tobe.healthy.member.domain.dto.out.MemberJoinCommandResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final RestTemplate restTemplate;
	private final MemberRepository memberRepository;
	private final JwtTokenGenerator tokenGenerator;
	private final FileService fileService;
	private final JavaMailSender mailSender;
	private final RedisService redisService;

	@Value("${file.upload.location}")
	private String uploadDir;

	public boolean validateUserIdDuplication(String memberId) {
		memberRepository.findByUserId(memberId).ifPresent(m -> {
			throw new CustomException(MEMBER_ID_DUPLICATION);
		});
		return true;
	}

	public Boolean validateEmailDuplication(String email) {
		memberRepository.findByEmail(email).ifPresent(m -> {
			throw new CustomException(MEMBER_EMAIL_DUPLICATION);
		});
		return true;
	}

	public String sendEmailVerification(String email) {
		// 1. 이메일 중복 확인
		memberRepository.findByEmail(email).ifPresent(e -> {
			throw new CustomException(MEMBER_EMAIL_DUPLICATION);
		});

		// 2. 인증번호를 redis에 저장한다.
		String authKey = getAuthCode();
		redisService.setValuesWithTimeout(email, authKey, 3 * 60 * 1000); // 3분

		// 3. 이메일에 인증번호 전송한다.
		sendAuthMail(email, authKey);

		return email;
	}

	public Boolean verifyEmailAuthNumber(String authNumber, String email) {
		String value = redisService.getValues(email);

		// 1. 일치하는 데이터가 없을경우
		if (isEmpty(value) || !value.equals(authNumber)) {
			throw new CustomException(MAIL_AUTH_CODE_NOT_VALID);
		}

		return true;
	}

	public MemberJoinCommandResult joinMember(MemberJoinCommand request) {
		validateDuplicationUserId(request.getUserId());
		validateDuplicationEmail(request.getEmail());

		String password = passwordEncoder.encode(request.getPassword());
		Member member = Member.join(request, password);
		memberRepository.save(member);

		return MemberJoinCommandResult.of(member);
	}

	public Tokens login(MemberLoginCommand request) {
		return memberRepository.findByUserId(request.getUserId())
			.filter(member -> passwordEncoder.matches(request.getPassword(), member.getPassword()))
			.map(tokenGenerator::create)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
	}

	public Tokens refreshToken(String userId, String refreshToken) {
		// 1. Redis에서 유효한 token이 있는지 조회한다.
		String result = redisService.getValues(userId);

		// 2. Refresh Token이 존재하지 않음.
		if (isEmpty(result)) {
			throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
		}

		// 3. Refresh Token이 유효하지 않을경우
		if (!result.equals(refreshToken)) {
			throw new CustomException(REFRESH_TOKEN_NOT_VALID);
		}

		Member member = memberRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		// 4. 새로운 AccessToken과 기존의 RefreshToken을 반환한다.
		return tokenGenerator.exchangeAccessToken(member.getId(), member.getUserId(), refreshToken);
	}

	public String findUserId(MemberFindIdCommand request) {
		Member entity = memberRepository.findByEmailAndName(request.getEmail(), request.getName())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		return entity.getUserId();
	}

	public String findMemberPW(MemberFindPWCommand request) {
		Member member = memberRepository.findByUserIdAndName(request.getUserId(), request.getName())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		sendResetPassword(member.getEmail(), member);
		return member.getEmail();
	}

	private void validateDuplicationUserId(String userId) {
		memberRepository.findByUserId(userId).ifPresent(m -> {
			throw new CustomException(MEMBER_ID_DUPLICATION);
		});
	}

	public String getAccessToken(String authCode) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> requestEntity = getMultiValueMapHttpEntity(authCode, headers);

		ResponseEntity<OAuthInfo> responseEntity = restTemplate.postForEntity(KAKAO_TOKEN_URL.getDescription(), requestEntity, OAuthInfo.class);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			OAuthInfo body = responseEntity.getBody();

			HttpHeaders header = new HttpHeaders();
			header.set("Authorization", "Bearer " + body.getAccessToken());
			// token을 받아서 사용자 정보를 조회한다.
			ResponseEntity<KakaoUserInfo> entity = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", GET, new HttpEntity<>(header), KakaoUserInfo.class);
			KakaoUserInfo dto = entity.getBody();

			String email = dto.getKakaoAccount().getEmail();
			String name = dto.getKakaoAccount().getProfile().getNickname();
			String imageName = dto.getProperties().getProfileImage();

			memberRepository.findByEmail(email).ifPresent(m -> {
				throw new CustomException(MEMBER_EMAIL_DUPLICATION);
			});

			byte[] image = restTemplate.getForObject(dto.getProperties().getProfileImage(), byte[].class);
			String savedFileName = System.currentTimeMillis() + "_" + UUID.randomUUID();
			String extension = imageName.substring(imageName.lastIndexOf("."));

			InputStream inputStream = new ByteArrayInputStream(image);
			Path copyOfLocation = Paths.get(uploadDir + separator + cleanPath(savedFileName + extension));
			Files.copy(inputStream, copyOfLocation, REPLACE_EXISTING);

			Profile profile = Profile.create(savedFileName, cleanPath(savedFileName), extension, uploadDir + separator, image.length);

			Member member = Member.join(email, name, profile);
			memberRepository.save(member);
		}
		return null;
	}

	public String getAccessToken3(String code, String scope) throws IOException {

		log.info("code => {} scope => {}", code, scope);
		//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(APPLICATION_FORM_URLENCODED);
//
//		HttpEntity<MultiValueMap<String, String>> requestEntity = getMultiValueMapHttpEntity(authCode, headers);
//
//		ResponseEntity<OAuthInfo> responseEntity = restTemplate.postForEntity(KAKAO_TOKEN_URL.getDescription(), requestEntity, OAuthInfo.class);
//
//		if (responseEntity.getStatusCode().is2xxSuccessful()) {
//			OAuthInfo body = responseEntity.getBody();
//
//			HttpHeaders header = new HttpHeaders();
//			header.set("Authorization", "Bearer " + body.getAccessToken());
//			// token을 받아서 사용자 정보를 조회한다.
//			ResponseEntity<KakaoUserInfo> entity = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", GET, new HttpEntity<>(header), KakaoUserInfo.class);
//			KakaoUserInfo dto = entity.getBody();
//
//			String email = dto.getKakaoAccount().getEmail();
//			String name = dto.getKakaoAccount().getProfile().getNickname();
//			String imageName = dto.getProperties().getProfileImage();
//
//			memberRepository.findByEmail(email).ifPresent(m -> {
//				throw new CustomException(MEMBER_EMAIL_DUPLICATION);
//			});
//
//			byte[] image = restTemplate.getForObject(dto.getProperties().getProfileImage(), byte[].class);
//			String savedFileName = System.currentTimeMillis() + "_" + UUID.randomUUID();
//			String extension = imageName.substring(imageName.lastIndexOf("."));
//
//			InputStream inputStream = new ByteArrayInputStream(image);
//			Path copyOfLocation = Paths.get(uploadDir + separator + cleanPath(savedFileName + extension));
//			Files.copy(inputStream, copyOfLocation, REPLACE_EXISTING);
//
//			Profile profile = Profile.create(savedFileName, cleanPath(savedFileName), extension, uploadDir + separator, image.length);
//
//			Member member = Member.join(email, name, profile);
//			memberRepository.save(member);
//		}
		return null;
	}

	private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String authCode, HttpHeaders headers) {
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("grant_type", GRANT_TYPE.getDescription());
		requestBody.add("client_id", CLIENT_ID.getDescription());       // 본인이 발급받은 key
		requestBody.add("redirect_uri", REDIRECT_URL.getDescription()); // 본인이 설정한 주소
		requestBody.add("client_secret", CLIENT_SECRET.getDescription());
		requestBody.add("code", authCode);
		return new HttpEntity<>(requestBody, headers);
	}

	public String sendAuthMail(String email) {
		// 1. 이메일 중복 확인
		memberRepository.findByEmail(email).ifPresent(e -> {
			throw new CustomException(MEMBER_EMAIL_DUPLICATION);
		});

		// 2. 인증번호를 redis에 저장한다.
		String authKey = getAuthCode();
		redisService.setValuesWithTimeout(email, authKey, 3 * 60 * 1000); // 3분

		// 3. 이메일에 인증번호 전송한다.
		sendAuthMail(email, authKey);

		return email;
	}

	private void sendAuthMail(String email, String authKey) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject("안녕하세요. 건강해짐 회원가입 인증번호입니다."); // 메일 제목
			String text = "안녕하세요. 건강해짐 인증번호는 authKey 입니다. \n확인후 입력해 주세요.".replace("authKey", authKey);
			mimeMessageHelper.setText(text, false); // 메일 본문 내용, HTML 여부
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new CustomException(MAIL_SEND_ERROR);
		}
	}

	private void validateDuplicationEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(m -> {
			throw new CustomException(MEMBER_EMAIL_DUPLICATION);
		});
	}

	private void sendResetPassword(String email, Member member) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			String resetPW = RandomStringUtils.random(12, true, true);
			member.resetPassword(passwordEncoder.encode(resetPW));
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject("안녕하세요. 건강해짐 초기화 비밀번호입니다."); // 메일 제목
			String text = "안녕하세요. 건강해짐 초기화 비밀번호는 resetPassword 입니다. \n로그인 후 반드시 비밀번호를 변경해 주세요.".replace("resetPassword", resetPW);
			mimeMessageHelper.setText(text, false); // 메일 본문 내용, HTML 여부
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new CustomException(MAIL_SEND_ERROR);
		}
	}

	private String getAuthCode() {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder();
		int num = 0;

		while (buffer.length() < 6) {
			num = random.nextInt(10);
			buffer.append(num);
		}

		return buffer.toString();
	}

	public String deleteMember(String userId, String password) {
		Member member = memberRepository.findByUserId(userId)
			.filter(m -> passwordEncoder.matches(password, m.getPassword()))
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		member.deleteMember();
		return member.getUserId();
	}

	public void getAccessToken(String code, String state) throws IOException {
		log.info("code => {} state => {} ", code, state);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("grant_type", "authorization_code");
		requestBody.add("client_id", "C1sJMU7fEMkDTN39y8Pt");
		requestBody.add("client_secret", "igvBuycGcG");
		requestBody.add("code", code);
		requestBody.add("state", state);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<OAuthInfo> responseEntity = restTemplate.postForEntity("https://nid.naver.com/oauth2.0/token", requestEntity, OAuthInfo.class);
		log.info("responseEntity => {}", responseEntity);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			OAuthInfo body = responseEntity.getBody();
			log.info("body => {}", body);

			HttpHeaders header = new HttpHeaders();
			header.set("Authorization", "Bearer " + body.getAccessToken());
			ResponseEntity<NaverUserInfo> entity = restTemplate.exchange("https://openapi.naver.com/v1/nid/me", GET, new HttpEntity<>(header), NaverUserInfo.class);
			NaverUserInfo dto = entity.getBody();
			String email = dto.getResponse().getEmail();
			String name = dto.getResponse().getName();

			memberRepository.findByEmail(email).ifPresent(m -> {
				throw new CustomException(MEMBER_EMAIL_DUPLICATION);
			});

			// 사용자 이메일, 이름을 받음
			log.info("dto => {}", dto);
			byte[] image = restTemplate.getForObject(dto.getResponse().getProfileImage(), byte[].class);
			log.info("image => {}", image);
			String savedFileName = System.currentTimeMillis() + "_" + UUID.randomUUID();
			String profileImage = dto.getResponse().getProfileImage();
			String extension = profileImage.substring(profileImage.lastIndexOf("."));

			InputStream inputStream = new ByteArrayInputStream(image);
			Path copyOfLocation = Paths.get(uploadDir + separator + cleanPath(savedFileName + extension));
			Files.copy(inputStream, copyOfLocation, REPLACE_EXISTING);

			Profile profile = Profile.create(savedFileName, cleanPath(savedFileName), extension, uploadDir + separator, image.length);

			Member member = Member.join(email, name, profile);
			memberRepository.save(member);
		}
	}
}

package com.tobe.healthy.member.application;

import static com.tobe.healthy.config.error.ErrorCode.MAIL_AUTH_CODE_NOT_VALID;
import static com.tobe.healthy.config.error.ErrorCode.MAIL_SEND_ERROR;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_EMAIL;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_NICKNAME;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static com.tobe.healthy.member.domain.entity.Oauth.CLIENT_ID;
import static com.tobe.healthy.member.domain.entity.Oauth.CLIENT_SECRET;
import static com.tobe.healthy.member.domain.entity.Oauth.GRANT_TYPE;
import static com.tobe.healthy.member.domain.entity.Oauth.KAKAO_TOKEN_URL;
import static com.tobe.healthy.member.domain.entity.Oauth.REDIRECT_URL;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.security.JwtTokenGenerator;
import com.tobe.healthy.config.security.JwtTokenProvider;
import com.tobe.healthy.file.application.FileService;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand;
import com.tobe.healthy.member.domain.dto.in.MemberFindPWCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo.KakaoUserInfo;
import com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult;
import com.tobe.healthy.member.domain.entity.BearerToken;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.domain.dto.in.VerifyAuthMailRequest;
import com.tobe.healthy.member.repository.BearerTokenRepository;
import com.tobe.healthy.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
	private final JwtTokenProvider tokenProvider;
	private final BearerTokenRepository bearerTokenRepository;
	private final FileService fileService;
	private Map<String, String> map = new ConcurrentHashMap<>(); // todo: 서버 이중화시 동시성 이슈 발생
	private final JavaMailSender javaMailSender;

	public MemberRegisterCommandResult create(MemberRegisterCommand request) {
		validateDuplicateEmail(request);
		validateDuplicateNickname(request);

		String password = passwordEncoder.encode(request.getPassword());
		Member member = Member.create(request, password);
		memberRepository.save(member);

		return MemberRegisterCommandResult.of(member);
	}

	public Tokens login(MemberLoginCommand request) {
		return memberRepository.findByEmail(request.getEmail())
			.filter(member -> passwordEncoder.matches(request.getPassword(), member.getPassword()))
			.map(tokenGenerator::create)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
	}

	public Tokens refresh(String refreshToken) {
		try {
			tokenProvider.decode(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new CustomException(REFRESH_TOKEN_EXPIRED);
		}

		BearerToken token = bearerTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new CustomException(REFRESH_TOKEN_NOT_FOUND));

		Long memberId = token.getMemberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		return tokenGenerator.create(member);
	}

	public String getAccessToken(String authCode) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> requestEntity = getMultiValueMapHttpEntity(authCode, headers);

		ResponseEntity<OAuthInfo> responseEntity = restTemplate.postForEntity(KAKAO_TOKEN_URL.getDescription(), requestEntity, OAuthInfo.class);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			OAuthInfo body = responseEntity.getBody();
			log.info("body => {}", body);

			HttpHeaders header = new HttpHeaders();
			header.set("Authorization", "Bearer " + body.getAccessToken());
			ResponseEntity<KakaoUserInfo> entity = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", GET, new HttpEntity<>(header), KakaoUserInfo.class);
			KakaoUserInfo dto = entity.getBody();

			byte[] image = restTemplate.getForObject(dto.getProperties().getProfileImage(), byte[].class);
			fileService.uploadFile(image, dto.getProperties().getProfileImage());

			// todo: 소셜 회원가입시 email을 필수로 받는게 나을 거 같음. UUID로 하면, 복잡해짐
		}

		return null;
	}

	private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String authCode, HttpHeaders headers) {
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("grant_type", GRANT_TYPE.getDescription());
		requestBody.add("client_id", CLIENT_ID.getDescription());       // 본인이 발급받은 key
		requestBody.add("redirect_uri", REDIRECT_URL.getDescription()); // 본인이 설정한 주소
		requestBody.add("client_secret", CLIENT_SECRET.getDescription());
		requestBody.add("code", authCode);
		return new HttpEntity<>(requestBody,headers);
	}

	public Boolean sendAuthMail(String email) {
		// 1. 이메일 중복 확인
		memberRepository.findByEmail(email).ifPresent(e -> {
			throw new CustomException(MEMBER_DUPLICATION_EMAIL);
		});

		String authKey = getAuthCode();
		map.put(email, authKey);

		// 2. 이메일 인증번호 전송
		sendAuthMail(email, authKey);

		return true;
	}

	private void sendAuthMail(String email, String authKey) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject("안녕하세요. 건강해짐 회원가입 인증번호입니다."); // 메일 제목
			String text = "안녕하세요. 건강해짐 인증번호는 authKey 입니다. \n확인후 입력해 주세요.".replace("authKey", authKey);
			mimeMessageHelper.setText(text, false); // 메일 본문 내용, HTML 여부
			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new CustomException(MAIL_SEND_ERROR);
		}
	}

	private void validateDuplicateEmail(MemberRegisterCommand request) {
		memberRepository.findByEmail(request.getEmail()).ifPresent(m -> {
			throw new CustomException(MEMBER_DUPLICATION_EMAIL);
		});
	}

	private void validateDuplicateNickname(MemberRegisterCommand request) {
		memberRepository.findByNickname(request.getNickname()).ifPresent(m -> {
			throw new CustomException(MEMBER_DUPLICATION_NICKNAME);
		});
	}

	public String findMemberId(MemberFindIdCommand request) {
		Member entity = memberRepository.findByMobileNumAndNickname(request.getMobileNum(), request.getNickname())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		return entity.getEmail();
	}

	public Boolean findMemberPW(MemberFindPWCommand request) {
		Member member = memberRepository.findByMobileNumAndEmail(request.getMobileNum(),
				request.getEmail())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		sendResetPassword(request.getEmail(), member);
		return true;
	}

	private void sendResetPassword(String email, Member member) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			String resetPW = RandomStringUtils.random(12, true, true);
			member.resetPassword(passwordEncoder.encode(resetPW));
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject("안녕하세요. 건강해짐 초기화 비밀번호입니다."); // 메일 제목
			String text = "안녕하세요. 건강해짐 초기화 비밀번호는 resetPassword 입니다. \n로그인 후 반드시 비밀번호를 변경해 주세요.".replace("resetPassword", resetPW);
			mimeMessageHelper.setText(text, false); // 메일 본문 내용, HTML 여부
			javaMailSender.send(mimeMessage);
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

	public Boolean verifyAuthMail(VerifyAuthMailRequest request) {
		String authKey = map.get(request.getEmail());
		if (StringUtils.isEmpty(authKey)) {
			throw new CustomException(MAIL_AUTH_CODE_NOT_VALID);
		}
		if (request.getAuthKey().equals(authKey)) {
			return true;
		}
		throw new CustomException(MAIL_AUTH_CODE_NOT_VALID);
	}
}

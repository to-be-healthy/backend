package com.tobe.healthy.member.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tobe.healthy.common.RedisKeyPrefix;
import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.config.OAuthProperties;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.OAuthError.KakaoError;
import com.tobe.healthy.config.error.OAuthError.NaverError;
import com.tobe.healthy.config.error.OAuthException;
import com.tobe.healthy.config.security.JwtTokenGenerator;
import com.tobe.healthy.file.domain.entity.Profile;
import com.tobe.healthy.file.repository.FileRepository;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.domain.dto.MemberDto;
import com.tobe.healthy.member.domain.dto.in.*;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand.MemberFindIdCommandResult;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo.NaverUserInfo;
import com.tobe.healthy.member.domain.dto.out.InvitationMappingResult;
import com.tobe.healthy.member.domain.dto.out.MemberJoinCommandResult;
import com.tobe.healthy.member.domain.entity.AlarmStatus;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.application.TrainerService;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import io.jsonwebtoken.impl.Base64UrlCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.member.domain.entity.SocialType.*;
import static java.io.File.separator;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.util.StringUtils.cleanPath;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

	private final WebClient webClient;
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final JwtTokenGenerator tokenGenerator;
	private final RedisService redisService;
	private final TrainerService trainerService;
	private final ObjectMapper objectMapper;
	private final OAuthProperties oAuthProperties;
	private final FileRepository fileRepository;
	private final TrainerMemberMappingRepository mappingRepository;
	private final GymRepository gymRepository;
	private final MailService mailService;

	@Value("${file.upload.location}")
	private String uploadDir;

	public boolean validateUserIdDuplication(String userId) {
		if (userId.length() < 4) {
			throw new CustomException(MEMBER_ID_NOT_VALID);
		}
		memberRepository.findByUserId(userId).ifPresent(m -> {
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
		memberRepository.findByEmail(email).ifPresent(e -> {
			throw new CustomException(MEMBER_EMAIL_DUPLICATION);
		});

		String authKey = getAuthCode();
		redisService.setValuesWithTimeout(email, authKey, 3 * 60 * 1000); // 3분

		// 3. 이메일에 인증번호 전송한다.
		mailService.sendAuthMail(email, authKey);

		return email;
	}

	public Boolean verifyEmailAuthNumber(String authNumber, String email) {
		String value = redisService.getValues(email);

		if (isEmpty(value) || !value.equals(authNumber)) {
			throw new CustomException(MAIL_AUTH_CODE_NOT_VALID);
		}

		return true;
	}

	public MemberJoinCommandResult joinMember(MemberJoinCommand request) {
		validateName(request.getName());
		validatePassword(request);
		validateDuplicationUserId(request.getUserId());
		validateDuplicationEmail(request.getEmail());

		String password = passwordEncoder.encode(request.getPassword());
		Member member = Member.join(request, password);
		memberRepository.save(member);

		return MemberJoinCommandResult.from(member);
	}

	private void validatePassword(MemberJoinCommand request) {
		if (!request.getPassword().equals(request.getPasswordConfirm())) {
			throw new CustomException(CONFIRM_PASSWORD_NOT_MATCHED);
		}
		String regexp = "^[A-Za-z0-9]+$";
		if (request.getPassword().length() < 8 || !Pattern.matches(regexp, request.getPassword())) {
			throw new CustomException(PASSWORD_POLICY_VIOLATION);
		}
	}

	private void validateName(String name) {
		if (name.length() < 2) {
			throw new CustomException(MEMBER_NAME_LENGTH_NOT_VALID);
		}

		String regexp = "^[가-힣A-Za-z]+$";
		if (!Pattern.matches(regexp, name)) {
			throw new CustomException(MEMBER_NAME_NOT_VALID);
		}
	}

	public Tokens login(MemberLoginCommand request) {
		return memberRepository.findByUserId(request.getUserId(), request.getMemberType())
			.filter(member -> passwordEncoder.matches(request.getPassword(), member.getPassword()))
			.map(tokenGenerator::create)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
	}

	public Tokens refreshToken(String userId, String refreshToken) {
		String result = redisService.getValues(userId);

		if (isEmpty(result)) {
			throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
		}

		if (!result.equals(refreshToken)) {
			throw new CustomException(REFRESH_TOKEN_NOT_VALID);
		}

		Member member = memberRepository.findByUserId(userId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		return tokenGenerator.exchangeAccessToken(member.getId(), member.getUserId(), member.getMemberType(), refreshToken, member.getGym());
	}

	public MemberFindIdCommandResult findUserId(MemberFindIdCommand request) {
		Member member = memberRepository.findByEmailAndName(request.getEmail(), request.getName())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		return new MemberFindIdCommandResult(member.getUserId().substring(member.getUserId().length() - 3) + "**", member.getCreatedAt());
	}

	public String findMemberPW(MemberFindPWCommand request) {
		Member member = memberRepository.findByUserIdAndName(request.getUserId(), request.getName())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		sendResetPassword(member.getEmail(), member);
		return member.getEmail();
	}

	public String deleteMember(String password, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.filter(m -> passwordEncoder.matches(password, m.getPassword()))
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		member.deleteMember();
		return member.getUserId();
	}

	public boolean changePassword(MemberPasswordChangeCommand request, Long memberId) {
		if (!request.getCurrPassword1().equals(request.getCurrPassword2())) {
			throw new CustomException(NOT_MATCH_PASSWORD);
		}

		Member member = memberRepository.findById(memberId)
			.filter(m -> passwordEncoder.matches(request.getCurrPassword1(), m.getPassword()))
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		String password = passwordEncoder.encode(request.getChangePassword());

		member.changePassword(password);

		return true;
	}

	public Boolean changeProfile(MultipartFile file, Long memberId) {
		if (!file.isEmpty()) {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

			String savedFileName = System.currentTimeMillis() + "_" + randomUUID();
			String extension = Objects.requireNonNull(file.getOriginalFilename())
				.substring(file.getOriginalFilename().lastIndexOf("."));

			Path location = Paths.get(uploadDir + separator + cleanPath(savedFileName + extension));
			Path locationParent = location.getParent();
			try {
				if (!Files.exists(locationParent)) {
					Files.createDirectories(locationParent);
				}
				Files.copy(file.getInputStream(), location, REPLACE_EXISTING);
			} catch (IOException e) {
				throw new CustomException(FILE_UPLOAD_ERROR);
			}

			Profile profile = Profile.create(savedFileName, cleanPath(file.getOriginalFilename()),
				extension, uploadDir + separator, (int) file.getSize());

			member.registerProfile(profile);
			fileRepository.save(profile);
		}
		return true;
	}

	public Boolean changeName(String name, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		member.changeName(name);
		return true;
	}

	public Boolean changeAlarm(AlarmStatus alarmStatus, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		member.changeAlarm(alarmStatus);
		return true;
	}

	public Boolean changeTrainerFeedback(AlarmStatus alarmStatus, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		member.changeTrainerFeedback(alarmStatus);
		return true;
	}

	public Tokens getKakaoAccessToken(SocialLoginCommand request) {
		IdToken response = getKakaoOAuthAccessToken(request.getCode(), request.getRedirectUrl());

		Optional<Member> findMember = memberRepository.findByEmailAndSocialType(response.getEmail(), KAKAO);

		if (findMember.isPresent()) {
			if (findMember.get().getMemberType().equals(request.getMemberType())) {
				return tokenGenerator.create(findMember.get());
			}
			throw new CustomException(MEMBER_NOT_FOUND);
		}

		Profile profile = getProfile(response.getPicture());
		Member member = Member.join(response.getEmail(), response.getNickname(), profile, request.getMemberType(), KAKAO);
		memberRepository.save(member);
		return tokenGenerator.create(member);
	}

	private byte[] getProfileImage(String imageName) {
		return webClient.get().uri(imageName)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(String.class).flatMap(error -> {
					log.error("error => {}", error);
					return Mono.error(new CustomException(PROFILE_ACCESS_FAILED));
				}))
			.bodyToMono(byte[].class)
			.share()
			.block();
	}

	private IdToken getKakaoOAuthAccessToken(String code, String redirectUrl) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
		request.add("grant_type", oAuthProperties.getKakao().getGrantType());
		request.add("client_id", oAuthProperties.getKakao().getClientId());
		request.add("redirect_uri", redirectUrl);
		request.add("code", code);
		request.add("client_secret", oAuthProperties.getKakao().getClientSecret());
		OAuthInfo result = webClient.post()
			.uri(oAuthProperties.getKakao().getTokenUri())
			.bodyValue(request)
			.headers(header -> header.setContentType(APPLICATION_FORM_URLENCODED))
			.retrieve()
			.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(KakaoError.class).flatMap(e -> {
					log.error("error => {}", e);
					return Mono.error(new OAuthException(e.getErrorDescription()));
				}))
			.bodyToMono(OAuthInfo.class)
			.share().block();
		try {
			String token = decordToken(result);
			return new ObjectMapper().readValue(token, IdToken.class);
		} catch (JsonProcessingException e) {
			log.error("error => {}", e);
			throw new CustomException(JSON_PARSING_ERROR);
		}
	}

	private static String decordToken(OAuthInfo result) {
		byte[] decode = new Base64UrlCodec().decode(result.getIdToken().split("\\.")[1]);
		return new String(decode, StandardCharsets.UTF_8);
	}

	public Tokens getNaverAccessToken(SocialLoginCommand request) {
		OAuthInfo response = getNaverOAuthAccessToken(request.getCode(), request.getState());

		NaverUserInfo authorization = getNaverUserInfo(response);

		Optional<Member> findMember = memberRepository.findByEmailAndSocialType(authorization.getResponse().getEmail(), NAVER);
		if (findMember.isPresent()) {
			if (findMember.get().getMemberType().equals(request.getMemberType())) {
				return tokenGenerator.create(findMember.get());
			}
			throw new CustomException(MEMBER_NOT_FOUND);
		}
		Profile profile = getProfile(authorization.getResponse().getProfileImage());
		Member member = Member.join(authorization.getResponse().getEmail(), authorization.getResponse().getName(), profile, request.getMemberType(), NAVER);
		memberRepository.save(member);
		return tokenGenerator.create(member);
	}

	private Profile getProfile(String profileImage) {
		byte[] image = getProfileImage(profileImage);
		String savedFileName = createFileUUID();
		String extension = getImageExtension(profileImage);

		try (InputStream inputStream = new ByteArrayInputStream(image)) {
			Path location = Paths.get(uploadDir + separator + cleanPath(savedFileName + extension));
			Path locationParent = location.getParent();
			if (!Files.exists(locationParent)) {
				Files.createDirectories(locationParent);
			}
			Files.copy(inputStream, location, REPLACE_EXISTING);
		} catch (IOException e) {
			log.error("error => {}", e);
			throw new CustomException(FILE_UPLOAD_ERROR);
		}

		return Profile.create(savedFileName, cleanPath(savedFileName), extension,
			uploadDir + separator, image.length);
	}

	private NaverUserInfo getNaverUserInfo(OAuthInfo oAuthInfo) {
		return webClient.get()
			.uri(oAuthProperties.getNaver().getUserInfoUri())
			.headers(
				header -> {
					header.setContentType(APPLICATION_FORM_URLENCODED);
					header.set("Authorization", "Bearer " + oAuthInfo.getAccessToken());
				}
			)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response ->
				response.bodyToMono(NaverError.class).flatMap(e -> {
					log.error("error => {}", e);
					return Mono.error(new OAuthException(e.getMessage()));
				}))
			.bodyToMono(NaverUserInfo.class)
			.share()
			.block();
	}

	private OAuthInfo getNaverOAuthAccessToken(String code, String state) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
		request.add("grant_type", oAuthProperties.getNaver().getGrantType());
		request.add("client_id", oAuthProperties.getNaver().getClientId());
		request.add("client_secret", oAuthProperties.getNaver().getClientSecret());
		request.add("code", code);
		request.add("state", state);
		return webClient.post()
			.uri(oAuthProperties.getNaver().getTokenUri())
			.bodyValue(request)
			.headers(header -> header.setContentType(APPLICATION_FORM_URLENCODED))
			.retrieve()
			.bodyToMono(OAuthInfo.class)
			.share()
			.block();
	}

	public Tokens getGoogleOAuth(SocialLoginCommand command) {
		OAuthInfo googleToken = getGoogleAccessToken(command.getCode());
		String[] check = googleToken.getIdToken().split("\\.");
		Base64.Decoder decoder = Base64.getDecoder();
		String payload = new String(decoder.decode(check[1]));
		Map<String, String> idToken = new HashMap<>();
		try {
			idToken = objectMapper.readValue(payload, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String email = idToken.get("email");
		String name = idToken.get("name");
		String imageName = idToken.get("picture");
		byte[] image = getProfileImage(imageName);
		String savedFileName = createFileUUID();
		String extension = ".jpg";

		Optional<Member> optionalMember = memberRepository.findGoogleByEmailAndSocialType(email);
		Member member;
		if (optionalMember.isEmpty()) { //회원가입
			Profile profile = Profile.create(savedFileName, cleanPath(savedFileName), extension,
					uploadDir + separator, image.length);
			member = Member.join(email, name, profile, command.getMemberType(), GOOGLE);
			memberRepository.save(member);
			try {
				Path copyOfLocation = Paths.get(uploadDir + separator + savedFileName + extension);
				Files.createDirectories(copyOfLocation.getParent());
				Files.copy(new ByteArrayInputStream(image), copyOfLocation, REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				throw new CustomException(SERVER_ERROR);
			}
		} else {
			member = optionalMember.get();
		}

		return memberRepository.findByUserId(member.getUserId(), command.getMemberType())
			.map(tokenGenerator::create)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
	}

	private OAuthInfo getGoogleAccessToken(String code) {
		String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("client_id", oAuthProperties.getGoogle().getClientId());
		requestBody.add("client_secret", oAuthProperties.getGoogle().getClientSecret());
		requestBody.add("grant_type", oAuthProperties.getGoogle().getGrantType());
		requestBody.add("redirect_uri", oAuthProperties.getGoogle().getRedirectUri());
		requestBody.add("code", decode);
		Mono<OAuthInfo> responseMono = null;
		try {
			responseMono = webClient.post()
					.uri(oAuthProperties.getGoogle().getTokenUri())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.bodyValue(requestBody)
					.retrieve()
//			.onStatus(HttpStatusCode::is4xxClientError,
//				response -> Mono.error(RuntimeException::new))
//			.onStatus(HttpStatusCode::is5xxServerError,
//				response -> Mono.error(RuntimeException::new))
					.bodyToMono(OAuthInfo.class);
		}catch (Exception e){
			e.printStackTrace();
		}
		return responseMono.share().block();
	}

	private String createFileUUID() {
		return System.currentTimeMillis() + "_" + UUID.randomUUID();
	}

	private String getImageExtension(String profileImage) {
		return profileImage.substring(profileImage.lastIndexOf("."));
	}

	private void validateDuplicationUserId(String userId) {
		if (Pattern.matches("^[가-힣]+$", userId)) {
			throw new CustomException(USERID_POLICY_VIOLATION);
		}
		memberRepository.findByUserId(userId).ifPresent(m -> {
			throw new CustomException(MEMBER_ID_DUPLICATION);
		});
	}

	private void validateDuplicationEmail(String email) {
		memberRepository.findByEmail(email).ifPresent(m -> {
			throw new CustomException(MEMBER_EMAIL_DUPLICATION);
		});
	}

	private void sendResetPassword(String email, Member member) {
		String resetPW = RandomStringUtils.random(12, true, true);
		member.resetPassword(passwordEncoder.encode(resetPW));
		mailService.sendResetPassword(email, resetPW);
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

	public MemberJoinCommandResult joinWithInvitation(MemberJoinCommand request) {
		MemberJoinCommandResult result = joinMember(request);
		trainerService.addMemberOfTrainer(request.getTrainerId(), result.getId());
		return result;
	}

	public InvitationMappingResult getInvitationMapping(String uuid) {
		String invitationKey = RedisKeyPrefix.INVITATION.getDescription() + uuid;
		String mappedData = redisService.getValues(invitationKey);
		if (isEmpty(mappedData)) {
			throw new CustomException(INVITE_LINK_NOT_FOUND);
		}
		HashMap<String, String> map = new HashMap<>();
		try {
			map = objectMapper.readValue(mappedData, HashMap.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Long trainerId = Long.valueOf(map.get("trainerId"));
		String email = map.get("email");
		Member member = memberRepository.findByMemberIdWithGym(trainerId);
		return InvitationMappingResult.create(member, email);
	}

	public MemberDto getMemberInfo(Long memberId) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Member member = memberRepository.findByMemberIdWithProfile(memberId);

		Optional<TrainerMemberMapping> mapping = mappingRepository.findTop1ByMemberIdOrderByCreatedAtDesc(memberId);

		if(mapping.isPresent()){
			Long trainerId = mapping.map(TrainerMemberMapping::getTrainerId).orElse(null);
			Member trainer = memberRepository.findByMemberIdWithGym(trainerId);
			return MemberDto.create(member, trainer.getGym());
		}else{
			return MemberDto.from(member);
		}
	}
}

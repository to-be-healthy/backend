package com.tobe.healthy.member.application;

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

import com.tobe.healthy.common.message.APIInit;
import com.tobe.healthy.common.message.model.request.Message;
import com.tobe.healthy.common.message.model.response.MessageModel;
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
import com.tobe.healthy.member.repository.BearerTokenRepository;
import com.tobe.healthy.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
	private final static Map<String, String> map = new ConcurrentHashMap<>();

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

		// POST 요청 보내기
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

	public Boolean isAvailableEmail(String email) {
		return memberRepository.findByEmail(email).isEmpty();
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
		Member entity = memberRepository.findByMobileNumAndNickname(request.getMobileNum(),
				request.getNickname())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		return entity.getEmail();
	}

	public String sendAuthenticationNumber(String mobileNum) {
		int randomNum = (int) ((Math.random() * 899999) + 100000);
		Message message = new Message(mobileNum, "010-4000-1278", "인증번호 : " + randomNum);
		map.put(mobileNum, String.valueOf(randomNum));
		Call<MessageModel> api = APIInit.getAPI().sendMessage(APIInit.getHeaders(), message);
		api.enqueue(new Callback<>() {
			@Override
			public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
				// 성공 시 200이 출력됩니다.
				if (response.isSuccessful()) {
					log.info("statusCode : " + response.code());
					MessageModel body = response.body();
					log.info("groupId : " + body.getGroupId());
					log.info("messageId : " + body.getMessageId());
					log.info("to : " + body.getTo());
					log.info("from : " + body.getFrom());
					log.info("type : " + body.getType());
					log.info("statusCode : " + body.getStatusCode());
					log.info("statusMessage : " + body.getStatusMessage());
					log.info("customFields : " + body.getCustomFields());
				} else {
					try {
						log.error(response.errorBody().string());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(Call<MessageModel> call, Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		return mobileNum;
	}

	public Boolean checkAuthenticationNumber(String mobileNum, String verificationNum) {
		String result = map.get(mobileNum);
		if (result.equals(verificationNum)) {
			return true;
		}
		return false;
	}

	public String findMemberPW(MemberFindPWCommand request) {
		Member entity = memberRepository.findByMobileNumAndEmail(request.getMobileNum(), request.getEmail())
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
		sendAuthenticationNumber(entity.getMobileNum());

		return entity.getMobileNum();
	}
}

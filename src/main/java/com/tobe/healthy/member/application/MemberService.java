package com.tobe.healthy.member.application;

import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_EMAIL;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_DUPLICATION_NICKNAME;
import static com.tobe.healthy.config.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_EXPIRED;
import static com.tobe.healthy.config.error.ErrorCode.REFRESH_TOKEN_NOT_FOUND;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.tobe.healthy.common.message.APIInit;
import com.tobe.healthy.common.message.model.request.Message;
import com.tobe.healthy.common.message.model.response.MessageModel;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.security.JwtTokenGenerator;
import com.tobe.healthy.config.security.JwtTokenProvider;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult;
import com.tobe.healthy.member.domain.entity.BearerToken;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.BearerTokenRepository;
import com.tobe.healthy.member.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtTokenGenerator tokenGenerator;
    private final JwtTokenProvider tokenProvider;
    private final BearerTokenRepository bearerTokenRepository;
    private final static Map<String, String> map = new ConcurrentHashMap<>();

    @Transactional
    public MemberRegisterCommandResult create(MemberRegisterCommand request) {
        validateDuplicateEmail(request);
        validateDuplicateNickname(request);

        String password = passwordEncoder.encode(request.getPassword());
        Member member = Member.create(request, password);
        memberRepository.save(member);

        return MemberRegisterCommandResult.of(member);
    }

    @Transactional
    public Tokens login(MemberLoginCommand request) {
        return memberRepository.findByEmail(request.getEmail())
            .filter(member -> passwordEncoder.matches(request.getPassword(), member.getPassword()))
            .map(tokenGenerator::create)
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    @Transactional
    public Tokens refresh(String refreshToken) {
        try {
            tokenProvider.decode(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(REFRESH_TOKEN_EXPIRED);
        }

        BearerToken token = bearerTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
            () -> new CustomException(REFRESH_TOKEN_NOT_FOUND));

        Long memberId = token.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
            ()-> new CustomException(MEMBER_NOT_FOUND));

        return tokenGenerator.create(member);
    }

    public String getAccessToken(String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // POST 요청을 위해 기본값이 false인 setDoOutput을 true로

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=b744b34e90d30c3a0ff41ad4ade070f7"); //본인이 발급받은 key
            sb.append("&redirect_uri=http://localhost:8080/api/auth/code/kakao"); // 본인이 설정한 주소
            sb.append("&client_secret=QMaOCZDGKnrCtnRbSl3nIRmsKVIPGJnd");
            sb.append("&code=" + authorize_code);

            bw.write(sb.toString());
            bw.flush();

            // 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body : " + result);

            // Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("access_token : " + access_Token);
            log.info("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return access_Token;
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
		Member entity = memberRepository.findByPhoneNumberAndNickname(request.getPhoneNumber(),
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
}

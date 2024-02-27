package com.tobe.healthy.member.application;

import static com.tobe.healthy.member.domain.entity.Oauth.KAKAO_TOKEN_URL;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.tobe.healthy.common.message.APIInit;
import com.tobe.healthy.common.message.model.request.Message;
import com.tobe.healthy.common.message.model.response.MessageModel;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService2 {

    private final RestTemplate restTemplate;
    private final static Map<String, String> map = new ConcurrentHashMap<>();

	public String getAccessToken(String authCode) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("grant_type", "authorization_code");
		requestBody.add("client_id", "b744b34e90d30c3a0ff41ad4ade070f7"); //본인이 발급받은 key
		requestBody.add("redirect_uri", "http://localhost:8080/api/auth/code/kakao"); // 본인이 설정한 주소
		requestBody.add("client_secret", "QMaOCZDGKnrCtnRbSl3nIRmsKVIPGJnd");
		requestBody.add("code", authCode);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody,
			headers);

		// POST 요청 보내기
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(
			KAKAO_TOKEN_URL.getDescription(), requestEntity, String.class);

		log.info("responseEntity : {}", responseEntity);

		// 결과 코드 확인
		HttpStatusCode statusCode = responseEntity.getStatusCode();
		log.info("responseCode : " + statusCode);

		// 응답 바디 확인
		String responseBody = responseEntity.getBody();
		log.info("response body : " + responseBody);

		// Gson 라이브러리를 사용하여 JSON 파싱
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(responseBody);

		String access_Token = element.getAsJsonObject().get("access_token").getAsString();
		String refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

		log.info("access_token : " + access_Token);
		log.info("refresh_token : " + refresh_Token);
		return access_Token;
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
}

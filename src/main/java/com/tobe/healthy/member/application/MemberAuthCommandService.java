package com.tobe.healthy.member.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tobe.healthy.common.Utils;
import com.tobe.healthy.common.redis.RedisKeyPrefix;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.OAuthProperties;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.OAuthError.GoogleError;
import com.tobe.healthy.config.error.OAuthError.KakaoError;
import com.tobe.healthy.config.error.OAuthError.NaverError;
import com.tobe.healthy.config.error.OAuthException;
import com.tobe.healthy.config.jwt.JwtTokenGenerator;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseAddCommand;
import com.tobe.healthy.member.domain.dto.in.*;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo.NaverUserInfo;
import com.tobe.healthy.member.domain.dto.out.CommandJoinMemberResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.application.TrainerService;
import io.jsonwebtoken.impl.Base64UrlCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.tobe.healthy.common.Utils.*;
import static com.tobe.healthy.config.error.ErrorCode.*;
import static com.tobe.healthy.member.domain.entity.SocialType.*;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberAuthCommandService {

    private final WebClient webClient;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtTokenGenerator tokenGenerator;
    private final RedisService redisService;
    private final TrainerService trainerService;
    private final ObjectMapper objectMapper;
    private final OAuthProperties oAuthProperties;
    private final AmazonS3 amazonS3;
    private final MailService mailService;
    private final CourseService courseService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String sendEmailVerification(String email) {
        memberRepository.findByEmail(email).ifPresent(e -> {
            throw new CustomException(MEMBER_EMAIL_DUPLICATION);
        });

        String authKey = Utils.getAuthCode(6);

        redisService.setValuesWithTimeout(email, authKey, EMAIL_AUTH_TIMEOUT); // 3분

        // 3. 이메일에 인증번호 전송한다.
        mailService.sendAuthMail(email, authKey);

        return email;
    }

    public Boolean verifyEmailAuthNumber(String authNumber, String email) {
        String value = redisService.getValues(email);

        if (isEmpty(value) || !value.equals(authNumber)) {
            throw new CustomException(MAIL_AUTH_CODE_NOT_VALID);
        }

        redisService.deleteValues(email);
        return true;
    }

    public CommandJoinMemberResult joinMember(CommandJoinMember request) {
        validateName(request.getName());
        validatePassword(request);
        validateDuplicationUserId(request.getUserId());
        validateDuplicationEmail(request.getEmail());

        String password = passwordEncoder.encode(request.getPassword());
        Member member = Member.join(request, password);
        memberRepository.save(member);

        return CommandJoinMemberResult.from(member);
    }

    public Tokens login(CommandLoginMember request) {
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

        return tokenGenerator.exchangeAccessToken(member.getId(),
                                                  member.getUserId(),
                                                  member.getMemberType(),
                                                  refreshToken,
                                                  member.getGym());
    }

    public String findMemberPW(CommandFindMemberPassword request) {
        Member member = memberRepository.findByUserIdAndName(request.getUserId(), request.getName())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        sendResetPassword(member.getEmail(), member);
        return member.getEmail();
    }

    public CommandJoinMemberResult joinWithInvitation(CommandJoinMember request) {
        CommandJoinMemberResult result = joinMember(request);
        Member member = memberRepository.findById(result.getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        mappingTrainerAndStudent(member, request.getUuid(), request.getName(), false);
        return result;
    }

    public Tokens getNaverAccessToken(CommandSocialLogin request) {
        OAuthInfo response = getNaverOAuthAccessToken(request.getCode(), request.getState());

        NaverUserInfo authorization = getNaverUserInfo(response);

        Optional<Member> findMember =
                memberRepository.findByEmailAndSocialType(authorization.getResponse().getEmail(), NAVER);

        if (findMember.isPresent()) {
            if (findMember.get().getMemberType().equals(request.getMemberType())) {
                return tokenGenerator.create(findMember.get());
            }
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        Member member = Member.join(authorization.getResponse().getEmail(),
                authorization.getResponse().getName(),
                request.getMemberType(),
                NAVER);
        MemberProfile profile = getProfile(authorization.getResponse().getProfileImage(), member);
        member.setMemberProfile(profile);
        memberRepository.save(member);

        //초대가입인 경우
        if (StringUtils.isNotEmpty(request.getUuid())) {
            mappingTrainerAndStudent(member, request.getUuid(), authorization.getResponse().getName(), true);
        }
        return tokenGenerator.create(member);
    }

    public Tokens getKakaoAccessToken(CommandSocialLogin request) {
        IdToken response = getKakaoOAuthAccessToken(request.getCode(), request.getRedirectUrl());

        Optional<Member> findMember = memberRepository.findByEmailAndSocialType(response.getEmail(), KAKAO);

        if (findMember.isPresent()) {
            if (findMember.get().getMemberType().equals(request.getMemberType())) {
                return tokenGenerator.create(findMember.get());
            }
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        Member member = Member.join(response.getEmail(), response.getNickname(), request.getMemberType(), KAKAO);
        MemberProfile profile = getProfile(response.getPicture(), member);
        member.setMemberProfile(profile);
        memberRepository.save(member);

        //초대가입인 경우
        if (StringUtils.isNotEmpty(request.getUuid())) {
            mappingTrainerAndStudent(member, request.getUuid(), response.getNickname(), true);
        }
        return tokenGenerator.create(member);
    }

    @Transactional
    public Tokens getGoogleOAuth(CommandSocialLogin request) {
        OAuthInfo googleToken = getGoogleAccessToken(request.getCode(), request.getRedirectUrl());
        String[] check = googleToken.getIdToken().split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(check[1]));
        Map<String, String> idToken = new HashMap<>();
        try {
            idToken = objectMapper.readValue(payload, Map.class);
        } catch (Exception e) {
            log.error("error => {}", e.getStackTrace()[0]);
        }
        String email = idToken.get("email");
        String name = idToken.get("name");
        String picture = idToken.get("picture");

        Optional<Member> findMember = memberRepository.findByEmailAndSocialType(email, GOOGLE);
        if (findMember.isPresent()) {
            if (findMember.get().getMemberType().equals(request.getMemberType())) {
                return tokenGenerator.create(findMember.get());
            }
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        Member member = Member.join(email, name, request.getMemberType(), GOOGLE);
        MemberProfile profile = getGoogleProfile(picture, member);
        member.setMemberProfile(profile);
        memberRepository.save(member);

        //초대가입인 경우
        if (StringUtils.isNotEmpty(request.getUuid())) {
            mappingTrainerAndStudent(member, request.getUuid(), name, true);
        }
        return tokenGenerator.create(member);
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
            log.error("error => {}", e.getStackTrace()[0]);
            throw new CustomException(JSON_PARSING_ERROR);
        }
    }

    private OAuthInfo getGoogleAccessToken(String code, String redirectUri) {
        String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", oAuthProperties.getGoogle().getClientId());
        requestBody.add("client_secret", oAuthProperties.getGoogle().getClientSecret());
        requestBody.add("grant_type", oAuthProperties.getGoogle().getGrantType());
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("code", decode);
        Mono<OAuthInfo> responseMono = null;
        try {
            responseMono = webClient.post()
                    .uri(oAuthProperties.getGoogle().getTokenUri())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(GoogleError.class).flatMap(e -> {
                                log.error("error => {}", e);
                                return Mono.error(new OAuthException(e.getErrorDescription()));
                            }))
                    .bodyToMono(OAuthInfo.class);
        } catch (Exception e) {
            log.error("error => {}", e.getStackTrace()[0]);
        }
        return responseMono.share().block();
    }


    public void mappingTrainerAndStudent(Member member, String uuid, String reqName, boolean isSocial) {
        Map<String, String> map = getInviteMappingData(uuid);
        String name = map.get("name");
        if (!isSocial && !name.equals(reqName)) throw new CustomException(INVITE_NAME_NOT_VALID);
        member.changeName(name);

        //트레이너 학생 매핑
        Long trainerId = Long.valueOf(map.get("trainerId"));
        trainerService.mappingMemberAndTrainer(trainerId, member.getId());

        //수강권 등록
        int lessonCnt = Integer.parseInt(map.get("lessonCnt"));
        courseService.addCourse(trainerId, CourseAddCommand.create(member.getId(), lessonCnt));

        String invitationKey = RedisKeyPrefix.INVITATION.getDescription() + uuid;
        redisService.deleteValues(invitationKey);
    }

    private Map<String, String> getInviteMappingData(String uuid) {
        String invitationKey = RedisKeyPrefix.INVITATION.getDescription() + uuid;
        String mappedData = redisService.getValues(invitationKey);
        if (isEmpty(mappedData)) {
            throw new CustomException(INVITE_LINK_NOT_FOUND);
        }
        HashMap<String, String> map = new HashMap<>();
        try {
            map = objectMapper.readValue(mappedData, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error("error => {}", e.getStackTrace()[0]);
        }
        return map;
    }

    private void validatePassword(CommandJoinMember request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(CONFIRM_PASSWORD_NOT_MATCHED);
        }
        if (Utils.validatePassword(request.getPassword())) {
            throw new CustomException(PASSWORD_POLICY_VIOLATION);
        }
    }

    private void validateName(String name) {
        if (Utils.validateNameLength(name)) {
            throw new CustomException(MEMBER_NAME_LENGTH_NOT_VALID);
        }

        if (Utils.validateNameFormat(name)) {
            throw new CustomException(MEMBER_NAME_NOT_VALID);
        }
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

    private static String decordToken(OAuthInfo result) {
        byte[] decode = new Base64UrlCodec().decode(result.getIdToken().split("\\.")[1]);
        return new String(decode, StandardCharsets.UTF_8);
    }

    private MemberProfile getProfile(String profileImage, Member member) {
        byte[] image = getProfileImage(profileImage);
        String savedFileName = createFileName("profile/");
        ObjectMetadata objectMetadata = createObjectMetadata(image.length, IMAGE_PNG_VALUE);
        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            amazonS3.putObject(
                    bucketName,
                    savedFileName,
                    inputStream,
                    objectMetadata
            );
            String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString();
            return MemberProfile.create(savedFileName, fileUrl, member);
        } catch (IOException e) {
            log.error("error => {}", e.getStackTrace()[0]);
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
    }

    private MemberProfile getGoogleProfile(String profileImage, Member member) {
        byte[] image = getProfileImage(profileImage);
        String savedFileName = createFileName("profile/");
        ObjectMetadata objectMetadata = createObjectMetadata(image.length, IMAGE_PNG_VALUE);
        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            amazonS3.putObject(bucketName, savedFileName, inputStream, objectMetadata);
            String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString();
            return MemberProfile.create(savedFileName, fileUrl, member);
        } catch (IOException e) {
            log.error("error => {}", e.getStackTrace()[0]);
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
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
}
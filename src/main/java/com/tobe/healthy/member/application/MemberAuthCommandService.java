package com.tobe.healthy.member.application;

import static com.tobe.healthy.common.Utils.CDN_DOMAIN;
import static com.tobe.healthy.common.Utils.EMAIL_AUTH_TIMEOUT;
import static com.tobe.healthy.common.Utils.S3_DOMAIN;
import static com.tobe.healthy.common.Utils.createObjectMetadata;
import static com.tobe.healthy.common.Utils.createProfileName;
import static com.tobe.healthy.common.error.ErrorCode.CONFIRM_PASSWORD_NOT_MATCHED;
import static com.tobe.healthy.common.error.ErrorCode.FILE_UPLOAD_ERROR;
import static com.tobe.healthy.common.error.ErrorCode.INVITE_LINK_NOT_FOUND;
import static com.tobe.healthy.common.error.ErrorCode.INVITE_NAME_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.JSON_PARSING_ERROR;
import static com.tobe.healthy.common.error.ErrorCode.MAIL_AUTH_CODE_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_EMAIL_DUPLICATION;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_ID_DUPLICATION;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_LOGIN_FAILED;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NAME_LENGTH_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NAME_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.MEMBER_NOT_FOUND;
import static com.tobe.healthy.common.error.ErrorCode.PASSWORD_POLICY_VIOLATION;
import static com.tobe.healthy.common.error.ErrorCode.PROFILE_ACCESS_FAILED;
import static com.tobe.healthy.common.error.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static com.tobe.healthy.common.error.ErrorCode.REFRESH_TOKEN_NOT_VALID;
import static com.tobe.healthy.common.error.ErrorCode.USERID_POLICY_VIOLATION;
import static com.tobe.healthy.member.domain.entity.SocialType.APPLE;
import static com.tobe.healthy.member.domain.entity.SocialType.GOOGLE;
import static com.tobe.healthy.member.domain.entity.SocialType.KAKAO;
import static com.tobe.healthy.member.domain.entity.SocialType.NAVER;
import static com.tobe.healthy.member.domain.entity.SocialType.NONE;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tobe.healthy.common.Utils;
import com.tobe.healthy.common.error.CustomException;
import com.tobe.healthy.common.error.OAuthError.GoogleError;
import com.tobe.healthy.common.error.OAuthError.KakaoError;
import com.tobe.healthy.common.error.OAuthError.NaverError;
import com.tobe.healthy.common.error.OAuthException;
import com.tobe.healthy.common.redis.RedisKeyPrefix;
import com.tobe.healthy.common.redis.RedisService;
import com.tobe.healthy.config.KeyUtil;
import com.tobe.healthy.config.OAuthProperties;
import com.tobe.healthy.config.jwt.JwtTokenGenerator;
import com.tobe.healthy.course.application.CourseService;
import com.tobe.healthy.course.domain.dto.in.CourseAddCommand;
import com.tobe.healthy.member.domain.dto.in.CommandFindMemberPassword;
import com.tobe.healthy.member.domain.dto.in.CommandJoinMember;
import com.tobe.healthy.member.domain.dto.in.CommandLoginMember;
import com.tobe.healthy.member.domain.dto.in.CommandRefreshToken;
import com.tobe.healthy.member.domain.dto.in.CommandSocialLogin;
import com.tobe.healthy.member.domain.dto.in.CommandValidateEmail;
import com.tobe.healthy.member.domain.dto.in.CommandVerification;
import com.tobe.healthy.member.domain.dto.in.IdToken;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo;
import com.tobe.healthy.member.domain.dto.in.OAuthInfo.NaverUserInfo;
import com.tobe.healthy.member.domain.dto.out.CommandFindMemberPasswordResult;
import com.tobe.healthy.member.domain.dto.out.CommandJoinMemberResult;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberProfile;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.domain.entity.NonMember;
import com.tobe.healthy.member.domain.entity.SocialType;
import com.tobe.healthy.member.domain.entity.Tokens;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.member.repository.NonMemberRepository;
import com.tobe.healthy.trainer.application.TrainerService;
import io.jsonwebtoken.impl.Base64UrlCodec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
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
    private final NonMemberRepository nonMemberRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${oauth.apple.team-id}")
    private String appleTeamId;

    @Value("${oauth.apple.client-id}")
    private String appleClientId;

    @Value("${oauth.apple.key-path}")
    private String appleKeyPath;

    @Value("${oauth.apple.login-key}")
    private String appleLoginKey;

    public String sendEmailVerification(CommandValidateEmail request) {
        memberRepository.findByEmail(request.getEmail()).ifPresent(e -> {
            throw new CustomException(MEMBER_EMAIL_DUPLICATION);
        });

        String authKey = Utils.getAuthCode(6);

        redisService.setValuesWithTimeout(request.getEmail(), authKey, EMAIL_AUTH_TIMEOUT); // 3분

        // 3. 이메일에 인증번호 전송한다.
        mailService.sendAuthMail(request.getEmail(), authKey);

        return request.getEmail();
    }

    public Boolean verifyEmailAuthNumber(CommandVerification request) {
        String value = redisService.getValues(request.getEmail());

        if (isEmpty(value) || !value.equals(request.getEmailKey())) {
            throw new CustomException(MAIL_AUTH_CODE_NOT_VALID);
        }

        redisService.deleteValues(request.getEmail());

        return true;
    }

    public CommandJoinMemberResult joinMember(CommandJoinMember request) {
        validateName(request.getName());
        validatePassword(request);
        validateDuplicationUserId(request.getUserId());
        validateDuplicationEmail(request.getEmail());

        String password = passwordEncoder.encode(request.getPassword());
        Member member = Member.join(request, password);

        log.info("[회원가입] member: {}", member);
        if (StringUtils.isEmpty(request.getUuid())) { //회원가입
            memberRepository.save(member);
            return CommandJoinMemberResult.from(member);

        } else { //미가입 회원이 초대받아서 가입하는 경우 (회원 정보 update)
            return updateNonMemberInfo(request, password);
        }
    }

    public Tokens login(CommandLoginMember request) {
        Member member = memberRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new CustomException(MEMBER_LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(MEMBER_LOGIN_FAILED);
        }

        if (!request.getMemberType().equals(member.getMemberType())) {
            throw new IllegalArgumentException(String.format("%s로 가입한 사용자입니다.", member.getTransformedMemberType()));
        }

        return tokenGenerator.create(member);
    }

    public Tokens refreshToken(CommandRefreshToken request) {
        String result = redisService.getValues(request.getUserId());

        if (isEmpty(result)) {
            throw new CustomException(REFRESH_TOKEN_NOT_FOUND);
        }

        if (!result.equals(request.getRefreshToken())) {
            throw new CustomException(REFRESH_TOKEN_NOT_VALID);
        }

        Member member = memberRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        return tokenGenerator.exchangeAccessToken(member.getId(),
                member.getName(),
                member.getUserId(),
                member.getMemberType(),
                request.getRefreshToken(),
                member.getGym());
    }

    public CommandFindMemberPasswordResult findMemberPW(CommandFindMemberPassword request) {
        Member member = memberRepository.findByEmailAndName(request.getEmail(), request.getName())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.getSocialType() != NONE) {
            return CommandFindMemberPasswordResult.from(
                    member,
                    String.format("%s은 %s 계정으로 가입되어 있습니다.", member.getEmail(), member.getSocialType().getDescription())
            );
        }

        sendResetPassword(member.getEmail(), member);

        return CommandFindMemberPasswordResult.from(
                member,
                String.format("%s으로 초기화된 비밀번호가 발송되었습니다.", member.getEmail())
        );
    }

    public CommandJoinMemberResult updateNonMemberInfo(CommandJoinMember request, String password) {
        String invitationLink = "https://main.to-be-healthy.shop/invite?type=student&uuid=" + request.getUuid();
        NonMember nonMember = nonMemberRepository.findByInvitationLink(invitationLink)
                .orElseThrow(() -> new CustomException(INVITE_LINK_NOT_FOUND));

        Member member = memberRepository.findById(nonMember.getMember().getId())
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        if(!member.getName().equals(request.getName())) throw new CustomException(INVITE_NAME_NOT_VALID);

        member.updateNonMemberInfo(request, password);
        nonMemberRepository.delete(nonMember);
        return CommandJoinMemberResult.from(member);
    }

    public Tokens getNaverAccessToken(CommandSocialLogin request) {
        OAuthInfo response = getNaverOAuthAccessToken(request.getCode(), request.getState());

        NaverUserInfo authorization = getNaverUserInfo(response);

        Optional<Member> findMember = memberRepository.findByEmail(authorization.getResponse().getEmail());

        if (findMember.isPresent()) {
            if (isJoinMember(findMember.get(), NAVER, request.getMemberType())) {
                return tokenGenerator.create(findMember.get());
            }
        }

        Member member = Member.join(
                authorization.getResponse().getEmail(),
                authorization.getResponse().getName(),
                request.getMemberType(),
                NAVER
        );

        MemberProfile profile = getProfile(authorization.getResponse().getProfileImage(), member);
        member.setMemberProfile(profile);
        memberRepository.save(member);

        //초대가입인 경우
        if (StringUtils.isNotEmpty(request.getUuid())) {
            mappingTrainerAndStudent(member, request.getUuid(), authorization.getResponse().getName(), true);
        }
        log.info("[네이버 회원가입 및 로그인] member: {}", member);
        return tokenGenerator.create(member);
    }

    public Tokens getKakaoAccessToken(CommandSocialLogin request) {
        IdToken response = getKakaoOAuthAccessToken(request.getCode(), request.getRedirectUrl());

        Optional<Member> findMember = memberRepository.findByEmail(response.getEmail());

        if (findMember.isPresent()) {
            if (isJoinMember(findMember.get(), KAKAO, request.getMemberType())) {
                return tokenGenerator.create(findMember.get());
            }
        }

        Member member = Member.join(response.getEmail(), response.getNickname(), request.getMemberType(), KAKAO);
        MemberProfile profile = getProfile(response.getPicture(), member);
        member.setMemberProfile(profile);
        memberRepository.save(member);

        //초대가입인 경우
        if (StringUtils.isNotEmpty(request.getUuid())) {
            mappingTrainerAndStudent(member, request.getUuid(), response.getNickname(), true);
        }
        log.info("[카카오 회원가입 및 로그인] member: {}", member);
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

        Optional<Member> findMember = memberRepository.findByEmail(email);

        if (findMember.isPresent()) {
            if (isJoinMember(findMember.get(), GOOGLE, request.getMemberType())) {
                return tokenGenerator.create(findMember.get());
            }
        }

        Member member = Member.join(email, name, request.getMemberType(), GOOGLE);
        MemberProfile profile = getGoogleProfile(picture, member);
        member.setMemberProfile(profile);
        memberRepository.save(member);

        //초대가입인 경우
        if (StringUtils.isNotEmpty(request.getUuid())) {
            mappingTrainerAndStudent(member, request.getUuid(), name, true);
        }
        log.info("[구글 회원가입 및 로그인] member: {}", member);
        return tokenGenerator.create(member);
    }

    public Tokens getAppleOAuth(CommandSocialLogin request) {
        String token = decordToken(request.getId_token());
        try {
            IdToken userInfo = new ObjectMapper().readValue(token, IdToken.class);
            Optional<Member> findMember = memberRepository.findByUserId(userInfo.getSub());
            log.info("userInfo => {}", userInfo);
            if (findMember.isPresent()) {
                if (isJoinMember(findMember.get(), APPLE, request.getMemberType())) {
                    return tokenGenerator.create(findMember.get());
                }
            }

            // TODO: 24. 7. 16. 애플로 로그인한 계정 삭제시 애플쪽에도 추가적으로 삭제처리 작업 필요
            String name = request.getUser().getName().getLastName() + request.getUser().getName().getFirstName();

            Member member = Member.builder()
                    .userId(userInfo.getSub())
                    .email(userInfo.getEmail())
                    .name(name)
                    .memberType(request.getMemberType())
                    .socialType(APPLE)
                    .build();

            memberRepository.save(member);

            //초대가입인 경우
            if (StringUtils.isNotEmpty(request.getUuid())) {
                mappingTrainerAndStudent(member, request.getUuid(), name, true);
            }

            log.info("[애플 회원가입 및 로그인] member: {}", member);

            return tokenGenerator.create(member);

        } catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
    }

    public String createClientSecret() {
        Date now = new Date();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .issuer(appleTeamId)
            .issueTime(now)
            .expirationTime(new Date(now.getTime() + 3600000))
            .audience("https://appleid.apple.com")
            .subject(appleClientId)
            .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(appleLoginKey).build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);

        try {
            ECPrivateKey ecPrivateKey = loadPrivateKey(appleKeyPath);
            JWSSigner signer = new ECDSASigner(ecPrivateKey);
            signedJWT.sign(signer);
        } catch (InvalidKeySpecException | IOException e) {
			throw new RuntimeException(e);
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}

		return signedJWT.serialize();
    }

    private ECPrivateKey loadPrivateKey(String keyPath) throws IOException, InvalidKeySpecException {
        File file = new File(keyPath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + keyPath);
        }

        try (PemReader pemReader = new PemReader(new FileReader(file))) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            return KeyUtil.getPrivateKeyFromBytes(content);
        }
    }


    private boolean isJoinMember(Member member, SocialType google, MemberType memberType) {
        if (member.getSocialType().equals(google)) {
            if (!member.getMemberType().equals(memberType)) {
                throw new IllegalArgumentException(String.format("%s로 가입한 사용자입니다.", member.getTransformedMemberType()));
            }
            return true;
        }
        throw new CustomException(MEMBER_NOT_FOUND);
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
            String token = decordToken(result.getIdToken());
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

    private static String decordToken(String idToken) {
        byte[] decode = new Base64UrlCodec().decode(idToken.split("\\.")[1]);
        return new String(decode, StandardCharsets.UTF_8);
    }

    private MemberProfile getProfile(String profileImage, Member member) {
        byte[] image = getProfileImage(profileImage);
        String savedFileName = createProfileName("origin/profile/");
        ObjectMetadata objectMetadata = createObjectMetadata(image.length, IMAGE_PNG_VALUE);
        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            amazonS3.putObject(
                    bucketName,
                    savedFileName,
                    inputStream,
                    objectMetadata
            );

            String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString().replaceAll(S3_DOMAIN, CDN_DOMAIN);

            return MemberProfile.create(savedFileName, fileUrl, member);

        } catch (IOException e) {
            log.error("error => {}", e.getStackTrace()[0]);
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
    }

    private MemberProfile getGoogleProfile(String profileImage, Member member) {
        byte[] image = getProfileImage(profileImage);
        String savedFileName = createProfileName("origin/profile/");
        ObjectMetadata objectMetadata = createObjectMetadata(image.length, IMAGE_PNG_VALUE);
        try (InputStream inputStream = new ByteArrayInputStream(image)) {
            amazonS3.putObject(bucketName, savedFileName, inputStream, objectMetadata);

            String fileUrl = amazonS3.getUrl(bucketName, savedFileName).toString().replaceAll(S3_DOMAIN, CDN_DOMAIN);

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
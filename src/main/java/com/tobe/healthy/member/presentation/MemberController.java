package com.tobe.healthy.member.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.ResponseCookie.from;

import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult;
import com.tobe.healthy.member.domain.entity.Tokens;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class MemberController {

    @Value("${jwt.refresh-token-valid-seconds}")
    private static Long refreshTokenValidSeconds;

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberRegisterCommandResult> create(@RequestBody MemberRegisterCommand request) {
        return ResponseEntity.ok(memberService.create(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@RequestBody MemberLoginCommand request) {
        Tokens tokens = memberService.login(request);

        return ResponseEntity.ok()
            .header(AUTHORIZATION, "Bearer " + tokens.getAccessToken())
            .header("Set-Cookie", createRefreshCookie(tokens.getRefreshToken()).toString())
            .body(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Tokens> refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        Tokens tokens = memberService.refresh(refreshToken);

        return ResponseEntity.ok()
            .header(AUTHORIZATION, "Bearer " + tokens.getAccessToken())
            .header("Set-Cookie", createRefreshCookie(tokens.getRefreshToken()).toString())
            .body(tokens);
    }

    @GetMapping("/email-check")
    public ResponseEntity<Boolean> isAvailableEmail(@RequestParam String email) {
        return ResponseEntity.ok(memberService.isAvailableEmail(email));
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth/refresh")
            .maxAge(refreshTokenValidSeconds)
            .build();
    }
}
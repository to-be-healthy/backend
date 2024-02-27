package com.tobe.healthy.member.presentation;

import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.MemberFindIdCommand;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberOauthCommandRequest;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult;
import com.tobe.healthy.member.domain.entity.Tokens;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private final MemberService memberService;

    @GetMapping("/code/kakao")
    public ResponseEntity<?> oauth(MemberOauthCommandRequest request) {
        log.info("request => " + request);
        return ResponseEntity.ok(memberService.getAccessToken(request.getCode()));
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestParam(value = "mobileNum") String mobileNum) {
        return ResponseEntity.ok(memberService.sendAuthenticationNumber(mobileNum));
    }

    @PostMapping("/verification")
    public ResponseEntity<Boolean> checkAuthenticationNumber(@RequestParam(value = "mobileNum") String mobileNum,
                                                             @RequestParam(value = "verificationNum") String verificationNum) {
        return ResponseEntity.ok(memberService.checkAuthenticationNumber(mobileNum, verificationNum));
    }

    @PostMapping("/join")
    public ResponseEntity<MemberRegisterCommandResult> create(@RequestBody @Valid MemberRegisterCommand request) {
        return ResponseEntity.ok(memberService.create(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@RequestBody @Valid MemberLoginCommand request) {
        return ResponseEntity.ok(memberService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Tokens> refresh(String refreshToken) {
        return ResponseEntity.ok(memberService.refresh(refreshToken));
    }

    @GetMapping("/email-check")
    public ResponseEntity<Boolean> isAvailableEmail(@RequestParam String email) {
        return ResponseEntity.ok(memberService.isAvailableEmail(email));
    }

    @PostMapping("/find")
    public ResponseEntity<String> findMemberId(@RequestBody @Valid MemberFindIdCommand request) {
        return ResponseEntity.ok(memberService.findMemberId(request));
    }
}
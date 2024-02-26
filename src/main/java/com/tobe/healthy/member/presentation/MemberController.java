package com.tobe.healthy.member.presentation;

import com.tobe.healthy.member.application.MemberService;
import com.tobe.healthy.member.domain.dto.in.MemberLoginCommand;
import com.tobe.healthy.member.domain.dto.in.MemberRegisterCommand;
import com.tobe.healthy.member.domain.dto.out.MemberRegisterCommandResult;
import com.tobe.healthy.member.domain.entity.Tokens;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberRegisterCommandResult> create(@RequestBody @Valid MemberRegisterCommand request) {
        return ResponseEntity.ok(memberService.create(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@RequestBody MemberLoginCommand request) {
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
}
package com.livingalone.springboot.domain.member.controller;


import com.livingalone.springboot.domain.member.dto.LoginDto;
import com.livingalone.springboot.domain.member.dto.SignUpDto;
import com.livingalone.springboot.domain.member.entity.Member;
import com.livingalone.springboot.domain.member.service.MemberService;
import com.livingalone.springboot.global.jwt.dto.TokenDto;
import com.livingalone.springboot.global.jwt.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    private final TokenService tokenService;

    @Autowired
    public MemberController(MemberService memberService, TokenService tokenService) {
        this.memberService = memberService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Boolean> signUp(@Valid @RequestBody SignUpDto signUpDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("아이디 혹은 비밀번호를 잘못입력했습니다.");
            return ResponseEntity.ok(false);
        }
        return memberService.signUp(signUpDto);
    }   // 회원가입

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        return tokenService.makeToken(loginDto);
    } // 회원 로그인


    @PostMapping("/memberId/check")
    public ResponseEntity<Boolean> checkDuplicate(@RequestBody HashMap<String, String> member) {
        String memberId = member.get("memberId");
        log.info(memberId);

        Optional<Member> byMember = memberService.findByMemberId(memberId);

        if (byMember.isEmpty()) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }   // 회원 아이디 중복 검사
}

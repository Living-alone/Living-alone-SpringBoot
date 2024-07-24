package com.livingalone.springboot.domain.member.service;

//import capstone.capstone.domain.member.dto.SignUpDto;
//import capstone.capstone.domain.member.entity.Member;
//import capstone.capstone.domain.member.repository.AuthorityJpaRepository;
//import capstone.capstone.domain.member.repository.MemberJpaRepository;
//import capstone.capstone.domain.profile.entity.Profile;
//import capstone.capstone.global.jwt.entity.Authority;
import com.livingalone.springboot.domain.member.dto.SignUpDto;
import com.livingalone.springboot.domain.member.entity.Member;
import com.livingalone.springboot.domain.member.repository.AuthorityJpaRepository;
import com.livingalone.springboot.domain.member.repository.MemberJpaRepository;
import com.livingalone.springboot.domain.profile.entity.Profile;
import com.livingalone.springboot.global.jwt.entity.Authority;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final String PHONE_PATTERN = "^(02|0[1-9][0-9]?)-[0-9]{3,4}-[0-9]{4}$";
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);

    private final MemberJpaRepository memberJpaRepository;//멤버 저장소
    private final PasswordEncoder passwordEncoder;
    private final AuthorityJpaRepository authorityJpaRepository;

    @Transactional
    public ResponseEntity<Boolean> signUp(SignUpDto memberDto) {
        if (memberJpaRepository.findByMemberId(memberDto.getMemberId()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }
        Authority authority = authorityJpaRepository.findByAuthority("ROLE_USER")
                .orElseGet(() -> Authority.builder()
                        .authority("ROLE_USER")
                        .build());

        Profile profile = new Profile();

        Member member = Member.builder()
                .memberId(memberDto.getMemberId())
                .name(memberDto.getName())
                .authority(authority)
                .profile(profile)
                .activate(true)
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .build();


        Member save = memberJpaRepository.save(member);
        log.info("멤버 저장 됨 {}", save.getId());
        log.info(save.getPassword());
        return ResponseEntity.ok(true);
    }

    public Optional<Member> findByMemberId(String memberId) {
        return memberJpaRepository.findByMemberId(memberId);
    }

    public Member findByEmail(String email) {
        return memberJpaRepository.findByMemberId(email).orElse(null);
    }

    public boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPhone(String phone) {
        Matcher matcher = phonePattern.matcher(phone);
        return matcher.matches();
    }
}
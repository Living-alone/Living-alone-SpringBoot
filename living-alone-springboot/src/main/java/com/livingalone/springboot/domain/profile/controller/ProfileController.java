//package com.livingalone.springboot.domain.profile.controller;
//
////import capstone.capstone.domain.member.dto.EditInformation;
////import capstone.capstone.domain.member.dto.Information;
////import capstone.capstone.domain.member.dto.ProfileInformation;
////import capstone.capstone.domain.profile.service.ProfileService;
////import capstone.capstone.domain.profile.valid.exception.IntroduceOutOfBoundException;
////import capstone.capstone.domain.profile.valid.exception.NotAuthorizedEditException;
//import com.livingalone.springboot.domain.member.dto.EditInformation;
//import com.livingalone.springboot.domain.member.dto.Information;
//import com.livingalone.springboot.domain.member.dto.ProfileInformation;
//import com.livingalone.springboot.domain.profile.service.ProfileService;
//import com.livingalone.springboot.domain.profile.valid.exception.IntroduceOutOfBoundException;
//import com.livingalone.springboot.domain.profile.valid.exception.NotAuthorizedEditException;
//import jakarta.validation.Valid;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//@Slf4j
//@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
//@AllArgsConstructor
//public class ProfileController {
//    private final ProfileService profileService;
//
//    @PostMapping("/information/add")
//    public ResponseEntity<Boolean> addInformation(@Valid @RequestBody Information information,
//                                                  BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            throw new IntroduceOutOfBoundException("한 줄 소개의 길이가 22자를 넘었습니다.");
//        }
//        log.info("멤버 아이디 = {}", information.getMemberId());
//        boolean resultInfo = profileService.addInformation(information);
//        boolean resultTechnic = profileService.addTechnic(information.getMemberId(), information.getTechnics());
//        return ResponseEntity.ok(resultInfo && resultTechnic);
//    }
//
//    @PostMapping("/information/edit")
//    public ResponseEntity<Boolean> editInformation(@Valid @RequestBody EditInformation editInformation,
//                                                   BindingResult bindingResult) throws NotAuthorizedEditException {
//        if (bindingResult.hasErrors()) {
//            throw new IntroduceOutOfBoundException("한 줄 소개의 길이가 22자를 넘었습니다.");
//        }
//        if (editInformation.getMemberId().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
//            boolean resultInfo = profileService.editInformation(editInformation);
//            boolean resultTechnic = profileService.editTechnics(editInformation.getMemberId(),
//                    editInformation.getTechnics());
//            return ResponseEntity.ok(resultInfo && resultTechnic);
//        } else {
//            throw new NotAuthorizedEditException("프로필 수정 권한이 없습니다.");
//        }
//    }
//
//    @GetMapping("/information/check/my")
//    public ResponseEntity<ProfileInformation> checkInformation() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String memberId = authentication.getName();
//        log.info("memberId = {}", memberId);
//        return ResponseEntity.ok(profileService.checkInformation(memberId));
//    }
//
//    @GetMapping("/information/check/{memberId}")
//    public ResponseEntity<ProfileInformation> checkInformationOther(@PathVariable String memberId) {
//        return ResponseEntity.ok(profileService.checkInformation(memberId));
//    }
//
//    @GetMapping("/information/profileImage/{memberId}")
//    public ResponseEntity<String> checkProfileImage(@PathVariable String memberId) {
//        return ResponseEntity.ok(profileService.checkProfileImage(memberId));
//    }
//}

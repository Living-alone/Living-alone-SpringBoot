package com.livingalone.springboot.chat.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.livingalone.springboot.chat.dto.ChatPartnerRequest;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.service.ChatService;
import com.livingalone.springboot.chat.utils.SecondaryJwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatController {

    private final ChatService chatService;

    // 참여중인 채팅방 목록 반환
    @PostMapping("chatRooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms(@RequestBody Map<String, Object> payload) {
        Long userId = ((Number) payload.get("userId")).longValue();
        List<ChatRoom> chatRooms = chatService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok().body(chatRooms);
    }

    // 채팅 상대 userId 반환
    @PostMapping("chatPartner")
    public ResponseEntity<Map<String, Long>> getChatPartner(@RequestBody ChatPartnerRequest chatPartnerRequest) {
        Long userId = chatService.getChatPartnerUserId(chatPartnerRequest);
        Map<String, Long> response = new HashMap<>();
        response.put("userId", userId);
        // 조회 실패 시 ( userId == -1L ) BAD_REQUEST 반환.
        return ResponseEntity.status(userId == -1 ? HttpStatus.BAD_REQUEST : HttpStatus.ACCEPTED).body(response);
    }


    @PostMapping("/getSecondaryToken")
    public ResponseEntity<String> getSecondaryToken() {
        // jwt -> userId 추출 로직으로 변경 필요.
        Long userId = 1L;

        String secondaryToken = chatService.generateSecondaryToken(userId);
        return ResponseEntity.ok().body("{\"token\": \"" + secondaryToken + "\"}");
    }

}
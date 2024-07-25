package com.livingalone.springboot.chat.controller;

import com.livingalone.springboot.chat.dto.ChatPartnerRequest;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
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

}
package com.livingalone.springboot.chat.controller;

import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("chatRooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms(@RequestBody Map<String, Object> payload) {
        Long userId = ((Number) payload.get("userId")).longValue();
        List<ChatRoom> chatRooms = chatService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok().body(chatRooms);
    }


}

package com.livingalone.springboot.chat.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.livingalone.springboot.chat.dto.ChatPartnerRequest;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatController {

    private final ChatService chatService;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

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

    @PostMapping("/imageUploadTest")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile multipartFile) {
        String fileName = "testImage.png";
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), null);
            PutObjectResult result = amazonS3.putObject(putObjectRequest);
            System.out.println("result = " + result);

            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
            return ResponseEntity.ok().body(fileUrl);
        }
        catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR");
        }
    }

}
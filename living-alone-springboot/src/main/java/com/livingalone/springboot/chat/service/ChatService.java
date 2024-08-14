package com.livingalone.springboot.chat.service;

import com.livingalone.springboot.chat.dto.ChatPartnerRequest;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.repository.ChatRoomRepository;
import com.livingalone.springboot.chat.utils.SecondaryJwtUtil;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    @Value("${secondary-token.secret}")
    private String secretKey;

    private final Long expiredMs = 1000 * 20L;

    public List<ChatRoom> getChatRoomsByUserId(Long userId) {
//        List<ChatRoom> chatRooms = chatRoomRepository.findByPostAuthorId(userId);
//        chatRooms.addAll(chatRoomRepository.findByPostViewerId(userId));
//        return chatRooms;

        // postAuthorId == userId || postViewerId == userId
        return chatRoomRepository.findByPostAuthorIdOrPostViewerId(userId, userId);
    }

    public Long getChatPartnerUserId(ChatPartnerRequest chatPartnerRequest) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByChatRoomIdAndUserId(chatPartnerRequest.getChatRoomId(), chatPartnerRequest.getUserId());
        // 요청과 일치하는 채팅방 없는 경우 -1 반환.
        if(optionalChatRoom.isEmpty()) return -1L;
        ChatRoom chatRoom = optionalChatRoom.get();
        // chatRoom의 postAuthorId, postViewerId 중 요청받은 userId와 다른 Id 반환.
        return Objects.equals(chatRoom.getPostAuthorId(), chatPartnerRequest.getUserId()) ? chatRoom.getPostViewerId() : chatRoom.getPostAuthorId();
    }

    // WebSocketSession 의 HTTP handshake 에서 header -> token 추출.
    public String extractTokenFromHeaders(WebSocketSession session) {
        String bearerToken = session.getHandshakeHeaders().getFirst("Authorization");
        System.out.println("headers = " + session.getHandshakeHeaders().toString());
        System.out.println("bearerToken = " + bearerToken);
        if(bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return bearerToken.substring(7);

    }

    // 임시 jwt 검증 메소드
    public boolean validateToken(String token) {
        if(token == null) return false;
        return true;
    }

    public String generateSecondaryToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return SecondaryJwtUtil.createJwt(userId, key, expiredMs);
    }

    /*
     * url 에서 param 추출하기 위한 메소드.
     * request url : "ws://~~/chat?userId=1&partnerUserId=2&postId=1"
     * url.split("&") -> userId=1, partnerUserId=2, postId=1
     * collect~~ -> stream 을 map 으로 변환.
     */
    public Map<String, String> extractParamsFromUrl(String url) {
        return Arrays.stream(url.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }

}

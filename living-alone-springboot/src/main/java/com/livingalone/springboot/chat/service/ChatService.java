package com.livingalone.springboot.chat.service;

import com.livingalone.springboot.chat.dto.ChatPartnerRequest;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

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

}

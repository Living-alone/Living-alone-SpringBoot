package com.livingalone.springboot.chat.service;

import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

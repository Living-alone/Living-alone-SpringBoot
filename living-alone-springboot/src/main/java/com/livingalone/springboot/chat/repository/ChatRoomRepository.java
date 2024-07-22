package com.livingalone.springboot.chat.repository;

import com.livingalone.springboot.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByPostAuthorId(Long userId);

    List<ChatRoom> findByPostViewerId(Long userId);

    List<ChatRoom> findByPostAuthorIdOrPostViewerId(Long postAuthorId, Long postViewerId);

}
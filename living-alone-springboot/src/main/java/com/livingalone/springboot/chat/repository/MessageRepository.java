package com.livingalone.springboot.chat.repository;

import com.livingalone.springboot.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatRoomIdOrderBySendAtAsc(Long chatRoomId);

}

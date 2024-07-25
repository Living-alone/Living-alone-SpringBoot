package com.livingalone.springboot.chat.repository;

import com.livingalone.springboot.chat.dto.ChatPartnerRequest;
import com.livingalone.springboot.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByPostAuthorId(Long userId);

    List<ChatRoom> findByPostViewerId(Long userId);

    List<ChatRoom> findByPostAuthorIdOrPostViewerId(Long postAuthorId, Long postViewerId);

    // 'c'는 별칭,  ':'는 파라미터 동적 바인딩 위해 필요.
    @Query("SELECT c FROM ChatRoom c WHERE c.chatRoomId = :chatRoomId AND (c.postAuthorId = :userId OR c.postViewerId = :userId)")
    Optional<ChatRoom> findByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("userId")  Long userId);

}
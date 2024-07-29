package com.livingalone.springboot.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private Long chatRoomId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String content;

    private LocalDateTime sendAt;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

}

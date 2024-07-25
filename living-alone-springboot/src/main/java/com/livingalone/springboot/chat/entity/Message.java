package com.livingalone.springboot.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
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

    private String image_url;

    public enum MessageType {
        TEXT,
        LOCATION,
        IMAGE
    }

}

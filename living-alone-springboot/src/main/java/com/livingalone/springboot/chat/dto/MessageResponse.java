package com.livingalone.springboot.chat.dto;

import com.livingalone.springboot.chat.entity.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MessageResponse {

    private MessageType messageType;

    private Long userId;

    private String content;

    private LocalDateTime sendAt;

    private byte[] imageFileData;

}

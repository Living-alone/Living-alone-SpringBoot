package com.livingalone.springboot.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MessageResponse {

    private Long userId;

    private String content;

    private LocalDateTime sendAt;

}

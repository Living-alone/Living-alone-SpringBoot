package com.livingalone.springboot.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatPartnerRequest {

    private Long chatRoomId;

    private Long userId;

}

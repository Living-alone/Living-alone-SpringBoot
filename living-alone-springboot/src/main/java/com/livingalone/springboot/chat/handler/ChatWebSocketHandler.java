package com.livingalone.springboot.chat.handler;

import com.livingalone.springboot.chat.repository.ChatRoomRepository;
import com.livingalone.springboot.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private ChatRoomRepository chatRoomRepository;

    private MessageRepository messageRepository;

    // 여러 스레드가 동일한 Set에 접근하기 때문에 동시성 문제 때문에 synchronizedSet 사용.
    // 현재 연결되어있는 session을 저장하는 set.
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    /*
     * 상대 유저와의 chatRoom 을 조회해보고, 채팅방이 있는 경우 이전 메세지들을 로드.
     *
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {


    }

    /*
     *
     * 1. 상대 유저 B가 이미 연결되어 있는 경우 ( == B의 session이 set에 들어있는 경우 ) -> 바로 메세지 보내고, message 테이블에 insert
     * 2. 연결되어 있지 않은 경우 -> message 테이블에만 insert
     *
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {


    }

    /*
     * Set에서 해당 session 제거
     *
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {


    }

}
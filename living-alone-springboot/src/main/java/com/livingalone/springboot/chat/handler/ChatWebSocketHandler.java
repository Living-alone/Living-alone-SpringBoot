package com.livingalone.springboot.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingalone.springboot.chat.dto.MessageDto;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.entity.Message;
import com.livingalone.springboot.chat.repository.ChatRoomRepository;
import com.livingalone.springboot.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatRoomRepository chatRoomRepository;

    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    // 여러 스레드가 동일한 Set에 접근하기 때문에 동시성 문제 때문에 synchronizedSet 사용.
    // 현재 연결되어있는 session을 저장하는 set.
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    /*
     * 상대 유저와의 chatRoom 을 조회해보고, 채팅방이 있는 경우 이전 메세지들을 로드.
     * 요청 url 예시 : ws://~~/chat?userId=1&partnerUserId=2&postId=1
     * postId는 optional ( 게시글에서 채팅방을 생성할 때만 존재 )
     * 1. userId, partnerUserId -> chatRoomId 조회
     * 2. chatRoomId -> message 조회
     *
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        String paramsString = session.getUri().getQuery();
        Map<String, String> params = extractParamsFromUrl(paramsString);

        Long userId = Long.parseLong(params.get("userId"));
        Long partnerUserId = Long.parseLong(params.get("partnerUserId"));

        // (userA, userB) 와 (userB, userA) -> 같은 채팅방으로.
        Optional<ChatRoom> optionalChatRoomFromUserIdAndPartnerUserId = chatRoomRepository.findByPostAuthorIdAndPostViewerId(userId, partnerUserId);
        Optional<ChatRoom> optionalChatRoomFromPartnerUserIdAndUserId = chatRoomRepository.findByPostAuthorIdAndPostViewerId(partnerUserId, userId);

        Long chatRoomId = -1L;

        if (optionalChatRoomFromUserIdAndPartnerUserId.isPresent()) chatRoomId = optionalChatRoomFromUserIdAndPartnerUserId.get().getChatRoomId();
        if (optionalChatRoomFromPartnerUserIdAndUserId.isPresent()) chatRoomId = optionalChatRoomFromPartnerUserIdAndUserId.get().getChatRoomId();

        // 과거 채팅방이 없는 경우 -> 채팅방 추가
        if(chatRoomId == -1L) {
            Long postId = Long.parseLong(params.get("postId"));
            ChatRoom chatRoom = ChatRoom.builder()
                        .postAuthorId(partnerUserId)
                        .postViewerId(userId)
                        .postId(postId)
                        .createdAt(LocalDateTime.now())
                        .build();
            chatRoomRepository.save(chatRoom);
            return;
        }
        List<Message> messages = messageRepository.findByChatRoomIdOrderBySendAtAsc(chatRoomId);
        for(Message message : messages) {
            MessageDto messageDto = MessageDto.builder()
                        .userId(message.getUserId())
                        .content(message.getContent())
                        .sendAt(message.getSendAt())
                        .build();

            String jsonFormatMessage = objectMapper.writeValueAsString(messageDto);
            session.sendMessage(new TextMessage(jsonFormatMessage));
        }









    }

    /*
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
        sessions.remove(session);

    }

    /*
     * url 에서 param 추출하기 위한 메소드.
     * request url : "ws://~~/chat?userId=1&partnerUserId=2&postId=1"
     * url.split("&") -> userId=1, partnerUserId=2, postId=1
     * collect~~ -> stream 을 map 으로 변환.
     */
    private Map<String, String> extractParamsFromUrl(String url) {
        return Arrays.stream(url.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }

}
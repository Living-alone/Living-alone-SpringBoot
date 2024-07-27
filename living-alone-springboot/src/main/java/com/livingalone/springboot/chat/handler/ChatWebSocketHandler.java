package com.livingalone.springboot.chat.handler;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingalone.springboot.chat.dto.MessageResponse;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.entity.Message;
import com.livingalone.springboot.chat.entity.MessageType;
import com.livingalone.springboot.chat.repository.ChatRoomRepository;
import com.livingalone.springboot.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatRoomRepository chatRoomRepository;

    private final MessageRepository messageRepository;

    private final ObjectMapper objectMapper;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.prefixUrl}")
    private String prefixUrl;

    // 여러 스레드가 동일한 Set에 접근하기 때문에 동시성 문제 때문에 synchronizedSet 사용.
    // 현재 연결되어있는 session을 저장하는 set.
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    private final Map<WebSocketSession, Long> getChatRoomIdBySession = new HashMap<>();

    private final Map<WebSocketSession, Long> getUserIdBySession = new HashMap<>();

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
        log.info("---- socket connected ----");
        sessions.add(session);
        String paramsString = session.getUri().getQuery();
        Map<String, String> params = extractParamsFromUrl(paramsString);

        Long userId = Long.parseLong(params.get("userId"));
        Long partnerUserId = Long.parseLong(params.get("partnerUserId"));

        getUserIdBySession.put(session, userId);

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
            getChatRoomIdBySession.put(session, chatRoomId);
            return;
        }
        getChatRoomIdBySession.put(session, chatRoomId);
        List<Message> messages = messageRepository.findByChatRoomIdOrderBySendAtAsc(chatRoomId);
        for(Message message : messages) {
            MessageResponse messageResponse = MessageResponse.builder()
                        .userId(message.getUserId())
                        .content(message.getContent())
                        .sendAt(message.getSendAt())
                        .build();

            String jsonFormatMessage = objectMapper.writeValueAsString(messageResponse);
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
     * 상대에게 image 그대로 전송.
     * DB 에는 Message 로 Format 해서 저장.
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        // binaryMessage -> 데이터 추출
        ByteBuffer byteBuffer = message.getPayload();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);

        /*
         * 파일이름 생성방법
         * 1. UUID.randomUUID()
         * 2. System.currentTimeMillis()
         */

        String fileName = UUID.randomUUID().toString() + ".png"; // 임의 확장자. -> 변경 해야함. ( jpg, png ... )
        amazonS3.putObject(bucketName, fileName, new ByteArrayInputStream(bytes), null);

        String filePath = prefixUrl + fileName;

        // Message Entity 로 맞춰서 포맷 후 저장
        Message messageEntity = Message.builder()
                .chatRoomId(getChatRoomIdBySession.get(session))
                .userId(getUserIdBySession.get(session))
                .messageType(MessageType.IMAGE)
                .sendAt(LocalDateTime.now())
                .image_url(filePath)
                .build();

        messageRepository.save(messageEntity);

    }

    /*
     * Set에서 해당 session 제거
     *
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        getUserIdBySession.remove(session);
        getChatRoomIdBySession.remove(session);

        log.info("---- socket closed ----");

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
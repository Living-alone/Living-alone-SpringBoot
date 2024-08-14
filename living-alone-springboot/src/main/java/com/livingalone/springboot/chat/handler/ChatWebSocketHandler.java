package com.livingalone.springboot.chat.handler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingalone.springboot.chat.dto.MessageResponse;
import com.livingalone.springboot.chat.entity.ChatRoom;
import com.livingalone.springboot.chat.entity.Message;
import com.livingalone.springboot.chat.entity.MessageType;
import com.livingalone.springboot.chat.repository.ChatRoomRepository;
import com.livingalone.springboot.chat.repository.MessageRepository;
import com.livingalone.springboot.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    private final ChatService chatService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 여러 스레드가 동일한 Set에 접근하기 때문에 동시성 문제 때문에 synchronizedSet 사용.
    // 현재 연결되어있는 session을 저장하는 set.
    // private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final Map<WebSocketSession, Long> getChatRoomIdBySession = new HashMap<>();

    private final Map<WebSocketSession, Long> getUserIdBySession = new HashMap<>();

    private final Map<WebSocketSession, Long> getPartnerUserIdBySession = new HashMap<>();

    private final Map<Long, WebSocketSession> getSessionByUserId = new HashMap<>();

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
//        String subProtocol = session.getAcceptedProtocol();
//        System.out.println("Accepted Protocol: " + subProtocol);

        String paramsString = session.getUri().getQuery();
        Map<String, String> params = chatService.extractParamsFromUrl(paramsString);

        Long userId = Long.parseLong(params.get("userId"));
        Long partnerUserId = Long.parseLong(params.get("partnerUserId"));

        getSessionByUserId.put(userId, session);

        getUserIdBySession.put(session, userId);
        getPartnerUserIdBySession.put(session, partnerUserId);

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
        // 과거에 나눴던 메세지들 전송.
        for(Message message : messages) {
            // 텍스트 메세지인 경우
            if(message.getMessageType() == MessageType.TEXT) {
                MessageResponse messageResponse = MessageResponse.builder()
                        .messageType(MessageType.TEXT)
                        .userId(message.getUserId())
                        .content(message.getContent())
                        .sendAt(message.getSendAt())
                        .build();

                String jsonFormatMessage = objectMapper.writeValueAsString(messageResponse);
                System.out.println("jsonFormatMessage = " + jsonFormatMessage);
//                session.sendMessage(new TextMessage(jsonFormatMessage));
            }



            // 이미지 파일인 경우
            if(message.getMessageType() == MessageType.IMAGE) {
                S3Object s3Object = amazonS3.getObject(bucketName, message.getImageUrl());
                S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

                MessageResponse messageResponse = MessageResponse.builder()
                        .messageType(MessageType.IMAGE)
                        .userId(message.getUserId())
                        .sendAt(message.getSendAt())
                        .imageFileData(s3ObjectInputStream.readAllBytes())
                        .build();

                String jsonFormatMessage = objectMapper.writeValueAsString(messageResponse);
//                session.sendMessage(new TextMessage(jsonFormatMessage));
            }

            // 위치 정보인 경우
            if(message.getMessageType() == MessageType.LOCATION) {

            }


        }









    }

    /*
     * 1. 상대 유저 B가 이미 연결되어 있는 경우 ( == B의 session이 set에 들어있는 경우 ) -> 바로 메세지 보내고, message 테이블에 insert
     * 2. 연결되어 있지 않은 경우 -> message 테이블에만 insert
     *
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = getUserIdBySession.get(session);
        Long partnerUserId = getPartnerUserIdBySession.get(session);
        Long chatRoomId = getChatRoomIdBySession.get(session);
        LocalDateTime currentTime = LocalDateTime.now();

        WebSocketSession partnerUserSession = getSessionByUserId.get(partnerUserId);

        log.info("textMessage = {}", message);

        // 상대 유저 B가 연결되어 있는 경우 메세지 전송.
        if(partnerUserSession != null) {
//            partnerUserSession.sendMessage(message);
            MessageResponse messageResponse = MessageResponse.builder()
                    .messageType(MessageType.TEXT)
                    .userId(userId)
                    .content(message.getPayload())
                    .sendAt(currentTime)
                    .build();

            String jsonFormatMessage = objectMapper.writeValueAsString(messageResponse);
            partnerUserSession.sendMessage(new TextMessage(jsonFormatMessage));
        }

        // message 테이블에 insert
        Message messageEntity = Message.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .messageType(MessageType.TEXT)
                .content(message.getPayload())
                .sendAt(LocalDateTime.now())
                .build();

        messageRepository.save(messageEntity);



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

        Long userId = getUserIdBySession.get(session);
        Long partnerUserId = getPartnerUserIdBySession.get(session);

        WebSocketSession partnerUserSession = getSessionByUserId.get(partnerUserId);

        // 상대 유저 B가 연결되어 있는 경우 메세지 전송.
        if(partnerUserSession != null) {
            MessageResponse messageResponse = MessageResponse.builder()
                    .messageType(MessageType.IMAGE)
                    .userId(userId)
                    .sendAt(LocalDateTime.now())
                    .imageFileData(bytes)
                    .build();

            // json 으로 파싱 후 전송
            try {
                String jsonFormatMessage = objectMapper.writeValueAsString(messageResponse);
                partnerUserSession.sendMessage(new TextMessage(jsonFormatMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        /*
         * 파일이름 생성방법
         * 1. UUID.randomUUID()
         * 2. System.currentTimeMillis()
         */
        String fileName = UUID.randomUUID().toString() + ".png"; // 임의 확장자. -> 변경 해야함. ( jpg, png ... )

        // S3 버킷에 이미지 저장
        amazonS3.putObject(bucketName, fileName, new ByteArrayInputStream(bytes), null);

        // db에 Message Entity 로 포맷 후 저장
        Message messageEntity = Message.builder()
                .chatRoomId(getChatRoomIdBySession.get(session))
                .userId(getUserIdBySession.get(session))
                .messageType(MessageType.IMAGE)
                .sendAt(LocalDateTime.now())
                .imageUrl(fileName)
                .build();

        messageRepository.save(messageEntity);


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);
        log.info("close reason : {} ", status.getReason());

        Long userId = getUserIdBySession.get(session);
        getUserIdBySession.remove(session);
        getSessionByUserId.remove(userId);
        getChatRoomIdBySession.remove(session);
        getPartnerUserIdBySession.remove(session);

        log.info("---- socket closed ----");
    }

}
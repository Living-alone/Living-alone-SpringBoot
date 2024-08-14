package com.livingalone.springboot.chat.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        HttpHeaders httpHeaders = request.getHeaders();
//        List<String> list = httpHeaders.getValuesAsList("sec-websocket-protocol");
//        System.out.println("get sec-websocket-protocol: " + (!list.isEmpty() ? list.get(0) : null));

        request.getURI().getQuery();

        // 이부분을 토큰 검증 로직으로 대체.
//        if(!list.contains("testToken")) return false;
//        return true;
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}

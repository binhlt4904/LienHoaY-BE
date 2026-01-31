package com.he180773.testreact.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery(); // userId=5

        String finalUserId = "anonymous";
        if (query != null && query.startsWith("userId=")) {
            finalUserId = query.split("=")[1];
        }
        String userIdForPrincipal = finalUserId; // biến này không bị thay đổi
        return () -> userIdForPrincipal;
    }

}

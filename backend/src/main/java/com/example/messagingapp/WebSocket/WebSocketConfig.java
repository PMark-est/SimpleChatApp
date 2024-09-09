package com.example.messagingapp.WebSocket;

import com.example.messagingapp.Services.GroupService;
import com.example.messagingapp.Services.MessageService;
import com.example.messagingapp.Services.UsersService;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig  implements WebSocketConfigurer {

    @Autowired
    private UsersService usersService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private GroupService groupService;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/myHandler")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler(){
        return new MyHandler(usersService, messageService, groupService);
    }
}
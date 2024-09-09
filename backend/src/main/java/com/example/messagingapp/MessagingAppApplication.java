package com.example.messagingapp;

import com.example.messagingapp.WebSocket.Messages.WSMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class MessagingAppApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(MessagingAppApplication.class, args);
    }
}

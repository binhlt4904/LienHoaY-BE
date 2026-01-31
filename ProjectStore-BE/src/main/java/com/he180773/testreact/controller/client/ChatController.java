package com.he180773.testreact.controller.client;

import com.he180773.testreact.dto.ChatMessage;
import com.he180773.testreact.entity.Message;
import com.he180773.testreact.repository.MessageRepository;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message) {
        System.out.println("hihi");
        // Lưu vào DB
        System.out.println("senderId: " + message.getSenderId());
        System.out.println("receiverId: " + message.getReceiverId());
        System.out.println("content: " + message.getContent());
        System.out.println("chatroomId: " + message.getChatRoomId());
        Message entity = new Message();
        entity.setSenderId(message.getSenderId());
        entity.setReceiverId(message.getReceiverId());
        entity.setContent(message.getContent());
        entity.setChatRoomId(message.getChatRoomId());
        entity.setSentAt(LocalDateTime.now());
        entity.setStatus("UNREAD");
        Message saved= messageRepository.save(entity);

        message.setSentAt(LocalDateTime.now());
        System.out.println("Status: " + saved.getStatus());


        // Gửi về tất cả admin đang theo dõi (nếu có)
        messagingTemplate.convertAndSend("/topic/admin-messages", saved);

        // Sau khi lưu message

        List<Message> messages= messageRepository.findAllByReceiverIdAndStatus("admin","UNREAD");
        List<Message> unreadMessage = new ArrayList<>();
        for(Message m : messages){
            System.out.println("MessageChatRoom  : " + m.getChatRoomId());
            if(unreadMessage.size()==0){
                unreadMessage.add(m);
            }
            else{

                for(Message m2 : unreadMessage){
                    if(!m2.getSenderId().equals(m.getSenderId())){
                        unreadMessage.add(m2);
                    }
                }
            }
        }
        Integer countUnread = messageRepository.countByReceiverIdAndStatus(message.getReceiverId(),"UNREAD");
        System.out.println("unreadMessage: " + unreadMessage);
// Gửi số lượng tin chưa đọc lên topic riêng
        messagingTemplate.convertAndSend("/topic/unread-notify", unreadMessage);

        System.out.println("countUnread: " + countUnread);
        System.out.println("receiverId: " + message.getReceiverId());
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiverId()),
                "/queue/unread-notify",
                countUnread);



// Gửi về cho người gửi (nếu cần echo lại)
        System.out.println("sent message"+ String.valueOf(message.getReceiverId()));
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()),
                "/queue/messages",
                saved
        );
    }





}


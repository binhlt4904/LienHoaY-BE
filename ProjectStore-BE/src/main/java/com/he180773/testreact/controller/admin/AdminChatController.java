package com.he180773.testreact.controller.admin;

import com.he180773.testreact.dto.ChatMessage;
import com.he180773.testreact.dto.ChatUserDTO;
import com.he180773.testreact.entity.Message;
import com.he180773.testreact.entity.User;
import com.he180773.testreact.repository.MessageRepository;
import com.he180773.testreact.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/chat")
public class AdminChatController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private SimpMessagingTemplate messagingTemplate;

    public AdminChatController(MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }
    @GetMapping("/chat-users")
    public List<ChatUserDTO> getAllUsersChattedWithAdmin() {
        List<Long> userIds= messageRepository.findDistinctChatUsers("admin");
        List<ChatUserDTO> result = new ArrayList<>();

        for (Long userId : userIds) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) continue;

            User user = userOpt.get();
            ChatUserDTO dto = new ChatUserDTO();
            dto.setUserId(user.getId().toString());
            dto.setUserName(user.getName());
            dto.setAvatar(user.getAvatar());

            List<Message> lastMsgList = messageRepository.findLastMessageWithAdmin(userId.toString(), PageRequest.of(0, 1));
            if (!lastMsgList.isEmpty()) {
                Message lastMsg = lastMsgList.get(0);
                dto.setLastMessage(lastMsg.getContent());
                dto.setLastSentAt(lastMsg.getSentAt());
            }

            result.add(dto);
        }
        result.sort((a, b) -> {
            if (a.getLastSentAt() == null && b.getLastSentAt() == null) return 0;
            if (a.getLastSentAt() == null) return 1;
            if (b.getLastSentAt() == null) return -1;
            return b.getLastSentAt().compareTo(a.getLastSentAt());
        });

        return result;
    }

    @GetMapping("/all-users")
    public List<ChatUserDTO> getAllUsersChatted() {
        List<User> users= userRepository.findAll();
        List<ChatUserDTO> result = new ArrayList<>();
        for (User user : users) {
            ChatUserDTO dto = new ChatUserDTO();
            dto.setUserId(user.getId().toString());
            dto.setUserName(user.getName());
            dto.setAvatar(user.getAvatar());
            result.add(dto);
        }
        return result;
    }

    @GetMapping("/search-users")
    public List<ChatUserDTO> searchUsers(@RequestParam("keyword") String keyword) {
        List<User> users= userRepository.searchAllByNameContains(keyword);

        List<ChatUserDTO> result = new ArrayList<>();
        for (User user : users) {
            ChatUserDTO dto = new ChatUserDTO();
            dto.setUserId(user.getId().toString());
            dto.setUserName(user.getName());
            dto.setAvatar(user.getAvatar());
            result.add(dto);
        }
        return result;
    }


    @GetMapping("/adminMessages/{userId}")
    public List<Message> getMessagesWithUser(@PathVariable String userId) {
        String chatroomId = generateChatroomId(userId, "admin");
        System.out.println("chatroomId: "+chatroomId);
        List<Message> ms = messageRepository.findByChatRoomIdOrderBySentAtAsc(chatroomId);
        for (Message m : ms) {
            System.out.println("Status: "+"id "+m.getStatus());
        }
        return ms;
    }

    private String generateChatroomId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "-" + userId2 : userId2 + "-" + userId1;
    }

    @GetMapping("/clientMessages/{userId}")
    public List<Message> getClientMessages(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return messageRepository.findBySenderIdOrReceiverId(userId, userId,pageable).getContent();
    }

    @PostMapping("/markAsRead")
    public ResponseEntity<Void> markAsRead(@RequestBody Map<String, Object> payload) {
        Long messageId = Long.parseLong(payload.get("messageId").toString());
        String currentUserId = payload.get("userId").toString();
        System.out.println(messageId+" meeee");

        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isEmpty()) return ResponseEntity.notFound().build();

        Message message = optionalMessage.get();
        String chatRoomId = message.getChatRoomId();
        String senderId = message.getSenderId();
        String receiverId = message.getReceiverId();
        List<Message> ms= messageRepository.findAll();
        for(Message m : ms){
            System.out.println("Status: "+"id "+m.getId()+m.getStatus());
            System.out.println("Sent_at: "+m.getSentAt()+" "+message.getSentAt());
        }

        // Tìm tất cả tin nhắn chưa đọc trong cùng chatRoom và gửi trước hoặc bằng thời gian hiện tại
        List<Message> unreadMessages = messageRepository.findUnreadMessagesBefore(
                chatRoomId,
                receiverId
        );

        System.out.println(unreadMessages+" unread");

        for (Message m : unreadMessages) {
            m.setStatus("READ");
        }

        List<Message> updated = messageRepository.saveAll(unreadMessages);

        // Gửi thông báo chỉ với tin nhắn cuối cùng được đọc (cho avatar hiện đúng vị trí)
        System.out.println("sender ne: "+ senderId);
        System.out.println(updated+" hihihihii");
        if (!updated.isEmpty()) {
            Message lastRead = updated.get(updated.size() - 1);

            if ("admin".equals(senderId)) {
                messagingTemplate.convertAndSend(
                        "/topic/message-read/" + receiverId,
                        lastRead
                );
            } else {
                System.out.println("senderId hehehee: "+senderId);


                messagingTemplate.convertAndSendToUser(
                        senderId,
                        "/queue/message-read",
                        lastRead
                );
            }
        }

        return ResponseEntity.ok().build();
    }



    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<?> countUnreadMessages(@PathVariable String userId) {
        List<Message> messages= new ArrayList<>();
        if(!userId.equals("admin")){
            messages= messageRepository.findAllByReceiverIdAndStatus(userId.toString(),"UNREAD");

        } else{
            messages= messageRepository.findAllByReceiverIdAndStatus("admin","UNREAD");
        }
        System.out.println("Messages: " + messages.size() + " for user: " + userId);

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
        System.out.println(unreadMessage+" unread");
        Integer countUnread = messageRepository.countByReceiverIdAndStatus(userId,"UNREAD");

        if(!userId.equals("admin")){
            return ResponseEntity.ok().body(countUnread);
        } else{
            return ResponseEntity.ok().body(unreadMessage);
        }
    }


    @PostMapping("/welcome")
    public ResponseEntity<?> sendWelcomeMessage(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");

        if (userId == null) return ResponseEntity.badRequest().body("UserId is required");

        Message welcomeMessage = new Message();
        welcomeMessage.setSenderId("admin");
        welcomeMessage.setReceiverId(userId);
        welcomeMessage.setContent("👋 Xin chào! Shop có thể hỗ trợ gì cho bạn?");
        welcomeMessage.setChatRoomId(userId + "-admin");
        welcomeMessage.setSentAt(LocalDateTime.now());
        welcomeMessage.setStatus("UNREAD");

        // Lưu DB và gửi qua WebSocket
        Message saved= messageRepository.save(welcomeMessage);

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
// Gửi số lượng tin chưa đọc lên topic riêng
        messagingTemplate.convertAndSend("/topic/unread-notify", unreadMessage);
        System.out.println("sent message"+ String.valueOf(welcomeMessage.getReceiverId()));
        messagingTemplate.convertAndSendToUser(
                String.valueOf(welcomeMessage.getReceiverId()),
                "/queue/messages",
                saved
        );

        return ResponseEntity.ok("Sent");
    }




// Gửi về cho người gửi (nếu cần echo lại)




}

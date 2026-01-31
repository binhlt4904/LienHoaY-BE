package com.he180773.testreact.repository;

import com.he180773.testreact.dto.ChatUserDTO;
import com.he180773.testreact.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT DISTINCT m.senderId FROM Message m WHERE m.receiverId = :adminId " +
            "UNION SELECT DISTINCT m.receiverId FROM Message m WHERE m.senderId = :adminId")
    List<Long> findDistinctChatUsers(@Param("adminId") String adminId);

    List<Message> findByChatRoomIdOrderBySentAtAsc(String chatroomId);

    Page<Message> findBySenderIdOrReceiverId(String senderId, String receiverId, Pageable pageable);
    @Query("""
    SELECT m FROM Message m 
    WHERE 
        (m.senderId = :userId ) OR 
        (m.receiverId = :userId )
    ORDER BY m.sentAt DESC
""")
    List<Message> findLastMessageWithAdmin(@Param("userId") String userId, Pageable pageable);

    Integer countByReceiverIdAndStatus(String receiverId, String status);

    @Query("SELECT m FROM Message m " +
            "WHERE m.chatRoomId = :chatRoomId " +
            "AND m.receiverId = :receiverId " +
            "AND m.status <> 'READ' " )
    List<Message> findUnreadMessagesBefore(
            @Param("chatRoomId") String chatRoomId,
            @Param("receiverId") String receiverId
    );

    List<Message> findAllByReceiverIdAndStatus( String receiver,String status);





}


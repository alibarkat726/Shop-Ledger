package com.App.Shop_Ledger.WebSocket.Services;
import com.App.Shop_Ledger.WebSocket.Model.ChatRoom;
import com.App.Shop_Ledger.WebSocket.Repository.ChatRoomRepo;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
@Autowired
    ChatRoomRepo chatRoomRepo;

ChatRoom chatRoom = new ChatRoom();

    public Optional<String> getRoomId(String senderId, String recipientId, boolean createRoomIfNotExists){
return chatRoomRepo.findBySenderIdAndRecipientId(senderId,recipientId)
        .map(ChatRoom ::getChatId)
        .or(
        () -> {
            if (createRoomIfNotExists){
               var chatId = createChatId(senderId,recipientId);
               return Optional.of(chatId);

            }
            return Optional.empty();
        }
);
    }

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s",senderId,recipientId);//senderId_recipientId

        ChatRoom senderRecipient =ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender =ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoomRepo.save(senderRecipient);
        chatRoomRepo.save(recipientSender);
        return chatId;
    }
}

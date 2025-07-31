package com.App.Shop_Ledger.WebSocket.Services;

import com.App.Shop_Ledger.WebSocket.Model.ChatMessage;
import com.App.Shop_Ledger.WebSocket.Repository.ChatMessageRepo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    @Autowired
    ChatMessageRepo messageRepo;
    @Autowired
    ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage){
        var chatId = chatRoomService
                .getRoomId(chatMessage
                        .getSenderId(),
                        chatMessage
                                .getRecipientId(), true)
                .orElseThrow();
        chatMessage.setChatId(chatId);
        messageRepo.save(chatMessage);
        return chatMessage;

    }
    public List<ChatMessage> findChatMessages(String senderId, String recipientId){
        var chatId= chatRoomService.getRoomId(senderId,recipientId,false);
        return chatId.map(messageRepo::findByChatId).orElse(new ArrayList<>());
    }
}

package com.App.Shop_Ledger.WebSocket.Controller;
import com.App.Shop_Ledger.WebSocket.Model.ChatMessage;
import com.App.Shop_Ledger.WebSocket.Services.ChatMessageService;
import com.App.Shop_Ledger.WebSocket.Services.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ChatMessageController {

    @Autowired
    ChatRoomService chatRoomService;

    @Autowired
    ChatMessageService messageService;

    @GetMapping("/message/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>>  getChatMessages(
            @PathVariable("senderId") String senderId,
            @PathVariable("recipientId") String recipientId
    )
    {
        return ResponseEntity.ok(messageService.findChatMessages(senderId,recipientId));
    }
}

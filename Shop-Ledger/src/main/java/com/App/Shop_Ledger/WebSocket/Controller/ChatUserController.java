package com.App.Shop_Ledger.WebSocket.Controller;
import com.App.Shop_Ledger.WebSocket.Services.ChatUserService;
import com.App.Shop_Ledger.WebSocket.Model.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ChatUserController {
    @Autowired
    ChatUserService userService;
    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    public ChatUser addUser(@Payload ChatUser user){
        userService.addUser(user);
        return user;
    }
    @MessageMapping("user.disconnect")
    @SendTo("/user/topic")
    public ChatUser disconnect(@Payload ChatUser user){
        userService.disconnectUser(user);
        return user;
    }
    public ResponseEntity<List<ChatUser>> connectedUsers(){
        return userService.getConnectedUsers();


    }
}
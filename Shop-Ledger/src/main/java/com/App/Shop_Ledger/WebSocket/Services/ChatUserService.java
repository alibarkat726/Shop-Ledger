package com.App.Shop_Ledger.WebSocket.Services;
import com.App.Shop_Ledger.WebSocket.Model.ChatUser;
import com.App.Shop_Ledger.WebSocket.Repository.ChatUserRepo;
import com.App.Shop_Ledger.WebSocket.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatUserService {

    @Autowired
    ChatUserRepo chatUserRepo;

    public void disconnectUser(ChatUser users){
        ChatUser users1 = (ChatUser) chatUserRepo.findByNickName(users.getNickName());
        if ( users1 != null){
            users1.setStatus(Status.OFFLINE);
            chatUserRepo.save(users1);
        }
    }
    public ResponseEntity<List<ChatUser>> getConnectedUsers(){
        List<ChatUser> user = chatUserRepo.findAllByStatus(Status.ONLINE);
        if (!user.isEmpty()){
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
    public ChatUser addUser(ChatUser user) {
        user.setStatus(Status.ONLINE);
        return chatUserRepo.save(user);
    }
}

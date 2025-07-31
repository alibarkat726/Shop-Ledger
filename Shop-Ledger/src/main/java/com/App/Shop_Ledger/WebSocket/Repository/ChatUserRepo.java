package com.App.Shop_Ledger.WebSocket.Repository;

import com.App.Shop_Ledger.User.Users;
import com.App.Shop_Ledger.WebSocket.Model.ChatUser;
import com.App.Shop_Ledger.WebSocket.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ChatUserRepo extends MongoRepository<ChatUser, String> {
    List<ChatUser> findAllByStatus(Status status);

    Object findByNickName(String nickName);

}

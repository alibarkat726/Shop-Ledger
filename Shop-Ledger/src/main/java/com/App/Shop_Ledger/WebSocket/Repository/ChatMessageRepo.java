package com.App.Shop_Ledger.WebSocket.Repository;
import com.App.Shop_Ledger.WebSocket.Model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ChatMessageRepo extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String s);

}




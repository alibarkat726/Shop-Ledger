package com.App.Shop_Ledger.WebSocket.Model;
import com.App.Shop_Ledger.WebSocket.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class ChatUser {
    @Id
    private String nickName;
    private String fullName;
    private Status status;
}
package com.App.Shop_Ledger.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "invite_tokens")
@Data
public class InviteToken {

    @Id
    private String id;

    private String userId;
    private String token;
    private Date expiresAt;
}

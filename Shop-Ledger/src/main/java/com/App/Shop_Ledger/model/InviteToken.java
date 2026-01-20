package com.App.Shop_Ledger.model;

import com.App.Shop_Ledger.Dto.Roles;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.management.relation.Role;
import java.util.Date;

@Document(collection = "invite_tokens")
@Data
public class InviteToken {

    @Id
    private String id;

    private String email;
    private String token;
    private Roles role;
    private String tenantId;
    private Date expiresAt;
    private boolean used;
}

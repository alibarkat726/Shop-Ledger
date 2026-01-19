package com.App.Shop_Ledger.User;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@Data
public class Users {

    @Id
    private String id;

    private String tenantId;
    private String username; // email
    private String password;

    private String role;
    private List<String> permissions;

    private String status;
}



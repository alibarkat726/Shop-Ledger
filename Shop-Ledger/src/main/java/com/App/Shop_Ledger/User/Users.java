package com.App.Shop_Ledger.User;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@AllArgsConstructor
@Document(collection = "user")

public class Users {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String role;


}

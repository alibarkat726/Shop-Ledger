package com.App.Shop_Ledger.User;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tenants")
@Data
public class Tenant {

    @Id
    private String id;
    private String name;
    private String plan;
    private String status;
}


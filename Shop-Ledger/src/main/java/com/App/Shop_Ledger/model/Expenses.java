package com.App.Shop_Ledger.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "Expenses")
public class Expenses {
    @Id
    private String id;
    private double amount;
    private String purchasedItem;
    private String description;
    @CreatedDate
    LocalDateTime createdDate;

    public Expenses(double amount, String purchasedItem, String description) {
        this.amount = amount;
        this.purchasedItem = purchasedItem;
        this.description = description;
    }
}




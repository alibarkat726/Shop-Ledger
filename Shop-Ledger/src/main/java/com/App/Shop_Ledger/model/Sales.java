package com.App.Shop_Ledger.model;


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
@Document(collection = "Sales")
public class Sales {
    @Id
    private String id;
    private double totalSalesByCard;
    private double totalAmountTransferredToAccount;
    private double totalSalesByCash;
    private double grossSale;
    private double totalExpense;
    private double discounts;
}

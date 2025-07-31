package com.App.Shop_Ledger.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

@Document(collection = "Receipts")
public class Receipt {


    public Receipt(double totalAmount, List<ReceiptProduct> products,double discountAmount,String customer,Charge charge) {
        this.totalAmount = totalAmount;
        this.products = products;
        this.discountAmount =discountAmount;
        this.customer = customer;
        this.charge =charge;
    }

    @Id
    private String id;
    private String customer;
    private double totalAmount;
    private List<ReceiptProduct> products;
    private double discountAmount;
    private Charge charge;
    @CreatedDate
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

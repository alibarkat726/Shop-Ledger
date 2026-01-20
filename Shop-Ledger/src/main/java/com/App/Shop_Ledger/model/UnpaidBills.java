package com.App.Shop_Ledger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "UnpaidBills")
public class UnpaidBills {
    @Id
    private String id;
    private String tenantId;
    private String customer;
    private double totalAmount;
    private List<ReceiptProduct> products;
    private double discountAmount;
    @CreatedDate
    private LocalDateTime createdDate;

    public UnpaidBills(double totalAmount, List<ReceiptProduct> receiptProducts, double discountAmount, String customer) {

        this.totalAmount = totalAmount;
        this.products = receiptProducts;
        this.discountAmount = discountAmount;
        this.customer = customer;
    }
}

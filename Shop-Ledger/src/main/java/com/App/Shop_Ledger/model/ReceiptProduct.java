package com.App.Shop_Ledger.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReceiptProduct {
private String name;
private Long price;
private int quantity;
private double discount;
}

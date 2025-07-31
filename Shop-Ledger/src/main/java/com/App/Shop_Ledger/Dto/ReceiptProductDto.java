package com.App.Shop_Ledger.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptProductDto {
    private String id;
    private int quantity;
    private double discount;
}

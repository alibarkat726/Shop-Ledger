package com.App.Shop_Ledger.Dto;

import lombok.Data;

@Data
public class PaidOutDto {
    private double amount;
    private String purchasedItem;
    private String description;
}

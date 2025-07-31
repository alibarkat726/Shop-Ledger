package com.App.Shop_Ledger.Dto;

import com.App.Shop_Ledger.model.Receipt;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class FilterSalesDto {

    private List<Receipt> receipts;

    private double grossSale;
    private double discounts;
    private double totalAmountTransferredToAccount;
    private double totalSalesByCash;
    private double totalSalesByCard;
    private double receiptsCount;

    public FilterSalesDto(List<Receipt> receipts, double totalAmount, double totalCount) {
        this.receipts = receipts;
        this.grossSale = totalAmount;
        this.receiptsCount = totalCount;

    }
}

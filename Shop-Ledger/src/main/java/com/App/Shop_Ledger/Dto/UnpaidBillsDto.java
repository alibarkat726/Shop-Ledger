package com.App.Shop_Ledger.Dto;

import com.App.Shop_Ledger.model.UnpaidBills;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UnpaidBillsDto {
    private List<UnpaidBills> unpaidBills;
    private String customer;
    private double totalCount;
    private double totalAmount;

    public UnpaidBillsDto(List<UnpaidBills> unpaidBills, String customer, double totalCount,double totalAmountPending) {
        this.unpaidBills = unpaidBills;
        this.customer = customer;
        this.totalAmount = totalAmountPending;
        this.totalCount = totalCount;

    }
}

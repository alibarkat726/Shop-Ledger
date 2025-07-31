package com.App.Shop_Ledger.Dto;

import com.App.Shop_Ledger.model.Charge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDto {
    private List<ReceiptProductDto> productIds;
    private String customer;
    private Charge charge;
}

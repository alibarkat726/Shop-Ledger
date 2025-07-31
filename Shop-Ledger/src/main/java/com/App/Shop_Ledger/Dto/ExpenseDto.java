package com.App.Shop_Ledger.Dto;

import com.App.Shop_Ledger.model.Expenses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private List<Expenses> expenses;
    private double totalCount;
    private double totalExpenses;
}

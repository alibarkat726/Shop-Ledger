package com.App.Shop_Ledger.Dto;

import lombok.Data;


@Data
public class InviteEmployeeRequest {
    private String email;
    private Roles role; // CASHIER, MANAGER
}


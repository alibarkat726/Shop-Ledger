package com.App.Shop_Ledger.Dto;

import lombok.Data;

@Data
public class InviteEmployeeRequest {
    private String email;
    private String role; // CASHIER, MANAGER
}


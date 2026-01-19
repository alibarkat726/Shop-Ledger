package com.App.Shop_Ledger.Dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String businessName;
    private String email;
    private String password;
}

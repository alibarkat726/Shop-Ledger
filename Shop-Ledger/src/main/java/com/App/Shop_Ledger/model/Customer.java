package com.App.Shop_Ledger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Customer {
    @Id
    String id;
    String name;
    String email;
    String num;
}

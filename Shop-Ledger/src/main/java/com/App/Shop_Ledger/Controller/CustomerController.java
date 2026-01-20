package com.App.Shop_Ledger.Controller;

import com.App.Shop_Ledger.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;


    @GetMapping("/get")
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('CREATE_RECEIPT')")
    public ResponseEntity<Object> getCustomers(){
        return customerService.getCustomer();
    }
}

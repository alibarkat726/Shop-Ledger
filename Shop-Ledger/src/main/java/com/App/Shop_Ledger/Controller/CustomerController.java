package com.App.Shop_Ledger.Controller;

import com.App.Shop_Ledger.Service.CustomerService;
import com.App.Shop_Ledger.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;


    @GetMapping("/get")
    public ResponseEntity<Object> getCustomers(){
        return customerService.getCustomer();
    }
}

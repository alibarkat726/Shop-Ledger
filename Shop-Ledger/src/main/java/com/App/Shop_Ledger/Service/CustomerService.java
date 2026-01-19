package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Repository.CustomerRepo;
import com.App.Shop_Ledger.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    CustomerRepo repo;

    public ResponseEntity<Object> getCustomer() {
        try {
            List<Customer> customer = repo.findAll();
            if (customer.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Customers found");
            }else return ResponseEntity.status(HttpStatus.OK).body(customer);
        }catch (Exception e){
            throw new RuntimeException("UnKnown error occured");
        }
    }
}

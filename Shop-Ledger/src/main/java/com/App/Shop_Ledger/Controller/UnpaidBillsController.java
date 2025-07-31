package com.App.Shop_Ledger.Controller;


import com.App.Shop_Ledger.Dto.ReceiptDto;
import com.App.Shop_Ledger.Dto.UnpaidBillsDto;
import com.App.Shop_Ledger.Service.UnpaidBillsService;
import com.App.Shop_Ledger.model.UnpaidBills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/Unpaid")
public class UnpaidBillsController {

    @Autowired
    UnpaidBillsService unpaidBillsService;
    @GetMapping("/get")
    public List<UnpaidBills> getBills(){
        return unpaidBillsService.getBills();
    }
    @PostMapping("/create")
    public ResponseEntity<?> unpaidBills(@RequestBody ReceiptDto request){
        return unpaidBillsService.UnpaidBills(request.getProductIds(),request.getCustomer());
    }
    @PutMapping("/markAsPaid/{id}")
    public ResponseEntity<Map<String, String>> markBillAsPaid(@PathVariable String id) {
        try {
            unpaidBillsService.markBillAsPaid(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bill marked as paid successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message" , e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @DeleteMapping("delete")

    public ResponseEntity<String> deleteById(@RequestParam String id){
       return unpaidBillsService.deleteById(id);
    }

    @GetMapping("/customer")
    public UnpaidBillsDto filterByCustomer(@RequestParam String customer){
        return unpaidBillsService.filterByCustomer(customer);
    }
}





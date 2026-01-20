package com.App.Shop_Ledger.Controller;


import com.App.Shop_Ledger.Dto.ReceiptDto;
import com.App.Shop_Ledger.Dto.UnpaidBillsDto;
import com.App.Shop_Ledger.Service.UnpaidBillsService;
import com.App.Shop_Ledger.model.UnpaidBills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('CREATE_RECEIPT')")
    public List<UnpaidBills> getBills(){
        return unpaidBillsService.getBills();
    } 
    
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_RECEIPT')")
    public ResponseEntity<?> unpaidBills(@RequestBody ReceiptDto request){
        return unpaidBillsService.UnpaidBills(request.getProductIds(),request.getCustomer());
    }
    
    @PutMapping("/markAsPaid/{id}")
    @PreAuthorize("hasAuthority('CREATE_RECEIPT')")
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
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')") // Restricted
    public ResponseEntity<String> deleteById(@RequestParam String id){
       return unpaidBillsService.deleteById(id);
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('CREATE_RECEIPT')")
    public UnpaidBillsDto filterByCustomer(@RequestParam String customer){
        return unpaidBillsService.filterByCustomer(customer);
    }
}

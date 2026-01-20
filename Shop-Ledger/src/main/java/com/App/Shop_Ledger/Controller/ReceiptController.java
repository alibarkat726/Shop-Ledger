package com.App.Shop_Ledger.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import com.App.Shop_Ledger.Dto.FilterSalesDto;
import com.App.Shop_Ledger.Dto.ReceiptDto;
import com.App.Shop_Ledger.Service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.App.Shop_Ledger.Service.ReceiptService;
import com.App.Shop_Ledger.model.Receipt;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/receipt")
@EnableMongoAuditing
public class ReceiptController {

    @Autowired
    ReceiptService receiptService;
    
    SalesService service;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CREATE_RECEIPT')")
    public ResponseEntity<?> createReceipt(@RequestBody ReceiptDto request){
        return receiptService.createReceipt(request.getProductIds(),request.getCustomer(),request.getCharge());
    }
    
    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public List<Receipt> getReceipt(){
        return receiptService.getReceipt();
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ResponseEntity<Map<String, Object>> getReceipt(@RequestParam String id) {
        try {
            Map<String, Object> receipt = receiptService.getReceiptById(id);
            return ResponseEntity.ok(receipt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/date")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public FilterSalesDto getReceiptByDate(@RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate){
        try {
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;

            if (startDate != null && !startDate.isEmpty()) {
                LocalDate parsedStartDate = LocalDate.parse(startDate);
                startDateTime = parsedStartDate.atStartOfDay();
            }

            if (endDate != null && !endDate.isEmpty()) {
                LocalDate parsedEndDate = LocalDate.parse(endDate);
                endDateTime = parsedEndDate.atTime(LocalTime.MAX);
            }
            return receiptService.filterReceiptByDate(startDateTime,endDateTime);
        }catch (DateTimeParseException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Invalid date format");
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('CREATE_RECEIPT')")
    public ResponseEntity<?> updateReceiptById(@RequestParam String id, @RequestBody Receipt receipt){
        try {
            ResponseEntity<?> receipt1 = receiptService.updateReceipt(id,receipt);
            return ResponseEntity.ok(receipt1);
        }catch (Exception e){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,"receipt not found");
        }

    }
    
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')") // Deleting receipts is sensitive, restricted to inventory managers or higher
    public ResponseEntity<String> deleteReceiptById(@RequestParam String id) {
        return receiptService.deleteReceiptById(id);
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public FilterSalesDto filterByCustomer(@RequestParam String customer){
        try {
            return receiptService.filterReceiptByCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

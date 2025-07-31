package com.App.Shop_Ledger.Controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import com.App.Shop_Ledger.Dto.FilterSalesDto;
import com.App.Shop_Ledger.Dto.ReceiptDto;
import com.App.Shop_Ledger.Service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createReceipt(@RequestBody ReceiptDto request){
       return receiptService.createReceipt(request.getProductIds(),request.getCustomer(),request.getCharge());
    }

    @GetMapping("/get/all")
    public List<Receipt>getReceipt(){
      return receiptService.getReceipt();
}

@GetMapping("/get")
public Receipt getReceiptById(@RequestParam String id){
        return receiptService.getReceiptById(id);
}


@GetMapping("/date")
    public FilterSalesDto getReceiptByDate(@RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate){
    try {
        // Initialize LocalDateTime boundaries as null
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // If a startDate is provided, parse and convert it to the start of that day.
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            startDateTime = parsedStartDate.atStartOfDay();
        }

        // If an endDate is provided, parse and convert it to the end of that day.
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate parsedEndDate = LocalDate.parse(endDate);
            endDateTime = parsedEndDate.atTime(LocalTime.MAX);
        }
//            LocalDateTime startOfDay = parsedDate.atStartOfDay();
//            LocalDateTime endOfDay = parsedDate.atTime(LocalTime.MAX);
            return receiptService.filterReceiptByDate(startDateTime,endDateTime);
        }catch (DateTimeParseException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Invalid date format");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Receipt> updateReceiptById(@RequestParam String id, @RequestBody Receipt receipt){
        try {
            Receipt receipt1 = receiptService.updateReceipt(id,receipt);
             return ResponseEntity.ok(receipt1);
        }catch (Exception e){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,"receipt not found");
        }

    }
@DeleteMapping("/delete")
    public ResponseEntity<String> deleteReceiptById(@RequestParam String id) {
  return receiptService.deleteReceiptById(id);
}

    @GetMapping("/customer")
    public FilterSalesDto filterByCustomer(@RequestParam String customer){
        try {
            return receiptService.filterReceiptByCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    }




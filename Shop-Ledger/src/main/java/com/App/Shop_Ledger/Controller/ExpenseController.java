package com.App.Shop_Ledger.Controller;

import com.App.Shop_Ledger.Dto.ExpenseDto;
import com.App.Shop_Ledger.Dto.PaidOutDto;
import com.App.Shop_Ledger.Service.ExpenseService;
import com.App.Shop_Ledger.model.Expenses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @PostMapping("/paidOut")
    @PreAuthorize("hasAuthority('CREATE_RECEIPT')")
    public ResponseEntity<Expenses> paidOut(@RequestBody PaidOutDto expense) {
        Expenses expenses = expenseService.paidOut(expense.getAmount(), expense.getPurchasedItem(), expense.getDescription());
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public List<Expenses> get() {
        return expenseService.getExpenses();
    }


    @GetMapping("/get")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public Object getExpensesById(@RequestParam(required = false) String id) {
        return expenseService.getExpenseById(id);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')") // Restricted
    public ResponseEntity<String> deleteExpenseByID(@RequestParam String id) {
        return expenseService.deleteExpenseById(id);
    }
    
    @GetMapping("/date")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ExpenseDto getReceiptByDate(@RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate) {
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
            return expenseService.filterReceiptByDate(startDateTime,endDateTime);
        }catch (DateTimeParseException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Invalid date format");
        }
    }

}

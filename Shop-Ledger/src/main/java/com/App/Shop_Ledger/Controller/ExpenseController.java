package com.App.Shop_Ledger.Controller;

import com.App.Shop_Ledger.Dto.ExpenseDto;
import com.App.Shop_Ledger.Service.ExpenseService;
import com.App.Shop_Ledger.model.Expenses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @PostMapping("/paidOut")
    public ResponseEntity<Expenses> paidOut(@RequestBody Expenses expense) {
        Expenses expenses = expenseService.paidOut(expense.getAmount(), expense.getPurchasedItem(), expense.getDescription());
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/all")
    public List<Expenses> get() {
        return expenseService.getExpenses();
    }


    @GetMapping("/get")
    public Object getExpensesById(@RequestParam(required = false) String id) {
        return expenseService.getExpenseById(id);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteExpenseByID(@RequestParam String id) {
        return expenseService.deleteExpenseById(id);
    }

    @GetMapping("/date")
    public ExpenseDto getReceiptByDate(@RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate) {
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
            return expenseService.filterReceiptByDate(startDateTime,endDateTime);
        }catch (DateTimeParseException e){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Invalid date format");
        }
    }

}

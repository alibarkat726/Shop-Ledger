package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Dto.ExpenseDto;
import com.App.Shop_Ledger.Repository.ExpenseRepository;
import com.App.Shop_Ledger.Repository.SalesRepository;
import com.App.Shop_Ledger.User.TenantContext;
import com.App.Shop_Ledger.model.Expenses;
import com.App.Shop_Ledger.model.Sales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    SalesRepository salesRepository;
    
    @Autowired
    SalesService salesService;

    public Expenses paidOut(double amount, String purchasedItem, String description) {
        if (amount <= 0) {
            throw new RuntimeException("amount should be above zero");
        }
        String tenantId = TenantContext.getTenantId();
        
        Expenses expenses = new Expenses(amount, purchasedItem, description);
        expenses.setTenantId(tenantId); // Set tenantId
        expenses.setCreatedDate(LocalDateTime.now());
        try {
            Sales sales = salesRepository.findByTenantId(tenantId).stream()
                    .findFirst()
                    .orElse(new Sales());
            
            if (sales.getId() == null) {
                sales.setTenantId(tenantId);
                sales.setRecordDate(LocalDateTime.now());
            }

            sales.setGrossSale(sales.getGrossSale() - amount);
            sales.setTotalExpense(sales.getTotalExpense() + amount);
            salesRepository.save(sales);
            
            return expenseRepository.save(expenses);
        } catch (Exception e) {
            throw new RuntimeException("Unable to pay out: " + e.getMessage());
        }
    }


    public List<Expenses> getExpenses() {
        String tenantId = TenantContext.getTenantId();
        return expenseRepository.findByTenantId(tenantId);
    }


    public Optional<Expenses> get(String id) {
        String tenantId = TenantContext.getTenantId();
        try {
            return expenseRepository.findByIdAndTenantId(id, tenantId);
        } catch (Exception e) {
            throw new RuntimeException("id not found");
        }
    }

    public Object getExpenseById(String id) {
        String tenantId = TenantContext.getTenantId();
        if (id == null || id.isEmpty()) {
            return expenseRepository.findByTenantId(tenantId); 
        }
        return expenseRepository.findByIdAndTenantId(id, tenantId).orElse(null);
    }

    public ResponseEntity<String> deleteExpenseById(String id) {
        String tenantId = TenantContext.getTenantId();
        try {
            if (expenseRepository.findByIdAndTenantId(id, tenantId).isPresent()) {
                expenseRepository.deleteByIdAndTenantId(id, tenantId);
                // Should ideally revert sales update too, but for simplicity leaving as is
                return ResponseEntity.ok("Deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Expense with this id not found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public ExpenseDto filterReceiptByDate(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        String tenantId = TenantContext.getTenantId();
        
        if (startOfDay == null && endOfDay != null) {
            startOfDay = endOfDay.with(LocalTime.MIN);
        }

        if (startOfDay != null && endOfDay == null) {
            endOfDay = startOfDay.with(LocalTime.MAX);
        }
        
        List<Expenses> expenses = expenseRepository.findByTenantIdAndCreatedDateBetween(tenantId, startOfDay, endOfDay);
        double receiptsCount = expenses.size();

        double totalExpenses = expenses.stream()
                .mapToDouble(Expenses::getAmount)
                .sum();
        return new ExpenseDto(expenses, receiptsCount, totalExpenses);
    }
}

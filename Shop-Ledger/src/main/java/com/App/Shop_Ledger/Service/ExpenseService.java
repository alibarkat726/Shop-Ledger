package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Dto.ExpenseDto;
import com.App.Shop_Ledger.Dto.FilterSalesDto;
import com.App.Shop_Ledger.Repository.ExpenseRepository;
import com.App.Shop_Ledger.Repository.SalesRepository;
import com.App.Shop_Ledger.model.Expenses;
import com.App.Shop_Ledger.model.Receipt;
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

    public Expenses paidOut(double amount, String purchasedItem, String description) {
        if (amount <= 0) {
            throw new RuntimeException("amount should be above zero");
        }
        Expenses expenses = new Expenses(amount, purchasedItem, description);
        try {
            List<Sales> allSaleRecord = salesRepository.findAll();
            Sales sales;
            if (allSaleRecord.isEmpty()) {
                sales = new Sales();
            } else {
                sales = allSaleRecord.get(allSaleRecord.size() - 1);
            }
//            for (int i = 0; i< allSaleRecord.size() - 1;i++){
//                salesRepository.delete(allSaleRecord.get(i));
//            }
            sales.setGrossSale(sales.getGrossSale() - amount);
            sales.setTotalExpense(sales.getTotalExpense() + amount);
            salesRepository.save(sales);
            return expenseRepository.save(expenses);
        } catch (Exception e) {
            throw new RuntimeException("Unable to paid out");
        }
    }


    public List<Expenses> getExpenses() {
        return expenseRepository.findAll();
    }


    public Optional<Expenses> get(String id) {
        try {
            return expenseRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("id not found");
        }
    }

    public Object getExpenseById(String id) {
        if (id == null || id.isEmpty()) {
            return expenseRepository.findAll(); // Return all expenses if id is not provided
        }
        return expenseRepository.findById(id).orElse(null); // Return expense if found, otherwise null
    }

    public ResponseEntity<String> deleteExpenseById(String id) {
        try {
            Optional<Expenses> expenses = expenseRepository.findById(id);
            if (expenses.isPresent()) {
                expenseRepository.deleteById(id);
                return ResponseEntity.ok("Deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Expense with this id doesn't found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public ExpenseDto filterReceiptByDate(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        if (startOfDay == null && endOfDay != null) {
            startOfDay = endOfDay.with(LocalTime.MIN);
        }

        if (startOfDay != null && endOfDay == null) {
            endOfDay = startOfDay.with(LocalTime.MAX);
        }
        List<Expenses> expenses =expenseRepository.findByCreatedDateBetween(startOfDay,endOfDay);
        double receiptsCount = expenses.stream().count();

        double totalExpenses = expenses.stream()
                .mapToDouble(Expenses::getAmount)
                .sum();
        return new ExpenseDto(expenses, receiptsCount, totalExpenses);
    }

    }



package com.App.Shop_Ledger.Repository;

import com.App.Shop_Ledger.model.Expenses;
import com.App.Shop_Ledger.model.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expenses, String> {




    List<Expenses> findByCreatedDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

}

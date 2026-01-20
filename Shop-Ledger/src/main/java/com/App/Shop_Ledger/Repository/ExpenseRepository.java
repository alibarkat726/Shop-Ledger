package com.App.Shop_Ledger.Repository;

import com.App.Shop_Ledger.model.Expenses;
import com.App.Shop_Ledger.model.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expenses, String> {

    List<Expenses> findByCreatedDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);


    List<Expenses> findByTenantId(String tenantId);
    Optional<Expenses> findByIdAndTenantId(String id, String tenantId);
    List<Expenses> findByTenantIdAndCreatedDateBetween(String tenantId, LocalDateTime start, LocalDateTime end);
    void deleteByIdAndTenantId(String id, String tenantId);
}

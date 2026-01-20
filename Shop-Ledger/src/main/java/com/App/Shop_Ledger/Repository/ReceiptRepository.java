package com.App.Shop_Ledger.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.App.Shop_Ledger.model.Receipt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ReceiptRepository extends MongoRepository<Receipt, String>{
    List<Receipt> findByCreateDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Receipt> findByCustomer(String customer);


    List<Receipt> findByTenantId(String tenantId);
    Optional<Receipt> findByIdAndTenantId(String id, String tenantId);
    List<Receipt> findByTenantIdAndCreateDateBetween(String tenantId, LocalDateTime start, LocalDateTime end);
    List<Receipt> findByTenantIdAndCustomer(String tenantId, String customer);
    void deleteByIdAndTenantId(String id, String tenantId);
}

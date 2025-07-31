package com.App.Shop_Ledger.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.App.Shop_Ledger.model.Receipt;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ReceiptRepository extends MongoRepository<Receipt, String>{
    List<Receipt> findByCreateDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Receipt> findByCustomer(String customer);

}

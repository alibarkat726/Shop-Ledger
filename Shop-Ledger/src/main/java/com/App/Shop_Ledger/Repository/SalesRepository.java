package com.App.Shop_Ledger.Repository;


import com.App.Shop_Ledger.model.Sales;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesRepository extends MongoRepository<Sales,String> {

    @Query("{ }")
    Optional<Sales> findFirst();

    List<Sales> findByTenantId(String tenantId);
    Optional<Sales> findByIdAndTenantId(String id, String tenantId);
}

package com.App.Shop_Ledger.Repository;

import com.App.Shop_Ledger.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends MongoRepository<Customer ,String> {


    List<Customer> findByTenantId(String tenantId);
    Optional<Customer> findByIdAndTenantId(String id, String tenantId);
    void deleteByIdAndTenantId(String id, String tenantId);
}

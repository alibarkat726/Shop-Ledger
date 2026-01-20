package com.App.Shop_Ledger.Repository;


import com.App.Shop_Ledger.model.UnpaidBills;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnpaidBillsRepository extends MongoRepository<UnpaidBills, String> {

    List<UnpaidBills> findByCustomer(String customer);


    List<UnpaidBills> findByTenantId(String tenantId);
    Optional<UnpaidBills> findByIdAndTenantId(String id, String tenantId);
    List<UnpaidBills> findByTenantIdAndCustomer(String tenantId, String customer);
    void deleteByIdAndTenantId(String id, String tenantId);
}

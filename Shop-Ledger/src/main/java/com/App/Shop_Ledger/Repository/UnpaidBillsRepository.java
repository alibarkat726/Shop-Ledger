package com.App.Shop_Ledger.Repository;


import com.App.Shop_Ledger.model.UnpaidBills;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnpaidBillsRepository extends MongoRepository<UnpaidBills, String> {

    List<UnpaidBills> findByCustomer(String customer);

}

package com.App.Shop_Ledger.Repository;

import com.App.Shop_Ledger.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends MongoRepository<Customer ,String> {

}

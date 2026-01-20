package com.App.Shop_Ledger.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepo extends MongoRepository<Tenant, String> {

    Optional<Tenant> findByName(String name);

    boolean existsByName(String name);
}


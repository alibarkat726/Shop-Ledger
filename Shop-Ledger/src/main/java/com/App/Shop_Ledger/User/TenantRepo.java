package com.App.Shop_Ledger.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepo extends MongoRepository<Tenant, String> {

    // Find tenant by business name (optional)
    Optional<Tenant> findByName(String name);

    // Check if tenant exists (useful for validations)
    boolean existsByName(String name);
}


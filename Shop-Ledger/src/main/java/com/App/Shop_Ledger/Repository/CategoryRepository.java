package com.App.Shop_Ledger.Repository;

import com.App.Shop_Ledger.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String>{
    Category findByName(String name);


    List<Category> findByTenantId(String tenantId);
    Category findByNameAndTenantId(String name, String tenantId);
    Optional<Category> findByIdAndTenantId(String id, String tenantId);
    void deleteByIdAndTenantId(String id, String tenantId);
}

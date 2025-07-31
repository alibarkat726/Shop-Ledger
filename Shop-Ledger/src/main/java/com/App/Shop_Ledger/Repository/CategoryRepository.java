package com.App.Shop_Ledger.Repository;

import com.App.Shop_Ledger.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String>{
    Category findByName(String name);
}

package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Repository.CategoryRepository;
import com.App.Shop_Ledger.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        try {
            return categoryRepository.save(category);

        }catch (Exception e){
            throw new RuntimeException("unable to save category");
        }

    }

    public ResponseEntity<Object> getCategory() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category List is empty");
        }else {
            return ResponseEntity.ok(categories);
        }
    }

    public Object getCategoryById(String id) {
        try {
            if (id == null || id.isEmpty()){
                return categoryRepository.findAll();
            }else {
                return categoryRepository.findById(id);
            }
        }catch (Exception e){
            throw new RuntimeException("Unable to find category with this request");
        }
    }

    public ResponseEntity<String> deleteCategoryById(String id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isPresent()){
                categoryRepository.deleteById(id);
                return ResponseEntity.ok("Category deleted Successfully");
            }else {
                return ResponseEntity.badRequest().body("Category with this id not found");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Repository.CategoryRepository;
import com.App.Shop_Ledger.Repository.productRepo;
import com.App.Shop_Ledger.model.Category;
import com.App.Shop_Ledger.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    productRepo productRepository;


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

    public ResponseEntity<Map<String, Object>> deleteCategoryById(String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(id);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                // Step 1: Find all products with this category
                List<Products> productsWithCategory = productRepository.findByCategory(category);
                // Step 2: Remove category reference from products
                if (productsWithCategory != null) {
                    for (Products product : productsWithCategory) {
                        product.setCategory(null); // remove reference
                    }
                    // Save all updated products
                    productRepository.saveAll(productsWithCategory);
                }
                // Step 3: Delete the category
                categoryRepository.deleteById(id);

                response.put("status", "success");
                response.put("message", "Category deleted successfully and references removed from products");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Category with this id not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (EmptyResultDataAccessException e) {
            response.put("status", "error");
            response.put("message", "Unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> updateCategory(String id, Category category) {
        Map<String, Object> response = new HashMap<>();
        try {
            Category updated = categoryRepository.findById(id).map(existingProduct -> {
                existingProduct.setDescription(category.getDescription());
                existingProduct.setName(category.getName());
                return categoryRepository.save(existingProduct);
            }).orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
            response.put("status", "success");
            response.put("message", "Category updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

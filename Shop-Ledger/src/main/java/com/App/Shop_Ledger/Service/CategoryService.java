package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Dto.CategoryDto;
import com.App.Shop_Ledger.Repository.CategoryRepository;
import com.App.Shop_Ledger.Repository.productRepo;
import com.App.Shop_Ledger.User.TenantContext;
import com.App.Shop_Ledger.model.Category;
import com.App.Shop_Ledger.model.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
    
    public Category createCategory(CategoryDto category) {
        try {
            String tenantId = TenantContext.getTenantId();
            Category category1 = new Category();
            category1.setName(category.getName());
            category1.setDescription(category.getDescription());
            category1.setTenantId(tenantId);
            return categoryRepository.save(category1);
        } catch (Exception e){
            throw new RuntimeException("unable to save category");
        }
    }
    
    public ResponseEntity<Object> getCategory() {
        String tenantId = TenantContext.getTenantId();
        List<Category> categories = categoryRepository.findByTenantId(tenantId);
        if (categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(categories); // Return empty list instead of 404 for better UX
        } else {
            return ResponseEntity.ok(categories);
        }
    }

    public Object getCategoryById(String id) {
        String tenantId = TenantContext.getTenantId();
        try {
            if (id == null || id.isEmpty()){
                return categoryRepository.findByTenantId(tenantId);
            } else {
                return categoryRepository.findByIdAndTenantId(id, tenantId);
            }
        } catch (Exception e){
            throw new RuntimeException("Unable to find category with this request");
        }
    }
    
    public ResponseEntity<Map<String, Object>> deleteCategoryById(String id) {
        Map<String, Object> response = new HashMap<>();
        String tenantId = TenantContext.getTenantId();
        
        try {
            Optional<Category> categoryOpt = categoryRepository.findByIdAndTenantId(id, tenantId);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                // Step 1: Find all products with this category for this tenant
                // Note: findByCategory might need tenant check if not implicitly safe, but productRepo logic should handle it?
                // Better to use a specific method if possible, but Category object is unique per tenant now.
                List<Products> productsWithCategory = productRepository.findByCategory(category);
                
                // Extra safety: filter products by tenantId (though category belongs to tenant, checking product tenant match is good)
                
                // Step 2: Remove category reference from products
                if (productsWithCategory != null) {
                    for (Products product : productsWithCategory) {
                        // Ensure we only modify products of this tenant
                        if (tenantId.equals(product.getTenantId())) {
                            product.setCategory(null); // remove reference
                        }
                    }
                    // Save all updated products
                    productRepository.saveAll(productsWithCategory);
                }
                
                // Step 3: Delete the category
                categoryRepository.deleteByIdAndTenantId(id, tenantId);
                
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
        String tenantId = TenantContext.getTenantId();
        
        try {
            Category updated = categoryRepository.findByIdAndTenantId(id, tenantId).map(existingCategory -> {
                existingCategory.setDescription(category.getDescription());
                existingCategory.setName(category.getName());
                return categoryRepository.save(existingCategory);
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

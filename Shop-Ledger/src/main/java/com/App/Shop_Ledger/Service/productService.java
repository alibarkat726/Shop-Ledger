package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Dto.ProductDto;
import com.App.Shop_Ledger.Repository.CategoryRepository;
import com.App.Shop_Ledger.Repository.productRepo;
import com.App.Shop_Ledger.User.UserRepo;
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
public class productService {

    @Autowired
    productRepo productRepo;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepo userRepo;

    public List<Products> getAllPrd(Products products) {
        return productRepo.findAll();
    }


    public ResponseEntity<Map<String, Object>> deleteProduct(String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (productRepo.findById(id).isPresent()) {
                productRepo.deleteById(id);
                response.put("status", "success");
                response.put("message", "Product deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Product with this id not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (EmptyResultDataAccessException e) {
            response.put("status", "error");
            response.put("message", "Unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    public ResponseEntity<Map<String, Object>> updateProduct(String id, Products updatedProduct) {
        Map<String, Object> response = new HashMap<>();
        try {
            Products updated = productRepo.findById(id).map(existingProduct -> {
                // Update existing product with new values
                existingProduct.setPrdName(updatedProduct.getPrdName());
                existingProduct.setCategory(updatedProduct.getCategory());
                existingProduct.setPrice(updatedProduct.getPrice());
                return productRepo.save(existingProduct);
            }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

            response.put("status", "success");
            response.put("message", "Product updated successfully");
            response.put("data", updated);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    public List<Products> getprd(String id) {

        Products product = productRepo.findById(id).orElse(null);

        if (product != null) {
            return List.of(product);
        } else {
            return productRepo.findAll();
        }

    }


    public List<Products> searchProduct(String keyword, boolean useFullText) {
        if (useFullText) {
            return productRepo.searchByFullText(keyword);
        }
        return productRepo.searchByPartialMatch(keyword);
    }

    public ResponseEntity<?> addPrd(ProductDto productDto) {
        Category category = categoryRepository.findByName(productDto.getCategory());
        System.out.println(category);

        if (category == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "This type of category not found in the category");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            Products products = new Products();
            products.setId(productDto.getId());
            products.setPrdName(productDto.getPrdName());
            products.setPrice(productDto.getPrice());
            products.setCategory(category);
            // products.setUserId(userId);
            productRepo.save(products);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Product added successfully");
            response.put("data", products);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }
    // Retrieve all products for a given user
//    public ResponseEntity<?> getProductsByUser(String userId) {
//        // Validate if the user exists
//        if (!userRepo.existsById(userId)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("User with id " + userId + " not found");
//        }
//        List<Products> products = productRepo.findByUserId(userId);
//        return ResponseEntity.ok(products);
//    }
}

  
      


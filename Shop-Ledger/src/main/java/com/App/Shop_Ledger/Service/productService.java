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

import java.util.List;
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

    public ResponseEntity<String> deleteProduct(String id) {
        try {
           if (productRepo.findById(id).isPresent()){
               productRepo.deleteById(id);
               return ResponseEntity.ok("product deleted successfully");
           }else {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with this id not found");
           }

        } catch (EmptyResultDataAccessException e) {
         throw new RuntimeException("not found");
        }
    }

    public Products updateProduct(String id, Products updatedProduct) {
        // Check if the product exists
        return productRepo.findById(id).map(existingProduct -> {
            // Update existing product with new values
            existingProduct.setPrdName(updatedProduct.getPrdName());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setPrice(updatedProduct.getPrice());

            // Save and return updated product
            return productRepo.save(existingProduct);
        }).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
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

    public ResponseEntity<?> addPrd(
//            String userId,

            ProductDto productDto) {
        Category category = categoryRepository.findByName(productDto.getCategory());
        System.out.println(category);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("this type of category not found in the category");
        } else {
            Products products = new Products();
            products.setId(productDto.getId());
            products.setPrdName(productDto.getPrdName());
            products.setPrice(productDto.getPrice());
            products.setCategory(category);
//            products.setUserId(userId);
            productRepo.save(products);
            return ResponseEntity.status(HttpStatus.CREATED).body(products);
        }
//        Category category = products.getCategory();
//        if (category == categoryRepository.findByName()) {
//            return productRepo.save(products);
//        }else {
//            throw new RuntimeException("unable to save product the category is not available");
//        }
    }
    // Retrieve all products for a given user
    public ResponseEntity<?> getProductsByUser(String userId) {
        // Validate if the user exists
        if (!userRepo.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with id " + userId + " not found");
        }
        List<Products> products = productRepo.findByUserId(userId);
        return ResponseEntity.ok(products);
    }
}

  
      


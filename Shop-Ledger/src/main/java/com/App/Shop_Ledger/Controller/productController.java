package com.App.Shop_Ledger.Controller;

import java.util.List;
import java.util.Map;

import com.App.Shop_Ledger.Dto.ProductDto;
import com.App.Shop_Ledger.User.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.App.Shop_Ledger.Service.productService;
import com.App.Shop_Ledger.model.Products;

@Slf4j
@RestController
@RequestMapping("/product")
public class productController {
    
    @Autowired
    productService productService;
    
    @Autowired
    UserRepo userRepo;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto){
        return productService.addPrd(productDto);
    }
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('MANAGE_INVENTORY') or hasAuthority('CREATE_RECEIPT')")
    public List<Products> getAllPrd(Products products){
        return productService.getAllPrd(products);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ResponseEntity<Map<String,Object>> deleteProductById(@RequestParam String id){
        return productService.deleteProduct(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ResponseEntity<Map<String,Object>> updateProduct(@RequestParam String id, @RequestBody Products products)  {
        return productService.updateProduct(id,products);
    }

    @GetMapping("/product")
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('MANAGE_INVENTORY') or hasAuthority('CREATE_RECEIPT')")
    public List<Products> getPrdById(@RequestParam String id){
        return productService.getprd(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('MANAGE_INVENTORY') or hasAuthority('CREATE_RECEIPT')")
    public ResponseEntity<List<Products>> searchProduct(@RequestParam String keyword,
                                                        @RequestParam(defaultValue = "false")boolean useFullText){
        return ResponseEntity.ok(productService.searchProduct(keyword,useFullText));
    }
}

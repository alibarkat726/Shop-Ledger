package com.App.Shop_Ledger.Controller;

import java.util.List;
import java.util.Optional;
import com.App.Shop_Ledger.Dto.ProductDto;
import com.App.Shop_Ledger.User.UserRepo;
import com.App.Shop_Ledger.User.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto){
//        Optional<Users> users = userRepo.findById(userId);
//        if (users.isEmpty()){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this id not found");
//        }else {
        ResponseEntity<?> products = productService.addPrd(productDto);
        return new ResponseEntity<>(products, HttpStatus.CREATED);
}
    @GetMapping("/id")
    public ResponseEntity<?> getProductsByUser(@RequestParam String userId) {
        return productService.getProductsByUser(userId);
    }

@GetMapping("/all")
public List<Products> getAllPrd(Products products){
        return productService.getAllPrd(products);
}


@DeleteMapping("/delete")
  public ResponseEntity<String> deleteProductById(@RequestParam String id){
        return productService.deleteProduct(id);
          }


@PutMapping("/update")
public void updateProduct(@RequestParam String id, @RequestBody Products products)  {
  productService.updateProduct(id,products);

}

@GetMapping("/product")
public List<Products> getPrdById(@RequestParam String id){
  
  return productService.getprd(id);
}

@GetMapping("/search")
    public ResponseEntity<List<Products>> searchProduct(@RequestParam String keyword,
                                                        @RequestParam(defaultValue = "false")boolean useFullText){

        return ResponseEntity.ok(productService.searchProduct(keyword,useFullText));
}

    }


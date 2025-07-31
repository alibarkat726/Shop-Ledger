package com.App.Shop_Ledger.Controller;


import com.App.Shop_Ledger.Service.CategoryService;
import com.App.Shop_Ledger.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<Category> creteCategory(@RequestBody Category category){
        Category category1 = categoryService.createCategory(category);
        return new ResponseEntity<>(category1 , HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public ResponseEntity<Object> category(){
        return categoryService.getCategory();
    }

    @GetMapping("/get")
    public Object getCategoryById(@RequestParam String id){
        return categoryService.getCategoryById(id);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteById(@RequestParam String id){
        return categoryService.deleteCategoryById(id);
    }
}

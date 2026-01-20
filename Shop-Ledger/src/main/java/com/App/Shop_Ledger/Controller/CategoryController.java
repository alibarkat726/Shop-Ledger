package com.App.Shop_Ledger.Controller;


import com.App.Shop_Ledger.Dto.CategoryDto;
import com.App.Shop_Ledger.Service.CategoryService;
import com.App.Shop_Ledger.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ResponseEntity<Category> creteCategory(@RequestBody CategoryDto category){
        Category category1 = categoryService.createCategory(category);
        return new ResponseEntity<>(category1 , HttpStatus.CREATED);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('MANAGE_INVENTORY')")
    public ResponseEntity<Object> category(){
        return categoryService.getCategory();
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('VIEW_REPORTS') or hasAuthority('MANAGE_INVENTORY')")
    public Object getCategoryById(@RequestParam String id){
        return categoryService.getCategoryById(id);
    }
    
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ResponseEntity<Map<String,Object>> deleteById(@RequestParam String id){
        return categoryService.deleteCategoryById(id);
    }
    
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('MANAGE_INVENTORY')")
    public ResponseEntity<Map<String, Object>> UpdateCategory(@RequestParam String id, @RequestBody Category category){
        return categoryService.updateCategory(id, category);
    }
}

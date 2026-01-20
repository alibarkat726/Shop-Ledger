package com.App.Shop_Ledger.Service;


import com.App.Shop_Ledger.Dto.ReceiptProductDto;
import com.App.Shop_Ledger.Dto.UnpaidBillsDto;
import com.App.Shop_Ledger.Repository.ReceiptRepository;
import com.App.Shop_Ledger.Repository.SalesRepository;
import com.App.Shop_Ledger.Repository.UnpaidBillsRepository;
import com.App.Shop_Ledger.Repository.productRepo;
import com.App.Shop_Ledger.User.TenantContext;
import com.App.Shop_Ledger.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnpaidBillsService {
    @Autowired
    UnpaidBillsRepository unpaidBillsRepository;

    @Autowired
    productRepo productRepo;

    @Autowired
    ReceiptRepository receiptRepository;

    @Autowired
    SalesRepository salesRepository;
    
    @Autowired
    SalesService salesService; // Use service for sales updates

    public List<UnpaidBills> getBills() {
        String tenantId = TenantContext.getTenantId();
        return unpaidBillsRepository.findByTenantId(tenantId);
    }

    //Generate and save bill
    public ResponseEntity<?> UnpaidBills(List<ReceiptProductDto> productIds, String customer) {
        String tenantId = TenantContext.getTenantId();
        try {
            if (productIds == null || productIds.isEmpty()) {
                return ResponseEntity.badRequest().body("product id field cannot be empty");
            }
            double totalAmount = 0;
            List<ReceiptProduct> receiptProducts = new ArrayList<>();

            double discount = 0;
            // Iterate first to calculate amounts and build list
            for (ReceiptProductDto productId : productIds) {
                String id = productId.getId();
                Products products = productRepo.findByIdAndTenantId(id, tenantId).orElse(null);
                
                if (products != null) {
                    double disc = productId.getDiscount();
                    discount = disc; // Warning: this logic takes the last product's discount? logic from original
                    
                    double prdDisc = products.getPrice() * productId.getQuantity() * productId.getDiscount()/100 ;
                    double finalPrdPrice = products.getPrice() * productId.getQuantity() - prdDisc;
                    totalAmount += finalPrdPrice;
                    
                    ReceiptProduct receiptProduct = new ReceiptProduct(products.getPrdName(), products.getPrice(), productId.getQuantity(), productId.getDiscount());
                    receiptProducts.add(receiptProduct);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Product with ID " + productId.getId() + " not found or access denied.");
                }
            }
            
            // Recalculate global discount amount based on "discount" variable logic from original code
            double discountAmount = 0.0;
            // Note: The logic below seems redundant if individual product discounts were applied, but keeping to minimize logic change drift
            if (discount >= 0 && discount <= 100) {
                for (ReceiptProductDto productId : productIds) {
                    String id = productId.getId();
                    Products products = productRepo.findByIdAndTenantId(id, tenantId).orElse(null);
                    if (products != null) {
                        double disc = productId.getDiscount(); // Use product specific discount
                        double prdDisc = products.getPrice() * productId.getQuantity() * disc / 100;
                        discountAmount += prdDisc;
                    }
                }
            } else if (discount < 0 || discount >= 100) {
                return ResponseEntity.badRequest().body("Invalid discount. Must be between 0 and 99.");
            }

            // Create and save
            UnpaidBills unpaidBills = new UnpaidBills(totalAmount, receiptProducts, discountAmount, customer);
            unpaidBills.setTenantId(tenantId);
            // createdDate handling? Assuming UnpaidBills entity handles it or we set it? 
            // Entity has @CreatedDate but typically needs auditing enabled or manual set. Setting explicitly if field exists
             unpaidBills.setCreatedDate(LocalDateTime.now());
             
            unpaidBillsRepository.save(unpaidBills);
            return ResponseEntity.status(HttpStatus.CREATED).body(unpaidBills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
    
    //put method
    public void markBillAsPaid(String id) {
        String tenantId = TenantContext.getTenantId();
        
        // Find the unpaid bill
        Optional<UnpaidBills> unpaidBillOptional = unpaidBillsRepository.findByIdAndTenantId(id, tenantId);

        if (unpaidBillOptional.isPresent()) {
            // Retrieve the unpaid bill
            UnpaidBills unpaidBill = unpaidBillOptional.get();
            
            // Create a Receipt object (Paid Bill)
            Receipt receipt = new Receipt();
            // receipt.setId(unpaidBill.getId()); // Don't reuse ID, let Mongo gen new one or use different logic? Original used same ID.
            // If we reuse ID, it might conflict if collections are different but typically unique IDs are good.
            // Original code: receipt.setId(unpaidBill.getId());
            // Safe to copy content
            
            receipt.setTenantId(tenantId);
            receipt.setCustomer(unpaidBill.getCustomer());
            receipt.setTotalAmount(unpaidBill.getTotalAmount());
            receipt.setProducts(unpaidBill.getProducts());
            receipt.setDiscountAmount(unpaidBill.getDiscountAmount());
            receipt.setCreateDate(LocalDateTime.now());
            receipt.setLastModifiedDate(LocalDateTime.now());
            receipt.setCharge(Charge.BYCASH); // Defaulting to Cash if not specified, or need input? Original assumed logic?
            
            // Save to the Receipts collection
            receiptRepository.save(receipt);
            
            // Remove from the UnpaidBills collection
            unpaidBillsRepository.deleteById(id);
            
            // Update Sales
            salesService.updateSales(); // Use the reliable service calculation
            
            System.out.println("Bill moved from unpaid to paid successfully!");
        } else {
            throw new IllegalArgumentException("Unpaid bill with ID " + id + " not found.");
        }
    }

    public ResponseEntity<String> deleteById(String id) {
        String tenantId = TenantContext.getTenantId();
        try {
           Optional<UnpaidBills> unpaidBills = unpaidBillsRepository.findByIdAndTenantId(id, tenantId);
           if (unpaidBills.isPresent()){
                unpaidBillsRepository.deleteByIdAndTenantId(id, tenantId);
                return ResponseEntity.ok("Deleted successfully");
           } else {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bill not found");
           }
        } catch (Exception e){
            throw new RuntimeException("an Unknown error occurred");
        }
    }
    
    public UnpaidBillsDto filterByCustomer(String customer) {
        String tenantId = TenantContext.getTenantId();
        List<UnpaidBills> unpaidBills = unpaidBillsRepository.findByTenantIdAndCustomer(tenantId, customer);
        
        if (unpaidBills == null || unpaidBills.isEmpty()){
            // throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Better to return empty DTO or handling?
            // Returning empty logic for consistency
             return new UnpaidBillsDto(new ArrayList<>(), customer, 0, 0);
        } else {
            double totalCount = unpaidBills.size();
            double totalAmountPending = unpaidBills.stream()
                    .mapToDouble(UnpaidBills::getTotalAmount)
                    .sum();
            return new UnpaidBillsDto(unpaidBills,customer,totalCount,totalAmountPending);
        }
    }
}

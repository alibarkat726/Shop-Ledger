package com.App.Shop_Ledger.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.App.Shop_Ledger.Dto.FilterSalesDto;
import com.App.Shop_Ledger.Dto.ReceiptDto;
import com.App.Shop_Ledger.Dto.ReceiptProductDto;
import com.App.Shop_Ledger.model.*;
import com.App.Shop_Ledger.User.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.App.Shop_Ledger.Repository.ReceiptRepository;
import com.App.Shop_Ledger.Repository.productRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ReceiptService {

    @Autowired
    private productRepo productRepository;

    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    @Autowired
    private SalesService salesService;

    // create receipts
    public ResponseEntity<?> createReceipt(List<ReceiptProductDto> productIds, String customer, Charge charge) {
        String tenantId = TenantContext.getTenantId();
        
        try {
            if (productIds == null || productIds.isEmpty()) {
                return ResponseEntity.badRequest().body("Product IDs list cannot be empty");
            }
            double totalAmount = 0;
            List<ReceiptProduct> receiptProducts = new ArrayList<>();
            double discountAmount = 0.0;
            
            for (ReceiptProductDto productId : productIds) {
                String id = productId.getId();
                // Validate product belongs to tenant
                Products product = productRepository.findByIdAndTenantId(id, tenantId).orElse(null);
                
                if (product != null) {
                    double prdPrice = product.getPrice() * productId.getQuantity();
                    double prdDisc = 0;
                    
                    if (productId.getDiscount() > 0) {
                        prdDisc = prdPrice * productId.getDiscount()/100;
                    }
                    
                    double finalPrdPrice = prdPrice - prdDisc;
                    totalAmount += finalPrdPrice;
                    discountAmount += prdDisc;
                    
                    // Create a ReceiptProduct object (snapshot of product at time of sale)
                    ReceiptProduct receiptProduct = new ReceiptProduct(
                            product.getPrdName(), 
                            product.getPrice(),
                            productId.getQuantity(),
                            productId.getDiscount()
                    );
                    receiptProducts.add(receiptProduct);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Product with ID " + productId.getId() + " not found or access denied.");
                }
            }

            // Create and save the receipt
            Receipt receipt = new Receipt();
            receipt.setTenantId(tenantId);
            receipt.setCustomer(customer);
            receipt.setProducts(receiptProducts);
            receipt.setTotalAmount(totalAmount);
            receipt.setDiscountAmount(discountAmount);
            receipt.setCharge(charge);
            receipt.setCreateDate(LocalDateTime.now());
            receipt.setLastModifiedDate(LocalDateTime.now());

            receiptRepository.save(receipt);

            // Update sales using the dedicated service
            salesService.updateSales();

            return ResponseEntity.status(HttpStatus.CREATED).body(receipt);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the receipt: " + e.getMessage());
        }
    }

    // get all receipts
    public List<Receipt> getReceipt() {
        String tenantId = TenantContext.getTenantId();
        return receiptRepository.findByTenantId(tenantId);
    }

    // Get receipt by id
    public Map<String, Object> getReceiptById(String id) {
        String tenantId = TenantContext.getTenantId();
        Receipt receipt = receiptRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));

        Map<String, Object> response = new HashMap<>();
        response.put("receiptId", receipt.getId());
        response.put("customer", receipt.getCustomer());
        response.put("date", receipt.getCreateDate().toString()); 
        response.put("totalAmount", receipt.getTotalAmount());
        response.put("discountAmount", receipt.getDiscountAmount());

        // Products
        List<Map<String, Object>> productList = new ArrayList<>();
        if (receipt.getProducts() != null) {
            for (ReceiptProduct product : receipt.getProducts()) {
                Map<String, Object> p = new HashMap<>();
                p.put("name", product.getName());
                p.put("qty", product.getQuantity());
                p.put("price", product.getPrice());
                p.put("total", product.getQuantity() * product.getPrice());
                productList.add(p);
            }
        }
        response.put("products", productList);

        if (receipt.getCharge() != null) {
            Map<String, Object> chargeMap = new HashMap<>();
            chargeMap.put("method", receipt.getCharge());
            chargeMap.put("amount", receipt.getTotalAmount()); 
            response.put("charge", chargeMap);
        }

        response.put("lastModifiedDate", receipt.getLastModifiedDate() != null ? receipt.getLastModifiedDate().toString() : "");

        return response;
    }

    // Filter By date
    public FilterSalesDto filterReceiptByDate(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        String tenantId = TenantContext.getTenantId();
        
        if (startOfDay == null && endOfDay != null) {
            startOfDay = endOfDay.with(LocalTime.MIN);
        }
        if (startOfDay != null && endOfDay == null) {
            endOfDay = startOfDay.with(LocalTime.MAX);
        }
        
        List<Receipt> receipts = receiptRepository.findByTenantIdAndCreateDateBetween(tenantId, startOfDay, endOfDay);
        double receiptsCount = receipts.size();
        
        // Calculate stats on the fly from filtered receipts
        double totalSalesByCard = receipts.stream()
                .filter(r -> Charge.BYCARD.equals(r.getCharge()))
                .mapToDouble(Receipt::getTotalAmount).sum();
                
        double totalSalesByCash = receipts.stream()
                .filter(r -> Charge.BYCASH.equals(r.getCharge()))
                .mapToDouble(Receipt::getTotalAmount).sum();

        double totalAmountTransferredToAccount = receipts.stream()
                .filter(r -> Charge.TRANSFERTOACCOUNT.equals(r.getCharge()))
                .mapToDouble(Receipt::getTotalAmount).sum();

        double grossSale = receipts.stream()
                .mapToDouble(Receipt::getTotalAmount).sum();

        double discounts = receipts.stream()
                .mapToDouble(Receipt::getDiscountAmount).sum();
                
        return new FilterSalesDto(receipts, grossSale, discounts, totalAmountTransferredToAccount, totalSalesByCash, totalSalesByCard, receiptsCount);
    }

    public ResponseEntity<?> updateReceipt(String id, Receipt receipt) {
        String tenantId = TenantContext.getTenantId();
        
        return receiptRepository.findByIdAndTenantId(id, tenantId).map(existingReceipt -> {
            // Update fields
            existingReceipt.setProducts(receipt.getProducts());
            
            // Recalculate total if products changed
            double totalAmount = 0;
            if (receipt.getProducts() != null) {
                 totalAmount = receipt.getProducts().stream()
                    .mapToDouble(rp -> rp.getPrice() * rp.getQuantity()) 
                    .sum();
            }
            existingReceipt.setTotalAmount(totalAmount);
            existingReceipt.setLastModifiedDate(LocalDateTime.now());
            
            receiptRepository.save(existingReceipt);
            
            // Recalculate sales totals
            salesService.updateSales();
            
            return ResponseEntity.ok("Receipt updated");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt not found"));
    }

    public void updateSales() {
        salesService.updateSales();
    }

    public ResponseEntity<String> deleteReceiptById(String id) {
        String tenantId = TenantContext.getTenantId();
        try {
            if (receiptRepository.findByIdAndTenantId(id, tenantId).isPresent()) {
                receiptRepository.deleteByIdAndTenantId(id, tenantId);
                salesService.updateSales();
                return ResponseEntity.ok("Receipt deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt with this id not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred");
        }
    }

    public FilterSalesDto filterReceiptByCustomer(String customer) {
        String tenantId = TenantContext.getTenantId();
        List<Receipt> receipts = receiptRepository.findByTenantIdAndCustomer(tenantId, customer);
        
        if (receipts == null || receipts.isEmpty()) {
            throw new RuntimeException("receipt with this customer not found");
        } else {
            double totalAmount = receipts.stream()
                    .mapToDouble(Receipt::getTotalAmount)
                    .sum();
            double totalCount = receipts.size();
            return new FilterSalesDto(receipts, totalAmount, totalCount);
        }
    }
}

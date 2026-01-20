package com.App.Shop_Ledger.Service;

import com.App.Shop_Ledger.Repository.SalesRepository;
import com.App.Shop_Ledger.Repository.ReceiptRepository;
import com.App.Shop_Ledger.User.TenantContext;
import com.App.Shop_Ledger.model.Sales;
import com.App.Shop_Ledger.model.Receipt; // Assuming Receipt is in model package
import com.App.Shop_Ledger.model.Charge; // Assuming Charge is in model package
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;
    
    @Autowired
    private ReceiptRepository receiptRepository;

    public Optional<Sales> getSales() {
        String tenantId = TenantContext.getTenantId();
        return salesRepository.findByTenantId(tenantId).stream().findFirst();
    }
    
    public void updateSales() {
        String tenantId = TenantContext.getTenantId();
        List<Receipt> receipts = receiptRepository.findByTenantId(tenantId);
        
        // Calculate totals
        double totalSalesByCard = receipts.stream()
                .filter(r -> Charge.BYCARD.equals(r.getCharge()))
                .mapToDouble(Receipt::getTotalAmount).sum();
                
        double totalSalesByCash = receipts.stream()
                .filter(r -> Charge.BYCASH.equals(r.getCharge()))
                .mapToDouble(Receipt::getTotalAmount).sum();
                
        double totalTransferred = receipts.stream()
                .filter(r -> Charge.TRANSFERTOACCOUNT.equals(r.getCharge()))
                .mapToDouble(Receipt::getTotalAmount).sum();
                
        double grossSale = receipts.stream()
                .mapToDouble(Receipt::getTotalAmount).sum();
                
        double discounts = receipts.stream()
                .mapToDouble(Receipt::getDiscountAmount).sum();
                
        // Find existing sales record for tenant or create new one
        Sales sales = salesRepository.findByTenantId(tenantId).stream()
                .findFirst()
                .orElse(new Sales());
        
        if (sales.getId() == null) {
            sales.setTenantId(tenantId);
            sales.setRecordDate(LocalDateTime.now());
        }
        
        sales.setTotalSalesByCard(totalSalesByCard);
        sales.setTotalSalesByCash(totalSalesByCash);
        sales.setTotalAmountTransferredToAccount(totalTransferred);
        sales.setGrossSale(grossSale);
        sales.setDiscounts(discounts);
        // Record date updated to show when last calculation happened
        sales.setRecordDate(LocalDateTime.now()); 
        
        salesRepository.save(sales);
    }
}

package com.App.Shop_Ledger.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import com.App.Shop_Ledger.Dto.FilterSalesDto;
import com.App.Shop_Ledger.Dto.ReceiptDto;
import com.App.Shop_Ledger.Dto.ReceiptProductDto;
import com.App.Shop_Ledger.Repository.SalesRepository;
import com.App.Shop_Ledger.model.*;
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
   private final  ReceiptRepository receiptRepository;
   @Autowired
   public ReceiptService(ReceiptRepository receiptRepository){
    this.receiptRepository = receiptRepository;
   }
   @Autowired
    SalesRepository salesRepository;
   //create receipts
   public ResponseEntity<?> createReceipt(List<ReceiptProductDto> productIds, String customer, Charge charge) {
       try {
           if (productIds == null || productIds.isEmpty()) {
               return ResponseEntity.badRequest().body("Product IDs list cannot be empty");
           }
           double totalAmount = 0;
           List<ReceiptProduct> receiptProducts = new ArrayList<>();
           // Loop through product IDs and get products from the database
           double discount = 0;
           double discountAmount = 0.0;
           for (ReceiptProductDto productId : productIds) {
               String id = productId.getId();
               Products product = productRepository.findById(id).orElse(null);
               if (product != null) {
                   discount = productId.getDiscount();
                   double prdPrice = product.getPrice() * productId.getQuantity();
                   double prdDisc =  prdPrice * productId.getDiscount()/100 ;
                   double finalPrdPrice = prdPrice - prdDisc;
                   totalAmount += finalPrdPrice;
                   discountAmount += prdDisc;
                   // Create a ReceiptProduct object with the product details
                   ReceiptProduct receiptProduct = new ReceiptProduct(product.getPrdName(), product.getPrice(),productId.getQuantity(),productId.getDiscount());
                   receiptProducts.add(receiptProduct);
//                 for (int i=0; i< productIds.size() -1 ;i++){
//                     discountAmount += prdDisc;
//                 }
                   System.out.println(productIds.size());
                   System.out.println(discountAmount);
               } else {
                   return ResponseEntity.status(HttpStatus.NOT_FOUND)
                           .body("Product with ID " + productId + " not found.");
               }
           }
           if (discount > 0 && discount < 100) {
               System.out.println(discountAmount);
           } else if (discount < 0 || discount >= 100) {
               return ResponseEntity.badRequest().body("Invalid discount. Must be between 0 and 99.");
           }
           //Discount
           //Card Charge
               if (charge == Charge.BYCARD){
                   List<Sales> cardSalesRecord = salesRepository.findAll();
                   Sales sales;
                   if (cardSalesRecord.isEmpty()){
                       sales = new Sales();
                   }else {
                       sales = cardSalesRecord.get(cardSalesRecord.size() - 1);
                   }

                   sales.setTotalSalesByCard(sales.getTotalSalesByCard() + totalAmount);
                   salesRepository.save(sales);

                   System.out.println("Charged by Card");

                   //if account transfer
               }else if (charge ==Charge.TRANSFERTOACCOUNT){
                   List<Sales> SalesRecord = salesRepository.findAll();
                   Sales sales;
                   if (SalesRecord.isEmpty()){
                       sales = new Sales();
                   }else {
                       sales = SalesRecord.get(SalesRecord.size() - 1);
                   }
                   sales.setTotalAmountTransferredToAccount(sales.getTotalAmountTransferredToAccount() + totalAmount);
                   salesRepository.save(sales);
                   System.out.println("Bill transferred to account ");

                   //if charged by card
               }else if (charge == Charge.BYCASH){
                   List<Sales> cashSalesRecord = salesRepository.findAll();
                   Sales sales;
                   if (cashSalesRecord.isEmpty()){
                       sales = new Sales();
                   }else {
                       sales = cashSalesRecord.get(cashSalesRecord.size() - 1);
                   }
                   sales.setTotalSalesByCash(sales.getTotalSalesByCash() + totalAmount);
                   salesRepository.save(sales);
                   System.out.println("paid By cash");
               }else {
                   System.out.println("please enter a valid  payment method");
               }

           // Create and save the receipt (createdDate is automatically populated)
           Receipt receipt = new Receipt(totalAmount,receiptProducts,discountAmount,customer,charge);
           receiptRepository.save(receipt);

           //Discount total update
           List<Sales> allDiscountRecord = salesRepository.findAll();
           Sales sales1;
           if (allDiscountRecord.isEmpty()){
               sales1 = new Sales();
           }else {
               sales1 = allDiscountRecord.get(allDiscountRecord.size() - 1);
           }

           sales1.setDiscounts(sales1.getDiscounts() + discountAmount);
           salesRepository.save(sales1);

//           Gross sale update
           List<Sales> allSalesRecord = salesRepository.findAll();
           Sales sales;
           if (allSalesRecord.isEmpty()){
               sales = new Sales();
           }else {
               sales = allSalesRecord.get(allSalesRecord.size() - 1);

           sales.setGrossSale(sales.getGrossSale() + totalAmount);
           salesRepository.save(sales);
           }
           return ResponseEntity.status(HttpStatus.CREATED).body(receipt);

       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("An error occurred while creating the receipt: " + e.getMessage());
       }
   }
//get all receipts
    public List<Receipt>getReceipt() {

           return receiptRepository.findAll();

    }
//Get receipt by id
public Map<String, Object> getReceiptById(String id) {
    Receipt receipt = receiptRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));

    Map<String, Object> response = new HashMap<>();
    response.put("receiptId", receipt.getId());
    response.put("customer", receipt.getCustomer());
    response.put("date", receipt.getCreateDate().toString());  // format later if needed
    response.put("totalAmount", receipt.getTotalAmount());
    response.put("discountAmount", receipt.getDiscountAmount());

    // Products
    List<Map<String, Object>> productList = new ArrayList<>();
    for (ReceiptProduct product : receipt.getProducts()) {
        Map<String, Object> p = new HashMap<>();
        p.put("name", product.getName());
        p.put("qty", product.getQuantity());
        p.put("price", product.getPrice());
        p.put("total", product.getQuantity() * product.getPrice());
        productList.add(p);
    }
    response.put("products", productList);

    // Charges (if you have multiple payment methods like Cash/Card)
    if (receipt.getCharge() != null) {
        Map<String, Object> chargeMap = new HashMap<>();
        chargeMap.put("method", receipt.getCharge());
        chargeMap.put("amount", receipt.getCharge());
        response.put("charge", chargeMap);
    }

    response.put("lastModifiedDate", receipt.getLastModifiedDate().toString());

    return response;
}

    //Filter By date
    public FilterSalesDto filterReceiptByDate(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        if (startOfDay == null && endOfDay != null) {
            startOfDay = endOfDay.with(LocalTime.MIN);
        }
        if (startOfDay != null && endOfDay == null) {
            endOfDay = startOfDay.with(LocalTime.MAX);
        }
       List<Receipt> receipts = receiptRepository.findByCreateDateBetween(startOfDay,endOfDay);
       double receiptsCount = receipts.stream().count();
        //totalSalesByCard
        double totalSalesByCard = receipts.stream()
                .filter(r -> Charge.BYCARD.equals(r.getCharge()))
                .mapToDouble(Receipt :: getTotalAmount)
                .sum();
        //TotalSalesByCash
        double totalSalesByCash = receipts.stream()
                .filter(r -> Charge.BYCASH.equals(r.getCharge()))
                .mapToDouble(Receipt :: getTotalAmount)
                .sum();

        //totalAmountTransferToAccount
        double totalAmountTransferredToAccount = receipts.stream()
                .filter(r -> Charge.TRANSFERTOACCOUNT.equals(r.getCharge()))
                .mapToDouble(Receipt :: getTotalAmount)
                .sum();
       //Gross sales

        double grossSale = receipts.stream()
                .mapToDouble(Receipt::getTotalAmount)
                .sum();
        //Discounts
        double discounts = receipts.stream()
                .mapToDouble(Receipt :: getDiscountAmount)
                .sum();
        return new FilterSalesDto(receipts,grossSale,discounts,totalAmountTransferredToAccount,totalSalesByCash,totalSalesByCard,receiptsCount);
    }

    public Receipt  updateReceipt(String id, Receipt receipt) {
return receiptRepository.findById(id).map(existingReceipts ->{
           existingReceipts.setProducts(receipt.getProducts());
           double totalAmount = receipt.getProducts().stream()
                           .mapToDouble(ReceiptProduct::getPrice)
                                   .sum();
    // Update grossSale in the Sales model
    double oldTotalAmount = existingReceipts.getTotalAmount(); // Get the old total amount
    Sales sales = salesRepository.findFirst().orElseGet(Sales::new); // Assuming a single Sales document
    sales.setGrossSale(sales.getGrossSale() - oldTotalAmount + totalAmount); // Adjust grossSale
    salesRepository.save(sales);
           existingReceipts.setTotalAmount(totalAmount);
           existingReceipts.setLastModifiedDate(receipt.getLastModifiedDate());
           Receipt receipt1 = receiptRepository.save(existingReceipts);
           return receipt1;
       }).orElseThrow(() -> new RuntimeException("receipt not found for id:" + id));
    }
    public void updateSales(){
        List<Sales> allSalesRecord = salesRepository.findAll();
        Sales sales;
        if (allSalesRecord.isEmpty()){
            sales = new Sales();
        }else {
            sales = allSalesRecord.get(allSalesRecord.size() - 1);
        }
        for (int i =0; i< allSalesRecord.size() - 1;i++){
            salesRepository.delete(allSalesRecord.get(i));
        }
    }

    public ResponseEntity<String> deleteReceiptById(String id) {
       try {
           if (receiptRepository.findById(id).isPresent()){
            receiptRepository.deleteById(id);
            return ResponseEntity.ok("Receipt deleted successfully");
       }else{
              return ResponseEntity.badRequest().body("Receipt with this id not found");
           }
       }catch (Exception e){
          throw new RuntimeException("Unknown error occurred");
       }

    }

    public FilterSalesDto filterReceiptByCustomer(String customer) {
        List<Receipt> receipts = receiptRepository.findByCustomer(customer);
        if (receipts == null || receipts.isEmpty()) {
            throw new RuntimeException("receipt with this customer not found");
        } else {
            double totalAmount = receipts.stream()
                    .mapToDouble(Receipt::getTotalAmount)
                    .sum();
            double totalCount = receipts.stream().count();
            return new FilterSalesDto(receipts, totalAmount, totalCount);
        }
    }
}

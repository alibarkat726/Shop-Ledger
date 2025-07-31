package com.App.Shop_Ledger.Service;


import com.App.Shop_Ledger.Dto.ReceiptProductDto;
import com.App.Shop_Ledger.Dto.UnpaidBillsDto;
import com.App.Shop_Ledger.Repository.ReceiptRepository;
import com.App.Shop_Ledger.Repository.SalesRepository;
import com.App.Shop_Ledger.Repository.UnpaidBillsRepository;
import com.App.Shop_Ledger.Repository.productRepo;
import com.App.Shop_Ledger.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<UnpaidBills> getBills() {
        return unpaidBillsRepository.findAll();
    }

    //Generate and save bill
    public ResponseEntity<?> UnpaidBills(List<ReceiptProductDto> productIds, String customer) {
        try {
            if (productIds == null || productIds.isEmpty()) {
                return ResponseEntity.badRequest().body("product id field cannot be empty");
            }
            double totalAmount = 0;
            List<ReceiptProduct> receiptProducts = new ArrayList<>();

            double discount = 0;
            for (ReceiptProductDto productId : productIds) {
                String id = productId.getId();
                Products products = productRepo.findById(id).orElse(null);
                if (products != null) {
                    double disc = productId.getDiscount();
                    discount = disc;
                    double prdDisc =  products.getPrice()*productId.getQuantity() * productId.getDiscount()/100 ;
                    double finalPrdPrice = products.getPrice() *productId.getQuantity() - prdDisc;
                    totalAmount += finalPrdPrice;
                    // Create a ReceiptProduct object with the product details
                    ReceiptProduct receiptProduct = new ReceiptProduct(products.getPrdName(), products.getPrice(),productId.getQuantity(),productId.getDiscount());
                    receiptProducts.add(receiptProduct);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Product with ID " + productId + " not found.");
                }
            }
            //Discount
            double discountAmount = 0.0;
            if (discount >= 0 && discount<=100) {
                for (ReceiptProductDto productId : productIds) {
                    String id = productId.getId();
                    Products products = productRepo.findById(id).orElse(null);
                    double disc = productId.getDiscount();
                    discount = disc;
                    double prdDisc = products.getPrice()*productId.getQuantity() * disc / 100;
                    discountAmount += prdDisc;
                }
            }
           else if (discount < 0 || discount >= 100) {
                return ResponseEntity.badRequest().body("Invalid discount. Must be between 0 and 99.");
            }

            // Create and save the receipt (createdDate is automatically populated)
            UnpaidBills unpaidBills = new UnpaidBills(totalAmount, receiptProducts, discountAmount, customer);
            unpaidBillsRepository.save(unpaidBills);
            return ResponseEntity.status(HttpStatus.CREATED).body(unpaidBills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the receipt: " + e.getMessage());
        }
    }
    //put method
    public void markBillAsPaid(String id) {
        // Find the unpaid bill
        Optional<UnpaidBills> unpaidBillOptional = unpaidBillsRepository.findById(id);

        if (unpaidBillOptional.isPresent()) {
            // Retrieve the unpaid bill
            UnpaidBills unpaidBill = unpaidBillOptional.get();
            // Create a PaidBills object
            Receipt receipt = new Receipt();
            receipt.setId(unpaidBill.getId());
            receipt.setCustomer(unpaidBill.getCustomer());
            receipt.setTotalAmount(unpaidBill.getTotalAmount());
           receipt.setProducts(unpaidBill.getProducts());
            receipt.setDiscountAmount(unpaidBill.getDiscountAmount());
            receipt.setCreateDate(LocalDateTime.now());
            // Save to the PaidBills collection
            receiptRepository.save(receipt);
            // Remove from the UnpaidBills collection
            unpaidBillsRepository.deleteById(id);
            updateSalesData(unpaidBill.getTotalAmount(), unpaidBill.getDiscountAmount());
            System.out.println("Bill moved from unpaid to paid successfully!");
        } else {
            throw new IllegalArgumentException("Unpaid bill with ID " + id + " not found.");
        }
    }
    private void updateSalesData(double amount, double discount) {
        // Fetch the sales record (assuming there is only one record; adjust if necessary)
        Optional<Sales> salesOptional = salesRepository.findAll().stream().findFirst(); // Replace "salesId" with the actual sales document ID
        if (salesOptional.isPresent()) {
            Sales sales = salesOptional.get();
            // Update grossSale and totalDiscount
            sales.setGrossSale(sales.getGrossSale() + amount);
            sales.setDiscounts(sales.getDiscounts() + discount);
            // Save the updated sales record
            salesRepository.save(sales);
        } else {
            throw new IllegalArgumentException("Sales record not found.");
        }
}
    public ResponseEntity<String> deleteById(String id) {
        try {
           Optional<UnpaidBills> unpaidBills = unpaidBillsRepository.findById(id);
           if (unpaidBills.isPresent()){
                unpaidBillsRepository.deleteById(id);
                return ResponseEntity.ok("id: " + id + "successfully");
           }else {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("id with " + id + "notFound");
           }
        }catch (Exception e){
            throw new RuntimeException("an Unknown error occurred");
        }
    }
    public UnpaidBillsDto filterByCustomer(String customer) {

            List<UnpaidBills> unpaidBills = unpaidBillsRepository.findByCustomer(customer);
            if (unpaidBills == null || unpaidBills.isEmpty()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }else {
        double totalCount = unpaidBills.stream().count();
           double totalAmountPending = unpaidBills.stream()
                   .mapToDouble(UnpaidBills::getTotalAmount)
                   .sum();
        return new UnpaidBillsDto(unpaidBills,customer,totalCount,totalAmountPending);
    }
    }
}





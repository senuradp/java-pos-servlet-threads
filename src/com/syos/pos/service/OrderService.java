/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.service;

import com.syos.pos.core.ServiceFactory;
import com.syos.pos.dto.BillDetailDTO;
import com.syos.pos.dto.BillHeaderDTO;
import com.syos.pos.dto.ProductDTO;
import com.syos.pos.entity.Product;
import com.syos.pos.repository.ShelfRepository;
import com.syos.pos.service.dao.IBillDetailService;
import com.syos.pos.service.dao.IBillHeaderService;
import com.syos.pos.service.dao.IProductService;
import com.syos.pos.service.dao.IShelfService;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author senu2k
 */

import java.util.concurrent.locks.ReentrantLock;
public class OrderService {

    private static final IProductService productService = (IProductService) ServiceFactory.getInstance().getDAO(ServiceFactory.ServiceType.PRODUCT);
    private static final IShelfService shelfService = (IShelfService) ServiceFactory.getInstance().getDAO(ServiceFactory.ServiceType.SHELF);
    private static final IBillHeaderService billHeaderService = (IBillHeaderService) ServiceFactory.getInstance().getDAO(ServiceFactory.ServiceType.BILL_HEADER);
    private static final IBillDetailService billDetailService = (IBillDetailService) ServiceFactory.getInstance().getDAO(ServiceFactory.ServiceType.BILL_DETAIL);

    private static OrderService orderServiceInstance;

    //make BillHeaderDTO as a map(serial number):value(BillHeaderDTO)
    private Map<String, BillHeaderDTO> billHeaderMap ;

//    private final BillHeaderDTO billHeaderDTO;
    private final BillDetailDTO billDetailDTO;
//    private BillHeaderDTO billHeaderDTO;
//    private BillDetailDTO billDetailDTO;
    private static final ReentrantLock lock = new ReentrantLock();
    private Map<String, ReentrantLock> productLocks = new HashMap<>();

    private OrderService() {
        this.billHeaderMap = new HashMap<>();
        this.billDetailDTO = new BillDetailDTO();
    }

    public static OrderService getInstance() {
        if (orderServiceInstance == null) {
            orderServiceInstance = new OrderService();
        }
        return orderServiceInstance;
    }

    public String createOrder() {
        lock.lock();
        try {
            Date currentDate = new Date();

            BillHeaderDTO billHeaderDTO = new BillHeaderDTO();
            billHeaderDTO.setBill_serial_number(generateSerialNumber());
            billHeaderDTO.setDate(currentDate);
            billHeaderDTO.setTotal_bill_price(0.0);

            //the BillHeaderDTO to the map
            billHeaderMap.put(billHeaderDTO.getBill_serial_number(), billHeaderDTO);

            return billHeaderDTO.getBill_serial_number();
        } finally {
            lock.unlock();
        }
    }

    public double addOrderProduct(String serial_number, String product_code, double qty) throws Exception {
        lock.lock();
        try {
            double total_price = 0;

            // productService.get // product by code (this gives the name and price and
            // thers and pass to th blow function)
            // productService.getProductByCode(product_code);

            ProductDTO product = productService.getProductByCode(product_code);

            if(product == null) {
                throw new Exception("Product with code " + product_code + " not found.");
            }

            String productName = product.getProduct_name();
            double price = product.getProduct_price();
            //check if serial number is in the array
            billHeaderMap.get(serial_number).addProduct(product_code, productName, qty, price);

            return billHeaderMap.get(serial_number).getTotal_bill_price();
        } finally {
            lock.unlock();
        }
    }

    public double addDiscount(String serial_number, double discount_amount) {
        double total_price = 0;

        billHeaderMap.get(serial_number).setDiscount(discount_amount);

        return billHeaderMap.get(serial_number).getTotal_bill_price();
    }
//    amount_tendered > billHeaderDTO.getTotal_bill_price()

    // Helper method to get or create a lock for a product
    private ReentrantLock getProductLock(String product_code) {
        productLocks.putIfAbsent(product_code, new ReentrantLock());
        return productLocks.get(product_code);
    }

    public double checkoutPay(String serial_number, double amount_tendered, String payment_type) throws Exception {
        ReentrantLock productLock = getProductLock(serial_number);

        // Lock the product to ensure exclusive access
        productLock.lock();
        boolean insufficientProduct = false; // Flag to track if any product is insufficient
        List<String> insufficientProducts = new ArrayList<>();
        try {
            // pass the payment type to bill header
            billHeaderMap.get(serial_number).setPayment_type(payment_type);

            if (amount_tendered < billHeaderMap.get(serial_number).getTotal_bill_price()) {
                throw new Exception("Amount tendered is less than total bill price. " + billHeaderMap.get(serial_number).getTotal_bill_price());
            }

            double balance = calculateBalancePay(serial_number, amount_tendered);

            billHeaderMap.get(serial_number).setAmount_tendered(amount_tendered);
            billHeaderMap.get(serial_number).setChange(balance);

            List<BillDetailDTO> billDetail = billHeaderMap.get(serial_number).getTypeOfBillDetails();

            for (int i = 0; i < billDetail.size(); i++) {

                BillDetailDTO billDetailDTO = billDetail.get(i);

                //update
                String product_code = billDetailDTO.getProduct_code();
                double qty = billDetailDTO.getItem_qty();

                double availableShelfQty = getAvailableQty(product_code);

                if (qty > availableShelfQty) {
                    insufficientProduct = true; // Set the flag if any product is insufficient
                    insufficientProducts.add(product_code);
                }
            }

            // Check if any product was insufficient, if not, save bill header and update shelves
            if (!insufficientProduct) {
                for (int i = 0; i < billDetail.size(); i++) {
                    BillDetailDTO billDetailDTO = billDetail.get(i);
                    String product_code = billDetailDTO.getProduct_code();
                    double qty = billDetailDTO.getItem_qty();
                    double availableShelfQty = getAvailableQty(product_code);
                    qty = availableShelfQty - qty;
                    updateShelf(product_code, qty);
                }
                billHeaderService.add(billHeaderMap.get(serial_number));
            } else {
                throw new Exception("One or more products have insufficient stock.: " + String.join(", ", insufficientProducts));
            }

            //remove the bill header from the array
            billHeaderMap.remove(serial_number);

            return balance;

        }
        finally {
            // Release the lock when done
            productLock.unlock();
        }
    }

    public double getAvailableQty(String product_code) throws Exception {

        return shelfService.getAvailableQty(product_code);

    }

    public void updateShelf(String product_code, double qty) throws Exception {
        lock.lock();
        try {
            shelfService.updateShelf(product_code, qty);
        } finally {
            lock.unlock();
        }

    }

    public String generateSerialNumber() {

        String prefix = "B00";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        String serial_number = prefix + timestamp;

        return serial_number;
    }

//    public double calculateTotalPrice(double price, double qty) {
//        double total_price = 0;
//
//        return 0;
//    }

    public double calculateBalancePay(String serial_number, double amount_tendered) {
        double balance = 0;

        balance = amount_tendered - billHeaderMap.get(serial_number).getTotal_bill_price();

        return balance;
    }

}

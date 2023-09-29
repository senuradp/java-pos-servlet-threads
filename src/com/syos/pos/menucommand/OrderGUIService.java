/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.menucommand;

import com.google.gson.*;
import com.syos.pos.controller.BatchController;
import com.syos.pos.gui.OrderAddGUI;
import com.syos.pos.service.OrderService;

import java.text.ParseException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.syos.pos.gui.OrderAddGUI;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author senu2k
 */
public class OrderGUIService {
//    void add() {
//         try {
//            Scanner scanner = new Scanner(System.in);
//            ProductController productController = new ProductController();
//            OrderService orderService = OrderService.getInstance();
//            orderService.createOrder();
//
//            System.out.println("Adding a new order...");
//
//
//            while (true) {
//                System.out.println("Enter product code (or 'done' to finish adding products): ");
//                String productCode = scanner.nextLine();
//
//                if (productCode.equals("done")) {
//                    break; // Exit the loop if 'done' is entered
//                }
//
//                System.out.println("Enter quantity: ");
//                double quantity = scanner.nextDouble();
//
//                // Check if the product code exists
//                boolean productCodeExists = productController.checkProductCodeExists(productCode);
//
//                if (!productCodeExists) {
//                    System.out.println("Product code does not exist!");
//                    continue; // Skip to the next iteration if product code does not exist
//                }
//
//                orderService.addOrderProduct(productCode, quantity);
//                System.out.println("Product added to the order!");
//
//                // Clear the scanner buffer
//                scanner.nextLine();
//            }
//
//            // Get payment type and discount once
//            System.out.println("Enter payment type (Cash/Card): ");
//            String pmt_type = scanner.nextLine();
//
//            System.out.println("Enter customer amount: ");
//            double customer_amount = scanner.nextDouble();
//
//            System.out.println("Enter discount: ");
//            double discount = scanner.nextDouble();
//
//            // Perform payment and checkout
//            orderService.addDiscount(discount);
////            orderService.checkoutPay(customer_amount, pmt_type);
//
//
////            double balanceAmount = orderService.calculateBalancePay(customer_amount);
//            double balanceAmount = orderService.checkoutPay(customer_amount, pmt_type);
//
////            balance
//            System.out.println("Balance: " + balanceAmount);
//
//
//            System.out.println("Order completed!");
//
//        } catch (Exception ex) {
//            Logger.getLogger(OrderGUIService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    private final Gson gson = new Gson();
    private final OrderAddGUI gui;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson customGson;
    {
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            DateFormat df = new SimpleDateFormat("MMM dd, yyyy");

            @Override
            public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (ParseException e) {
                    Logger.getLogger(BatchController.class.getName()).log(Level.SEVERE, "Failed to parse date: " + json.getAsString(), e);
                    return null;
                }
            }
        });
        customGson = gsonBuilder.create();
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger LOGGER = Logger.getLogger(OrderGUIService.class.getName());

    public OrderGUIService(OrderAddGUI gui) {
        this.gui = gui;
    }

    public String createOrder() {
        Future<String> future = executorService.submit(() -> {
            try {

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/OrderController?action=createOrder"))
                        .POST(HttpRequest.BodyPublishers.ofString("{}"))  // Empty payload
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String orderSerial = customGson.fromJson(response.body(), String.class);
                    return orderSerial;  // Or do something else
                } else {
                    return "Failed to create order. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to create order.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create order.";
        }
    }

    public String addProduct(String productCode, String quantityText) {
        Future<String> future = executorService.submit(() -> {
            try {
                // Create payload for HTTP request
                String jsonPayload = customGson.toJson(Map.of("productCode", productCode, "quantity", quantityText));

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/OrderController?action=addProduct"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
//                    double totalBill = customGson.fromJson(response.body(), Double.class);
//                    gui.updateTotalBill("Total Bill: " + totalBill);
                    gui.appendOrderSummary("Product added to the order: " + productCode + " - Quantity: " + quantityText + "\n");
                    gui.clearProductInputs();
                    return "Product added successfully!";
                } else {
                    gui.appendOrderSummary("Failed to add product. Response code: " + response.statusCode());
                    return "Failed to add product.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                gui.appendOrderSummary("Failed to add product.");
                return "Failed to add product.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add product.";
        }
    }

    public String checkout(String paymentType, String customerAmountText, String discountText) {
        Future<String> future = executorService.submit(() -> {
            try {
                // Create payload for HTTP request
                String jsonPayload = customGson.toJson(Map.of("paymentType", paymentType, "customerAmount", customerAmountText, "discount", discountText));

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/OrderController?action=checkout"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    gui.appendOrderSummary("Checkout completed.\n");
                    return "Checkout completed successfully!";
                } else {
                    gui.appendOrderSummary("Failed to checkout. Response code: " + response.statusCode());
                    return "Failed to checkout.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                gui.appendOrderSummary("Failed to checkout.");
                return "Failed to checkout.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to checkout.";
        }
    }



    void update() {
        System.out.println("Not available");
    }

    void delete() {
        System.out.println("Not available");
    }

    void getAll() {
        System.out.println("Not available");
    }
}

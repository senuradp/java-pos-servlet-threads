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

    public String addProduct(String serial,String productCode, String quantityText) {
        Future<String> future = executorService.submit(() -> {
            try {
                // Create payload for HTTP request
                String jsonPayload = customGson.toJson(Map.of("serial", serial,"productCode", productCode, "quantity", quantityText));

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/OrderController?action=addProduct"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    double totalBill = customGson.fromJson(response.body(), Double.class);
                    gui.updateTotalBill("Total Bill: " + totalBill);
                    gui.appendOrderSummary("Product added to the order: " + productCode + " - Quantity: " + quantityText + "\n");
                    gui.clearProductInputs();
                    return "Product added successfully!";
                } else {
                    String errorMessage = response.body(); // Get the error message from the response
                    gui.appendOrderSummary("Failed to checkout. Error: " + errorMessage);
                    return "Failed to checkout. Error: " + errorMessage;
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

    public String checkout(String serial, String paymentType, String customerAmountText, String discountText) {
        Future<String> future = executorService.submit(() -> {
            try {
                // Create payload for HTTP request
                String jsonPayload = customGson.toJson(Map.of("serial", serial,"paymentType", paymentType, "customerAmount", customerAmountText, "discount", discountText));

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/OrderController?action=checkout"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    double balance = customGson.fromJson(response.body(), Double.class);
                    gui.appendOrderSummary("Checkout completed.\n" +
                            "Customer Amount: " + customerAmountText + "\n" +
                            "Discount: " + discountText + "\n" +
                            "Balance: " + balance + "\n");
                    return "Checkout completed successfully! Balance: " + balance;
                } else {
                    String errorMessage = response.body(); // Get the error message from the response
                    gui.appendOrderSummary("Failed to checkout. Error: " + errorMessage);
                    return "Failed to checkout. Error: " + errorMessage;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.menucommand;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.syos.pos.controller.BatchController;
import com.syos.pos.dto.BatchDTO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author senu2k
 */
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.nio.charset.StandardCharsets;

public class BatchGUIService {
    BatchController batchController = new BatchController();

    private final Gson gson = new Gson();
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
                    Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, "Failed to parse date: " + json.getAsString(), e);
                    return null;
                }
            }
        });
        customGson = gsonBuilder.create();
    }
//    private final ExecutorService executorService = Executors.newFixedThreadPool(5);  // 5 threads in the pool
// Use a cached thread pool instead of a fixed thread pool
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    public String add(String batchCode, String productCode, String expiryDate, String purchaseDate, double batchQty, double availableQty, boolean isSold) {
        Future<String> future = executorService.submit(() -> {
            BatchDTO batchDTO = new BatchDTO();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                batchDTO.setBatch_code(batchCode);
                batchDTO.setProduct_code(productCode);
                batchDTO.setBatch_qty(batchQty);
                batchDTO.setExpiry_date(dateFormat.parse(expiryDate));
                batchDTO.setPurchase_date(dateFormat.parse(purchaseDate));
                batchDTO.setAvailable_qty(availableQty);
                batchDTO.setIs_sold(isSold);

                // Convert the BatchDTO object to JSON
                String jsonPayload = customGson.toJson(batchDTO);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/BatchController?action=add"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return "Batch added successfully!";
                } else {
                    return "Failed to add batch. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to add batch.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add batch.";
        }
    }

    public List<BatchDTO> getAll() {
        List<BatchDTO> result = new ArrayList<>();

        Future<List<BatchDTO>> future = executorService.submit(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/BatchController?action=getAll"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // Convert JSON response to List<BatchDTO>
                    Type listType = new TypeToken<List<BatchDTO>>() {}.getType();
                    result.addAll(customGson.fromJson(response.body(), listType));
                } else {
                    Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, "HTTP request failed with response code: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, e);
            }

            return result;
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public String update(String batchCode, String productCode, String expiryDate, String purchaseDate, double batchQty, double availableQty, boolean isSold) {
        Future<String> future = executorService.submit(() -> {
            BatchDTO batchDTO = new BatchDTO();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                batchDTO.setBatch_code(batchCode);
                batchDTO.setProduct_code(productCode);
                batchDTO.setBatch_qty(batchQty);
                batchDTO.setExpiry_date(dateFormat.parse(expiryDate));
                batchDTO.setPurchase_date(dateFormat.parse(purchaseDate));
                batchDTO.setAvailable_qty(availableQty);
                batchDTO.setIs_sold(isSold);

                // Convert the BatchDTO object to JSON
                String jsonPayload = customGson.toJson(batchDTO);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/BatchController?action=update"))
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload)) // Change to PUT method
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return "Batch updated successfully!";
                } else {
                    return "Failed to update batch. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to update batch.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to update batch.";
        }
    }

    public String delete(String batchCode) {
        Future<String> future = executorService.submit(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/BatchController?action=delete&batchCode=" + batchCode))
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return "Batch deleted successfully!";
                } else if (response.statusCode() == 404) {  // Assuming 404 for "not found"
                    return "Batch not found.";
                } else {
                    return "Failed to delete batch. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to delete batch.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete batch.";
        }
    }

    //    void getAll() {
//        try {
//            BatchController batchController = new BatchController();
//            List<BatchDTO> batches = batchController.getAll();
//
//            if (!batches.isEmpty()) {
//                System.out.println("All Batches:");
//                for (BatchDTO batch : batches) {
//                    System.out.println("Batch Code: " + batch.getBatch_code());
//                    System.out.println("Product Code: " + batch.getProduct_code());
//                    System.out.println("Expiry Date: " + batch.getExpiry_date());
//                    System.out.println("Purchase Date: " + batch.getPurchase_date());
//                    System.out.println("Batch Quantity: " + batch.getBatch_qty());
//                    System.out.println("Available Quantity: " + batch.getAvailable_qty());
//                    System.out.println("Is Sold: " + batch.getIs_sold());
//                    System.out.println("-----------------------");
//                }
//            } else {
//                System.out.println("No batches found.");
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public String[] getByCode(String batchCode) {
        Future<String[]> future = executorService.submit(() -> {
            try {
                // Separate call to check if batch code exists
//                if (!batchController.checkBatchCodeExists(batchCode)) {
//                    return null;
//                }
                HttpClient existCheckClient = HttpClient.newHttpClient();
                HttpRequest existCheckRequest = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/BatchController?action=checkBatchCodeExists&batchCode=" + batchCode))
                        .GET()
                        .build();

                HttpResponse<String> existCheckResponse = existCheckClient.send(existCheckRequest, HttpResponse.BodyHandlers.ofString());

                if (existCheckResponse.statusCode() != 200 || !"true".equals(existCheckResponse.body())) {
                    return null;
                }

                // Main request to get batch details
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/BatchController?action=getByCode&batchCode=" + batchCode))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    BatchDTO batchDTO = customGson.fromJson(response.body(), BatchDTO.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String[] batchDetails = new String[6];
                    batchDetails[0] = batchDTO.getProduct_code();
                    batchDetails[1] = dateFormat.format(batchDTO.getExpiry_date());
                    batchDetails[2] = dateFormat.format(batchDTO.getPurchase_date());
                    batchDetails[3] = String.valueOf(batchDTO.getBatch_qty());
                    batchDetails[4] = String.valueOf(batchDTO.getAvailable_qty());
                    batchDetails[5] = String.valueOf(batchDTO.getIs_sold());

                    return batchDetails;

                } else if (response.statusCode() == 404) {
                    return null;
                } else {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public String[] getByCode(String batchCode) {
//        BatchController batchController = new BatchController();
//
//        // Check if the batch code exists
//        boolean batchCodeExists = false;
//        try {
//            batchCodeExists = batchController.checkBatchCodeExists(batchCode);
//        } catch (Exception ex) {
//            Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        if (batchCodeExists) {
//            // If the batch code exists, proceed to retrieve the batch details
//            BatchDTO batchDTO = null;
//            try {
//                batchDTO = batchController.getBatchDetails(batchCode);
//            } catch (Exception ex) {
//                Logger.getLogger(ProductGUIService.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            if (batchDTO != null) {
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                String[] batchDetails = new String[6];
//
//                // Populate the array with batch details
//                batchDetails[0] = batchDTO.getProduct_code();
//                batchDetails[1] = dateFormat.format(batchDTO.getExpiry_date());
//                batchDetails[2] = dateFormat.format(batchDTO.getPurchase_date());
//                batchDetails[3] = String.valueOf(batchDTO.getBatch_qty());
//                batchDetails[4] = String.valueOf(batchDTO.getAvailable_qty());
//                batchDetails[5] = String.valueOf(batchDTO.getIs_sold());
//
//                return batchDetails;
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }


}
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
import com.syos.pos.controller.BatchController;
import com.syos.pos.dto.BatchDTO;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author senu2k
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchGUIService {

    private final Gson gson = new Gson();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);  // 5 threads in the pool

    public String add(String batchCode, String productCode, String expiryDate, String purchaseDate, double batchQty, double availableQty, boolean isSold) {
        BatchController batchController = new BatchController();
        BatchDTO batchDTO = new BatchDTO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Check if the batch code already exists
        boolean batchCodeExists = false;
        try {
            batchCodeExists = batchController.checkBatchCodeExists(batchCode);
        } catch (Exception ex) {
            Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (batchCodeExists) {
                return "Batch code already exists!";
            } else {
                batchDTO.setBatch_code(batchCode);
                batchDTO.setProduct_code(productCode);
                batchDTO.setBatch_qty(batchQty);
                batchDTO.setExpiry_date(dateFormat.parse(expiryDate));
                batchDTO.setPurchase_date(dateFormat.parse(purchaseDate));
                batchDTO.setAvailable_qty(availableQty);
                batchDTO.setIs_sold(isSold);

                batchController.addBatch(batchDTO);
                return "Batch added successfully!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add batch.";
        }
    }

    public String update(String batchCode, String productCode, String expiryDate, String purchaseDate, double batchQty, double availableQty, boolean isSold) {
        BatchController batchController = new BatchController();
        BatchDTO batchDTO = new BatchDTO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Check if the batch code already exists
        boolean batchCodeExists = false;
        try {
            batchCodeExists = batchController.checkBatchCodeExists(batchCode);
        } catch (Exception ex) {
            Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (batchCodeExists) {
                batchDTO.setBatch_code(batchCode);
                batchDTO.setProduct_code(productCode);
                batchDTO.setBatch_qty(batchQty);
                batchDTO.setExpiry_date(dateFormat.parse(expiryDate));
                batchDTO.setPurchase_date(dateFormat.parse(purchaseDate));
                batchDTO.setAvailable_qty(availableQty);
                batchDTO.setIs_sold(isSold);

                batchController.updateBatch(batchDTO);
                return "Batch updated successfully!";
            } else {
                return "Batch code does not exist!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to updated batch.";
        }
    }

    public String delete(String batchCode) {
        try {
            BatchController batchController = new BatchController();
            boolean batchCodeExists = batchController.checkBatchCodeExists(batchCode);

            if (batchCodeExists) {
                try {
                    batchController.deleteBatch(batchCode);
                    return "Batch deleted successfully!";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Failed to delete batch.";
                }
            } else {
                return "Batch not found.";
            }
        } catch (Exception ex) {
            Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, ex);
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
    public List<BatchDTO> getAll() {
        List<BatchDTO> result = new ArrayList<>();
        Runnable task = () -> {
            try {
                // Replace the URL with the correct URL for your BatchController servlet
                URL url = new URL("http://localhost:8080/syosposclientserver/BatchController?action=getAll");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder responseText = new StringBuilder();

                    while ((line = in.readLine()) != null) {
                        responseText.append(line);
                    }
                    in.close();

                    // Convert JSON response to List<BatchDTO>
                    Type listType = new TypeToken<List<BatchDTO>>() {}.getType();
                    result.addAll(customGson.fromJson(responseText.toString(), listType));
                } else {
                    // Handle the error response if needed
                    Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, "HTTP request failed with response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, e);
            }
        };

        executorService.submit(task);
        return result;  // Note: This will likely return before task is completed
    }

    public String[] getByCode(String batchCode) {
        BatchController batchController = new BatchController();

        // Check if the batch code exists
        boolean batchCodeExists = false;
        try {
            batchCodeExists = batchController.checkBatchCodeExists(batchCode);
        } catch (Exception ex) {
            Logger.getLogger(BatchGUIService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (batchCodeExists) {
            // If the batch code exists, proceed to retrieve the batch details
            BatchDTO batchDTO = null;
            try {
                batchDTO = batchController.getBatchDetails(batchCode);
            } catch (Exception ex) {
                Logger.getLogger(ProductGUIService.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (batchDTO != null) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String[] batchDetails = new String[6];

                // Populate the array with batch details
                batchDetails[0] = batchDTO.getProduct_code();
                batchDetails[1] = dateFormat.format(batchDTO.getExpiry_date());
                batchDetails[2] = dateFormat.format(batchDTO.getPurchase_date());
                batchDetails[3] = String.valueOf(batchDTO.getBatch_qty());
                batchDetails[4] = String.valueOf(batchDTO.getAvailable_qty());
                batchDetails[5] = String.valueOf(batchDTO.getIs_sold());

                return batchDetails;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


}
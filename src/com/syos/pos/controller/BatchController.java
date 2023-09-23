/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.controller;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.syos.pos.core.ServiceFactory;
import com.syos.pos.dto.BatchDTO;
import com.syos.pos.service.dao.IBatchService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

//@WebServlet("/BatchController") // Define the servlet mapping here
public class BatchController extends HttpServlet {

    private static final IBatchService batchService = (IBatchService) ServiceFactory.getInstance().getDAO(ServiceFactory.ServiceType.BATCH);

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

    @Override
    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getAll".equals(action)) {
            try {
                List<BatchDTO> allBatches = batchService.getAll();
                String jsonBatches = customGson.toJson(allBatches);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try (PrintWriter out = response.getWriter()) {
                    out.print(jsonBatches);
                }
            } catch (Exception ex) {
                Logger.getLogger(BatchController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        else if get by id
//        else if ("getById".equals(action)) {
//            // Code to handle getting a single batch by its ID
//            String batchId = request.getParameter("batchId");
//            BatchDTO batch = batchService.getById(batchId);
//            String jsonBatch = customGson.toJson(batch);
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            try (PrintWriter out = response.getWriter()) {
//                out.print(jsonBatch);
//            }
//        }
//        else if ("getExpiring".equals(action)) {
//            // Code to handle getting expiring batches
//            List<BatchDTO> expiringBatches = batchService.getExpiringBatches();
//            String jsonExpiringBatches = customGson.toJson(expiringBatches);
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            try (PrintWriter out = response.getWriter()) {
//                out.print(jsonExpiringBatches);
//            }
//        }
        else {
            // Default case: if the action is not recognized
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid action");
        }
    }

    public  boolean addBatch(BatchDTO batchDTO){
        return batchService.add(batchDTO);
    }

    public  boolean updateBatch(BatchDTO batchDTO){
        return batchService.update(batchDTO);
    }

    public  boolean deleteBatch(String code) throws Exception{
        return batchService.delete(code);
    }

//    public  List<BatchDTO> getAll() throws Exception{
//        // call the server and get the json response and convet it to a batchdto list
//        return batchService.getAll();
//    }

    // check batch code exists
    public  boolean checkBatchCodeExists(String batch_code) throws Exception{
        return batchService.checkBatchCodeExists(batch_code);
    }

    // get batch details
    public  BatchDTO getBatchDetails(String batch_code) throws Exception{
        return batchService.getBatchDetails(batch_code);
    }

    // udpate batch qty
    // public static boolean updateBatchQty(String product_code, double qty) throws Exception{
    //     return batchService.updateBatchQty(product_code, qty);
    // }

    public List<BatchDTO> getExpiringBatchDetails(String product_code){
        return batchService.getExpiringBatchDetails(product_code);
    }

}
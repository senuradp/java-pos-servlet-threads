/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.controller;

import com.syos.pos.core.ServiceFactory;
import com.syos.pos.dto.ProductDTO;
import com.syos.pos.dto.ShelfDTO;
import com.syos.pos.service.dao.IShelfService;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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

/**
 *
 * @author senu2k
 */
public class ShelfController extends HttpServlet {

    private static final IShelfService shelfService = (IShelfService)ServiceFactory.getInstance().getDAO(ServiceFactory.ServiceType.SHELF);

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

    private void sendJsonResponse(HttpServletResponse response, Object result) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(customGson.toJson(result));
    }

//    public static boolean addItem(ShelfDTO shelfDTO){
//        return shelfService.add(shelfDTO);
//    }
    // restock
//    public boolean reStockShelf(String product_code, double restock_qty) throws Exception{
//        return shelfService.reStockShelf(product_code, restock_qty);
//    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if ("add".equals(action)) {
                StringBuilder jsonBuffer = new StringBuilder();
                String line;
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
                String jsonData = jsonBuffer.toString();

                try {
                    ShelfDTO newShelf = customGson.fromJson(jsonData, ShelfDTO.class);

                    // Directly using shelfService.add() here instead of calling addItem()
                    boolean result = shelfService.add(newShelf);
                    out.print(customGson.toJson(result));
                } catch (Exception ex) {
                    Logger.getLogger(ShelfController.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("An error occurred while processing the request.");
                }
            }
            else if ("restock".equals(action)) {
                String productCode = request.getParameter("product_code");
                double restockQty = Double.parseDouble(request.getParameter("restock_qty"));

                try {
                    boolean result = shelfService.reStockShelf(productCode, restockQty);
                    out.print(customGson.toJson(result));
                } catch (Exception ex) {
                    Logger.getLogger(ShelfController.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("An error occurred while processing the request.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Invalid action");
            }
        }
    }


    //    public static boolean updateItem(ShelfDTO shelfDTO){
//        return shelfService.update(shelfDTO);
//    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("update".equals(action)) {
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            String jsonData = jsonBuffer.toString();

            try {
                ShelfDTO updatedProduct = customGson.fromJson(jsonData, ShelfDTO.class);

                // Directly using productService.update() here instead of calling updateItem()
                boolean result = shelfService.update(updatedProduct);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(customGson.toJson(result));
            } catch (Exception ex) {
                Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("An error occurred while processing the request.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid action");
        }
    }

//    public static boolean deleteItem(String code) throws Exception{
//       return shelfService.delete(code);
//    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("deleteItem".equals(action)) {
                String shelfCode = request.getParameter("shelfCode");
                boolean result = shelfService.delete(shelfCode);
                sendJsonResponse(response, result);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid action");
            }
        } catch (Exception ex) {
            Logger.getLogger(ShelfController.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing the request.");
        }
    }

//    public static List<ShelfDTO> getAll() throws Exception{
//        return shelfService.getAll();
//    }
//    // get shelf capacity
//    public double getShelfCapacity(String shelf_code) throws Exception{
//        return shelfService.getShelfCapacity(shelf_code);
//    }
//
//    // get batch details
//    public  ShelfDTO getShelfDetails(String shelf_code) throws Exception{
//        return shelfService.getShelfDetails(shelf_code);
//    }
//
//    public  boolean checkShelfCodeExists(String shelf_code) throws Exception{
//        return shelfService.checkShelfCodeExists(shelf_code);
//    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if ("getAll".equals(action)) {
                List<ShelfDTO> allShelves = shelfService.getAll();
                out.print(customGson.toJson(allShelves));
            } else if ("getShelfDetails".equals(action)) {
                String shelfCode = request.getParameter("shelfCode");
                ShelfDTO shelf = shelfService.getShelfDetails(shelfCode);
                out.print(customGson.toJson(shelf));
            } else if ("getShelfCapacity".equals(action)) {
                String shelfCode = request.getParameter("shelfCode");
                double capacity = shelfService.getShelfCapacity(shelfCode);
                out.print(customGson.toJson(capacity));
            } else if ("checkShelfCodeExists".equals(action)) {
                String shelfCode = request.getParameter("shelfCode");
                boolean exists = shelfService.checkShelfCodeExists(shelfCode);
                out.print(customGson.toJson(exists));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Invalid action");
            }
        } catch (Exception ex) {
            Logger.getLogger(ShelfController.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing the request.");
        }
    }



    
}

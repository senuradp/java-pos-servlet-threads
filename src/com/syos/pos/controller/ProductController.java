/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.controller;

import com.syos.pos.core.ServiceFactory;
import com.syos.pos.dto.ProductDTO;
import com.syos.pos.service.dao.IProductService;
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
public class ProductController extends HttpServlet {
    
    private final IProductService productService = (IProductService)ServiceFactory.getInstance().getDAO(ServiceFactory.ServiceType.PRODUCT);

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

//    public  List<ProductDTO> getAll() throws Exception{
//        return productService.getAll();
//    }

//    public  ProductDTO getProductByCode(String code)throws Exception{
//        return productService.getProductByCode(code);
//    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if ("getAll".equals(action)) {
                try {
                    List<ProductDTO> allProducts = productService.getAll();
                    String jsonProducts = customGson.toJson(allProducts);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    try{
                        out.print(jsonProducts);
                    } catch (Exception ex) {
                        Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("An error occurred while processing the request.");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if ("getByCode".equals(action)) {
                String productCode = request.getParameter("productCode"); // Note: Make sure the parameter name matches what you send from the frontend.
                try {
                    ProductDTO product = productService.getProductByCode(productCode);
                    String jsonProduct = customGson.toJson(product);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    try{
                        out.print(jsonProduct);
                    } catch (Exception ex) {
                        Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("An error occurred while processing the request.");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if ("checkCodeExists".equals(action)) {
                String code = request.getParameter("product_code");

                try {
                    boolean result = productService.checkProductCodeExists(code);
                    out.print(customGson.toJson(result));
                } catch (Exception ex) {
                    Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("An error occurred while processing the request.");
                }
            }
            else {
                // Default case: if the action is not recognized
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid action");
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing the request.");
        }
    }


//    public boolean addItem(ProductDTO productDTO){
//        return productService.add(productDTO);
//    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            String jsonData = jsonBuffer.toString();

            try {
                ProductDTO newProduct = customGson.fromJson(jsonData, ProductDTO.class);

                // Directly using productService.add() here instead of calling addItem()
                boolean result = productService.add(newProduct);

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

//    public  boolean updateItem(ProductDTO productDTO){
//        return productService.update(productDTO);
//    }
    @Override
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
                ProductDTO updatedProduct = customGson.fromJson(jsonData, ProductDTO.class);

                // Directly using productService.update() here instead of calling updateItem()
                boolean result = productService.update(updatedProduct);

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


//    public  boolean deleteItem(String code) throws Exception{
//       return productService.delete(code);
//    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String productCode = request.getParameter("productCode");  // Replace 'productCode' with the parameter name you are using

            try {
                // Directly using productService.delete() here instead of calling deleteItem()
                boolean result = productService.delete(productCode);

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


//    public  boolean checkProductCodeExists(String code) {
//        return productService.checkProductCodeExists(code);
//    }
    
}

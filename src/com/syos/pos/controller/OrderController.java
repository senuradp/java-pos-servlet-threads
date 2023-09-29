package com.syos.pos.controller;

import com.google.gson.*;
import com.syos.pos.menucommand.OrderGUIService;
import com.syos.pos.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderController  extends HttpServlet {

    private OrderService orderService = OrderService.getInstance();
//    private OrderService orderService;

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

    private OrderGUIService service;

    @Override
    public void init() throws ServletException {
        service = new OrderGUIService(null);  // GUI is not used in servlet context
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
//        String action = (String) request.getAttribute("action");
        // Get the current session from the request
//        HttpSession session = request.getSession(true);

        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if ("addProduct".equals(action)) {
                StringBuilder jsonBuffer = new StringBuilder();
                String line;
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
                String jsonData = jsonBuffer.toString();

//                if(session.getAttribute("orderSerial") == null) {
//                    String orderSerial = orderService.createOrder();
//                    session.setAttribute("orderSerial", orderSerial);
//                }

                try {
                    // Parse JSON directly without DTO
                    String productCode = customGson.fromJson(jsonData, JsonObject.class).get("productCode").getAsString();
                    double quantity = customGson.fromJson(jsonData, JsonObject.class).get("quantity").getAsDouble();

                    double result = orderService.addOrderProduct(productCode, quantity);
                    out.print(customGson.toJson(result));
                } catch (Exception ex) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("An error occurred while processing the request.");
                }
            }
            else if ("checkout".equals(action)) {
                StringBuilder jsonBuffer = new StringBuilder();
                String line;
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    jsonBuffer.append(line);
                }
                String jsonData = jsonBuffer.toString();
                try {
                    JsonObject json = customGson.fromJson(jsonData, JsonObject.class);
                    String paymentType = json.get("paymentType").getAsString();
                    double customerAmount = json.get("customerAmount").getAsDouble();
                    double discount = json.get("discount").getAsDouble();

                    orderService.addDiscount(discount);
                    double balanceAmount = orderService.checkoutPay(customerAmount, paymentType);
//                    session.invalidate();
                    out.print(customGson.toJson(balanceAmount));
                } catch (Exception ex) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("An error occurred while processing the request.");
                }
            }
            else if ("createOrder".equals(action)) {
                try {
                    String orderSerial = orderService.createOrder();
                    out.print(customGson.toJson(orderSerial));
                } catch (Exception ex) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("An error occurred while creating the order.");
                }
            }
            else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Invalid action");
            }
        }
    }

}


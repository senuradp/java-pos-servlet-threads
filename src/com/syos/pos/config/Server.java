//package com.syos.pos.config;
//
//import com.sun.net.httpserver.HttpServer;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpExchange;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.util.List;
//import com.google.gson.Gson;
//import com.syos.pos.controller.BatchController;
//import com.syos.pos.dto.BatchDTO;
//
//public class Server {
//    public static void main(String[] args) throws IOException {
//        // Create an HTTP server on localhost, port 8080
//        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//
//        // Create a context for handling requests at the "/batches" path
//        server.createContext("/batches", new BatchHandler());
//
//        // Start the server
//        server.start();
//        System.out.println("Server started on http://localhost:8080");
//    }
//
//    static class BatchHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            // Create a BatchController instance to access your getAll method
//            BatchController batchController = new BatchController();
//
//            // Call the getAll method to fetch batch data
//            List<BatchDTO> batches = batchController.getAll();
//
//            // Convert the list of batches to JSON
//            Gson gson = new Gson();
//            String response = gson.toJson(batches);
//
//            // Set response headers
//            exchange.getResponseHeaders().set("Content-Type", "application/json");
//            exchange.sendResponseHeaders(200, response.length());
//
//            // Write the JSON response
//            try (OutputStream os = exchange.getResponseBody()) {
//                os.write(response.getBytes());
//            }
//        }
//    }
//}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.menucommand;

import com.google.gson.reflect.TypeToken;
import com.syos.pos.controller.ProductController;
import com.syos.pos.dto.ProductDTO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.*;

import java.util.ArrayList;

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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author senu2k
 */
public class ProductGUIService {

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

    private final ExecutorService executorService = Executors.newCachedThreadPool();

//    public String add(String productCode, String productName, double productPrice) {
//        ProductController productController = new ProductController();
//        ProductDTO productDTO = new ProductDTO();
//
//        // Check if the product code already exists
//        boolean productCodeExists = productController.checkProductCodeExists(productCode);
//
//        try {
//            if (productCodeExists) {
//                return "Product code already exists!";
//            } else {
//                productDTO.setProduct_code(productCode);
//                productDTO.setProduct_name(productName);
//                productDTO.setProduct_price(productPrice);
//
//                productController.addItem(productDTO);
//                return "Product added successfully!";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to add product.";
//        }
//    }
    public String add(String productCode, String productName, double productPrice) {
        Future<String> future = executorService.submit(() -> {
            try {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setProduct_code(productCode);
                productDTO.setProduct_name(productName);
                productDTO.setProduct_price(productPrice);

                // Convert the ProductDTO object to JSON
                String jsonPayload = customGson.toJson(productDTO);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ProductController?action=add"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return "Product added successfully!";
                } else {
                    return "Failed to add product. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
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

//    public String update(String productCode, String productName, double productPrice) {
//       ProductController productController = new ProductController();
//        ProductDTO productDTO = new ProductDTO();
//
//        // Check if the product code already exists
//        boolean productCodeExists = productController.checkProductCodeExists(productCode);
//
//        try {
//            if (productCodeExists) {
//                productDTO.setProduct_code(productCode);
//                productDTO.setProduct_name(productName);
//                productDTO.setProduct_price(productPrice);
//
//                productController.updateItem(productDTO);
//                return "Product updated successfully!";
//            } else {
//                return "Product code does not exist!";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to updated product.";
//        }
//    }
    public String update(String productCode, String productName, double productPrice) {
        Future<String> future = executorService.submit(() -> {
            try {
                // Create ProductDTO object and populate its fields
                ProductDTO productDTO = new ProductDTO();
                productDTO.setProduct_code(productCode);
                productDTO.setProduct_name(productName);
                productDTO.setProduct_price(productPrice);

                // Convert the ProductDTO object to JSON
                String jsonPayload = customGson.toJson(productDTO);

                // Create and send the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ProductController?action=update"))
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the HTTP response status
                if (response.statusCode() == 200) {
                    return "Product updated successfully!";
                } else {
                    return "Failed to update product. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to update product.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to update product.";
        }
    }

//    public String delete(String productCode) {
//        try {
//            ProductController productController = new ProductController();
//            boolean productCodeExists = productController.checkProductCodeExists(productCode);
//
//            if (productCodeExists) {
//                try {
//                    productController.deleteItem(productCode);
//                    return "Product deleted successfully!";
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return "Failed to delete product.";
//                }
//            } else {
//                return "Product not found.";
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(ProductGUIService.class.getName()).log(Level.SEVERE, null, ex);
//            return "Failed to delete product.";
//        }
//    }
    public String delete(String productCode) {
        Future<String> future = executorService.submit(() -> {
            try {
                // Prepare the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/yourContextPath/ProductController?action=delete&productCode=" + productCode))
                        .DELETE() // Use the DELETE method
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the HTTP response status
                if (response.statusCode() == 200) {
                    return "Product deleted successfully!";
                } else {
                    return "Failed to delete product. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to delete product.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete product.";
        }
    }


//    void getAll() {
//        ProductController productController = new ProductController();
//
//        try {
//            List<ProductDTO> products = productController.getAll();
//
//            if (products.isEmpty()) {
//                System.out.println("No products found!");
//                return;
//            }
//
//            for (ProductDTO product : products) {
//                System.out.println("Product code: " + product.getProduct_code());
//                System.out.println("Product name: " + product.getProduct_name());
//                System.out.println("Product price: " + product.getProduct_price());
//                System.out.println("--------------------------------");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    void getAll() {
        Future<List<ProductDTO>> future = executorService.submit(() -> {
            try {
                // Prepare the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ProductController?action=getAll"))
                        .GET() // Use the GET method
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the HTTP response status
                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    Type productListType = new TypeToken<ArrayList<ProductDTO>>() {}.getType();

                    // Deserialize JSON response to List<ProductDTO>
                    List<ProductDTO> products = new Gson().fromJson(responseBody, productListType);
                    return products;
                } else {
                    throw new Exception("Failed to get products. Response code: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to get products.", e);
            }
        });

        try {
            List<ProductDTO> products = future.get();
            if (products.isEmpty()) {
                System.out.println("No products found!");
            } else {
                for (ProductDTO product : products) {
                    System.out.println("Product code: " + product.getProduct_code());
                    System.out.println("Product name: " + product.getProduct_name());
                    System.out.println("Product price: " + product.getProduct_price());
                    System.out.println("--------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public String[] getByCode(String productCode) {
//        ProductController productController = new ProductController();
//
//        // Check if the product code exists
//        boolean productCodeExists = productController.checkProductCodeExists(productCode);
//
//        if (productCodeExists) {
//            // If the product code exists, proceed to retrieve the product details
//            ProductDTO productDTO = null;
//            try {
//                productDTO = productController.getProductByCode(productCode);
//            } catch (Exception ex) {
//                Logger.getLogger(ProductGUIService.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            if (productDTO != null) {
//                // Convert the product details to an array of strings
//                String[] productDetails = new String[2];
//                productDetails[0] = productDTO.getProduct_name();
//                productDetails[1] = String.valueOf(productDTO.getProduct_price());
//                return productDetails;
//            } else {
//                return null; // Return a specific message
//            }
//        } else {
//
//            return null;
//        }
//    }
    public String[] getByCode(String productCode) {
        Future<String[]> future = executorService.submit(() -> {
            try {
                // Prepare the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ProductController?action=getByCode&productCode="+productCode))
                        .GET() // Use the GET method
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the HTTP response status
                if (response.statusCode() == 200) {
                    String responseBody = response.body();

                    // Deserialize JSON response to ProductDTO
                    ProductDTO product = new Gson().fromJson(responseBody, ProductDTO.class);

                    String[] productDetails = new String[2];
                    productDetails[0] = product.getProduct_name();
                    productDetails[1] = String.valueOf(product.getProduct_price());
                    return productDetails;
                } else {
                    throw new Exception("Failed to get product by code. Response code: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to get product by code.", e);
            }
        });

        try {
            String[] productDetails = future.get();
            return productDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkProductCodeExists(String productCode) {
        Future<Boolean> future = executorService.submit(() -> {
            try {
                // Prepare the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ProductController?action=checkCodeExists&productCode=" + productCode))
                        .GET() // Use the GET method
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the HTTP response status
                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    return customGson.fromJson(responseBody, Boolean.class); // Use customGson for deserialization
                } else {
                    throw new Exception("Failed to check product code existence. Response code: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to check product code existence.", e);
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

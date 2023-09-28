/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.menucommand;

import com.syos.pos.controller.ShelfController;
import com.syos.pos.dto.ProductDTO;
import com.syos.pos.dto.ShelfDTO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

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
public class ShelfGUIService {

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
    // Define a logger for the ShelfGUIService class
    private static final Logger LOGGER = Logger.getLogger(ShelfGUIService.class.getName());

//    public String add(String shelfCode, String productCode, double capacity, double availableQty) {
//        ShelfController shelfController = new ShelfController();
//        ShelfDTO shelfDTO = new ShelfDTO();
//
//        try {
//            // Check if the shelf code already exists
//            boolean shelfCodeExists = shelfController.checkShelfCodeExists(shelfCode);
//
//            if (shelfCodeExists) {
//                // Log a warning if the shelf code already exists
//                LOGGER.warning("Shelf code already exists: " + shelfCode);
//                return "Shelf code already exists!";
//            } else {
//                if (availableQty <= capacity) {
//                    shelfDTO.setShelf_code(shelfCode);
//                    shelfDTO.setProduct_code(productCode);
//                    shelfDTO.setCapacity(capacity);
//                    shelfDTO.setAvailable_qty(availableQty);
//
//                    shelfController.addItem(shelfDTO);
//                    LOGGER.info("Shelf added successfully: " + shelfCode);
//                    return "Shelf added successfully!";
//                } else {
//                    // Log an error if available quantity exceeds capacity
//                    LOGGER.severe("Available quantity exceeds capacity for shelf: " + shelfCode);
//                    return "Available quantity exceeds capacity. Please enter a valid quantity.";
//                }
//            }
//        } catch (Exception e) {
//            // Log the exception and error message
//            LOGGER.log(Level.SEVERE, "Failed to add shelf: " + e.getMessage(), e);
//            return "Failed to add shelf.";
//        }
//    }
    public String add(String shelfCode, String productCode, double capacity, double availableQty) {
        Future<String> future = executorService.submit(() -> {
            try {
                ShelfDTO shelfDTO = new ShelfDTO();
                shelfDTO.setShelf_code(shelfCode);
                shelfDTO.setProduct_code(productCode);
                shelfDTO.setCapacity(capacity);
                shelfDTO.setAvailable_qty(availableQty);

                // Convert the ShelfDTO object to JSON
                String jsonPayload = customGson.toJson(shelfDTO);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ShelfController?action=add"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return "Shelf added successfully!";
                } else {
                    return "Failed to add shelf. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to add shelf.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add shelf.";
        }
    }

    //    public String update(String shelfCode, String productCode, double capacity, double availableQty) {
//        ShelfController shelfController = new ShelfController();
//        ShelfDTO shelfDTO = new ShelfDTO();
//
//        try {
//            // Check if the shelf code already exists
//            boolean shelfCodeExists = shelfController.checkShelfCodeExists(shelfCode);
//
//            if (shelfCodeExists) {
//                if (availableQty <= capacity) {
//                    shelfDTO.setShelf_code(shelfCode);
//                    shelfDTO.setProduct_code(productCode);
//                    shelfDTO.setCapacity(capacity);
//                    shelfDTO.setAvailable_qty(availableQty);
//
//                    shelfController.updateItem(shelfDTO);
//                    LOGGER.info("Shelf added successfully: " + shelfCode);
//                    return "Shelf added successfully!";
//                } else {
//                    // Log an error if available quantity exceeds capacity
//                    LOGGER.severe("Available quantity exceeds capacity for shelf: " + shelfCode);
//                    return "Available quantity exceeds capacity. Please enter a valid quantity.";
//                }
//            } else {
//                LOGGER.warning("Shelf code does not exist: " + shelfCode);
//                return "Shelf code does not exist!";
//            }
//        } catch (Exception e) {
//            // Log the exception and error message
//            LOGGER.log(Level.SEVERE, "Failed to add shelf: " + e.getMessage(), e);
//            return "Failed to add shelf.";
//        }
//    }
    public String update(String shelfCode, String productCode, double capacity, double availableQty) {
        Future<String> future = executorService.submit(() -> {
            try {
                ShelfDTO shelfDTO = new ShelfDTO();
                shelfDTO.setShelf_code(shelfCode);
                shelfDTO.setProduct_code(productCode);
                shelfDTO.setCapacity(capacity);
                shelfDTO.setAvailable_qty(availableQty);

                // Convert the ShelfDTO object to JSON
                String jsonPayload = customGson.toJson(shelfDTO);

                // Prepare the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ShelfController?action=update"))
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the HTTP response status
                if (response.statusCode() == 200) {
                    return "Shelf updated successfully!";
                } else {
                    LOGGER.severe("Failed to update shelf. Response code: " + response.statusCode());
                    return "Failed to update shelf. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to update shelf: " + e.getMessage(), e);
                return "Failed to update shelf.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update shelf: " + e.getMessage(), e);
            return "Failed to update shelf.";
        }
    }

//    public String delete(String shelfCode) {
//        try {
//            ShelfController shelfController = new ShelfController();
//            boolean shelfCodeExists = shelfController.checkShelfCodeExists(shelfCode);
//
//            if (shelfCodeExists) {
//                try {
//                    shelfController.deleteItem(shelfCode);
//                    return "Shelf deleted successfully!";
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return "Failed to delete shelf.";
//                }
//            } else {
//                return "Shelf not found.";
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, ex);
//            return "Failed to delete shelf.";
//        }
//    }
    public String delete(String shelfCode) {
        Future<String> future = executorService.submit(() -> {
            try {
                // Convert the shelfCode to JSON
                String jsonPayload = customGson.toJson(Map.of("shelfCode", shelfCode));

                // Prepare the HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ShelfController?action=delete"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the HTTP response status
                if (response.statusCode() == 200) {
                    return "Shelf deleted successfully!";
                } else {
                    LOGGER.severe("Failed to delete shelf. Response code: " + response.statusCode());
                    return "Failed to delete shelf. Response code: " + response.statusCode();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to delete shelf: " + e.getMessage(), e);
                return "Failed to delete shelf.";
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete shelf: " + e.getMessage(), e);
            return "Failed to delete shelf.";
        }
    }

//    void getAll() {
//        try {
//            ShelfController shelfController = new ShelfController();
//            List<ShelfDTO> shelves = shelfController.getAll();
//
//            if (!shelves.isEmpty()) {
//                System.out.println("All Shelves:");
//                for (ShelfDTO shelf : shelves) {
//                    System.out.println("Shelf Code: " + shelf.getShelf_code());
//                    System.out.println("Product Code: " + shelf.getProduct_code());
//                    System.out.println("Capacity: " + shelf.getCapacity());
//                    System.out.println("Available Quantity: " + shelf.getAvailable_qty());
//                    System.out.println("-----------------------");
//                }
//            } else {
//                System.out.println("No shelves found.");
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    void getAll() {
        Future<Void> future = executorService.submit(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ShelfController?action=getAll"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Type listType = new TypeToken<List<ShelfDTO>>() {}.getType();
                    List<ShelfDTO> shelves = customGson.fromJson(response.body(), listType);

                    if (shelves != null && !shelves.isEmpty()) {
                        System.out.println("All Shelves:");
                        for (ShelfDTO shelf : shelves) {
                            System.out.println("Shelf Code: " + shelf.getShelf_code());
                            System.out.println("Product Code: " + shelf.getProduct_code());
                            System.out.println("Capacity: " + shelf.getCapacity());
                            System.out.println("Available Quantity: " + shelf.getAvailable_qty());
                            System.out.println("-----------------------");
                        }
                    } else {
                        System.out.println("No shelves found.");
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, e);
            }
            return null;
        });

        try {
            future.get();
        } catch (Exception e) {
            Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, e);
        }
    }

//     public String[] getByCode(String shelfCode) {
//        ShelfController shelfController = new ShelfController();
//
//        // Check if the shelf code exists
//        boolean shelfCodeExists = false;
//        try {
//            shelfCodeExists = shelfController.checkShelfCodeExists(shelfCode);
//        } catch (Exception ex) {
//            Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        if (shelfCodeExists) {
//            // If the shelf code exists, proceed to retrieve the shelf details
//            ShelfDTO shelfDTO = null;
//            try {
//                shelfDTO = shelfController.getShelfDetails(shelfCode);
//            } catch (Exception ex) {
//                Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            if (shelfDTO != null) {
//                String[] shelfDetails = new String[3];
//
//                // Populate the array with shelf details
//                shelfDetails[0] = shelfDTO.getShelf_code();
//                shelfDetails[1] = String.valueOf(shelfDTO.getCapacity());
//                shelfDetails[2] = String.valueOf(shelfDTO.getAvailable_qty());
//
//                return shelfDetails;
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
    public String[] getByCode(String shelfCode) {
        Future<String[]> future = executorService.submit(() -> {
            try {
                String jsonPayload = customGson.toJson(Map.of("shelfCode", shelfCode));
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ShelfController?action=getShelfDetails&shelfCode="+shelfCode))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ShelfDTO shelf = customGson.fromJson(response.body(), ShelfDTO.class);

                    if (shelf != null) {
                        String[] shelfDetails = new String[3];
                        shelfDetails[0] = shelf.getProduct_code();
                        shelfDetails[1] = String.valueOf(shelf.getCapacity());
                        shelfDetails[2] = String.valueOf(shelf.getAvailable_qty());
                        return shelfDetails;
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, e);
            }
            return null;
        });

        try {
            return future.get();
        } catch (Exception e) {
            Logger.getLogger(ShelfGUIService.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public boolean reStockShelf(String productCode, double quantity) {
        Future<Boolean> future = executorService.submit(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/syosposclientserver/ShelfController?action=restock&product_code=" + productCode + "&restock_qty=" + quantity))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    return customGson.fromJson(responseBody, Boolean.class);
                } else {
                    throw new Exception("Failed to restock shelf. Response code: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to restock shelf.", e);
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

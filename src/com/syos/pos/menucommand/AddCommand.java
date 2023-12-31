/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.menucommand;

import com.syos.pos.gui.*;

/**
 *
 * @author senu2k
 */
public class AddCommand implements Command {
    private final BatchGUIService batchService;
    private final ProductGUIService productService;
    private final ShelfGUIService shelfService;
    private final OrderGUIService orderService;
    String entity; // package-private access

    public AddCommand(String entity, BatchGUIService batchService, ProductGUIService productService, ShelfGUIService shelfService, OrderGUIService orderService) {
        this.entity = entity;
        this.batchService = batchService;
        this.productService = productService;
        this.shelfService = shelfService;
        this.orderService = orderService;
    }

    @Override
    public void execute() {
        // Check the entity and open the corresponding GUI
        switch (entity.toLowerCase()) {
            case "batch":
                BatchAddGUI batchAddGUI = new BatchAddGUI(batchService);
                batchAddGUI.setVisible(true);
                break;
            case "product":
                ProductAddGUI productAddGUI = new ProductAddGUI(productService);
                productAddGUI.setVisible(true);
                break;
            case "shelf":
                ShelfAddGUI shelfAddGUI = new ShelfAddGUI(shelfService);
                shelfAddGUI.setVisible(true);
                break;
            case "order":
                // Create a new batch header for this order
                String orderSerial = orderService.createOrder();
                // Do something with the orderSerial, perhaps pass it to the OrderAddGUI or log it
                System.out.println("Created a new order with serial: " + orderSerial);
                OrderAddGUI orderAddGUI = new OrderAddGUI(orderSerial);
                orderAddGUI.setVisible(true);

                break;
            default:
                System.out.println("Invalid entity!");
                break;
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.menucommand;

/**
 *
 * @author senu2k
 */
public class GetAllCommand implements Command {
    private final BatchGUIService batchService;
    private final ProductGUIService productService;
    private final ShelfGUIService shelfService;
    private final OrderServiceMenu orderService;
    String entity; // package-private access

    GetAllCommand(BatchGUIService batchService, ProductGUIService productService, ShelfGUIService shelfService, OrderServiceMenu orderService) {
        this.batchService = batchService;
        this.productService = productService;
        this.shelfService = shelfService;
        this.orderService = orderService;
    }

    @Override
    public void execute() {
        switch (entity) {
            case "batch":
                batchService.getAll();
                break;
            case "product":
                productService.getAll();
                break;
            case "shelf":
                shelfService.getAll();
                break;
            case "order":
                orderService.getAll();
                break;
            default:
                System.out.println("Invalid entity!");
                break;
        }
    }
}

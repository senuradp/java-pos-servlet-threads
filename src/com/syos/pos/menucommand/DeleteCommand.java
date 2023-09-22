/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.menucommand;

import com.syos.pos.gui.BatchDeleteGUI;
import com.syos.pos.gui.ProductDeleteGUI;
import com.syos.pos.gui.ShelfDeleteGUI;

/**
 *
 * @author senu2k
 */
public class DeleteCommand implements Command {
    private final BatchGUIService batchService;
    private final ProductGUIService productService;
    private final ShelfGUIService shelfService;
    private final OrderServiceMenu orderService;
    String entity; // package-private access

    public DeleteCommand(String entity, BatchGUIService batchService, ProductGUIService productService, ShelfGUIService shelfService, OrderServiceMenu orderService) {
        this.entity = entity;
        this.batchService = batchService;
        this.productService = productService;
        this.shelfService = shelfService;
        this.orderService = orderService;
    }

    @Override
    public void execute() {
        switch (entity) {
            case "batch":
                BatchDeleteGUI batchdeleteGUI = new BatchDeleteGUI(batchService);
                batchdeleteGUI.setVisible(true);
                break;
            case "product":
                ProductDeleteGUI productDeleteGUI = new ProductDeleteGUI(productService);
                productDeleteGUI.setVisible(true);
                break;
            case "shelf":
                ShelfDeleteGUI shelfDeleteGUI = new ShelfDeleteGUI(shelfService);
                shelfDeleteGUI.setVisible(true);
                break;
            case "order":
                orderService.delete();
                break;
            default:
                System.out.println("Invalid entity!");
                break;
        }
    }
}

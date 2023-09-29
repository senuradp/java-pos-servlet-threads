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
public class UpdateCommand  implements Command {
    private final BatchGUIService batchService;
    private final ProductGUIService productService;
    private final ShelfGUIService shelfService;
    private final OrderGUIService orderService;
    String entity; // package-private access

    public UpdateCommand(String entity, BatchGUIService batchService, ProductGUIService productService, ShelfGUIService shelfService, OrderGUIService orderService) {
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
                BatchUpdateGUI batchUpdateGUI = new BatchUpdateGUI(batchService);
                batchUpdateGUI.setVisible(true);
                break;
            case "product":
                ProductUpdateGUI productUpdateGUI = new ProductUpdateGUI(productService);
                productUpdateGUI.setVisible(true);
                break;
            case "shelf":
                ShelfUpdateGUI shelfUpdateGUI = new ShelfUpdateGUI(shelfService);
                shelfUpdateGUI.setVisible(true);
                break;
            case "order":
//                orderService.update();
                break;
            default:
                System.out.println("Invalid entity!");
                break;
        }
    }
}
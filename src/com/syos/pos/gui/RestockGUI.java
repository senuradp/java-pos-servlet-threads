/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.gui;

/**
 *
 * @author senu2k
 */

import com.syos.pos.controller.ProductController;
import com.syos.pos.controller.ShelfController;
import com.syos.pos.menucommand.ProductGUIService;
import com.syos.pos.menucommand.ShelfGUIService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RestockGUI extends JFrame {
    private JTextField productCodeField;
    private JTextField quantityField;
    private JButton restockButton;

    public RestockGUI() {
        setTitle("Restock Shelf");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create components
        JLabel productCodeLabel = new JLabel("Enter product code:");
        productCodeField = new JTextField(10);

        JLabel quantityLabel = new JLabel("Enter quantity to restock:");
        quantityField = new JTextField(10);

        restockButton = new JButton("Restock");

        // Create panel to hold components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.add(productCodeLabel);
        panel.add(productCodeField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(restockButton);

        // Add action listener to the restock button
        restockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RestockWorker().execute();
            }
        });

        // Add panel to the frame
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    class RestockWorker extends SwingWorker<Boolean, Void> {
        @Override
        protected Boolean doInBackground() {
            String productCode = productCodeField.getText();
            String quantityText = quantityField.getText();

            if (productCode.isEmpty() || quantityText.isEmpty()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(RestockGUI.this, "Please enter product code and quantity.", "Error", JOptionPane.ERROR_MESSAGE));
                return false;
            }

            double quantity;
            try {
                quantity = Double.parseDouble(quantityText);
            } catch (NumberFormatException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(RestockGUI.this, "Invalid quantity format.", "Error", JOptionPane.ERROR_MESSAGE));
                return false;
            }

            ProductGUIService productGUIService = new ProductGUIService();
            ShelfGUIService shelfGUIService = new ShelfGUIService();

            if (productGUIService.checkProductCodeExists(productCode)) {
                try {
                    return shelfGUIService.reStockShelf(productCode, quantity);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(RestockGUI.this, "Product code not found.", "Error", JOptionPane.ERROR_MESSAGE));
                return false;
            }
        }

        @Override
        protected void done() {
            try {
                if (get()) {
                    JOptionPane.showMessageDialog(RestockGUI.this, "Shelf restocked successfully!");
                } else {
                    JOptionPane.showMessageDialog(RestockGUI.this, "Failed to restock shelf.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(RestockGUI.this, "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RestockGUI restockGUI = new RestockGUI();
                restockGUI.setVisible(true);
            }
        });
    }
}

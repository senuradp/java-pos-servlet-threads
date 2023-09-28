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
import com.syos.pos.menucommand.ProductGUIService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProductAddGUI extends JFrame {
    private JTextField productCodeField;
    private JTextField productNameField;
    private JTextField productPriceField;
    private JLabel resultArea;
    
    private final ProductGUIService productService; // Accept the service in the constructor

    public ProductAddGUI(ProductGUIService productService) {
        
        this.productService = productService; // Initialize the service
        
        setTitle("Add Product");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a panel to hold the form components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Labels and input fields for product details
        JLabel productCodeLabel = new JLabel("Product Code:");
        productCodeField = new JTextField(20);

        JLabel productNameLabel = new JLabel("Product Name:");
        productNameField = new JTextField(20);

        JLabel productPriceLabel = new JLabel("Product Price:");
        productPriceField = new JTextField(20);

        // Create a "Add Product" button
        JButton addProductButton = new JButton("Add Product");
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductButton.setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        String productCode = productCodeField.getText();
                        String productName = productNameField.getText();
                        String productPriceText = productPriceField.getText();

                        try {
                            double productPrice = Double.parseDouble(productPriceText);
                            String result = productService.add(productCode, productName, productPrice);

                            SwingUtilities.invokeLater(() -> {
                                resultArea.setText(result);
                            });
                        } catch (NumberFormatException ex) {
                            SwingUtilities.invokeLater(() -> {
                                resultArea.setText("Invalid product price format.");
                            });
                        }

                        return null;
                    }

                    @Override
                    protected void done() {
                        addProductButton.setEnabled(true);
                    }
                };

                worker.execute();
            }
        });

        // Result area to display the output
        resultArea = new JLabel();
//        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        // Result area to display the output
        resultArea = new JLabel();

        // Add components to the panel with constraints
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(productCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(productCodeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(productNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(productNameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(productPriceLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(productPriceField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 3; // Span across three columns
        panel.add(addProductButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 3; // Span across three columns
        panel.add(resultArea, constraints);

        // Add the panel to the frame
        getContentPane().add(panel);

        // Center the frame on the screen
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProductGUIService productService = new ProductGUIService();
                ProductAddGUI addProductGUI = new ProductAddGUI(productService);
                addProductGUI.setVisible(true);
            }
        });
    }
}



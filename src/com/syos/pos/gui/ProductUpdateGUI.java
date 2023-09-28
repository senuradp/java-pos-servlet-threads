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

public class ProductUpdateGUI extends JFrame {
    private JTextField productCodeField;
    private JTextField productNameField;
    private JTextField productPriceField;
    private JLabel resultArea;
    
    private ProductGUIService productService;

    public ProductUpdateGUI(ProductGUIService productService) {
        this.productService = productService;

        setTitle("Update Product");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel productCodeLabel = new JLabel("Product Code:");
        productCodeField = new JTextField(20);

        JButton getProductButton = new JButton("Get Product");
        getProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        String productCode = productCodeField.getText();
                        String[] productDetails = productService.getByCode(productCode);
                        if (productDetails != null) {
                            SwingUtilities.invokeLater(() -> {
                                productNameField.setText(productDetails[0]);
                                productPriceField.setText(productDetails[1]);
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(null, "Product not found for code: " + productCode, "Product Not Found", JOptionPane.ERROR_MESSAGE);
                            });
                        }
                        return null;
                    }
                };
                worker.execute();
            }
        });

        JLabel productNameLabel = new JLabel("Product Name:");
        productNameField = new JTextField(20);

        JLabel productPriceLabel = new JLabel("Price:");
        productPriceField = new JTextField(20);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateButton.setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        String updatedProductCode = productCodeField.getText();
                        String updatedProductName = productNameField.getText();
                        String updatedProductPriceText = productPriceField.getText();

                        try {
                            double productPrice = Double.parseDouble(updatedProductPriceText);
                            String result = productService.update(updatedProductCode, updatedProductName, productPrice);

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
                        updateButton.setEnabled(true);
                    }
                };

                worker.execute();
            }
        });

        resultArea = new JLabel();

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(productCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(productCodeField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(getProductButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(productNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(productNameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(productPriceLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(productPriceField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 3;
        panel.add(updateButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 3;
        panel.add(resultArea, constraints);

        getContentPane().add(panel);

        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProductGUIService productService = new ProductGUIService();
                ProductUpdateGUI updateProductGUI = new ProductUpdateGUI(productService);
                updateProductGUI.setVisible(true);
            }
        });
    }
}
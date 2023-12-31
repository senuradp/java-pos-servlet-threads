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

import com.syos.pos.menucommand.BatchGUIService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BatchUpdateGUI extends JFrame {
    private JTextField batchCodeField;
    private JTextField productCodeField;
    private JTextField expiryDateField;
    private JTextField purchaseDateField;
    private JTextField batchQtyField;
    private JTextField availableQtyField;
    private JCheckBox isSoldCheckBox;
    private JLabel resultArea;

    private BatchGUIService batchService;

    public BatchUpdateGUI(BatchGUIService batchService) {
        this.batchService = batchService;

        setTitle("Update Batch");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a panel to hold the form components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Labels and input fields for batch details
        JLabel batchCodeLabel = new JLabel("Batch Code:");
        batchCodeField = new JTextField(20);
//        batchCodeField.setEditable(false);

        JLabel productCodeLabel = new JLabel("Product Code:");
        productCodeField = new JTextField(20);

        JLabel expiryDateLabel = new JLabel("Expiry Date (yyyy-MM-dd):");
        expiryDateField = new JTextField(20);

        JLabel purchaseDateLabel = new JLabel("Purchase Date (yyyy-MM-dd):");
        purchaseDateField = new JTextField(20);

        JLabel batchQtyLabel = new JLabel("Batch Quantity:");
        batchQtyField = new JTextField(20);

        JLabel availableQtyLabel = new JLabel("Available Quantity:");
        availableQtyField = new JTextField(20);

        JLabel isSoldLabel = new JLabel("Is Sold:");
        String[] isSoldOptions = {"Yes", "No"};
        JComboBox<String> isSoldComboBox = new JComboBox<>(isSoldOptions);

        // Create a "Get Batch" button
        JButton getBatchButton = new JButton("Get Batch");
        getBatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the batch code entered by the user
                String batchCode = batchCodeField.getText();

                // Call the service to retrieve batch details
                String[] batchDetails = batchService.getByCode(batchCode);

                if (batchDetails != null) {
                    // Set the retrieved values to the text fields
                    productCodeField.setText(batchDetails[0]);
                    expiryDateField.setText(batchDetails[1]);
                    purchaseDateField.setText(batchDetails[2]); 
                    batchQtyField.setText(batchDetails[3]); 
                    availableQtyField.setText(batchDetails[4]); 
                    isSoldComboBox.setSelectedItem(batchDetails[5]);
                } else {
                    // Handle the case where the batch is not found
                    JOptionPane.showMessageDialog(null, "Batch not found for code: " + batchCode, "Batch Not Found", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create an "Update" button
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateButton.setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            String updatedBatchCode = batchCodeField.getText();
                            String updatedProductCode = productCodeField.getText();
                            String updatedExpiryDate = expiryDateField.getText();
                            String updatedPurchaseDate = purchaseDateField.getText();
                            double batchQty = Double.parseDouble(batchQtyField.getText());
                            double availableQty = Double.parseDouble(availableQtyField.getText());
                            boolean updatedIsSold = "Yes".equals(isSoldComboBox.getSelectedItem().toString());

                            String result = batchService.update(updatedBatchCode, updatedProductCode, updatedExpiryDate, updatedPurchaseDate, batchQty, availableQty, updatedIsSold);

                            SwingUtilities.invokeLater(() -> {
                                resultArea.setText(result);
                            });
                        } catch (NumberFormatException ex) {
                            SwingUtilities.invokeLater(() -> {
                                resultArea.setText("Invalid quantity format.");
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

                // Clear the input fields
//                batchCodeField.setText("");
//                productCodeField.setText("");
//                expiryDateField.setText("");
//                purchaseDateField.setText("");
//                batchQtyField.setText("");
//                availableQtyField.setText("");
//                isSoldCheckBox.setSelected(false);
            }
        });

        // Result area to display the output
        resultArea = new JLabel();

        // Add components to the panel with constraints
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(batchCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(batchCodeField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        panel.add(getBatchButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(productCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(productCodeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(expiryDateLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(expiryDateField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(purchaseDateLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(purchaseDateField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(batchQtyLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        panel.add(batchQtyField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(availableQtyLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 5;
        panel.add(availableQtyField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        panel.add(isSoldLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 6;
        panel.add(isSoldComboBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.gridwidth = 3; // Span across three columns
        panel.add(updateButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 8;
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
                BatchGUIService batchService = new BatchGUIService();
                BatchUpdateGUI updateBatchGUI = new BatchUpdateGUI(batchService);
                updateBatchGUI.setVisible(true);
            }
        });
    }
}


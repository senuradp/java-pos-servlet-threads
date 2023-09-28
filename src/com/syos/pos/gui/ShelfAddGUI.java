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

import com.syos.pos.menucommand.ShelfGUIService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShelfAddGUI extends JFrame {
    private JTextField shelfCodeField;
    private JTextField productCodeField;
    private JTextField capacityField;
    private JTextField availableQtyField;
    private JLabel resultArea;
    private ShelfGUIService shelfService; // Reference to the ShelfGUIService

    public ShelfAddGUI(ShelfGUIService shelfService) {
        this.shelfService = shelfService; // Initialize the service

        setTitle("Add Shelf");
        setSize(850, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a panel to hold the form components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Labels and input fields for shelf details
        JLabel shelfCodeLabel = new JLabel("Shelf Code:");
        shelfCodeField = new JTextField(20);

        JLabel productCodeLabel = new JLabel("Product Code:");
        productCodeField = new JTextField(20);

        JLabel capacityLabel = new JLabel("Capacity:");
        capacityField = new JTextField(20);

        JLabel availableQtyLabel = new JLabel("Available Quantity:");
        availableQtyField = new JTextField(20);

        // Create a "Add Shelf" button
        JButton addShelfButton = new JButton("Add Shelf");
        addShelfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addShelfButton.setEnabled(false);

                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        String shelfCode = shelfCodeField.getText();
                        String productCode = productCodeField.getText();
                        String capacityText = capacityField.getText();
                        String availableQtyText = availableQtyField.getText();

                        try {
                            double capacity = Double.parseDouble(capacityText);
                            double availableQty = Double.parseDouble(availableQtyText);
                            String result = shelfService.add(shelfCode, productCode, capacity, availableQty);

                            SwingUtilities.invokeLater(() -> {
                                resultArea.setText(result);
                            });
                        } catch (NumberFormatException ex) {
                            SwingUtilities.invokeLater(() -> {
                                resultArea.setText("Invalid number format.");
                            });
                        }

                        return null;
                    }

                    @Override
                    protected void done() {
                        addShelfButton.setEnabled(true);
                    }
                };

                worker.execute();
            }
        });

        // Result area to display the output
        resultArea = new JLabel();

                // Add components to the panel with constraints
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(shelfCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(shelfCodeField, constraints);

        // Add other components with similar constraints
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(productCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(productCodeField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(capacityLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(capacityField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(availableQtyLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(availableQtyField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2; // Span across two columns
        panel.add(addShelfButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2; // Span across two columns
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
                ShelfGUIService shelfService = new ShelfGUIService(); // Initialize the service
                ShelfAddGUI addShelfGUI = new ShelfAddGUI(shelfService); // Pass the service to the constructor
                addShelfGUI.setVisible(true);
            }
        });
    }
}

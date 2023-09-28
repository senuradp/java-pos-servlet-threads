/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syos.pos.gui;


import com.syos.pos.controller.ProductController;
import com.syos.pos.menucommand.OrderServiceMenu;
import com.syos.pos.menucommand.ProductGUIService;
import com.syos.pos.service.OrderService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author senu2k
 */

public class OrderAddGUI extends JFrame {
    private JTextField productCodeField;
    private JTextField quantityField;
    private JTextField paymentTypeField;
    private JTextField customerAmountField;
    private JTextField discountField;
    private JTextArea orderSummaryArea;

    private ProductGUIService productGUIService;
    private OrderService orderService;

    public OrderAddGUI() {
        productGUIService = new ProductGUIService();
        orderService = OrderService.getInstance();
        orderService.createOrder();

        setTitle("Order Entry System");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Create and configure Swing components (text fields, buttons, etc.)
        productCodeField = new JTextField(10);
        quantityField = new JTextField(10);
        paymentTypeField = new JTextField(10);
        customerAmountField = new JTextField(10);
        discountField = new JTextField(10);
        orderSummaryArea = new JTextArea(10, 30);

        JButton addProductButton = new JButton("Add Product");
        JButton checkoutButton = new JButton("Checkout");

        // Create action listeners for buttons
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkout();
            }
        });

        // Create panels to organize components
        JPanel productPanel = new JPanel();
        productPanel.add(new JLabel("Product Code: "));
        productPanel.add(productCodeField);
        productPanel.add(new JLabel("Quantity: "));
        productPanel.add(quantityField);
        productPanel.add(addProductButton);

        JPanel paymentPanel = new JPanel();
        paymentPanel.add(new JLabel("Payment Type: "));
        paymentPanel.add(paymentTypeField);
        paymentPanel.add(new JLabel("Customer Amount: "));
        paymentPanel.add(customerAmountField);
        paymentPanel.add(new JLabel("Discount: "));
        paymentPanel.add(discountField);
        paymentPanel.add(checkoutButton);

        // Set layout for the main frame
        setLayout(new BorderLayout());

        // Add components to the main frame
        add(productPanel, BorderLayout.NORTH);
        add(new JScrollPane(orderSummaryArea), BorderLayout.CENTER);
        add(paymentPanel, BorderLayout.SOUTH);
    }

    private void addProduct() {
        try {
            String productCode = productCodeField.getText();
            double quantity = Double.parseDouble(quantityField.getText());

            // Check if the product code exists
            boolean productCodeExists = productGUIService.checkProductCodeExists(productCode);

            if (!productCodeExists) {
                JOptionPane.showMessageDialog(this, "Product code does not exist!");
                return;
            }


            orderService.addOrderProduct(productCode, quantity);
            orderSummaryArea.append("Product added to the order: " + productCode + " - Quantity: " + quantity + "\n");

            // Clear input fields
            productCodeField.setText("");
            quantityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkout() {
        try {
            String paymentType = paymentTypeField.getText();
            double customerAmount = Double.parseDouble(customerAmountField.getText());
            double discount = Double.parseDouble(discountField.getText());

            orderService.addDiscount(discount);
            double balanceAmount = orderService.checkoutPay(customerAmount, paymentType);

            orderSummaryArea.append("Balance: " + balanceAmount + "\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
        } catch (Exception ex) {
            if (ex.getMessage().contains("Amount tendered is less than total bill price.")) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            } else {
                Logger.getLogger(OrderServiceMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OrderAddGUI().setVisible(true);
            }
        });
    }
}


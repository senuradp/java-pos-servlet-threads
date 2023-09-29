package com.syos.pos.gui;

import com.syos.pos.menucommand.OrderGUIService;

import javax.swing.*;
import java.awt.*;

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
    private JButton addProductButton;

    private JLabel totalBillLabel; //new
    private final OrderGUIService guiService;

    public OrderAddGUI() {
        this.guiService = new OrderGUIService(this);
        setTitle("Order Entry System");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        productCodeField = new JTextField(10);
        quantityField = new JTextField(10);
        paymentTypeField = new JTextField(10);
        customerAmountField = new JTextField(10);
        discountField = new JTextField(10);
        orderSummaryArea = new JTextArea(10, 30);

        addProductButton = new JButton("Add Product");
        JButton checkoutButton = new JButton("Checkout");

        addProductButton.addActionListener(e -> {
            String code = productCodeField.getText();
            String qty = quantityField.getText();
            if (validateInputs(code, qty)) {
                guiService.addProduct(code, qty);
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            }
        });

        checkoutButton.addActionListener(e -> {
            // You can add further validation here
            guiService.checkout(
                    paymentTypeField.getText(),
                    customerAmountField.getText(),
                    discountField.getText()
            );
        });

        JPanel productPanel = new JPanel();
        productPanel.add(new JLabel("Product Code: "));
        productPanel.add(productCodeField);
        productPanel.add(new JLabel("Quantity: "));
        productPanel.add(quantityField);
        productPanel.add(addProductButton);
        productPanel.add(new JLabel("Total Bill: "));
        productPanel.add(totalBillLabel = new JLabel("0.00"));

        JPanel paymentPanel = new JPanel();
        paymentPanel.add(new JLabel("Payment Type: "));
        paymentPanel.add(paymentTypeField);
        paymentPanel.add(new JLabel("Customer Amount: "));
        paymentPanel.add(customerAmountField);
        paymentPanel.add(new JLabel("Discount: "));
        paymentPanel.add(discountField);
        paymentPanel.add(checkoutButton);

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(productPanel);
        topPanel.add(paymentPanel);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(orderSummaryArea), BorderLayout.CENTER);
    }

    private boolean validateInputs(String code, String qty) {
        return !(code == null || code.isEmpty() || qty == null || qty.isEmpty());
    }

    public void appendOrderSummary(String summary) {
        orderSummaryArea.append(summary);
    }

    public void updateTotalBill(String totalBill) {
        // Update the total bill display in the GUI
        // This will depend on how you've implemented your GUI
        // For example, if you have a JLabel for this:
        totalBillLabel.setText(totalBill);
    }

    public void clearProductInputs() {
        productCodeField.setText("");
        quantityField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OrderAddGUI().setVisible(true);
        });
    }
}

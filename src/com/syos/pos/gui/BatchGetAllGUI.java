package com.syos.pos.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import com.syos.pos.dto.BatchDTO;
import com.syos.pos.menucommand.BatchGUIService;

public class BatchGetAllGUI {

    private JFrame frame;
    private JTextArea textArea;
    private BatchGUIService batchService;
    private SimpleDateFormat dateFormat;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                BatchGetAllGUI window = new BatchGetAllGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public BatchGetAllGUI() {
        batchService = new BatchGUIService();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JButton btnGetAllBatches = new JButton("Get All Batches");
        btnGetAllBatches.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<BatchDTO> batches = batchService.getAll();
                StringBuilder sb = new StringBuilder();
                for (BatchDTO batch : batches) {
                    sb.append("Batch Code: ").append(batch.getBatch_code()).append("\n");
                    sb.append("Product Code: ").append(batch.getProduct_code()).append("\n");
                    sb.append("Batch Quantity: ").append(batch.getBatch_qty()).append("\n");
                    sb.append("Expiry Date: ").append(dateFormat.format(batch.getExpiry_date())).append("\n");
                    sb.append("Purchase Date: ").append(dateFormat.format(batch.getPurchase_date())).append("\n");
                    sb.append("Available Quantity: ").append(batch.getAvailable_qty()).append("\n");
                    sb.append("Is Sold: ").append(batch.getIs_sold()).append("\n");
                    sb.append("-----------------\n");
                }
                textArea.setText(sb.toString());
            }
        });
        frame.getContentPane().add(btnGetAllBatches, BorderLayout.NORTH);

        textArea = new JTextArea();
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
}


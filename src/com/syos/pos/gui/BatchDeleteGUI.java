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

public class BatchDeleteGUI extends JFrame {
    private JTextField batchCodeField;
    private JLabel resultLabel;
    private BatchGUIService batchService;

    public BatchDeleteGUI(BatchGUIService batchService) {
        this.batchService = batchService;

        setTitle("Delete Batch");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel batchCodeLabel = new JLabel("Batch Code:");
        batchCodeField = new JTextField(20);

        JButton deleteBatchButton = new JButton("Delete Batch");
        deleteBatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DeleteBatchWorker().execute();
            }
        });

        resultLabel = new JLabel();

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(batchCodeLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(batchCodeField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(deleteBatchButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(resultLabel, constraints);

        getContentPane().add(panel);
        setLocationRelativeTo(null);
    }

    private class DeleteBatchWorker extends SwingWorker<String, Void> {
        @Override
        protected String doInBackground() throws Exception {
            String batchCode = batchCodeField.getText();
            return batchService.delete(batchCode);
        }

        @Override
        protected void done() {
            try {
                String deletionResult = get();
                resultLabel.setText(deletionResult);
            } catch (Exception e) {
                resultLabel.setText("An error occurred: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                BatchGUIService batchService = new BatchGUIService();
                BatchDeleteGUI deleteBatchGUI = new BatchDeleteGUI(batchService);
                deleteBatchGUI.setVisible(true);
            }
        });
    }
}


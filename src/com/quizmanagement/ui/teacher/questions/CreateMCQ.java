/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.quizmanagement.ui.teacher.questions;

import com.quizmanagement.util.Utils;
import static com.quizmanagement.ui.styles.*;
import java.awt.Cursor;
import java.util.ArrayList;
import javax.swing.*;


public class CreateMCQ extends javax.swing.JPanel {
    
    private final Utils utils = new Utils();
    
    public String getQuestionText() {
        return jTextArea1.getText().trim();
    }

    public ArrayList<String> getOptions() {
        ArrayList<String> options = new ArrayList<>();
        options.add(optionAText.getText().trim());
        options.add(optionBText.getText().trim());
        options.add(optionCText.getText().trim());
        options.add(optionDText.getText().trim());
        return options;
    }
    
    public JTextArea getQuestionTextField() {
        return jTextArea1;
    }
    
    public JScrollPane getOptionAField() {
        return optAPane;
    }
    
    public String getCorrectOption() {
        if (optionA.isSelected()) return optionAText.getText().trim();
        if (optionB.isSelected()) return optionCText.getText().trim();
        if (optionC.isSelected()) return optionBText.getText().trim();
        if (optionD.isSelected()) return optionDText.getText().trim();
        return null; // No option selected
    }
    
     public int  getCorrectOptionIndex() {
        if (optionA.isSelected()) return 0;
        if (optionB.isSelected()) return 1;
        if (optionC.isSelected()) return 2;
        if (optionD.isSelected()) return 3;
        return -1; // No option selected
    }
     
   public JLabel getErrMessageLabel() {
        return errMessage;
    }
   

    public void setQuestionText(String text) {
        jTextArea1.setText(text);
    }

    public void setOptions(ArrayList<String> options) {
        if (options.size() >= 4) {
            optionAText.setText(options.get(0));
            optionBText.setText(options.get(1));
            optionCText.setText(options.get(2));
            optionDText.setText(options.get(3));
        }
    }

public void setCorrectOptionIndex(int index) {
    switch (index) {
        case 0: optionA.setSelected(true); break;
        case 1: optionB.setSelected(true); break;
        case 2: optionC.setSelected(true); break;
        case 3: optionD.setSelected(true); break;
    }
}

    public void clearErrorState() {
        errMessage.setText(" ");
//        styles.Borders.setDefaultBorder(jTextArea1);
//        styles.Borders.setDefaultBorder(optAPane);
//        styles.Borders.setDefaultBorder(optionAText);
//        styles.Borders.setDefaultBorder(optionBText);
//        styles.Borders.setDefaultBorder(optionCText);
//        styles.Borders.setDefaultBorder(optionDText);
    }
    public void showError(String message, JComponent field, boolean severe) {
        utils.setErrorMessage(message, field, errMessage, severe, false);
    }
    
    public CreateMCQ() {
        initComponents();
        stylize();
    }
    
    private void stylize() {
        // Panel background
        setBackground(DARK_BG);
        
        // Question text area
        styleTextArea(jTextArea1, jScrollPane1);
        
        // Option text areas
        styleTextArea(optionAText, optAPane);
        styleTextArea(optionBText, jScrollPane3);
        styleTextArea(optionCText, jScrollPane4);
        styleTextArea(optionDText, jScrollPane5);
        
        // Radio buttons
        styleRadioButton(optionA);
        styleRadioButton(optionB);
        styleRadioButton(optionC);
        styleRadioButton(optionD);
        
        // Error label
        errMessage.setFont(fontSmall());
        errMessage.setForeground(ERROR_COLOR);
    }
    
    private void styleTextArea(JTextArea area, JScrollPane pane) {
        area.setBackground(CARD_BG);
        area.setForeground(TEXT_COLOR);
        area.setCaretColor(TEXT_COLOR);
        area.setFont(fontBody());
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        pane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        pane.getViewport().setBackground(CARD_BG);
    }
    
    private void styleRadioButton(JRadioButton rb) {
        rb.setBackground(DARK_BG);
        rb.setForeground(TEXT_COLOR);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        answersGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        optionA = new javax.swing.JRadioButton();
        optionC = new javax.swing.JRadioButton();
        optionB = new javax.swing.JRadioButton();
        optionD = new javax.swing.JRadioButton();
        optAPane = new javax.swing.JScrollPane();
        optionAText = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        optionBText = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        optionCText = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        optionDText = new javax.swing.JTextArea();
        errMessage = new javax.swing.JLabel();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        answersGroup.add(optionA);

        answersGroup.add(optionC);

        answersGroup.add(optionB);

        answersGroup.add(optionD);

        optionAText.setColumns(20);
        optionAText.setRows(5);
        optAPane.setViewportView(optionAText);

        optionBText.setColumns(20);
        optionBText.setRows(5);
        jScrollPane3.setViewportView(optionBText);

        optionCText.setColumns(20);
        optionCText.setRows(5);
        jScrollPane4.setViewportView(optionCText);

        optionDText.setColumns(20);
        optionDText.setRows(5);
        jScrollPane5.setViewportView(optionDText);

        errMessage.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(errMessage)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(optionA)
                                .addComponent(optionB))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane3)
                                .addComponent(optAPane, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(30, 30, 30)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(optionC)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(optionD)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(errMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(optionC)
                    .addComponent(optionA)
                    .addComponent(optAPane, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(optionD))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(optionB)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap(58, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup answersGroup;
    private javax.swing.JLabel errMessage;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    public javax.swing.JTextArea jTextArea1;
    public javax.swing.JScrollPane optAPane;
    private javax.swing.JRadioButton optionA;
    private javax.swing.JTextArea optionAText;
    private javax.swing.JRadioButton optionB;
    private javax.swing.JTextArea optionBText;
    private javax.swing.JRadioButton optionC;
    private javax.swing.JTextArea optionCText;
    private javax.swing.JRadioButton optionD;
    private javax.swing.JTextArea optionDText;
    // End of variables declaration//GEN-END:variables
}

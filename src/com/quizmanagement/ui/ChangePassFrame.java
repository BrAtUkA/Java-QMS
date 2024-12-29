package com.quizmanagement.ui;

import com.quizmanagement.db.UserDAO;
import com.quizmanagement.util.PasswordUtils;
import com.quizmanagement.util.Utils;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class ChangePassFrame extends javax.swing.JFrame {
    
    private String username;
    private static final Utils utils = new Utils();
    
    public void setUsername(String username) {
         this.username = username;
    }
    
    private void stylize(){
      styles.Hover.addHoverEffect(
            backButton, 
            "<html><span style='color: #AAAAAA;'>&lt; Back</span></html>", 
            "<html><u><span style='color: #AAAAAA;'>&lt; Back</span></u></html>"
        );
      
      resetAllBordersToDefault();
    }
    private void setErrorMessage(String message, javax.swing.JTextField field, boolean severeError) {
         resetAllBordersToDefault();
         utils.setErrorMessage(message, field, messageLabel, severeError);
    }
    public void succeedPassChange(String message, javax.swing.JTextField... fields) {
        utils.setSuccessMessage(message, messageLabel, fields); 

        Timer timer = new Timer(1500, (ActionEvent e) -> {
            ((Timer) e.getSource()).stop();
            LoginFrame logFrame = new LoginFrame();
            utils.moveTo(ChangePassFrame.this, logFrame); 
        });

    timer.setRepeats(false); 
    timer.start(); 
}
    private void resetAllBordersToDefault() {
       styles.Borders.setDefaultBorder(newPassField);
       styles.Borders.setDefaultBorder(cnfNewPassField);

    }
    
    public ChangePassFrame() {
         setUndecorated(true);
         initComponents();
         stylize();
         this.setTitle("QMS - Recovery");
         styles.Drag.addDragFunctionality(this);
         newPassField.putClientProperty("JTextField.placeholderText", "Password");
         cnfNewPassField.putClientProperty("JTextField.placeholderText", "Confirm Password");
         
         this.getRootPane().requestFocus();
         pack();
         repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        changePassButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        minButton = new javax.swing.JButton();
        backButton = new javax.swing.JLabel();
        newPassField = new javax.swing.JPasswordField();
        cnfNewPassField = new javax.swing.JPasswordField();
        messageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Recovery");
        jLabel1.setToolTipText("");

        changePassButton.setText("Change");
        changePassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePassButtonActionPerformed(evt);
            }
        });

        exitButton.setBackground(new java.awt.Color(204, 34, 34));
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitButtonMouseClicked(evt);
            }
        });

        minButton.setBackground(new java.awt.Color(224, 184, 43));
        minButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minButtonMouseClicked(evt);
            }
        });

        backButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        backButton.setForeground(new java.awt.Color(170, 170, 170));
        backButton.setText("< Back");
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });

        messageLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                        .addComponent(minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageLabel)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(changePassButton, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(newPassField)
                        .addComponent(cnfNewPassField, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)))
                .addGap(69, 69, 69))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exitButton)
                            .addComponent(minButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(messageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newPassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cnfNewPassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(changePassButton)
                .addGap(18, 18, 18)
                .addComponent(backButton)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void changePassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePassButtonActionPerformed
            char[] passwordChars = newPassField.getPassword();
            String password = new String(passwordChars);
            
            char[] cnfPasswordChars = cnfNewPassField.getPassword();
            String cnfPassword = new String(cnfPasswordChars);
            
            if (!password.equals(cnfPassword)) {
                setErrorMessage("Passwords do not match!", cnfNewPassField, true);
                return;
            }
            
            if (password.length() < 8) {
                setErrorMessage("Password must be 8 characters long!", newPassField, true);
                return;
            }
            
            if (cnfPassword.length() < 8) {
                setErrorMessage("Password must be 8 characters long!", cnfNewPassField, true);
                return;
            }
            
            // Hash the new password before storing
            String hashedPassword = PasswordUtils.hashPassword(password);
            
            UserDAO userDAO = new UserDAO();
            boolean updated = userDAO.updateUserPassword(username, hashedPassword);
            if (updated) {
                succeedPassChange("Password changed successfully!", newPassField, cnfNewPassField);
            } else {
                setErrorMessage("Failed to update password!", cnfNewPassField, true);
            }
    }//GEN-LAST:event_changePassButtonActionPerformed

    
    private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitButtonMouseClicked
       utils.exit();
    }//GEN-LAST:event_exitButtonMouseClicked
    private void minButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minButtonMouseClicked
       utils.minimize(this);
    }//GEN-LAST:event_minButtonMouseClicked
    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        VerifyFrame verFrame = new VerifyFrame();
        utils.moveTo(this, verFrame);
    }//GEN-LAST:event_backButtonMouseClicked

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backButton;
    private static javax.swing.JButton changePassButton;
    private javax.swing.JPasswordField cnfNewPassField;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton minButton;
    private javax.swing.JPasswordField newPassField;
    // End of variables declaration//GEN-END:variables
}

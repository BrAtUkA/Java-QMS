package com.quizmanagement.ui;

import com.quizmanagement.db.UserDAO;
import com.quizmanagement.objs.User;
import com.quizmanagement.util.PasswordUtils;
import com.quizmanagement.util.Utils;


public class VerifyFrame extends javax.swing.JFrame {
    
    private static final Utils utils = new Utils();
    
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
         utils.setErrorMessage(message, field, errMessage, severeError);
     }
    private void resetAllBordersToDefault() {
       styles.Borders.setDefaultBorder(usrNameField);
       styles.Borders.setDefaultBorder(ansField);

    }
    
    private void checkVerifyButtonState() {
        String username = usrNameField.getText();
        String answer = ansField.getText();
        
        boolean enableButton =  answer.length() >= 1 &&  username.length() >= 3;

        verifyButton.setEnabled(enableButton);
    }   
    private void initListeners() {
        Utils.addSimpleDocumentListener(ansField, this::checkVerifyButtonState);
        Utils.addSimpleDocumentListener(usrNameField, this::checkVerifyButtonState);
    }

    public VerifyFrame() {
         setUndecorated(true);
         initComponents();
         stylize();
         this.setTitle("QMS - Recovery");
         styles.Drag.addDragFunctionality(this);
         usrNameField.putClientProperty("JTextField.placeholderText", "Username");
         ansField.putClientProperty("JTextField.placeholderText", "Answer");
         initListeners();
         
         this.getRootPane().requestFocus();
         pack();
         repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        verifyButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        minButton = new javax.swing.JButton();
        secQuestion = new javax.swing.JComboBox<>();
        backButton = new javax.swing.JLabel();
        ansField = new javax.swing.JTextField();
        usrNameField = new javax.swing.JTextField();
        errMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Recovery");
        jLabel1.setToolTipText("");

        verifyButton.setText("Verify");
        verifyButton.setEnabled(false);
        verifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyButtonActionPerformed(evt);
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

        secQuestion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "What was your first car's name?", "What was your first pet's name?", "Street you grew up on?", "Childhood best friend?", "Name of your elementary school?", "Favorite book?", "In what city were you born?", "What is your favorite color?" }));

        backButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        backButton.setForeground(new java.awt.Color(170, 170, 170));
        backButton.setText("< Back");
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });

        errMessage.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addContainerGap(263, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(errMessage)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(verifyButton, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(secQuestion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ansField)
                        .addComponent(usrNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(58, 58, 58))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(errMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(usrNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(secQuestion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ansField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(verifyButton)
                .addGap(18, 18, 18)
                .addComponent(backButton)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void verifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyButtonActionPerformed
        String username = usrNameField.getText().trim();
        String answer = ansField.getText().trim();
        int selectedQuestion = secQuestion.getSelectedIndex() + 1; 

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            setErrorMessage("User not found!", usrNameField, true);
            return;
        }
        
        boolean verified = false;

        if (selectedQuestion >= 1 && selectedQuestion <= 4) {
            // selected question is sec question  1
            if (user.getSecurityQuestion1() == selectedQuestion && 
                PasswordUtils.verifySecurityAnswer(answer, user.getSecurityAnswer1())) {
                verified = true;
            }
        } else if (selectedQuestion >= 5 && selectedQuestion <= 8) {
             // selected question is sec question 2
            if (user.getSecurityQuestion2() == (selectedQuestion - 4) && 
                PasswordUtils.verifySecurityAnswer(answer, user.getSecurityAnswer2())) {
                verified = true;
            }
        }

        if (verified) {
            ChangePassFrame changePassFrame = new ChangePassFrame();
            changePassFrame.setUsername(username);
            utils.moveTo(VerifyFrame.this, changePassFrame);
        } else {
            setErrorMessage("Incorrect answer!", ansField, false);
        }

      
    }//GEN-LAST:event_verifyButtonActionPerformed

    private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitButtonMouseClicked
       utils.exit();
    }//GEN-LAST:event_exitButtonMouseClicked
    private void minButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minButtonMouseClicked
       utils.minimize(this);
    }//GEN-LAST:event_minButtonMouseClicked
    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        LoginFrame logFrame = new LoginFrame();
        utils.moveTo(this, logFrame);
    }//GEN-LAST:event_backButtonMouseClicked


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ansField;
    private javax.swing.JLabel backButton;
    private javax.swing.JLabel errMessage;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton minButton;
    private javax.swing.JComboBox<String> secQuestion;
    private javax.swing.JTextField usrNameField;
    private static javax.swing.JButton verifyButton;
    // End of variables declaration//GEN-END:variables
}

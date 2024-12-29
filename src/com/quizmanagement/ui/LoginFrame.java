package com.quizmanagement.ui;

import com.quizmanagement.ui.student.StudentPortal;
import com.quizmanagement.ui.teacher.TeacherPortal;
import com.quizmanagement.db.UserDAO;
import com.quizmanagement.objs.User;
import com.quizmanagement.util.PasswordUtils;
import com.quizmanagement.util.Utils;


public class LoginFrame extends javax.swing.JFrame {
    
    private static final Utils utils = new Utils();
    
    private void stylize(){
        styles.Hover.addHoverEffect(
            frgtPassButton, 
            "<html><span style='color: #5A7AFA;'>Forgot password?</span></html>", 
            "<html><u><span style='color: #5A7AFA;'>Forgot password?</span></u></html>"
        );
        
        styles.Hover.addHoverEffect(
            regButton, 
            "<html><span style='color: white;'>Register</span></html>", 
            "<html><u><span style='color: white;'>Register</span></u></html>"
        );
        
        resetAllBordersToDefault();
    }
    
    private void setErrorMessage(String message, javax.swing.JTextField field, boolean severeError) {
         resetAllBordersToDefault();
         utils.setErrorMessage(message, field, errMessage, severeError);
    }
    
    
    private void resetAllBordersToDefault() {
       styles.Borders.setDefaultBorder(usrNameField);
       styles.Borders.setDefaultBorder(passField);
    }
    
    private void checkLoginButtonState() {
        String username = usrNameField.getText().trim();
        String password = new String(passField.getPassword());

        boolean enableButton = username.length() >= 3 && password.length() >= 8;

        loginButton.setEnabled(enableButton);
    }
    
    private void initListeners() {
        Utils.addSimpleDocumentListener(usrNameField, this::checkLoginButtonState);
        Utils.addSimpleDocumentListener(passField, this::checkLoginButtonState);
    }

    public LoginFrame() {
         setUndecorated(true);
         initComponents();
         stylize();
         this.setTitle("QMS - Login");
         styles.Drag.addDragFunctionality(this);
         usrNameField.putClientProperty("JTextField.placeholderText", "Username");
         passField.putClientProperty("JTextField.placeholderText", "Password");
         initListeners();
         
         this.getRootPane().requestFocus();
        pack();
        repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        usrNameField = new javax.swing.JTextField();
        loginButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        minButton = new javax.swing.JButton();
        passField = new javax.swing.JPasswordField();
        regButton = new javax.swing.JLabel();
        frgtPassButton = new javax.swing.JLabel();
        errMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jLabel1.setText("QMS");

        usrNameField.setToolTipText("Username");
        usrNameField.setName(""); // NOI18N

        loginButton.setText("Login");
        loginButton.setEnabled(false);
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
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

        regButton.setText("Register");
        regButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                regButtonMouseClicked(evt);
            }
        });

        frgtPassButton.setForeground(new java.awt.Color(52, 130, 233));
        frgtPassButton.setText("Forgot Password?");
        frgtPassButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                frgtPassButtonMouseClicked(evt);
            }
        });

        errMessage.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(regButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(errMessage)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(frgtPassButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(loginButton))
                        .addComponent(usrNameField)
                        .addComponent(passField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                            .addComponent(minButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usrNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginButton)
                    .addComponent(frgtPassButton))
                .addGap(33, 33, 33)
                .addComponent(regButton)
                .addGap(43, 43, 43))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
           String username = usrNameField.getText().trim();
           char[] passwordChars = passField.getPassword();
           String password = new String(passwordChars);

           UserDAO userDAO = new UserDAO();
           User user = userDAO.getUserByUsername(username);

           if (user == null) {
               setErrorMessage("Invalid Username!", usrNameField, true);
               return;
           }

           // Verify password using secure hashing
           if (PasswordUtils.verifyPassword(password, user.getHashedPassword())) {
               if (user.getRole().equalsIgnoreCase("STUDENT")){
                    StudentPortal stdFrame = new StudentPortal(username);
                    utils.moveTo(this, stdFrame);
                    
               } else if (user.getRole().equalsIgnoreCase("TEACHER")){
                    TeacherPortal teacherFrame = new TeacherPortal(username);
                    utils.moveTo(this, teacherFrame);
               }
           } else {
                setErrorMessage("Invalid Password!", passField, true);
           }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void regButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_regButtonMouseClicked
        RegFrame regFrame = new RegFrame();
        utils.moveTo(this, regFrame);
    }//GEN-LAST:event_regButtonMouseClicked
    private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitButtonMouseClicked
       utils.exit();
    }//GEN-LAST:event_exitButtonMouseClicked
    private void minButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minButtonMouseClicked
       utils.minimize(this);
    }//GEN-LAST:event_minButtonMouseClicked
    private void frgtPassButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_frgtPassButtonMouseClicked
         VerifyFrame verfFrame = new VerifyFrame();
        utils.moveTo(this, verfFrame);
    }//GEN-LAST:event_frgtPassButtonMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel errMessage;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel frgtPassButton;
    private javax.swing.JLabel jLabel1;
    private static javax.swing.JButton loginButton;
    private javax.swing.JButton minButton;
    private javax.swing.JPasswordField passField;
    private javax.swing.JLabel regButton;
    private javax.swing.JTextField usrNameField;
    // End of variables declaration//GEN-END:variables
}

package com.quizmanagement.ui;

import com.quizmanagement.db.UserDAO;
import com.quizmanagement.objs.User;
import com.quizmanagement.util.PasswordUtils;
import com.quizmanagement.util.Utils;

import javax.swing.JOptionPane;
import com.quizmanagement.ui.styles.Borders;

public class RegFrame extends javax.swing.JFrame {
    
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
       Borders.setDefaultBorder(usrNameField);
       Borders.setDefaultBorder(emailField);
       Borders.setDefaultBorder(passwField);
       Borders.setDefaultBorder(CnfPswField);
       Borders.setDefaultBorder(q1AnsField);
       Borders.setDefaultBorder(q2AnsField);
    }
    
    public RegFrame() {
        setUndecorated(true);
        initComponents();
        
        stylize();
        this.setTitle("QMS - Register");
        styles.Drag.addDragFunctionality(this);
        emailField.putClientProperty("JTextField.placeholderText", "E-mail");
        usrNameField.putClientProperty("JTextField.placeholderText", "Username");
        passwField.putClientProperty("JTextField.placeholderText", "Password");
        CnfPswField.putClientProperty("JTextField.placeholderText", "Confirm Password");
        q1AnsField.putClientProperty("JTextField.placeholderText", "Answer Security Question 1");
        q2AnsField.putClientProperty("JTextField.placeholderText", "Answer Security Question 2");
       
       
       this.getRootPane().requestFocus();
       pack();
       repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userTypeGrp = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        backButton = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        q1ComboBox = new javax.swing.JComboBox<>();
        q1AnsField = new javax.swing.JTextField();
        q2ComboBox = new javax.swing.JComboBox<>();
        q2AnsField = new javax.swing.JTextField();
        teacherRadio = new javax.swing.JRadioButton();
        studentRadio = new javax.swing.JRadioButton();
        passwField = new javax.swing.JPasswordField();
        CnfPswField = new javax.swing.JPasswordField();
        usrNameField = new javax.swing.JTextField();
        exitButton = new javax.swing.JButton();
        minButton = new javax.swing.JButton();
        regButton = new javax.swing.JButton();
        errMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        jLabel1.setText("Register");

        backButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        backButton.setForeground(new java.awt.Color(170, 170, 170));
        backButton.setText("< Back");
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });

        q1ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "What was your first car's name?", "What was your first pet's name?", "Street you grew up on?", "Childhood best friend?" }));

        q2ComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name of your elementary school?", "Favorite book?", "In what city were you born?", "What is your favorite color?" }));

        userTypeGrp.add(teacherRadio);
        teacherRadio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        teacherRadio.setText("Teacher");

        userTypeGrp.add(studentRadio);
        studentRadio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        studentRadio.setText("Student");

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

        regButton.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        regButton.setText("Register");
        regButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regButtonActionPerformed(evt);
            }
        });

        errMessage.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addGap(14, 311, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))))
            .addGroup(layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(errMessage)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(CnfPswField)
                            .addComponent(passwField)
                            .addComponent(emailField)
                            .addComponent(usrNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(studentRadio)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(teacherRadio)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(regButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(q1ComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(q1AnsField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(q2ComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(q2AnsField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(79, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(exitButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(errMessage)
                .addGap(4, 4, 4)
                .addComponent(usrNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CnfPswField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(q1ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q1AnsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(q2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q2AnsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teacherRadio)
                    .addComponent(regButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentRadio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(backButton)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        LoginFrame logFrame = new LoginFrame();
        utils.moveTo(this, logFrame);
    }//GEN-LAST:event_backButtonMouseClicked
    private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitButtonMouseClicked
        utils.exit();
    }//GEN-LAST:event_exitButtonMouseClicked
    private void minButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minButtonMouseClicked
        utils.minimize(this);
    }//GEN-LAST:event_minButtonMouseClicked

    private void regButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regButtonActionPerformed
            String username = usrNameField.getText().trim();
            String email = emailField.getText().trim();

            char[] passwordChars = passwField.getPassword();
            String password = new String(passwordChars);

            char[] cnfPasswordChars = CnfPswField.getPassword();
            String cnfPassword = new String(cnfPasswordChars);
            
            String role = studentRadio.isSelected() ? "STUDENT" : "TEACHER";
            

            // check if username already exists
            UserDAO userDAO = new UserDAO();
            User existingUser = userDAO.getUserByUsername(username);

            if (existingUser != null) {
                setErrorMessage("Username already exists!", usrNameField, true);
                return;
            }

                // verfiyinf  inputs
                if (username.length() < 3) {
                    setErrorMessage("Username must be at least 3 characters long!", usrNameField, true);
                    return;
                }

                if (!email.contains("@") || !email.contains(".")) {
                    setErrorMessage("Invalid email address!", emailField, false);
                    return;
                }

                if (password.length() < 8) {
                    setErrorMessage("Password must be at least 8 characters long!", passwField, true);
                    return;
                }

                if (!password.equals(cnfPassword)) {
                    setErrorMessage("Passwords do not match!", CnfPswField, false);
                    return;
                }

                if (q1AnsField.getText().trim().isEmpty()) {
                    setErrorMessage("Security question must be answered!", q1AnsField, false);
                    return;
                }

                if (q2AnsField.getText().trim().isEmpty()) {
                    setErrorMessage("Security question must be answered!", q2AnsField, false);
                    return;
                }

                if (!teacherRadio.isSelected() && !studentRadio.isSelected()) {
                    setErrorMessage("You must select a role (Teacher or Student)!", null, true);
                    return;
                }

            errMessage.setText(" ");
            resetAllBordersToDefault();

            int confirm = JOptionPane.showConfirmDialog(this, 
                    "Confirm registration as \"" + role + "\"?", "Confirm Registration", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return; 
            }

            int q1 = q1ComboBox.getSelectedIndex() + 1;
            int q2 = q2ComboBox.getSelectedIndex() + 1;
            String ans1 = q1AnsField.getText().trim();
            String ans2 = q2AnsField.getText().trim();

            // Hash password and security answers before storing
            String hashedPassword = PasswordUtils.hashPassword(password);
            String hashedAns1 = PasswordUtils.hashSecurityAnswer(ans1);
            String hashedAns2 = PasswordUtils.hashSecurityAnswer(ans2);

            User user = new User();
            user.setUsername(username);
            user.setHashedPassword(hashedPassword); 
            user.setEmail(email);
            user.setRole(role);
            user.setSecurityQuestion1(q1);
            user.setSecurityAnswer1(hashedAns1);
            user.setSecurityQuestion2(q2);
            user.setSecurityAnswer2(hashedAns2);

            boolean success = userDAO.createUser(user);
            
            if (success) {
                JOptionPane.showMessageDialog(RegFrame.this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                LoginFrame logFrame = new LoginFrame();
                utils.moveTo(RegFrame.this, logFrame);
            } else {
                setErrorMessage("Registration failed...", usrNameField, true);
            }
    }//GEN-LAST:event_regButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField CnfPswField;
    private javax.swing.JLabel backButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel errMessage;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton minButton;
    private javax.swing.JPasswordField passwField;
    private javax.swing.JTextField q1AnsField;
    private javax.swing.JComboBox<String> q1ComboBox;
    private javax.swing.JTextField q2AnsField;
    private javax.swing.JComboBox<String> q2ComboBox;
    private javax.swing.JButton regButton;
    private javax.swing.JRadioButton studentRadio;
    private javax.swing.JRadioButton teacherRadio;
    private javax.swing.ButtonGroup userTypeGrp;
    private javax.swing.JTextField usrNameField;
    // End of variables declaration//GEN-END:variables
}

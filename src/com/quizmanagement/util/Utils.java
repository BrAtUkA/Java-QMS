package com.quizmanagement.util;

import com.quizmanagement.ui.LoginFrame;
import com.quizmanagement.ui.styles;
import com.quizmanagement.ui.styles.Borders;

import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;


public class Utils {
    public void minimize(JFrame frame){
            frame.setExtendedState(JFrame.ICONIFIED);
    }
    
    public void exit(){
        System.exit(0);
    }
    
    public void moveTo(JFrame curr, JFrame next){
        Point location = curr.getLocation();
        next.setLocation(location);
        next.setVisible(true);
        curr.dispose();
    }    
    
    public void logout(JFrame curr) {
        int response = JOptionPane.showConfirmDialog(
            curr,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            LoginFrame next = new LoginFrame();
            moveTo(curr, next);
        } 
    }
    
    /**
     * Adds a simple DocumentListener to a text component that calls the callback
     * on any text change (insert, remove, or update).
     * 
     * @param component the text component to listen to (JTextField, JPasswordField, JTextArea, etc.)
     * @param callback the callback to execute on any document change
     */
    public static void addSimpleDocumentListener(JTextComponent component, Runnable callback) {
        component.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                callback.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                callback.run();
            }
        });
    }
     
    public void setSuccessMessage(String message, javax.swing.JLabel msgLabel, javax.swing.JTextField... fields) {
        msgLabel.setForeground(styles.SUCCESS_COLOR);
        msgLabel.setText(message); 

        if (fields != null) {
            for (javax.swing.JTextField field : fields) {
                field.setBackground(java.awt.Color.GREEN);
                field.setForeground(java.awt.Color.BLACK); 
                Borders.setSuccessBorder(field); 
            }
        }
    }
  
    
    public void setErrorMessage(String message, 
                                javax.swing.JComponent field,  
                                javax.swing.JLabel errMessage, 
                                boolean severeError) {
        setErrorMessage(message, field, errMessage, severeError, true);
    }

    public void setErrorMessage(String message, 
                                javax.swing.JComponent field,  
                                javax.swing.JLabel errMessage, 
                                boolean severeError, 
                                boolean fieldError) {

          if (severeError) errMessage.setForeground(styles.ERROR_COLOR);
          if (!severeError) errMessage.setForeground(styles.WARNING_COLOR);

          errMessage.setText(message);

          if (fieldError){
            if (field != null) {
                if (severeError) {
                   Borders.setErrorBorder(field);
                } else {
                    Borders.setWarningBorder(field);
                }
            }
          }
        }
}

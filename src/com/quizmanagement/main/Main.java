/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quizmanagement.main;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.quizmanagement.db.DBConnection;
import com.quizmanagement.ui.LoginFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author saadi
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(new FlatMacDarkLaf());
            
        } catch (UnsupportedLookAndFeelException ex) {
            logger.error("Failed to initialize FlatLaf theme", ex);
        }

        // Add shutdown hook to properly close database connection pool
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutting down, closing database connections...");
            DBConnection.shutdown();
        }));

        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

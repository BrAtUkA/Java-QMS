package com.quizmanagement.ui.teacher;

import com.quizmanagement.db.UserDAO;
import com.quizmanagement.objs.User;
import com.quizmanagement.ui.styles;
import static com.quizmanagement.ui.styles.*;
import com.quizmanagement.util.Utils;

import java.awt.*;
import javax.swing.*;


public class TeacherPortal extends javax.swing.JFrame {
    
    private String username;
    private static final Utils utils = new Utils();
    private final UserDAO userDAO = new UserDAO();
    private User user;
    
    private StatisticsPanel statisticsPanel;
    private JTabbedPane mainTabbedPane;
    private JPanel headerPanel;
    
    public TeacherPortal(String username) {
        this.username = username;
        this.user = userDAO.getUserByUsername(username);
        
        setUndecorated(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quiz Management System - Teacher");
        
        initUI();
        styles.Drag.addDragFunctionality(this);
        
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }
    
    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(DARKER_BG);
        setContentPane(contentPane);
        
        // Header
        headerPanel = createHeader();
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Main tabbed pane
        mainTabbedPane = new JTabbedPane();
        styles.styleTabPane(mainTabbedPane);
        
        CreateQuizPanel2 quizzesPanel2 = new CreateQuizPanel2(username);
        statisticsPanel = new StatisticsPanel(user);
        
        mainTabbedPane.addTab("  Create Quiz  ", quizzesPanel2);
        mainTabbedPane.addTab("  Statistics  ", statisticsPanel);
        
        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBackground(DARK_BG);
        tabWrapper.setBorder(BorderFactory.createEmptyBorder(0, SPACING_LG, SPACING_LG, SPACING_LG));
        tabWrapper.add(mainTabbedPane, BorderLayout.CENTER);
        
        contentPane.add(tabWrapper, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARKER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(SPACING_SM, SPACING_XL, SPACING_SM, 6)
        ));
        
        // Left - Title
        JLabel titleLabel = new JLabel("Quiz Management System");
        titleLabel.setFont(fontH2());
        titleLabel.setForeground(TEXT_COLOR);
        header.add(titleLabel, BorderLayout.WEST);
        
        // Right - User info and controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_MD, 0));
        rightPanel.setBackground(DARKER_BG);
        
        // User badge (circular avatar + name + role pill)
        JPanel userBadge = styles.UserBadge.create(username, "Teacher", SUCCESS_COLOR);
        rightPanel.add(userBadge);
        
        // Logout button (subtle ghost style)
        JButton logoutBtn = Buttons.createGhost("Logout");
        logoutBtn.addActionListener(e -> utils.logout(this));
        rightPanel.add(logoutBtn);
        
        // Window controls (matching auth pages: 23x19 buttons, 6px gap)
        JPanel windowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        windowControls.setBackground(DARKER_BG);
        
        JButton minBtn = Buttons.createWindowControl(new Color(224, 184, 43), new Color(200, 160, 30), 14);
        minBtn.setToolTipText("Minimize");
        minBtn.addActionListener(e -> utils.minimize(this));
        windowControls.add(minBtn);
        
        JButton closeBtn = Buttons.createWindowControl(new Color(204, 34, 34), new Color(180, 30, 30), 14);
        closeBtn.setToolTipText("Close");
        closeBtn.addActionListener(e -> utils.exit());
        windowControls.add(closeBtn);
        
        rightPanel.add(windowControls);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    // Keep old generated code for NetBeans compatibility but don't use it
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerText = new javax.swing.JLabel();
        exitButton = new javax.swing.JButton();
        minButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();
        MainViewPane = new javax.swing.JTabbedPane();
        logout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        headerText.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        headerText.setText("Teacher QMS - ");
        headerText.setToolTipText("");

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

        messageLabel.setText(" ");

        MainViewPane.setToolTipText("");

        logout.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        logout.setText("Logout >");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 847, Short.MAX_VALUE)
                        .addComponent(messageLabel)
                        .addGap(259, 259, 259))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(MainViewPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(headerText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logout)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(headerText, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(messageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(exitButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                            .addComponent(logout, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(minButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(MainViewPane, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitButtonMouseClicked
       utils.exit();
    }//GEN-LAST:event_exitButtonMouseClicked
    private void minButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minButtonMouseClicked
       utils.minimize(this);
    }//GEN-LAST:event_minButtonMouseClicked

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
       utils.logout(this);
    }//GEN-LAST:event_logoutMouseClicked

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane MainViewPane;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel headerText;
    private javax.swing.JButton logout;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton minButton;
    // End of variables declaration//GEN-END:variables
}

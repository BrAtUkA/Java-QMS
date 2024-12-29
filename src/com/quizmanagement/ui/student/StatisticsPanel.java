package com.quizmanagement.ui.student;

import com.quizmanagement.db.QuizDAO;
import com.quizmanagement.objs.ClassObj;
import com.quizmanagement.objs.Quiz;
import com.quizmanagement.objs.User;

import com.quizmanagement.ui.shared.TablePanel;
import com.quizmanagement.ui.shared.TablePanel.ColumnConfig;
import com.quizmanagement.ui.shared.TablePanel.FixedColumnPanel;
import com.quizmanagement.ui.styles;
import static com.quizmanagement.ui.styles.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class StatisticsPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsPanel.class);
    private final User user;
    private final QuizDAO quizDAO;
    private TablePanel tablePanel;
    private JPanel contentPanel;
    private List<ClassObj> classes;
    private JButton refreshButton;
    
    // Stat card labels for updating
    private JLabel totalAttemptedLabel;
    private JLabel avgScoreLabel;
    private JLabel bestScoreLabel;
    
    // Stats tracking
    private int totalAttempted = 0;
    private double avgScore = 0.0;
    private double bestScore = 0.0;
    
    private static final ColumnConfig[] COLUMNS = {
        new ColumnConfig("Quiz Title", 0.45),  
        new ColumnConfig("Score", 0.20),        
        new ColumnConfig("Status", 0.20, FlowLayout.RIGHT) 
    };

    public StatisticsPanel(User user, JTabbedPane mainViewPane) {
        this.user = user;
        this.quizDAO = new QuizDAO();
        
        setupUI();
        loadClasses();
    }

   
    private void refreshResults() {
        if (classSelector.getSelectedItem() != null) {
            String selected = classSelector.getSelectedItem().toString();
            int classId = Integer.parseInt(selected.split(":")[0].trim());
            refreshButton.setEnabled(false);
            
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    loadQuizResults(classId);
                    return null;
                }

                @Override
                protected void done() {
                    refreshButton.setEnabled(true);
                }
            };
            worker.execute();
        }
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(0, SPACING_LG));
        setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XL, SPACING_XL, SPACING_XL));
        setBackground(DARK_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(DARK_BG);
        
        tablePanel = new TablePanel(COLUMNS);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(SPACING_LG, SPACING_LG));
        headerPanel.setBackground(DARK_BG);
        
        // Top row: Title and controls
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(DARK_BG);
        
        JLabel titleLabel = new JLabel("My Results");
        titleLabel.setFont(fontH1());
        titleLabel.setForeground(TEXT_COLOR);
        topRow.add(titleLabel, BorderLayout.WEST);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_SM, 0));
        controlsPanel.setBackground(DARK_BG);

        classSelector = new JComboBox<>();
        classSelector.setBackground(CARD_BG);
        classSelector.setForeground(TEXT_COLOR);
        classSelector.setPreferredSize(new Dimension(200, 32));
        classSelector.setFont(fontBody());
        classSelector.addActionListener(e -> onClassSelected());

        refreshButton = Buttons.createRefreshIcon(28);
        refreshButton.addActionListener(e -> refreshResults());
        
        controlsPanel.add(classSelector);
        controlsPanel.add(refreshButton);
        
        topRow.add(controlsPanel, BorderLayout.EAST);
        headerPanel.add(topRow, BorderLayout.NORTH);
        
        // Stats row
        JPanel statsRow = createStatsRow();
        headerPanel.add(statsRow, BorderLayout.CENTER);

        return headerPanel;
    }
    
    private JPanel createStatsRow() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, SPACING_LG, 0));
        statsPanel.setBackground(DARK_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, 0, SPACING_MD, 0));
        
        // Total attempted
        JPanel attemptedCard = styles.StatCard.create("Quizzes Completed", "0", HIGHLIGHT);
        totalAttemptedLabel = findValueLabel(attemptedCard);
        statsPanel.add(attemptedCard);
        
        // Average score
        JPanel avgCard = styles.StatCard.create("Average Score", "N/A", WARNING_COLOR);
        avgScoreLabel = findValueLabel(avgCard);
        statsPanel.add(avgCard);
        
        // Best score
        JPanel bestCard = styles.StatCard.create("Best Score", "N/A", SUCCESS_COLOR);
        bestScoreLabel = findValueLabel(bestCard);
        statsPanel.add(bestCard);
        
        return statsPanel;
    }
    
    private JLabel findValueLabel(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getFont().getSize() >= FONT_H1) {
                    return label;
                }
            }
        }
        return null;
    }
    
    private void loadClasses() {
        classes = quizDAO.getClassesForStudent(user.getUserId());
        classSelector.removeAllItems();
        
        for (ClassObj classObj : classes) {
            classSelector.addItem(classObj.getClassId() + ": " + classObj.getClassName());
        }
        
        if (classSelector.getItemCount() > 0) {
            classSelector.setSelectedIndex(0);
        }
    }
    
    private void onClassSelected() {
        if (classSelector.getSelectedItem() != null) {
            String selected = classSelector.getSelectedItem().toString();
            int classId = Integer.parseInt(selected.split(":")[0].trim());
            
            loadQuizResults(classId);
        }
    }
    
    private void loadQuizResults(int classId) {
        tablePanel.clearRows();
        List<Quiz> quizzes = quizDAO.getQuizzesForClassDirectly(classId);
        Date currentDate = new Date();
        Font rowFont = fontBody();
        
        // Reset stats
        totalAttempted = 0;
        double totalScore = 0;
        bestScore = 0;

        for (Quiz quiz : quizzes) {
            if (quiz.getDeadline() != null && quiz.getDeadline().before(currentDate) 
                && quizDAO.hasAttemptedQuiz(user.getUserId(), quiz.getQuizId())) {
                
                double score = addQuizResultRow(quiz, rowFont);
                if (score >= 0) {
                    totalAttempted++;
                    totalScore += score;
                    if (score > bestScore) {
                        bestScore = score;
                    }
                }
            }
        }
        
        // Calculate average
        avgScore = totalAttempted > 0 ? totalScore / totalAttempted : 0;
        
        // Update stat cards
        updateStats();
        
        // Show empty state if no results
        updateContentVisibility();
    }
    
    private void updateStats() {
        if (totalAttemptedLabel != null) {
            totalAttemptedLabel.setText(String.valueOf(totalAttempted));
        }
        if (avgScoreLabel != null) {
            if (totalAttempted > 0) {
                avgScoreLabel.setText(String.format("%.1f%%", avgScore));
            } else {
                avgScoreLabel.setText("N/A");
            }
        }
        if (bestScoreLabel != null) {
            if (totalAttempted > 0) {
                bestScoreLabel.setText(String.format("%.1f%%", bestScore));
            } else {
                bestScoreLabel.setText("N/A");
            }
        }
    }
    
    private void updateContentVisibility() {
        contentPanel.removeAll();
        
        if (totalAttempted == 0) {
            JPanel emptyState = styles.EmptyState.create(
                "No results yet",
                "Complete quizzes to see your results here."
            );
            contentPanel.add(emptyState, BorderLayout.CENTER);
        } else {
            contentPanel.add(tablePanel, BorderLayout.CENTER);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private double addQuizResultRow(Quiz quiz, Font font) {
        try {
            int attemptId = quizDAO.getLatestAttemptId(user.getUserId(), quiz.getQuizId());
            if (attemptId == -1) return -1;

            double score = quizDAO.getScoreForAttempt(attemptId);
            JPanel row = tablePanel.createRow();

            // Title 
            ((FixedColumnPanel)row.getComponent(0)).add(
                TablePanel.createStyledLabel(quiz.getTitle(), font, true));

            // Score
            JLabel scoreLabel = createScoreLabel(score, font);
            ((FixedColumnPanel)row.getComponent(1)).add(scoreLabel);

            // Pass/Fail status
            JLabel statusLabel = createStatusLabel(score, font);
            ((FixedColumnPanel)row.getComponent(2)).add(statusLabel);
            
            return score;

        } catch (SQLException e) {
            logger.error("Failed to load quiz result for quiz ID: {}", quiz.getQuizId(), e);
            return -1;
        }
    }
    
    private JLabel createScoreLabel(double score, Font font) {
        JLabel label = new JLabel(String.format("%.1f%%", score));
        label.setFont(font.deriveFont(Font.BOLD));
        
        if (score >= 80) {
            label.setForeground(new Color(46, 204, 113));
        } else if (score >= 60) {
            label.setForeground(new Color(241, 196, 15));
        } else {
            label.setForeground(new Color(231, 76, 60));
        }
        
        return label;
    }
    
    private JLabel createStatusLabel(double score, Font font) {
        String status = score >= 60 ? "PASSED" : "FAILED";
        JLabel label = new JLabel(status);
        label.setFont(font.deriveFont(Font.BOLD));
        label.setForeground(score >= 60 ? 
            new Color(46, 204, 113) : new Color(231, 76, 60));
        return label;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        classSelector = new javax.swing.JComboBox<>();

        classSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 768, Short.MAX_VALUE)
                .addComponent(classSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(classSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(537, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> classSelector;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}

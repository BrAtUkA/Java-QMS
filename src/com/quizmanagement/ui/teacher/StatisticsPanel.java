package com.quizmanagement.ui.teacher;

import com.quizmanagement.db.QuizDAO;
import com.quizmanagement.objs.Quiz;
import com.quizmanagement.objs.User;
import com.quizmanagement.ui.shared.TablePanel;
import com.quizmanagement.ui.shared.TablePanel.ColumnConfig;
import com.quizmanagement.ui.shared.TablePanel.FixedColumnPanel;
import com.quizmanagement.ui.styles;
import static com.quizmanagement.ui.styles.*;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class StatisticsPanel extends javax.swing.JPanel {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    
    private final User user;
    private final QuizDAO quizDAO;
    private TablePanel tablePanel;
    private JPanel contentPanel;
    private JLabel totalQuizzesLabel;
    private JLabel totalAttemptsLabel;
    private JLabel avgScoreLabel;
    
    private int totalQuizzes = 0;
    private int totalAttempts = 0;
    private double overallAvgScore = 0.0;
    
    private static final ColumnConfig[] COLUMNS = {
        new ColumnConfig("Quiz Name", 0.25),
        new ColumnConfig("Class", 0.20),
        new ColumnConfig("Attempts", 0.12),
        new ColumnConfig("Avg Score", 0.15),
        new ColumnConfig("Pass Rate", 0.20, FlowLayout.CENTER)
    };
    
    public StatisticsPanel(User user) {
        this.user = user;
        this.quizDAO = new QuizDAO();
        
        setupUI();
        loadStatistics();
    }
    
    // Keep no-arg constructor for compatibility
    public StatisticsPanel() {
        this.user = null;
        this.quizDAO = new QuizDAO();
        
        setupUI();
        showNoUserState();
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
        
        // Top row: Title and refresh button
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(DARK_BG);
        
        JLabel titleLabel = new JLabel("Quiz Statistics");
        titleLabel.setFont(fontH1());
        titleLabel.setForeground(TEXT_COLOR);
        topRow.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = Buttons.createRefreshIcon(28);
        refreshButton.addActionListener(e -> refreshStatistics());
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setBackground(DARK_BG);
        buttonWrapper.add(refreshButton);
        topRow.add(buttonWrapper, BorderLayout.EAST);
        
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
        
        // Total quizzes
        JPanel quizzesCard = styles.StatCard.create("Total Quizzes", "0", HIGHLIGHT);
        totalQuizzesLabel = findValueLabel(quizzesCard);
        statsPanel.add(quizzesCard);
        
        // Total attempts
        JPanel attemptsCard = styles.StatCard.create("Total Attempts", "0", SUCCESS_COLOR);
        totalAttemptsLabel = findValueLabel(attemptsCard);
        statsPanel.add(attemptsCard);
        
        // Overall avg score
        JPanel avgCard = styles.StatCard.create("Overall Avg Score", "N/A", WARNING_COLOR);
        avgScoreLabel = findValueLabel(avgCard);
        statsPanel.add(avgCard);
        
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
    
    private void showNoUserState() {
        contentPanel.removeAll();
        JPanel emptyState = styles.EmptyState.create(
            "No user data",
            "Unable to load statistics without user information."
        );
        contentPanel.add(emptyState, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void loadStatistics() {
        if (user == null) {
            showNoUserState();
            return;
        }
        
        List<Quiz> quizzes = quizDAO.getQuizzesByTeacher(user.getUserId());
        Font rowFont = fontBody();
        
        totalQuizzes = quizzes.size();
        totalAttempts = 0;
        double totalScoreSum = 0;
        int scoredQuizzes = 0;
        
        for (Quiz quiz : quizzes) {
            int attempts = quizDAO.getAttemptCountForQuiz(quiz.getQuizId());
            double avgScore = quizDAO.getAverageScoreForQuiz(quiz.getQuizId());
            
            totalAttempts += attempts;
            if (attempts > 0 && avgScore >= 0) {
                totalScoreSum += avgScore;
                scoredQuizzes++;
            }
            
            addQuizRow(quiz, attempts, avgScore, rowFont);
        }
        
        overallAvgScore = scoredQuizzes > 0 ? totalScoreSum / scoredQuizzes : 0;
        
        updateStats();
        updateContentVisibility();
    }
    
    private void updateStats() {
        if (totalQuizzesLabel != null) {
            totalQuizzesLabel.setText(String.valueOf(totalQuizzes));
        }
        if (totalAttemptsLabel != null) {
            totalAttemptsLabel.setText(String.valueOf(totalAttempts));
        }
        if (avgScoreLabel != null) {
            if (totalAttempts > 0) {
                avgScoreLabel.setText(String.format("%.1f%%", overallAvgScore));
            } else {
                avgScoreLabel.setText("N/A");
            }
        }
    }
    
    private void updateContentVisibility() {
        contentPanel.removeAll();
        
        if (totalQuizzes == 0) {
            JPanel emptyState = styles.EmptyState.create(
                "No quizzes yet",
                "Create your first quiz to start seeing statistics here."
            );
            contentPanel.add(emptyState, BorderLayout.CENTER);
        } else {
            contentPanel.add(tablePanel, BorderLayout.CENTER);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private static final double PASSING_THRESHOLD = 50.0; // 50% to pass
    
    private void addQuizRow(Quiz quiz, int attempts, double avgScore, Font font) {
        JPanel row = tablePanel.createRow();
        
        // Quiz Name
        ((FixedColumnPanel)row.getComponent(0)).add(
            TablePanel.createStyledLabel(quiz.getTitle(), font, true));
        
        // Class
        String className = quizDAO.getClassNameByQuizId(quiz.getQuizId());
        ((FixedColumnPanel)row.getComponent(1)).add(
            TablePanel.createStyledLabel(className, font, false));
        
        // Attempts
        ((FixedColumnPanel)row.getComponent(2)).add(
            TablePanel.createStyledLabel(String.valueOf(attempts), font, false));
        
        // Avg Score
        String avgScoreText = attempts > 0 ? String.format("%.1f%%", avgScore) : "N/A";
        JLabel avgLabel = TablePanel.createStyledLabel(avgScoreText, font, false);
        if (attempts > 0) {
            if (avgScore >= 70) {
                avgLabel.setForeground(SUCCESS_COLOR);
            } else if (avgScore >= 50) {
                avgLabel.setForeground(WARNING_COLOR);
            } else {
                avgLabel.setForeground(DANGER_COLOR);
            }
        }
        ((FixedColumnPanel)row.getComponent(3)).add(avgLabel);
        
        // Pass Rate (actual percentage of students who passed)
        double passRate = attempts > 0 ? quizDAO.getPassRateForQuiz(quiz.getQuizId(), PASSING_THRESHOLD) : -1;
        JPanel passRateBar = createPassRateBar(passRate, attempts > 0 && passRate >= 0);
        ((FixedColumnPanel)row.getComponent(4)).add(passRateBar);
    }
    
    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 10;
    private static final int LABEL_WIDTH = 45;
    
    private JPanel createPassRateBar(double percentage, boolean hasData) {
        // Use BoxLayout for precise horizontal alignment
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setOpaque(false);
        
        // Fixed-size progress bar with rounded corners
        JPanel barOuter = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background track
                g2.setColor(DARKER_BG);
                g2.fillRoundRect(0, 0, BAR_WIDTH, BAR_HEIGHT, 5, 5);
                
                // Draw filled portion
                if (hasData && percentage > 0) {
                    Color fillColor;
                    if (percentage >= 70) {
                        fillColor = SUCCESS_COLOR;
                    } else if (percentage >= 50) {
                        fillColor = WARNING_COLOR;
                    } else {
                        fillColor = DANGER_COLOR;
                    }
                    g2.setColor(fillColor);
                    int fillWidth = (int) (BAR_WIDTH * (percentage / 100.0));
                    g2.fillRoundRect(0, 0, Math.max(fillWidth, 5), BAR_HEIGHT, 5, 5);
                }
                
                g2.dispose();
            }
        };
        barOuter.setOpaque(false);
        barOuter.setPreferredSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));
        barOuter.setMinimumSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));
        barOuter.setMaximumSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));
        
        container.add(barOuter);
        container.add(Box.createHorizontalStrut(SPACING_SM));
        
        // Fixed-width percentage label for alignment
        JLabel percentLabel = new JLabel(hasData ? String.format("%.0f%%", percentage) : "-");
        percentLabel.setFont(fontSmall());
        percentLabel.setForeground(MUTED_TEXT);
        percentLabel.setPreferredSize(new Dimension(LABEL_WIDTH, BAR_HEIGHT + 4));
        percentLabel.setMinimumSize(new Dimension(LABEL_WIDTH, BAR_HEIGHT + 4));
        percentLabel.setMaximumSize(new Dimension(LABEL_WIDTH, BAR_HEIGHT + 4));
        container.add(percentLabel);
        
        return container;
    }
    
    public void refreshStatistics() {
        SwingUtilities.invokeLater(() -> {
            tablePanel.clearRows();
            loadStatistics();
        });
    }
    
    private void initCustom() {
        // Legacy method - kept for compatibility
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1084, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

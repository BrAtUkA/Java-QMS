package com.quizmanagement.ui.student;

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
import java.util.Date;
import java.util.List;

public class Dashboard extends JPanel {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    
    private final User user;
    private final QuizDAO quizDAO;
    private final JTabbedPane mainViewPane;
    private TablePanel tablePanel;
    private JPanel contentPanel;
    private JLabel pendingCountLabel;
    private JLabel completedCountLabel;
    private JLabel avgScoreLabel;
    
    private int pendingCount = 0;
    private int completedCount = 0;
    private double avgScore = 0.0;
    
    private static final ColumnConfig[] COLUMNS = {
        new ColumnConfig("Quiz Name", 0.220),
        new ColumnConfig("Class Name", 0.255),
        new ColumnConfig("Deadline", 0.120),
        new ColumnConfig("Duration", 0.080),
        new ColumnConfig("Questions", 0.100),
        new ColumnConfig("", 0.110, FlowLayout.RIGHT)
    };
    
    public Dashboard(User user, JTabbedPane mainViewPane) {
        this.user = user;
        this.mainViewPane = mainViewPane;
        this.quizDAO = new QuizDAO();
        
        setupUI();
        loadQuizzes();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(0, SPACING_LG));
        setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XL, SPACING_XL, SPACING_XL));
        setBackground(DARK_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Content area that will hold either the table or empty state
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(DARK_BG);
        
        tablePanel = new TablePanel(COLUMNS);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(SPACING_LG, SPACING_LG));
        headerPanel.setBackground(DARK_BG);
        
        // Top row: Welcome text and refresh button
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(DARK_BG);

        JLabel welcomeLabel = new JLabel("Welcome back, " + user.getUsername());
        welcomeLabel.setFont(fontH1());
        welcomeLabel.setForeground(TEXT_COLOR);
        topRow.add(welcomeLabel, BorderLayout.WEST);

        JButton refreshButton = Buttons.createRefreshIcon(28);
        refreshButton.addActionListener(e -> refreshDashboard());
        
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
        
        // Pending quizzes stat
        JPanel pendingCard = styles.StatCard.create("Pending Quizzes", "0", WARNING_COLOR);
        pendingCountLabel = findValueLabel(pendingCard);
        statsPanel.add(pendingCard);
        
        // Completed quizzes stat
        JPanel completedCard = styles.StatCard.create("Completed", "0", SUCCESS_COLOR);
        completedCountLabel = findValueLabel(completedCard);
        statsPanel.add(completedCard);
        
        // Average score stat
        JPanel avgCard = styles.StatCard.create("Average Score", "N/A", HIGHLIGHT);
        avgScoreLabel = findValueLabel(avgCard);
        statsPanel.add(avgCard);
        
        return statsPanel;
    }
    
    // Helper to find the value label in a stat card
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
    
    private void loadQuizzes() {
        List<Quiz> quizzes = quizDAO.getAllQuizzesForStudent(user.getUserId());
        Font rowFont = fontBody();
        
        // Reset counts
        pendingCount = 0;
        completedCount = 0;
        double totalScore = 0;
        int scoredQuizzes = 0;
        int rowsAdded = 0;

        for (Quiz quiz : quizzes) {
            boolean hasAttempted = quizDAO.hasAttemptedQuiz(user.getUserId(), quiz.getQuizId());
            boolean deadlinePassed = quiz.getDeadline() != null && isDeadlinePassed(quiz.getDeadline());
            
            if (hasAttempted) {
                if (deadlinePassed) {
                    // Deadline passed - result is revealed, count as completed
                    Double score = quizDAO.getQuizScore(user.getUserId(), quiz.getQuizId());
                    completedCount++;
                    if (score != null) {
                        totalScore += score;
                        scoredQuizzes++;
                    }
                    // Don't show in table - it's done
                } else {
                    // Attempted but deadline not passed - result not revealed yet
                    // Show as "Attempted" so student knows they've done it
                    addAttemptedQuizRow(quiz, rowFont);
                    rowsAdded++;
                }
            } else if (!deadlinePassed) {
                // Not attempted yet and deadline not passed - show with Start button
                pendingCount++;
                addQuizRow(quiz, rowFont);
                rowsAdded++;
            }
            // Skip non-attempted quizzes with passed deadlines
        }
        
        avgScore = scoredQuizzes > 0 ? totalScore / scoredQuizzes : 0;
        
        // Update stat cards
        updateStats();
        
        // Show empty state if no rows in table
        updateContentVisibility(rowsAdded);
    }
    
    private boolean isDeadlinePassed(Date deadline) {
        if (deadline == null) return false;
        // Compare dates only (ignore time) - deadline day is still valid
        java.util.Calendar deadlineCal = java.util.Calendar.getInstance();
        deadlineCal.setTime(deadline);
        deadlineCal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        deadlineCal.set(java.util.Calendar.MINUTE, 59);
        deadlineCal.set(java.util.Calendar.SECOND, 59);
        return new Date().after(deadlineCal.getTime());
    }
    
    private void updateStats() {
        if (pendingCountLabel != null) {
            pendingCountLabel.setText(String.valueOf(pendingCount));
        }
        if (completedCountLabel != null) {
            completedCountLabel.setText(String.valueOf(completedCount));
        }
        if (avgScoreLabel != null) {
            if (completedCount > 0) {
                avgScoreLabel.setText(String.format("%.1f%%", avgScore));
            } else {
                avgScoreLabel.setText("N/A");
            }
        }
    }
    
    private void updateContentVisibility(int rowsAdded) {
        contentPanel.removeAll();
        
        // Show empty state if no rows in the table
        if (rowsAdded == 0) {
            String title, message;
            if (completedCount > 0) {
                title = "All caught up!";
                message = "You've completed all your quizzes. Check back later for new assignments.";
            } else {
                title = "No quizzes yet!";
                message = "You have no quizzes assigned. Check back later for new assignments.";
            }
            JPanel emptyState = styles.EmptyState.create(title, message);
            contentPanel.add(emptyState, BorderLayout.CENTER);
        } else {
            contentPanel.add(tablePanel, BorderLayout.CENTER);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void addQuizRow(Quiz quiz, Font font) {
        JPanel row = tablePanel.createRow();
        
        // Add columns
        ((FixedColumnPanel)row.getComponent(0)).add(
            TablePanel.createStyledLabel(quiz.getTitle(), font, true));
            
        ((FixedColumnPanel)row.getComponent(1)).add(
            TablePanel.createStyledLabel(quizDAO.getClassNameByQuizId(quiz.getQuizId()), font, false));
        
        // Format deadline with color coding
        JLabel deadlineLabel = TablePanel.createStyledLabel(formatDeadline(quiz.getDeadline()), font, false);
        if (isDeadlineSoon(quiz.getDeadline())) {
            deadlineLabel.setForeground(WARNING_COLOR);
        }
        ((FixedColumnPanel)row.getComponent(2)).add(deadlineLabel);
            
        ((FixedColumnPanel)row.getComponent(3)).add(
            TablePanel.createStyledLabel(quiz.getTimeLimit() + " min", font, false));
            
        ((FixedColumnPanel)row.getComponent(4)).add(
            TablePanel.createStyledLabel(String.valueOf(quiz.getQuestions().size()), font, false));
        
        // Add action button
        FixedColumnPanel buttonColumn = (FixedColumnPanel)row.getComponent(5);
        JButton startButton = Buttons.createTableCTA("Start");
        startButton.addActionListener(e -> startQuiz(quiz));
        buttonColumn.add(startButton);
    }
    
    private void addAttemptedQuizRow(Quiz quiz, Font font) {
        JPanel row = tablePanel.createRow();
        
        // Quiz name
        ((FixedColumnPanel)row.getComponent(0)).add(
            TablePanel.createStyledLabel(quiz.getTitle(), font, true));
            
        // Class name
        ((FixedColumnPanel)row.getComponent(1)).add(
            TablePanel.createStyledLabel(quizDAO.getClassNameByQuizId(quiz.getQuizId()), font, false));
        
        // Deadline (same as pending quizzes)
        JLabel deadlineLabel = TablePanel.createStyledLabel(formatDeadline(quiz.getDeadline()), font, false);
        ((FixedColumnPanel)row.getComponent(2)).add(deadlineLabel);
            
        // Duration
        ((FixedColumnPanel)row.getComponent(3)).add(
            TablePanel.createStyledLabel(quiz.getTimeLimit() + " min", font, false));
            
        // Questions
        ((FixedColumnPanel)row.getComponent(4)).add(
            TablePanel.createStyledLabel(String.valueOf(quiz.getQuestions().size()), font, false));
        
        // "Attempted" button (disabled style)
        FixedColumnPanel buttonColumn = (FixedColumnPanel)row.getComponent(5);
        JButton attemptedButton = Buttons.createTableCTA("Attempted");
        attemptedButton.setEnabled(false);
        buttonColumn.add(attemptedButton);
    }
    
    private boolean isDeadlineSoon(Date deadline) {
        if (deadline == null) return false;
        long diff = deadline.getTime() - System.currentTimeMillis();
        long hoursUntilDeadline = diff / (1000 * 60 * 60);
        return hoursUntilDeadline <= 24 && hoursUntilDeadline > 0;
    }
    
    private String formatDeadline(Date deadline) {
        return deadline != null ? DATE_FORMAT.format(deadline) : "No deadline";
    }
    
    public void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            tablePanel.clearRows();
            loadQuizzes();
        });
    }
    
    private void startQuiz(Quiz quiz) {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Do you want to start the quiz: " + quiz.getTitle() + "?",
            "Confirm Start Quiz",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            AttemptQuizPanel quizPanel = new AttemptQuizPanel(user, 1, quiz, mainViewPane);
            mainViewPane.addTab("Quiz", quizPanel);
            mainViewPane.setSelectedComponent(quizPanel);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1032, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

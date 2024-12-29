package com.quizmanagement.ui.student;

import com.quizmanagement.db.QuizDAO;
import com.quizmanagement.objs.MCQQuestion;
import com.quizmanagement.objs.Question;
import com.quizmanagement.objs.Quiz;
import com.quizmanagement.objs.User;
import com.quizmanagement.ui.student.questions.AttemptMCQ;
import com.quizmanagement.ui.styles;
import static com.quizmanagement.ui.styles.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AttemptQuizPanel extends javax.swing.JPanel {
    private static final Logger logger = LoggerFactory.getLogger(AttemptQuizPanel.class);
    private int currentQuestionIndex = 0;
    private final javax.swing.JTabbedPane MainViewPane;
    private Timer countdownTimer;
    private int timeRemainingInSeconds;
    private int totalTimeInSeconds;
    
    private final List<JPanel> qPanels = new ArrayList<>();
    private final List<Question> questions;
    private final List<String> userAnswers = new ArrayList<>();

    private final Quiz quiz;
    private final User user;
    private final QuizDAO quizDAO = new QuizDAO();
    
    // Custom UI components
    private JLabel timerLabel;
    private JPanel timerPanel;
    private JPanel progressPanel;
    private JLabel progressLabel;
    private JPanel progressBarContainer;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JTabbedPane questionTabs;
    private JButton prevButton;
    private JButton nextBtn;


    public AttemptQuizPanel(User user, int classId, Quiz quiz, javax.swing.JTabbedPane MainViewPane) {
        this.MainViewPane = MainViewPane;
        this.user = user;
        this.quiz = quiz;
        this.questions = quiz.getQuestions();
        this.totalTimeInSeconds = quiz.getTimeLimit() * 60;

        initCustomUI();
        setupQuiz();
        initTimer();
    }
    
    private void initCustomUI() {
        setLayout(new BorderLayout(0, SPACING_MD));
        setBackground(DARK_BG);
        setBorder(BorderFactory.createEmptyBorder(SPACING_XL, SPACING_XL, SPACING_XL, SPACING_XL));
        
        // Header with quiz info, timer, and progress
        headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Content area for questions
        contentPanel = new JPanel(new BorderLayout(0, SPACING_MD));
        contentPanel.setBackground(DARK_BG);
        
        questionTabs = new JTabbedPane();
        styles.styleTabPane(questionTabs);
        contentPanel.add(questionTabs, BorderLayout.CENTER);
        
        // Navigation buttons
        JPanel navPanel = createNavigationPanel();
        contentPanel.add(navPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(SPACING_XL, SPACING_MD));
        header.setBackground(DARK_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, SPACING_MD, 0));
        
        // Left side: Quiz title
        JLabel titleLabel = new JLabel("Quiz: " + quiz.getTitle());
        titleLabel.setFont(fontH2());
        titleLabel.setForeground(TEXT_COLOR);
        header.add(titleLabel, BorderLayout.WEST);
        
        // Right side: Timer panel
        timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_SM, 0));
        timerPanel.setBackground(CARD_BG);
        timerPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD));
        
        JLabel timerIcon = new JLabel("Time: ");
        timerIcon.setFont(fontBody());
        timerIcon.setForeground(MUTED_TEXT);
        timerPanel.add(timerIcon);
        
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_H2));
        timerLabel.setForeground(TEXT_COLOR);
        timerPanel.add(timerLabel);
        
        header.add(timerPanel, BorderLayout.EAST);
        
        // Center: Progress section
        JPanel progressSection = new JPanel(new BorderLayout(SPACING_SM, SPACING_XS));
        progressSection.setBackground(DARK_BG);
        
        progressLabel = new JLabel("Question 1 of " + questions.size());
        progressLabel.setFont(fontSmall());
        progressLabel.setForeground(MUTED_TEXT);
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progressSection.add(progressLabel, BorderLayout.NORTH);
        
        progressBarContainer = styles.ProgressBar.create(0, questions.size());
        progressSection.add(progressBarContainer, BorderLayout.CENTER);
        
        header.add(progressSection, BorderLayout.CENTER);
        
        return header;
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(DARK_BG);
        navPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, 0, 0, 0));
        
        prevButton = Buttons.createSecondary("Previous");
        prevButton.addActionListener(e -> navigatePrevious());
        
        nextBtn = Buttons.createPrimary("Next");
        nextBtn.addActionListener(e -> navigateNext());
        
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftNav.setBackground(DARK_BG);
        leftNav.add(prevButton);
        navPanel.add(leftNav, BorderLayout.WEST);
        
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightNav.setBackground(DARK_BG);
        rightNav.add(nextBtn);
        navPanel.add(rightNav, BorderLayout.EAST);
        
        return navPanel;
    }

    private void setupQuiz() {
        for (Question q : questions) {
            if (q instanceof MCQQuestion) {
                addQuestionPanel((MCQQuestion) q);
            }
        }
        
        updateNavigationButtons();
        updateQuizDisplay();
    }

    private void updateQuizDisplay() {
        updateQuestionNumberLabel();
        questionTabs.removeAll();
        if (!qPanels.isEmpty()) {
            questionTabs.addTab("Question " + (currentQuestionIndex + 1), qPanels.get(currentQuestionIndex));
        }
    }

    private void initTimer() {
        timeRemainingInSeconds = totalTimeInSeconds;
        updateTimerLabel();
        countdownTimer = new Timer(1000, e -> updateCountdown());
        countdownTimer.start();
    }

    private void updateCountdown() {
        if (timeRemainingInSeconds > 0) {
            timeRemainingInSeconds--;
            updateTimerLabel();
        } else {
            countdownTimer.stop();
            JOptionPane.showMessageDialog(this, "Time's up! The quiz has ended.");
            finishQuiz();
        }
    }

    private void updateTimerLabel() {
        int minutes = timeRemainingInSeconds / 60;
        int seconds = timeRemainingInSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        
        // Color-code timer based on remaining time
        double percentRemaining = (double) timeRemainingInSeconds / totalTimeInSeconds;
        
        if (percentRemaining <= 0.1) {
            // Less than 10% time: Red, pulsing
            timerLabel.setForeground(DANGER_COLOR);
            timerPanel.setBackground(new Color(DANGER_COLOR.getRed(), DANGER_COLOR.getGreen(), DANGER_COLOR.getBlue(), 40));
        } else if (percentRemaining <= 0.25) {
            // Less than 25% time: Orange/Warning
            timerLabel.setForeground(WARNING_COLOR);
            timerPanel.setBackground(CARD_BG);
        } else {
            // Normal: White
            timerLabel.setForeground(TEXT_COLOR);
            timerPanel.setBackground(CARD_BG);
        }
    }

    private void addQuestionPanel(MCQQuestion q) {
        JPanel newPanel = new AttemptMCQ(q);
        qPanels.add(newPanel);
        userAnswers.add(null);
    }

    private void updateNavigationButtons() {
        prevButton.setEnabled(currentQuestionIndex > 0);
        
        boolean isLastQuestion = currentQuestionIndex == qPanels.size() - 1;
        nextBtn.setText(isLastQuestion ? "Finish Quiz" : "Next");
        
        // Style finish button differently
        if (isLastQuestion) {
            nextBtn.setBackground(SUCCESS_COLOR);
        } else {
            nextBtn.setBackground(HIGHLIGHT);
        }
    }

    private void updateQuestionNumberLabel() {
        int current = currentQuestionIndex + 1;
        int total = qPanels.isEmpty() ? 0 : qPanels.size();
        
        progressLabel.setText("Question " + current + " of " + total);
        
        // Update progress bar
        styles.ProgressBar.update(progressBarContainer, current, total);
    }
    
    private void navigatePrevious() {
        if (currentQuestionIndex > 0) {
            storeCurrentAnswer();
            currentQuestionIndex--;
            questionTabs.removeAll();
            questionTabs.addTab("Question " + (currentQuestionIndex + 1), qPanels.get(currentQuestionIndex));
            updateNavigationButtons();
            updateQuestionNumberLabel();
        }
    }
    
    private void navigateNext() {
        AttemptMCQ currentPanel = (AttemptMCQ) qPanels.get(currentQuestionIndex);
        
        if (!currentPanel.isAnswerSelected()) {
            currentPanel.showError("Please select an answer before proceeding", null, true);
            return;
        }
        
        currentPanel.clearErrorState();
        
        if (currentQuestionIndex == qPanels.size() - 1) {
            finishQuiz();
        } else {
            storeCurrentAnswer();
            currentQuestionIndex++;
            questionTabs.removeAll();
            questionTabs.addTab("Question " + (currentQuestionIndex + 1), qPanels.get(currentQuestionIndex));
            updateNavigationButtons();
            updateQuestionNumberLabel();
        }
    }

    private void finishQuiz() {
        storeCurrentAnswer();
        saveQuizResults();
    }

    private void saveQuizResults() {
        try {
            storeCurrentAnswer();

            int attemptId = quizDAO.createQuizAttempt(user.getUserId(), quiz.getQuizId());

            // unanswered questions are -1
            while (userAnswers.size() < questions.size()) {
                userAnswers.add("-1");
            }

            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                String userAns = userAnswers.get(i);
                if (userAns == null || userAns.isEmpty()) {
                    userAns = "-1";
                }
                quizDAO.saveAnswer(attemptId, q.getQuestionId(), userAns);
            }

            double finalScore = quizDAO.computeScoreForAttempt(attemptId);
            quizDAO.insertResult(attemptId, finalScore);

            showCompletionMessage(finalScore);
            removeQuizTab();

        } catch (Exception e) {
            logger.error("Failed to save quiz results for user {} and quiz {}", user.getUserId(), quiz.getQuizId(), e);
            JOptionPane.showMessageDialog(this, 
                "Error saving quiz results. Please contact your teacher.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCompletionMessage(double score) {
        String message = String.format("Quiz completed! Your score: %.2f%%", score);
        JOptionPane.showMessageDialog(this, message);
    }

    private void removeQuizTab() {
        if (MainViewPane != null) {
            int index = MainViewPane.indexOfComponent(this);
            if (index != -1) {
                MainViewPane.remove(index);
            }
        }
    }

    private void storeCurrentAnswer() {
        try {
            JPanel panel = qPanels.get(currentQuestionIndex);
            if (panel instanceof AttemptMCQ) {
                AttemptMCQ mcqPanel = (AttemptMCQ) panel;
                int selectedIndex = mcqPanel.getSelectedOptionIndex();
                // If no answer selected, store as -1
                String answer = (selectedIndex == -1) ? "-1" : String.valueOf(selectedIndex);
                userAnswers.set(currentQuestionIndex, answer);
            }
        } catch (Exception e) {
            // If any error occurs during storing the answer, use -1
            if (currentQuestionIndex < userAnswers.size()) {
                userAnswers.set(currentQuestionIndex, "-1");
            }
        }
    }

    public void discardQuizAttempt() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
        userAnswers.clear();
    }
    
    // Keep generated code for NetBeans compatibility but don't use it
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nextButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        qPanel = new javax.swing.JTabbedPane();
        jSeparator3 = new javax.swing.JSeparator();
        errMessage = new javax.swing.JLabel();
        quizName = new javax.swing.JLabel();
        TimerLabel = new javax.swing.JLabel();
        qNumber = new javax.swing.JLabel();

        nextButton.setBackground(new java.awt.Color(43, 99, 223));
        nextButton.setText("Next >");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        backButton.setText("< Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        qPanel.setPreferredSize(new java.awt.Dimension(653, 490));

        errMessage.setText(" ");

        quizName.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        quizName.setText("Quiz Title");

        TimerLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        TimerLabel.setText("00:00:00");

        qNumber.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        qNumber.setText("1/10");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator3)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(quizName)
                        .addGap(91, 91, 91)
                        .addComponent(errMessage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(qNumber)
                        .addGap(18, 18, 18)
                        .addComponent(TimerLabel)
                        .addGap(20, 20, 20))))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nextButton))
                    .addComponent(qPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(errMessage)
                            .addComponent(TimerLabel)
                            .addComponent(qNumber))
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(quizName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(backButton))
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
      
    // Event Handlers / nav
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        navigateNext();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        navigatePrevious();
    }//GEN-LAST:event_backButtonActionPerformed
       

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel TimerLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JLabel errMessage;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton nextButton;
    private javax.swing.JLabel qNumber;
    private javax.swing.JTabbedPane qPanel;
    private javax.swing.JLabel quizName;
    // End of variables declaration//GEN-END:variables
}

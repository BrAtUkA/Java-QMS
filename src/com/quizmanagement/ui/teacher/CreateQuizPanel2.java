package com.quizmanagement.ui.teacher;

import com.quizmanagement.db.QuizDAO;
import com.quizmanagement.db.UserDAO;
import com.quizmanagement.objs.*;
import com.quizmanagement.ui.shared.MCQOptionPanel;
import com.quizmanagement.ui.styles;
import static com.quizmanagement.ui.styles.*;
import com.quizmanagement.util.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizPanel2 extends JPanel {
    
    // Data
    private final String username;
    private User user;
    private final UserDAO userDAO = new UserDAO();
    private final QuizDAO quizDAO = new QuizDAO();
    private final Utils utils = new Utils();
    
    private List<ClassObj> classes = new ArrayList<>();
    private List<Quiz> quizzes = new ArrayList<>();
    private Quiz currentQuiz = new Quiz();
    private final List<QuestionData> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    
    // State
    private boolean loadingQuiz = false;
    private boolean isModified = false;
    private int lastSelectedQuizIndex = -1;
    private boolean isTempQuizActive = false;
    private boolean settingsExpanded = false;
    
    // UI Components - Header
    private JComboBox<String> classSelector;
    private JComboBox<String> quizSelector;
    private JTextField titleField;
    private JLabel errorLabel;
    private JLabel unsavedIndicator;
    
    // UI Components - Settings (collapsible)
    private JPanel settingsPanel;
    private JButton settingsToggle;
    private JTextArea descriptionArea;
    private JSpinner timeLimitSpinner;
    private JSpinner totalQuestionsSpinner;
    private JSpinner deadlineMonthSpinner;
    private JSpinner deadlineDaySpinner;
    private JSpinner deadlineYearSpinner;
    
    // UI Components - Question Editor
    private JPanel questionCard;
    private JLabel questionNumberLabel;
    private JPanel dotsPanel;
    private JTextArea questionTextArea;
    private JSpinner pointsSpinner;
    private MCQOptionPanel[] optionPanels = new MCQOptionPanel[4];
    
    // UI Components - Navigation
    private JButton prevButton;
    private JButton nextButton;
    private JButton addQuestionButton;
    private JButton saveButton;
    
    public CreateQuizPanel2(String username) {
        this.username = username;
        this.user = userDAO.getUserByUsername(username);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this, "No user found with username: " + username);
            return;
        }
        
        setupUI();
        setupWindowCloseProtection();
        loadClasses();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(DARK_BG);
        setBorder(new EmptyBorder(SPACING_LG, SPACING_XL, SPACING_LG, SPACING_XL));
        
        // Main scrollable content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(DARK_BG);
        
        mainContent.add(createHeaderSection());
        mainContent.add(Box.createVerticalStrut(SPACING_MD));
        mainContent.add(createSettingsSection());
        mainContent.add(Box.createVerticalStrut(SPACING_LG));
        mainContent.add(createQuestionSection());
        mainContent.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DARK_BG);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);
    }
    
    private void setupWindowCloseProtection() {
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {}
            
            @Override
            public void ancestorRemoved(AncestorEvent event) {
                // Panel being removed - check for unsaved changes
                if (isModified && currentQuiz.getQuizId() != 0) {
                    // Only prompt for existing quizzes being edited
                    // New unsaved quizzes are expected to be discarded
                }
            }
            
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }
    
    // ==================== HEADER SECTION ====================
    
    private JPanel createHeaderSection() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(DARK_BG);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        // Row 1: Class and Quiz selectors + New Quiz button
        JPanel selectorRow = new JPanel(new BorderLayout(SPACING_LG, 0));
        selectorRow.setBackground(DARK_BG);
        selectorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel selectorsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_MD, 0));
        selectorsLeft.setBackground(DARK_BG);
        
        classSelector = createStyledCombo(200);
        classSelector.addActionListener(e -> onClassSelected());
        
        quizSelector = createStyledCombo(260);
        quizSelector.addActionListener(e -> onQuizSelected());
        
        selectorsLeft.add(createLabeledField("Class", classSelector));
        selectorsLeft.add(Box.createHorizontalStrut(SPACING_LG));
        selectorsLeft.add(createLabeledField("Quiz", quizSelector));
        
        // New Quiz button on the right
        JPanel buttonRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonRight.setBackground(DARK_BG);
        
        JButton newQuizButton = Buttons.createPrimary("+ New Quiz");
        newQuizButton.addActionListener(e -> createNewQuiz());
        
        // Align button to bottom of the row
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.Y_AXIS));
        buttonWrapper.setBackground(DARK_BG);
        buttonWrapper.add(Box.createVerticalStrut(18)); // Align with combo bottom
        buttonWrapper.add(newQuizButton);
        buttonRight.add(buttonWrapper);
        
        selectorRow.add(selectorsLeft, BorderLayout.WEST);
        selectorRow.add(buttonRight, BorderLayout.EAST);
        
        header.add(selectorRow);
        header.add(Box.createVerticalStrut(SPACING_MD));
        
        // Row 2: Title field with unsaved indicator
        JPanel titleRow = new JPanel(new BorderLayout(SPACING_SM, 0));
        titleRow.setBackground(DARK_BG);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Title input with unsaved indicator on the right
        JPanel titleWithIndicator = new JPanel(new BorderLayout(SPACING_SM, 0));
        titleWithIndicator.setBackground(DARK_BG);
        
        titleField = new JTextField();
        titleField.setFont(new Font(FONT_FAMILY, Font.BOLD, 20));
        titleField.setBackground(CARD_BG);
        titleField.setForeground(TEXT_COLOR);
        titleField.setCaretColor(TEXT_COLOR);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD)
        ));
        titleField.putClientProperty("JTextField.placeholderText", "Enter quiz title...");
        Utils.addSimpleDocumentListener(titleField, this::onTitleChanged);
        
        unsavedIndicator = new JLabel("● Unsaved");
        unsavedIndicator.setFont(fontSmall());
        unsavedIndicator.setForeground(WARNING_COLOR);
        unsavedIndicator.setVisible(false);
        
        titleWithIndicator.add(titleField, BorderLayout.CENTER);
        titleWithIndicator.add(unsavedIndicator, BorderLayout.EAST);
        
        errorLabel = new JLabel(" ");
        errorLabel.setFont(fontSmall());
        errorLabel.setForeground(DANGER_COLOR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titleRow.add(titleWithIndicator, BorderLayout.CENTER);
        titleRow.add(errorLabel, BorderLayout.SOUTH);
        
        header.add(titleRow);
        
        return header;
    }
    
    // ==================== SETTINGS SECTION (Collapsible) ====================
    
    private JPanel createSettingsSection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(DARK_BG);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Toggle button
        settingsToggle = new JButton("> Quiz Settings");
        settingsToggle.setFont(fontBody());
        settingsToggle.setForeground(SECONDARY_TEXT);
        settingsToggle.setBackground(DARK_BG);
        settingsToggle.setBorder(new EmptyBorder(SPACING_XS, 0, SPACING_XS, 0));
        settingsToggle.setContentAreaFilled(false);
        settingsToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsToggle.addActionListener(e -> toggleSettings());
        
        wrapper.add(settingsToggle);
        
        // Collapsible settings panel
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setBackground(CARD_BG);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(SPACING_MD, SPACING_LG, SPACING_MD, SPACING_LG)
        ));
        settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.setVisible(false);
        settingsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(SPACING_XS, SPACING_SM, SPACING_XS, SPACING_SM);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Total Questions, Time Limit
        gbc.gridx = 0; gbc.gridy = 0;
        settingsPanel.add(createSettingsLabel("Questions:"), gbc);
        
        gbc.gridx = 1;
        totalQuestionsSpinner = createStyledSpinner(1, 1, 100);
        totalQuestionsSpinner.setValue(10);
        totalQuestionsSpinner.addChangeListener(e -> onTotalQuestionsChanged());
        settingsPanel.add(totalQuestionsSpinner, gbc);
        
        gbc.gridx = 2;
        settingsPanel.add(Box.createHorizontalStrut(SPACING_LG), gbc);
        
        gbc.gridx = 3;
        settingsPanel.add(createSettingsLabel("Time Limit:"), gbc);
        
        gbc.gridx = 4;
        timeLimitSpinner = createStyledSpinner(1, 1, 180);
        timeLimitSpinner.setValue(30);
        settingsPanel.add(timeLimitSpinner, gbc);
        
        gbc.gridx = 5;
        JLabel minsLabel = new JLabel("mins");
        minsLabel.setForeground(SECONDARY_TEXT);
        minsLabel.setFont(fontSmall());
        settingsPanel.add(minsLabel, gbc);
        
        gbc.gridx = 6;
        settingsPanel.add(Box.createHorizontalStrut(SPACING_LG), gbc);
        
        gbc.gridx = 7;
        settingsPanel.add(createSettingsLabel("Deadline:"), gbc);
        
        gbc.gridx = 8;
        JPanel deadlinePanel = createDeadlinePanel();
        settingsPanel.add(deadlinePanel, gbc);
        
        // Row 2: Description
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        settingsPanel.add(createSettingsLabel("Description:"), gbc);
        
        gbc.gridx = 2;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(2, 40);
        descriptionArea.setBackground(DARKER_BG);
        descriptionArea.setForeground(TEXT_COLOR);
        descriptionArea.setCaretColor(TEXT_COLOR);
        descriptionArea.setFont(fontBody());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new EmptyBorder(SPACING_XS, SPACING_SM, SPACING_XS, SPACING_SM));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        descScroll.setPreferredSize(new Dimension(300, 50));
        settingsPanel.add(descScroll, gbc);
        
        wrapper.add(Box.createVerticalStrut(SPACING_XS));
        wrapper.add(settingsPanel);
        
        return wrapper;
    }
    
    private void toggleSettings() {
        settingsExpanded = !settingsExpanded;
        settingsPanel.setVisible(settingsExpanded);
        settingsToggle.setText((settingsExpanded ? "v " : "> ") + "Quiz Settings");
        revalidate();
    }
    
    // ==================== QUESTION SECTION ====================
    
    private JPanel createQuestionSection() {
        questionCard = new JPanel();
        questionCard.setLayout(new BoxLayout(questionCard, BoxLayout.Y_AXIS));
        questionCard.setBackground(CARD_BG);
        questionCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(SPACING_LG, SPACING_XL, SPACING_LG, SPACING_XL)
        ));
        questionCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Question header: number + dots + points
        JPanel questionHeader = new JPanel(new BorderLayout());
        questionHeader.setBackground(CARD_BG);
        questionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Left: Question number
        questionNumberLabel = new JLabel("Question 1");
        questionNumberLabel.setFont(fontH2());
        questionNumberLabel.setForeground(TEXT_COLOR);
        
        // Center: Navigation dots
        dotsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        dotsPanel.setBackground(CARD_BG);
        updateNavigationDots();
        
        // Right: Points spinner
        JPanel pointsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_XS, 0));
        pointsPanel.setBackground(CARD_BG);
        JLabel ptsLabel = new JLabel("Points:");
        ptsLabel.setForeground(SECONDARY_TEXT);
        ptsLabel.setFont(fontSmall());
        pointsSpinner = createStyledSpinner(1, 1, 100);
        pointsPanel.add(ptsLabel);
        pointsPanel.add(pointsSpinner);
        
        questionHeader.add(questionNumberLabel, BorderLayout.WEST);
        questionHeader.add(dotsPanel, BorderLayout.CENTER);
        questionHeader.add(pointsPanel, BorderLayout.EAST);
        
        questionCard.add(questionHeader);
        questionCard.add(Box.createVerticalStrut(SPACING_MD));
        
        // Question text area
        questionTextArea = new JTextArea(4, 50);
        questionTextArea.setBackground(DARKER_BG);
        questionTextArea.setForeground(TEXT_COLOR);
        questionTextArea.setCaretColor(TEXT_COLOR);
        questionTextArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setBorder(new EmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD));
        
        JScrollPane questionScroll = new JScrollPane(questionTextArea);
        questionScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        questionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        questionCard.add(questionScroll);
        questionCard.add(Box.createVerticalStrut(SPACING_LG));
        
        // Options label
        JLabel optionsLabel = new JLabel("Answer Options (select the correct one)");
        optionsLabel.setFont(fontBody());
        optionsLabel.setForeground(SECONDARY_TEXT);
        optionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionCard.add(optionsLabel);
        questionCard.add(Box.createVerticalStrut(SPACING_SM));
        
        // Options grid (2x2)
        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, SPACING_MD, SPACING_MD));
        optionsGrid.setBackground(CARD_BG);
        optionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        String[] labels = {"A", "B", "C", "D"};
        
        for (int i = 0; i < 4; i++) {
            optionPanels[i] = new MCQOptionPanel(labels[i], true);  // editable = true
            optionPanels[i].setOnSelectionChange(this::markModified);
            optionsGrid.add(optionPanels[i]);
        }
        
        // Set up siblings for mutual exclusion
        for (MCQOptionPanel panel : optionPanels) {
            panel.setSiblings(optionPanels);
        }
        
        questionCard.add(optionsGrid);
        
        return questionCard;
    }
    
    // ==================== BOTTOM BAR ====================
    
    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(DARKER_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(SPACING_MD, SPACING_XL, SPACING_MD, SPACING_XL)
        ));
        
        // Left: Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SM, 0));
        navPanel.setBackground(DARKER_BG);
        
        prevButton = Buttons.createSecondary("← Previous");
        prevButton.addActionListener(e -> navigatePrevious());
        
        nextButton = Buttons.createSecondary("Next →");
        nextButton.addActionListener(e -> navigateNext());
        
        addQuestionButton = Buttons.createSecondary("+ Add Question");
        addQuestionButton.addActionListener(e -> addNewQuestion());
        
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(Box.createHorizontalStrut(SPACING_LG));
        navPanel.add(addQuestionButton);
        
        // Right: Save button
        saveButton = Buttons.createSuccess("Save Quiz");
        saveButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        saveButton.addActionListener(e -> saveQuiz());
        
        bar.add(navPanel, BorderLayout.WEST);
        bar.add(saveButton, BorderLayout.EAST);
        
        updateNavigationState();
        
        return bar;
    }
    
    // ==================== HELPER METHODS - UI Components ====================
    
    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK_BG);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(fontSmall());
        lbl.setForeground(SECONDARY_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(2));
        panel.add(field);
        
        return panel;
    }
    
    private JLabel createSettingsLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(fontBody());
        label.setForeground(SECONDARY_TEXT);
        return label;
    }
    
    private JComboBox<String> createStyledCombo(int width) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(fontBody());
        combo.setPreferredSize(new Dimension(width, 28));
        combo.setMaximumSize(new Dimension(width, 28));
        // Let FlatLaf handle all the styling
        return combo;
    }

    private JSpinner createStyledSpinner(int value, int min, int max) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        spinner.setPreferredSize(new Dimension(70, 28));
        spinner.setFont(fontBody());
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(DARKER_BG);
            tf.setForeground(TEXT_COLOR);
            tf.setCaretColor(TEXT_COLOR);
        }
        return spinner;
    }
    
    private JPanel createDeadlinePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setBackground(CARD_BG);
        
        // Month spinner
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        deadlineMonthSpinner = new JSpinner(new SpinnerListModel(months));
        deadlineMonthSpinner.setPreferredSize(new Dimension(70, 28));
        deadlineMonthSpinner.setFont(fontBody());
        
        // Day spinner (1-31)
        deadlineDaySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        deadlineDaySpinner.setPreferredSize(new Dimension(55, 28));
        deadlineDaySpinner.setFont(fontBody());
        
        // Year spinner (current year to +5 years)
        int currentYear = LocalDate.now().getYear();
        deadlineYearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear, currentYear + 5, 1));
        deadlineYearSpinner.setPreferredSize(new Dimension(75, 28));
        deadlineYearSpinner.setFont(fontBody());
        // Remove comma separator from year
        JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(deadlineYearSpinner, "#");
        deadlineYearSpinner.setEditor(yearEditor);
        
        // Set default to today + 7 days
        LocalDate defaultDeadline = LocalDate.now().plusDays(7);
        deadlineMonthSpinner.setValue(months[defaultDeadline.getMonthValue() - 1]);
        deadlineDaySpinner.setValue(defaultDeadline.getDayOfMonth());
        deadlineYearSpinner.setValue(defaultDeadline.getYear());
        
        panel.add(deadlineMonthSpinner);
        panel.add(deadlineDaySpinner);
        panel.add(deadlineYearSpinner);
        
        return panel;
    }
    
    private LocalDate getDeadlineDate() {
        String monthStr = (String) deadlineMonthSpinner.getValue();
        int month = java.util.Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                             "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").indexOf(monthStr) + 1;
        int day = (Integer) deadlineDaySpinner.getValue();
        int year = (Integer) deadlineYearSpinner.getValue();
        
        // Clamp day to valid range for the month
        int maxDay = LocalDate.of(year, month, 1).lengthOfMonth();
        day = Math.min(day, maxDay);
        
        return LocalDate.of(year, month, day);
    }
    
    private void setDeadlineDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now().plusDays(7);
        }
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        deadlineMonthSpinner.setValue(months[date.getMonthValue() - 1]);
        deadlineDaySpinner.setValue(date.getDayOfMonth());
        deadlineYearSpinner.setValue(date.getYear());
    }
    
    private void updateNavigationDots() {
        dotsPanel.removeAll();
        int total = questions.isEmpty() ? 1 : questions.size();
        
        for (int i = 0; i < total; i++) {
            final int index = i;
            JButton dot = new JButton();
            dot.setPreferredSize(new Dimension(8, 8));
            dot.setMinimumSize(new Dimension(8, 8));
            dot.setMaximumSize(new Dimension(8, 8));
            dot.setBorder(null);
            dot.setFocusPainted(false);
            dot.setContentAreaFilled(true);
            dot.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // All dots are gray/muted, current one is slightly lighter
            if (i == currentQuestionIndex) {
                dot.setBackground(SECONDARY_TEXT);
            } else {
                dot.setBackground(BORDER_COLOR);
            }
            
            dot.addActionListener(e -> goToQuestion(index));
            dotsPanel.add(dot);
        }
        
        dotsPanel.revalidate();
        dotsPanel.repaint();
    }
    
    private void updateNavigationState() {
        prevButton.setEnabled(currentQuestionIndex > 0);
        
        int totalLimit = (int) totalQuestionsSpinner.getValue();
        nextButton.setEnabled(currentQuestionIndex < questions.size() - 1);
        addQuestionButton.setEnabled(questions.size() < totalLimit);
        
        questionNumberLabel.setText("Question " + (currentQuestionIndex + 1));
        updateNavigationDots();
    }
    
    // ==================== DATA MANAGEMENT ====================
    
    private void loadClasses() {
        classes = userDAO.getClassesForTeacher(user.getUserId());
        if (classes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No classes assigned to this teacher.");
            return;
        }
        
        classSelector.removeAllItems();
        for (ClassObj c : classes) {
            classSelector.addItem(c.getClassId() + ": " + c.getClassName());
        }
        
        if (classSelector.getItemCount() > 0) {
            classSelector.setSelectedIndex(0);
            onClassSelected();
        }
    }
    
    private void onClassSelected() {
        // Special case: Don't prompt for unsaved changes when creating a NEW quiz
        // (new quizzes have quizId == 0). For existing quizzes being edited, prompt.
        if (isModified && currentQuiz.getQuizId() != 0) {
            if (!handleUnsavedChanges()) {
                // User cancelled - but we can't easily revert class selector
                // Just load the new class anyway since they already switched
            }
        }
        
        // Clear temp quiz state when switching classes
        if (isTempQuizActive) {
            isTempQuizActive = false;
        }
        clearModified();
        
        if (classSelector.getSelectedIndex() < 0) return;
        String val = (String) classSelector.getSelectedItem();
        if (val == null || val.isEmpty()) return;
        
        int classId = Integer.parseInt(val.split(":")[0].trim());
        loadQuizzesForClass(classId);
    }
    
    private void loadQuizzesForClass(int classId) {
        quizzes = quizDAO.getQuizzesForClass(classId, user.getUserId());
        quizSelector.removeAllItems();
        
        int count = 1;
        for (Quiz q : quizzes) {
            quizSelector.addItem(count + ": " + q.getTitle());
            count++;
        }
        
        quizSelector.addItem("+ Create New Quiz");
        quizSelector.setSelectedIndex(0);
        lastSelectedQuizIndex = 0;
        isTempQuizActive = false;
        onQuizSelected();
    }
    
    private void onQuizSelected() {
        if (loadingQuiz) return;
        
        int newIndex = quizSelector.getSelectedIndex();
        if (newIndex == -1) return;
        
        String val = (String) quizSelector.getSelectedItem();
        if (val == null) return;
        
        // Check for unsaved changes when switching quizzes
        if (isModified && newIndex != lastSelectedQuizIndex) {
            if (!handleUnsavedChanges()) {
                revertToLastSelectedQuiz();
                return;
            }
            // If we get here, changes were either saved or discarded
            if (isTempQuizActive) {
                removeTempQuizFromCombo();
                isTempQuizActive = false;
            }
        }
        
        loadingQuiz = true;
        try {
            if (val.equals("+ Create New Quiz")) {
                if (isTempQuizActive) {
                    removeTempQuizFromCombo();
                    isTempQuizActive = false;
                    clearModified();
                }
                createNewQuiz();
            } else {
                String[] parts = val.split(":");
                if (parts.length >= 2) {
                    int quizNum = Integer.parseInt(parts[0].trim());
                    int quizIndex = quizNum - 1;
                    if (quizIndex >= 0 && quizIndex < quizzes.size()) {
                        loadQuiz(quizzes.get(quizIndex));
                        isTempQuizActive = false;
                    }
                }
            }
            lastSelectedQuizIndex = quizSelector.getSelectedIndex();
        } finally {
            loadingQuiz = false;
        }
    }
    
    private void createNewQuiz() {
        int createNewIndex = quizSelector.getItemCount() - 1;
        int newQuizNumber = quizzes.size() + 1;
        String line = newQuizNumber + ": Untitled (Unsaved)";
        
        quizSelector.insertItemAt(line, createNewIndex);
        quizSelector.setSelectedIndex(createNewIndex);
        isTempQuizActive = true;
        markModified();
        
        // Reset form
        questions.clear();
        questions.add(new QuestionData());
        currentQuestionIndex = 0;
        
        titleField.setText("Untitled");
        descriptionArea.setText("");
        timeLimitSpinner.setValue(30);
        totalQuestionsSpinner.setValue(10);
        setDeadlineDate(LocalDate.now().plusDays(7));
        pointsSpinner.setValue(1);
        
        currentQuiz = new Quiz();
        currentQuiz.setCreatedBy(user.getUserId());
        currentQuiz.setTitle("Untitled");
        currentQuiz.setQuizId(0);
        
        loadCurrentQuestion();
        updateNavigationState();
        lastSelectedQuizIndex = quizSelector.getSelectedIndex();
    }
    
    private void loadQuiz(Quiz qData) {
        currentQuiz = quizDAO.getQuizById(qData.getQuizId());
        
        titleField.setText(currentQuiz.getTitle());
        descriptionArea.setText(currentQuiz.getDescription() != null ? currentQuiz.getDescription() : "");
        timeLimitSpinner.setValue(currentQuiz.getTimeLimit() > 0 ? currentQuiz.getTimeLimit() : 30);
        
        if (currentQuiz.getDeadline() != null) {
            setDeadlineDate(currentQuiz.getDeadline().toLocalDateTime().toLocalDate());
        } else {
            setDeadlineDate(LocalDate.now().plusDays(7));
        }
        
        questions.clear();
        if (currentQuiz.getQuestions() != null && !currentQuiz.getQuestions().isEmpty()) {
            for (Question q : currentQuiz.getQuestions()) {
                if (q instanceof MCQQuestion) {
                    MCQQuestion mcq = (MCQQuestion) q;
                    QuestionData qd = new QuestionData();
                    qd.text = mcq.getText();
                    qd.options = mcq.getOptions();
                    qd.correctIndex = mcq.getCorrectIndex();
                    qd.points = mcq.getPoints();
                    questions.add(qd);
                }
            }
        }
        
        if (questions.isEmpty()) {
            questions.add(new QuestionData());
        }
        
        totalQuestionsSpinner.setValue(questions.size());
        currentQuestionIndex = 0;
        loadCurrentQuestion();
        updateNavigationState();
        
        clearModified();
        isTempQuizActive = false;
        updateQuizComboItem();
    }
    
    private void loadCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) return;
        
        QuestionData qd = questions.get(currentQuestionIndex);
        questionTextArea.setText(qd.text);
        pointsSpinner.setValue(qd.points);
        
        for (int i = 0; i < 4; i++) {
            String opt = (qd.options != null && i < qd.options.size()) ? qd.options.get(i) : "";
            optionPanels[i].setText(opt);
            optionPanels[i].setSelected(i == qd.correctIndex);
        }
        
        questionNumberLabel.setText("Question " + (currentQuestionIndex + 1));
        updateNavigationDots();
    }
    
    private boolean saveCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) return false;
        
        String text = questionTextArea.getText().trim();
        if (text.isEmpty()) {
            showError("Question text cannot be empty.");
            questionTextArea.requestFocus();
            return false;
        }
        
        ArrayList<String> opts = new ArrayList<>();
        for (MCQOptionPanel op : optionPanels) {
            String optText = op.getText().trim();
            if (optText.isEmpty()) {
                showError("All options must be filled in.");
                op.focus();
                return false;
            }
            opts.add(optText);
        }
        
        int correctIdx = -1;
        for (int i = 0; i < 4; i++) {
            if (optionPanels[i].isSelected()) {
                correctIdx = i;
                break;
            }
        }
        
        if (correctIdx == -1) {
            showError("Please select the correct answer.");
            return false;
        }
        
        QuestionData qd = questions.get(currentQuestionIndex);
        qd.text = text;
        qd.options = opts;
        qd.correctIndex = correctIdx;
        qd.points = (int) pointsSpinner.getValue();
        
        markModified();
        clearError();
        updateNavigationDots();
        return true;
    }
    
    // ==================== NAVIGATION ====================
    
    private void navigatePrevious() {
        if (!saveCurrentQuestion()) return;
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            loadCurrentQuestion();
            updateNavigationState();
        }
    }
    
    private void navigateNext() {
        if (!saveCurrentQuestion()) return;
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            loadCurrentQuestion();
            updateNavigationState();
        }
    }
    
    private void goToQuestion(int index) {
        if (index == currentQuestionIndex) return;
        if (!saveCurrentQuestion()) return;
        
        currentQuestionIndex = index;
        loadCurrentQuestion();
        updateNavigationState();
    }
    
    private void addNewQuestion() {
        if (!saveCurrentQuestion()) return;
        
        int totalLimit = (int) totalQuestionsSpinner.getValue();
        if (questions.size() >= totalLimit) {
            JOptionPane.showMessageDialog(this,
                "You've reached the question limit. Increase 'Total Questions' in settings to add more.",
                "Limit Reached", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        questions.add(new QuestionData());
        currentQuestionIndex = questions.size() - 1;
        loadCurrentQuestion();
        updateNavigationState();
        markModified();
    }
    
    // ==================== SAVE ====================
    
    private void saveQuiz() {
        if (!saveCurrentQuestion()) return;
        
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Quiz title cannot be empty.");
            titleField.requestFocus();
            return;
        }
        
        LocalDate selectedDate = getDeadlineDate();
        Timestamp deadline = null;
        if (selectedDate.isBefore(LocalDate.now())) {
            showError("Deadline cannot be in the past.");
            return;
        }
        deadline = Timestamp.valueOf(selectedDate.atStartOfDay());
        
        int classId = getSelectedClassId();
        if (classId == -1) {
            showError("Please select a class.");
            return;
        }
        
        currentQuiz.setTitle(title);
        currentQuiz.setDescription(descriptionArea.getText().trim());
        currentQuiz.setTimeLimit((int) timeLimitSpinner.getValue());
        currentQuiz.setCreatedBy(user.getUserId());
        
        try {
            if (currentQuiz.getQuizId() == 0) {
                int quizId = quizDAO.insertQuiz(title, currentQuiz.getDescription(), 
                    currentQuiz.getTimeLimit(), user.getUserId(), deadline, classId);
                currentQuiz.setQuizId(quizId);
                insertAllQuestions(quizId);
                JOptionPane.showMessageDialog(this, "Quiz created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                quizDAO.updateQuiz(currentQuiz.getQuizId(), title, currentQuiz.getDescription(), 
                    currentQuiz.getTimeLimit(), deadline);
                quizDAO.deleteQuestionsForQuiz(currentQuiz.getQuizId());
                insertAllQuestions(currentQuiz.getQuizId());
                JOptionPane.showMessageDialog(this, "Quiz updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            clearModified();
            isTempQuizActive = false;
            loadQuizzesForClass(classId);
            
        } catch (Exception e) {
            showError("Error saving quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void insertAllQuestions(int quizId) throws SQLException {
        for (QuestionData qd : questions) {
            if (qd.text != null && !qd.text.isEmpty()) {
                int questionId = quizDAO.insertQuestion(quizId, "MCQ", qd.text, qd.points);
                quizDAO.insertMCQOptions(questionId, qd.options, qd.correctIndex);
            }
        }
    }
    
    // ==================== UTILITY ====================
    
    private int getSelectedClassId() {
        String val = (String) classSelector.getSelectedItem();
        if (val == null || val.isEmpty()) return -1;
        return Integer.parseInt(val.split(":")[0].trim());
    }
    
    private void onTitleChanged() {
        if (!loadingQuiz) {
            markModified();
            updateQuizComboItem();
        }
    }
    
    private void onTotalQuestionsChanged() {
        int newLimit = (int) totalQuestionsSpinner.getValue();
        if (newLimit < questions.size()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "This will delete " + (questions.size() - newLimit) + " question(s). Continue?",
                "Confirm Reduction", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                while (questions.size() > newLimit) {
                    questions.remove(questions.size() - 1);
                }
                currentQuestionIndex = Math.min(currentQuestionIndex, questions.size() - 1);
                loadCurrentQuestion();
                markModified();
            } else {
                totalQuestionsSpinner.setValue(questions.size());
            }
        }
        updateNavigationState();
    }
    
    private void updateQuizComboItem() {
        int index = quizSelector.getSelectedIndex();
        if (index < 0) return;
        
        String title = titleField.getText().trim();
        if (title.isEmpty()) title = "Untitled";
        
        boolean unsaved = (currentQuiz.getQuizId() == 0) || isModified;
        String suffix = unsaved ? " (Unsaved)" : "";
        
        String oldVal = (String) quizSelector.getSelectedItem();
        String numPart;
        if (oldVal != null && oldVal.contains(":")) {
            numPart = oldVal.split(":")[0].trim();
        } else {
            numPart = String.valueOf(quizzes.size() + 1);
        }
        
        loadingQuiz = true;
        try {
            quizSelector.insertItemAt(numPart + ": " + title + suffix, index);
            quizSelector.removeItemAt(index + 1);
            quizSelector.setSelectedIndex(index);
        } finally {
            loadingQuiz = false;
        }
    }
    
    private void markModified() {
        isModified = true;
        updateUnsavedIndicator();
        updateQuizComboItem();
    }
    
    private void clearModified() {
        isModified = false;
        updateUnsavedIndicator();
    }
    
    private void updateUnsavedIndicator() {
        if (unsavedIndicator != null) {
            unsavedIndicator.setVisible(isModified);
        }
    }
    
    private static final int CONFIRM_SAVE = 0;
    private static final int CONFIRM_DISCARD = 1;
    private static final int CONFIRM_CANCEL = 2;
    
    private int showUnsavedConfirmation() {
        String[] options = {"Save & Continue", "Discard", "Cancel"};
        int result = JOptionPane.showOptionDialog(this,
            "You have unsaved changes. What would you like to do?",
            "Unsaved Changes",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]);
        
        if (result == 0) return CONFIRM_SAVE;
        if (result == 1) return CONFIRM_DISCARD;
        return CONFIRM_CANCEL;
    }
    
    private boolean handleUnsavedChanges() {
        if (!isModified) return true;
        
        int result = showUnsavedConfirmation();
        
        if (result == CONFIRM_SAVE) {
            if (trySaveQuizSilent()) {
                return true;
            } else {
                return false;
            }
        } else if (result == CONFIRM_DISCARD) {
            if (isTempQuizActive) {
                removeTempQuizFromCombo();
                isTempQuizActive = false;
            }
            clearModified();
            return true;
        }
        
        return false;
    }
    
    private boolean trySaveQuizSilent() {
        if (!saveCurrentQuestion()) return false;
        
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Quiz title cannot be empty.");
            return false;
        }
        
        LocalDate selectedDate = getDeadlineDate();
        if (selectedDate.isBefore(LocalDate.now())) {
            showError("Deadline cannot be in the past.");
            return false;
        }
        Timestamp deadline = Timestamp.valueOf(selectedDate.atStartOfDay());
        
        int classId = getSelectedClassId();
        if (classId == -1) {
            showError("Please select a class.");
            return false;
        }
        
        currentQuiz.setTitle(title);
        currentQuiz.setDescription(descriptionArea.getText().trim());
        currentQuiz.setTimeLimit((int) timeLimitSpinner.getValue());
        currentQuiz.setCreatedBy(user.getUserId());
        
        try {
            if (currentQuiz.getQuizId() == 0) {
                int quizId = quizDAO.insertQuiz(title, currentQuiz.getDescription(), 
                    currentQuiz.getTimeLimit(), user.getUserId(), deadline, classId);
                currentQuiz.setQuizId(quizId);
                insertAllQuestions(quizId);
            } else {
                quizDAO.updateQuiz(currentQuiz.getQuizId(), title, currentQuiz.getDescription(), 
                    currentQuiz.getTimeLimit(), deadline);
                quizDAO.deleteQuestionsForQuiz(currentQuiz.getQuizId());
                insertAllQuestions(currentQuiz.getQuizId());
            }
            
            clearModified();
            isTempQuizActive = false;
            return true;
            
        } catch (Exception e) {
            showError("Error saving quiz: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void revertToLastSelectedQuiz() {
        loadingQuiz = true;
        try {
            if (lastSelectedQuizIndex >= 0 && lastSelectedQuizIndex < quizSelector.getItemCount()) {
                quizSelector.setSelectedIndex(lastSelectedQuizIndex);
            }
        } finally {
            loadingQuiz = false;
        }
    }
    
    private void removeTempQuizFromCombo() {
        int count = quizSelector.getItemCount();
        for (int i = 0; i < count; i++) {
            String item = (String) quizSelector.getItemAt(i);
            if (item != null && item.contains("(Unsaved)")) {
                quizSelector.removeItemAt(i);
                break;
            }
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    private void clearError() {
        errorLabel.setText(" ");
    }
    
    private static class QuestionData {
        String text = "";
        ArrayList<String> options = new ArrayList<>();
        int correctIndex = -1;
        int points = 1;
        
        QuestionData() {
            for (int i = 0; i < 4; i++) options.add("");
        }
        
        boolean isComplete() {
            return text != null && !text.isEmpty() 
                && options.stream().noneMatch(String::isEmpty)
                && correctIndex >= 0;
        }
    }
}

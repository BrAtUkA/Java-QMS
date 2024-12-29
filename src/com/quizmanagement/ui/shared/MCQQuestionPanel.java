package com.quizmanagement.ui.shared;

import com.quizmanagement.objs.MCQQuestion;
import static com.quizmanagement.ui.styles.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class MCQQuestionPanel extends JPanel {
    private final JTextArea questionTextArea;
    private final MCQOptionPanel[] optionPanels = new MCQOptionPanel[4];
    private final boolean editable;
    private JLabel errorLabel;
    private Runnable onModified;
    
    private static final String[] OPTION_LABELS = {"A", "B", "C", "D"};
    
    public MCQQuestionPanel() {
        this(true);
    }
    
    public MCQQuestionPanel(boolean editable) {
        this.editable = editable;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(CARD_BG);
        setBorder(new EmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG));
        
        // Error label at top
        errorLabel = new JLabel(" ");
        errorLabel.setFont(fontSmall());
        errorLabel.setForeground(DANGER_COLOR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(errorLabel);
        add(Box.createVerticalStrut(SPACING_SM));
        
        // Question label
        JLabel questionLabel = new JLabel(editable ? "Question Text" : "Question");
        questionLabel.setFont(fontBody());
        questionLabel.setForeground(SECONDARY_TEXT);
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(questionLabel);
        add(Box.createVerticalStrut(SPACING_SM));
        
        // Question text area
        questionTextArea = new JTextArea(4, 40);
        questionTextArea.setBackground(DARKER_BG);
        questionTextArea.setForeground(TEXT_COLOR);
        questionTextArea.setCaretColor(TEXT_COLOR);
        questionTextArea.setFont(fontBody());
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setBorder(new EmptyBorder(SPACING_SM, SPACING_SM, SPACING_SM, SPACING_SM));
        questionTextArea.setEditable(editable);
        
        JScrollPane questionScroll = new JScrollPane(questionTextArea);
        questionScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        questionScroll.setBackground(DARKER_BG);
        questionScroll.getViewport().setBackground(DARKER_BG);
        questionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        add(questionScroll);
        add(Box.createVerticalStrut(SPACING_LG));
        
        // Options label
        String optionsLabelText = editable ? "Answer Options (select the correct one)" : "Select your answer";
        JLabel optionsLabel = new JLabel(optionsLabelText);
        optionsLabel.setFont(fontBody());
        optionsLabel.setForeground(SECONDARY_TEXT);
        optionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(optionsLabel);
        add(Box.createVerticalStrut(SPACING_SM));
        
        // Options grid (2x2)
        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, SPACING_MD, SPACING_MD));
        optionsGrid.setBackground(CARD_BG);
        optionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        for (int i = 0; i < 4; i++) {
            optionPanels[i] = new MCQOptionPanel(OPTION_LABELS[i], editable);
            optionPanels[i].setOnSelectionChange(() -> {
                if (onModified != null) {
                    onModified.run();
                }
            });
            optionsGrid.add(optionPanels[i]);
        }
        
        // Set up siblings for mutual exclusion
        for (MCQOptionPanel panel : optionPanels) {
            panel.setSiblings(optionPanels);
        }
        
        add(optionsGrid);
    }
    
    /**
     * Initialize from an MCQQuestion object.
     */
    public MCQQuestionPanel(MCQQuestion question, boolean editable) {
        this(editable);
        loadQuestion(question);
    }
    
    /**
     * Load data from an MCQQuestion.
     */
    public void loadQuestion(MCQQuestion question) {
        setQuestionText(question.getText());
        setOptions(question.getOptions());
        
        // In edit mode, also set the correct answer
        if (editable) {
            int correctIndex = question.getCorrectIndex();
            if (correctIndex >= 0 && correctIndex < 4) {
                optionPanels[correctIndex].setSelected(true);
            }
        }
    }
    
    /**
     * Set callback for when the question is modified.
     */
    public void setOnModified(Runnable callback) {
        this.onModified = callback;
    }
    
    // ========== Question Text ==========
    
    public String getQuestionText() {
        return questionTextArea.getText().trim();
    }
    
    public void setQuestionText(String text) {
        questionTextArea.setText(text);
    }
    
    // ========== Options ==========
    
    public ArrayList<String> getOptions() {
        ArrayList<String> options = new ArrayList<>();
        for (MCQOptionPanel panel : optionPanels) {
            options.add(panel.getText().trim());
        }
        return options;
    }
    
    public void setOptions(ArrayList<String> options) {
        if (options == null) return;
        for (int i = 0; i < Math.min(4, options.size()); i++) {
            optionPanels[i].setText(options.get(i));
        }
    }
    
    // ========== Selection ==========
    
    /**
     * Get the index of the selected option (0-3), or -1 if none selected.
     */
    public int getSelectedIndex() {
        for (int i = 0; i < 4; i++) {
            if (optionPanels[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Set the selected option by index.
     */
    public void setSelectedIndex(int index) {
        for (int i = 0; i < 4; i++) {
            optionPanels[i].setSelected(i == index);
        }
    }
    
    /**
     * Check if any answer is selected.
     */
    public boolean isAnswerSelected() {
        return getSelectedIndex() != -1;
    }
    
    /**
     * Get the text of the selected option, or empty string if none.
     */
    public String getSelectedOptionText() {
        int idx = getSelectedIndex();
        if (idx >= 0 && idx < 4) {
            return optionPanels[idx].getText();
        }
        return "";
    }
    
    // ========== Error Display ==========
    
    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(DANGER_COLOR);
    }
    
    public void clearError() {
        errorLabel.setText(" ");
    }
    
    // ========== Option Panel Access ==========
    
    public MCQOptionPanel getOptionPanel(int index) {
        if (index >= 0 && index < 4) {
            return optionPanels[index];
        }
        return null;
    }
    
    public void focusQuestionText() {
        questionTextArea.requestFocus();
    }
    
    public void focusOption(int index) {
        if (index >= 0 && index < 4) {
            optionPanels[index].focus();
        }
    }
}

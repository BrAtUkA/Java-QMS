package com.quizmanagement.ui.student.questions;

import com.quizmanagement.objs.MCQQuestion;
import com.quizmanagement.ui.shared.MCQQuestionPanel;
import static com.quizmanagement.ui.styles.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AttemptMCQ extends JPanel {
    
    private final MCQQuestionPanel questionPanel;
    
    public AttemptMCQ(MCQQuestion question) {
        setLayout(new BorderLayout());
        setBackground(DARK_BG);
        
        // Create the shared question panel in non-editable mode
        questionPanel = new MCQQuestionPanel(question, false);
        
        // Wrap in a scroll pane for longer questions
        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(DARK_BG);
        scrollPane.getViewport().setBackground(DARK_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // ========== Public API (maintains backward compatibility) ==========
    
    public String getQuestionText() {
        return questionPanel.getQuestionText();
    }
    
    public void setQuestionText(String text) {
        questionPanel.setQuestionText(text);
    }
    
    public void setOptions(ArrayList<String> options) {
        questionPanel.setOptions(options);
    }
    
    public int getSelectedOptionIndex() {
        return questionPanel.getSelectedIndex();
    }
    
    public boolean isAnswerSelected() {
        return questionPanel.isAnswerSelected();
    }
    
    public String getUserSelectedAnswer() {
        return questionPanel.getSelectedOptionText();
    }
    
    public void showError(String message, JComponent field, boolean severe) {
        questionPanel.showError(message);
    }
    
    public void clearErrorState() {
        questionPanel.clearError();
    }
    
    public JLabel getErrMessageLabel() {
        // For backward compatibility - returns null as we don't expose internal label
        return null;
    }
}

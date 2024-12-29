
package com.quizmanagement.objs;

import java.util.ArrayList;

public class MCQQuestion extends Question {
    private ArrayList<String> options;
    private int correctIndex;
    
    public MCQQuestion(String text, ArrayList<String> options, int correctIndex) {
        super("MCQ", text);
        this.options = options;
        this.correctIndex = correctIndex;
    }
    
    public ArrayList<String> getOptions() {
        return options;
    }
    
    public int getCorrectIndex() {
        return correctIndex;
    }
}
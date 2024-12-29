package com.quizmanagement.objs;


public abstract class Question {
    protected String type; 
    protected String text;
    protected int points;
    protected int id;

    public int getQuestionId() {
        return this.id;
    }
    
    public void  setQuestionId(int id) {
        this.id = id;
    }
    
    public Question(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    public String getType() { return type; }
    public String getText() { return text; }
}

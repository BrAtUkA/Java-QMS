
package com.quizmanagement.objs;

public class ClassObj{
    private int classId;
    private String className;

    public ClassObj(int classId, String className) {
        this.classId = classId;
        this.className = className;
    }

    public int getClassId() {
        return classId;
    }
    
    public String getClassName() {
        return className;
    }
}
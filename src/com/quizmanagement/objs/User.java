package com.quizmanagement.objs;

public class User {
    private int userId;
    private String username;
    private String hashedPassword;
    private String email;
    private String role;

    private int securityQuestion1;
    private String securityAnswer1;
    private int securityQuestion2;
    private String securityAnswer2;

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public int getSecurityQuestion1() {
        return securityQuestion1;
    }
    public void setSecurityQuestion1(int securityQuestion1) {
        this.securityQuestion1 = securityQuestion1;
    }
    public String getSecurityAnswer1() {
        return securityAnswer1;
    }
    public void setSecurityAnswer1(String securityAnswer1) {
        this.securityAnswer1 = securityAnswer1;
    }
    public int getSecurityQuestion2() {
        return securityQuestion2;
    }
    public void setSecurityQuestion2(int securityQuestion2) {
        this.securityQuestion2 = securityQuestion2;
    }
    public String getSecurityAnswer2() {
        return securityAnswer2;
    }
    public void setSecurityAnswer2(String securityAnswer2) {
        this.securityAnswer2 = securityAnswer2;
    }
}

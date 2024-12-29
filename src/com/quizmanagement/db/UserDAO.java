package com.quizmanagement.db;

import com.quizmanagement.objs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setHashedPassword(rs.getString("hashed_password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setSecurityQuestion1(rs.getInt("security_question_1"));
                user.setSecurityAnswer1(rs.getString("security_answer_1"));
                user.setSecurityQuestion2(rs.getInt("security_question_2"));
                user.setSecurityAnswer2(rs.getString("security_answer_2"));
                return user;
            }
        } catch (SQLException e) {
            logger.error("Failed to get user by username: {}", username, e);
        }
        return null;
    }

    public int getUserIdByUsername(String username) {
        User user = getUserByUsername(username);
        return user != null ? user.getUserId() : -1;
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO Users (username, hashed_password, email, role, " +
                    "security_question_1, security_answer_1, security_question_2, security_answer_2) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getHashedPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getSecurityQuestion1());
            stmt.setString(6, user.getSecurityAnswer1());
            stmt.setInt(7, user.getSecurityQuestion2());
            stmt.setString(8, user.getSecurityAnswer2());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to create user: {}", user.getUsername(), e);
            return false;
        }
    }

    public boolean updateUserPassword(String username, String newHashedPassword) {
        String sql = "UPDATE Users SET hashed_password = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newHashedPassword);
            stmt.setString(2, username);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update password for user: {}", username, e);
            return false;
        }
    }
    
    public ArrayList<ClassObj> getClassesForTeacher(int teacherId) {
        String sql = "SELECT class_id, class_name FROM classes WHERE teacher_id = ?";
        ArrayList<ClassObj> classes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                classes.add(new ClassObj(
                    rs.getInt("class_id"),
                    rs.getString("class_name")
                ));
            }
        } catch (SQLException e) {
            logger.error("Failed to get classes for teacher ID: {}", teacherId, e);
        }
        return classes;
    }
}
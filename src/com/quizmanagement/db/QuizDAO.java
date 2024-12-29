package com.quizmanagement.db;

import com.quizmanagement.objs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {
    private static final Logger logger = LoggerFactory.getLogger(QuizDAO.class);
    
    public String getClassNameByQuizId(int quizId) {
        String sql = "SELECT c.class_name FROM quizzes q JOIN classes c ON q.class_id = c.class_id WHERE q.quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("class_name");
            }
        } catch (SQLException e) {
            logger.error("Failed to get class name for quiz ID: {}", quizId, e);
        }
        return "Unknown Class";
    }
    
    public List<ClassObj> getClassesForStudent(int userId) {
        String sql = "SELECT c.class_id, c.class_name FROM classmembership cm " +
                    "JOIN classes c ON cm.class_id = c.class_id " +
                    "WHERE cm.user_id = ?";
        List<ClassObj> classList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classList.add(new ClassObj(
                    rs.getInt("class_id"), 
                    rs.getString("class_name")
                ));
            }
        } catch (SQLException e) {
            logger.error("Failed to get classes for student ID: {}", userId, e);
        }
        return classList;
    }

    public List<Quiz> getQuizzesForClassDirectly(int classId) {
        String sql = "SELECT quiz_id, title, description, time_limit, deadline " +
                    "FROM quizzes WHERE class_id = ?";
        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setQuizId(rs.getInt("quiz_id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                quiz.setTimeLimit(rs.getInt("time_limit"));
                quiz.setDeadline(rs.getTimestamp("deadline"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            logger.error("Failed to get quizzes for class ID: {}", classId, e);
        }
        return quizzes;
    }

    public List<Quiz> getAllQuizzesForStudent(int userId) {
        List<Quiz> allQuizzes = new ArrayList<>();
        List<ClassObj> classes = getClassesForStudent(userId);

        for (ClassObj classObj : classes) {
            List<Quiz> quizzes = getQuizzesForClassDirectly(classObj.getClassId());
            for (Quiz partialQuiz : quizzes) {
                Quiz fullQuiz = getQuizById(partialQuiz.getQuizId());
                allQuizzes.add(fullQuiz);
            }
        }
        return allQuizzes;
    }

//    public void finalizeQuizAttempt(int attemptId) throws SQLException {
//        String sql = "UPDATE quizattempts SET end_time = CURRENT_TIMESTAMP WHERE attempt_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, attemptId);
//            stmt.executeUpdate();
//        }
//    }
    
    public int getLatestAttemptId(int userId, int quizId) {
        String sql = "SELECT attempt_id FROM quizattempts " +
                    "WHERE user_id = ? AND quiz_id = ? " +
                    "ORDER BY start_time DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("attempt_id");
            }
        } catch (SQLException e) {
            logger.error("Failed to get latest attempt ID for user {} and quiz {}", userId, quizId, e);
        }
        return -1;  //  -1 if no attempt found
    }
    
    public void deleteQuestionsForQuiz(int quizId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // FK constaraint 
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM mcq_options WHERE question_id IN (SELECT question_id FROM questions WHERE quiz_id = ?)")) {
                    ps.setInt(1, quizId);
                    ps.executeUpdate();
                }
                
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM questions WHERE quiz_id = ?")) {
                    ps.setInt(1, quizId);
                    ps.executeUpdate();
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public Quiz getQuizById(int quizId) {
        String quizSql = "SELECT title, description, time_limit, deadline FROM quizzes WHERE quiz_id = ?";
        String questionSql = "SELECT question_id, question_text, points FROM questions WHERE quiz_id = ? AND question_type = 'MCQ'";
        Quiz quiz = new Quiz();
        ArrayList<Question> questions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement quizStmt = conn.prepareStatement(quizSql);
             PreparedStatement questionStmt = conn.prepareStatement(questionSql)) {

            //  quiz details
            quizStmt.setInt(1, quizId);
            ResultSet quizRs = quizStmt.executeQuery();
            if (quizRs.next()) {
                quiz.setQuizId(quizId);
                quiz.setTitle(quizRs.getString("title"));
                quiz.setDescription(quizRs.getString("description"));
                quiz.setTimeLimit(quizRs.getInt("time_limit"));
                quiz.setDeadline(quizRs.getTimestamp("deadline"));
            }

            //  questions details
            questionStmt.setInt(1, quizId);
            ResultSet questionRs = questionStmt.executeQuery();
            while (questionRs.next()) {
                int questionId = questionRs.getInt("question_id");
                String text = questionRs.getString("question_text");
                int points = questionRs.getInt("points");
                questions.add(loadMCQQuestion(conn, questionId, text, points));
            }

            quiz.setQuestions(questions);

        } catch (SQLException e) {
            logger.error("Failed to get quiz by ID: {}", quizId, e);
        }
        return quiz;
    }
    
    public List<Quiz> getQuizzesForClass(int classId, int teacherId) {
        String sql = "SELECT quiz_id, title, description, time_limit, deadline FROM quizzes WHERE class_id = ? AND created_by = ?";
        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setQuizId(rs.getInt("quiz_id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                quiz.setTimeLimit(rs.getInt("time_limit"));
                quiz.setDeadline(rs.getTimestamp("deadline"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            logger.error("Failed to get quizzes for class {} and teacher {}", classId, teacherId, e);
        }
        return quizzes;
    }

    public int insertQuiz(String title, String description, int timeLimit, int createdBy, 
                         Timestamp deadline, Integer classId) throws SQLException {
        String sql = "INSERT INTO quizzes (title, description, time_limit, created_by, deadline, class_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, timeLimit);
            stmt.setInt(4, createdBy);
            stmt.setTimestamp(5, deadline);
            stmt.setInt(6, classId);
            
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Failed to retrieve quiz_id.");
            }
        }
    }

    public void updateQuiz(int quizId, String title, String description, int timeLimit, Timestamp deadline) 
            throws SQLException {
        String sql = "UPDATE quizzes SET title = ?, description = ?, time_limit = ?, deadline = ? WHERE quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, timeLimit);
            stmt.setTimestamp(4, deadline);
            stmt.setInt(5, quizId);
            stmt.executeUpdate();
        }
    }

    
    // MCQ question  DAOs
    public int insertQuestion(int quizId, String text, int points) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, question_type, question_text, points) VALUES (?, 'MCQ', ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, quizId);
            stmt.setString(2, text);
            stmt.setInt(3, points);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Failed to retrieve question_id.");
            }
        }
    }
    public int insertQuestion(int quizId, String type, String text, int points) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, question_type, question_text, points) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, quizId);
            stmt.setString(2, type);
            stmt.setString(3, text);
            stmt.setInt(4, points);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Failed to retrieve question_id.");
            }
        }
    }


    
    public void insertMCQOptions(int questionId, ArrayList<String> options, int correctIndex) 
            throws SQLException {
        String sql = "INSERT INTO mcq_options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < options.size(); i++) {
                stmt.setInt(1, questionId);
                stmt.setString(2, options.get(i));
                stmt.setBoolean(3, i == correctIndex);
                stmt.executeUpdate();
            }
        }
    }

    public MCQQuestion loadMCQQuestion(Connection conn, int questionId, String text, int points) 
            throws SQLException {
        String sql = "SELECT option_text, is_correct FROM mcq_options WHERE question_id = ?";
        ArrayList<String> options = new ArrayList<>();
        int correctIndex = -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            int index = 0;
            while (rs.next()) {
                options.add(rs.getString("option_text"));
                if (rs.getBoolean("is_correct")) {
                    correctIndex = index;
                }
                index++;
            }
        }

        MCQQuestion mcq = new MCQQuestion(text, options, correctIndex);
        mcq.setQuestionId(questionId);
        mcq.setPoints(points);
        return mcq;
    }

    
    
    
    // quiz  Attempt & scoring 
    public int createQuizAttempt(int userId, int quizId) throws SQLException {
        String sql = "INSERT INTO quizattempts (user_id, quiz_id, start_time) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Failed to retrieve attempt_id.");
            }
        }
    }

    public void saveAnswer(int attemptId, int questionId, String userAnswer) throws SQLException {
        String sql = "INSERT INTO answers (attempt_id, question_id, user_answer) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            stmt.setInt(2, questionId);
            stmt.setString(3, userAnswer);
            stmt.executeUpdate();
        }
    }

    public double computeScoreForAttempt(int attemptId) throws SQLException {
        String sql = "SELECT a.question_id, q.points, a.user_answer FROM answers a " +
                    "JOIN questions q ON a.question_id = q.question_id WHERE a.attempt_id = ?";
        double score = 0.0;
        double totalPoints = 0.0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int points = rs.getInt("points");
                totalPoints += points;
                
                if (checkMCQAnswer(conn, rs.getInt("question_id"), rs.getString("user_answer"))) {
                    score += points;
                }
            }
        }
        return totalPoints == 0 ? 0 : (score / totalPoints) * 100.0;
    }

    private boolean checkMCQAnswer(Connection conn, int questionId, String userAnswer) 
            throws SQLException {
        String sql = "SELECT option_text, is_correct FROM mcq_options WHERE question_id = ? ORDER BY option_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            int optionIndex = 0;
            while (rs.next()) {
                if (rs.getBoolean("is_correct") && 
                    userAnswer.equals(String.valueOf(optionIndex))) {
                    return true;
                }
                optionIndex++;
            }
        }
        return false;
    }

    // results DAOs
    public void insertResult(int attemptId, double score) throws SQLException {
        String sql = "INSERT INTO results (attempt_id, score) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            stmt.setDouble(2, score);
            stmt.executeUpdate();
        }
    }
    
    public double getScoreForAttempt(int attemptId) throws SQLException {
        String sql = "SELECT score FROM results WHERE attempt_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("score");
            }
        } catch (SQLException e) {
            logger.error("Failed to get score for attempt ID: {}", attemptId, e);
        }
        System.out.println("Attempt ID: [" + attemptId + "] Fetching score failed.");
        return -1;
        }
    
    
    public boolean hasAttemptedQuiz(int userId, int quizId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM quizattempts WHERE user_id = ? AND quiz_id = ?")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("Failed to check if user {} attempted quiz {}", userId, quizId, e);
            return false;
        }
    }
    
    public Double getQuizScore(int userId, int quizId) {
        String sql = "SELECT r.score FROM results r " +
                    "JOIN quizattempts qa ON r.attempt_id = qa.attempt_id " +
                    "WHERE qa.user_id = ? AND qa.quiz_id = ? " +
                    "ORDER BY qa.start_time DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double score = rs.getDouble("score");
                    return rs.wasNull() ? null : score;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get quiz score for user {} quiz {}", userId, quizId, e);
        }
        return null;
    }
    
    public List<Quiz> getQuizzesByTeacher(int teacherId) {
        String sql = "SELECT q.quiz_id, q.title, q.description, q.time_limit, q.deadline " +
                    "FROM quizzes q " +
                    "JOIN classes c ON q.class_id = c.class_id " +
                    "WHERE c.teacher_id = ? " +
                    "ORDER BY q.quiz_id DESC";
        List<Quiz> quizzes = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz();
                    quiz.setQuizId(rs.getInt("quiz_id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setDescription(rs.getString("description"));
                    quiz.setTimeLimit(rs.getInt("time_limit"));
                    quiz.setDeadline(rs.getTimestamp("deadline"));
                    quizzes.add(quiz);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get quizzes for teacher {}", teacherId, e);
        }
        return quizzes;
    }
    
    public int getAttemptCountForQuiz(int quizId) {
        String sql = "SELECT COUNT(*) FROM quizattempts WHERE quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get attempt count for quiz {}", quizId, e);
        }
        return 0;
    }
    
    public double getAverageScoreForQuiz(int quizId) {
        String sql = "SELECT AVG(r.score) as avg_score FROM results r " +
                    "JOIN quizattempts qa ON r.attempt_id = qa.attempt_id " +
                    "WHERE qa.quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_score");
                    return rs.wasNull() ? -1 : avg;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get average score for quiz {}", quizId, e);
        }
        return -1;
    }
    
    public double getPassRateForQuiz(int quizId, double passingThreshold) {
        String sql = "SELECT " +
                    "COUNT(CASE WHEN r.score >= ? THEN 1 END) * 100.0 / NULLIF(COUNT(*), 0) as pass_rate " +
                    "FROM results r " +
                    "JOIN quizattempts qa ON r.attempt_id = qa.attempt_id " +
                    "WHERE qa.quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, passingThreshold);
            stmt.setInt(2, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double rate = rs.getDouble("pass_rate");
                    return rs.wasNull() ? -1 : rate;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get pass rate for quiz {}", quizId, e);
        }
        return -1;
    }
}
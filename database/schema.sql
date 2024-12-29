-- Quiz Management System Database Schema
-- MySQL 8.0+
-- 
-- Run this script to create the database and all required tables:
--   mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS quiz_management;
USE quiz_management;

-- =====================================================
-- USERS TABLE
-- Stores all system users (students, teachers, admins)
-- =====================================================
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `hashed_password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `role` ENUM('STUDENT','TEACHER','ADMIN') NOT NULL DEFAULT 'STUDENT',
  `security_question_1` VARCHAR(255) DEFAULT NULL,
  `security_answer_1` VARCHAR(255) DEFAULT NULL,
  `security_question_2` VARCHAR(255) DEFAULT NULL,
  `security_answer_2` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- CLASSES TABLE
-- Represents courses/classes created by teachers
-- =====================================================
CREATE TABLE IF NOT EXISTS `classes` (
  `class_id` INT NOT NULL AUTO_INCREMENT,
  `class_name` VARCHAR(100) NOT NULL,
  `teacher_id` INT DEFAULT NULL,
  PRIMARY KEY (`class_id`),
  KEY `teacher_id` (`teacher_id`),
  CONSTRAINT `classes_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- CLASS MEMBERSHIP TABLE
-- Junction table for student enrollment in classes
-- =====================================================
CREATE TABLE IF NOT EXISTS `classmembership` (
  `class_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`class_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `classmembership_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`) ON DELETE CASCADE,
  CONSTRAINT `classmembership_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- QUIZZES TABLE
-- Quiz definitions with settings and deadlines
-- =====================================================
CREATE TABLE IF NOT EXISTS `quizzes` (
  `quiz_id` INT NOT NULL AUTO_INCREMENT,
  `class_id` INT DEFAULT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `time_limit` INT DEFAULT NULL COMMENT 'Time limit in minutes',
  `created_by` INT DEFAULT NULL,
  `created_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `modified_date` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deadline` DATETIME DEFAULT NULL,
  PRIMARY KEY (`quiz_id`),
  KEY `class_id` (`class_id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `quizzes_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`) ON DELETE CASCADE,
  CONSTRAINT `quizzes_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- QUESTIONS TABLE
-- Quiz questions (supports MCQ, TF, MATCH types)
-- =====================================================
CREATE TABLE IF NOT EXISTS `questions` (
  `question_id` INT NOT NULL AUTO_INCREMENT,
  `quiz_id` INT DEFAULT NULL,
  `question_type` ENUM('MCQ','TF','MATCH') NOT NULL,
  `question_text` TEXT NOT NULL,
  `points` INT DEFAULT '1',
  PRIMARY KEY (`question_id`),
  KEY `quiz_id` (`quiz_id`),
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`quiz_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- MCQ OPTIONS TABLE
-- Answer options for multiple choice questions
-- =====================================================
CREATE TABLE IF NOT EXISTS `mcq_options` (
  `option_id` INT NOT NULL AUTO_INCREMENT,
  `question_id` INT DEFAULT NULL,
  `option_text` VARCHAR(255) NOT NULL,
  `is_correct` TINYINT(1) DEFAULT '0',
  PRIMARY KEY (`option_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `mcq_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- QUIZ ATTEMPTS TABLE
-- Records of student quiz attempts
-- =====================================================
CREATE TABLE IF NOT EXISTS `quizattempts` (
  `attempt_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT DEFAULT NULL,
  `quiz_id` INT DEFAULT NULL,
  `start_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`attempt_id`),
  KEY `user_id` (`user_id`),
  KEY `quiz_id` (`quiz_id`),
  CONSTRAINT `quizattempts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `quizattempts_ibfk_2` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`quiz_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- ANSWERS TABLE
-- Student answers for each question in an attempt
-- =====================================================
CREATE TABLE IF NOT EXISTS `answers` (
  `answer_id` INT NOT NULL AUTO_INCREMENT,
  `attempt_id` INT DEFAULT NULL,
  `question_id` INT DEFAULT NULL,
  `user_answer` TEXT,
  PRIMARY KEY (`answer_id`),
  KEY `attempt_id` (`attempt_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`attempt_id`) REFERENCES `quizattempts` (`attempt_id`) ON DELETE CASCADE,
  CONSTRAINT `answers_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- RESULTS TABLE
-- Final scores and feedback for quiz attempts
-- =====================================================
CREATE TABLE IF NOT EXISTS `results` (
  `result_id` INT NOT NULL AUTO_INCREMENT,
  `attempt_id` INT DEFAULT NULL,
  `score` DECIMAL(5,2) DEFAULT NULL,
  `feedback` TEXT,
  PRIMARY KEY (`result_id`),
  KEY `attempt_id` (`attempt_id`),
  CONSTRAINT `results_ibfk_1` FOREIGN KEY (`attempt_id`) REFERENCES `quizattempts` (`attempt_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================
-- VIEWS
-- Pre-built queries for common data retrieval
-- =====================================================

-- Quiz Results Summary View
-- Aggregates quiz attempt data with user and quiz info
CREATE OR REPLACE VIEW `quiz_results_summary` AS
SELECT 
    qa.attempt_id,
    u.username,
    q.title AS quiz_title,
    qa.start_time,
    r.score,
    r.feedback
FROM quizattempts qa
JOIN users u ON qa.user_id = u.user_id
JOIN quizzes q ON qa.quiz_id = q.quiz_id
LEFT JOIN results r ON qa.attempt_id = r.attempt_id;

-- Teacher View
-- Shows all quizzes created by each teacher with their classes
CREATE OR REPLACE VIEW `teacher_view` AS
SELECT 
    u.username AS teacher_name,
    c.class_name,
    q.title AS quiz_title,
    q.deadline,
    q.time_limit,
    COUNT(DISTINCT qa.attempt_id) AS total_attempts
FROM users u
JOIN classes c ON u.user_id = c.teacher_id
LEFT JOIN quizzes q ON c.class_id = q.class_id
LEFT JOIN quizattempts qa ON q.quiz_id = qa.quiz_id
WHERE u.role = 'TEACHER'
GROUP BY u.user_id, c.class_id, q.quiz_id;

-- =====================================================
-- SAMPLE DATA (Optional)
-- Uncomment to add test accounts
-- =====================================================

-- INSERT INTO users (username, hashed_password, email, role)
-- VALUES ('teacher1', 'hashed_password_here', 'teacher1@example.com', 'TEACHER');

-- INSERT INTO users (username, hashed_password, email, role)
-- VALUES ('student1', 'hashed_password_here', 'student1@example.com', 'STUDENT');

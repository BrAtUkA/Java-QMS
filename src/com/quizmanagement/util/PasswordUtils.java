package com.quizmanagement.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for password hashing and verification.
 * Uses SHA-256 with salt for secure password storage.
 */
public class PasswordUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String SEPARATOR = "$";
    
    /**
     * Generates a random salt for password hashing.
     * 
     * @return Base64 encoded salt string
     */
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hashes a password using SHA-256 with the provided salt.
     * 
     * @param password the plain text password
     * @param salt the salt to use for hashing
     * @return the hashed password as a hex string
     */
    private static String hashWithSalt(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            String saltedPassword = salt + password;
            byte[] hashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Hashing algorithm not found: {}", HASH_ALGORITHM, e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Hashes a password with a newly generated salt.
     * The result format is: salt$hashedPassword
     * 
     * @param plainPassword the plain text password to hash
     * @return the salt and hashed password combined, or null if input is null/empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }
        
        String salt = generateSalt();
        String hashedPassword = hashWithSalt(plainPassword, salt);
        
        // Format: salt$hash
        return salt + SEPARATOR + hashedPassword;
    }
    
    /**
     * Verifies a plain text password against a stored hash.
     * 
     * @param plainPassword the plain text password to verify
     * @param storedHash the stored hash in format: salt$hashedPassword
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        
        // Reject invalid hash format
        if (!storedHash.contains(SEPARATOR)) {
            logger.error("Invalid hash format - password must be hashed");
            return false;
        }
        
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split("\\" + SEPARATOR, 2);
            if (parts.length != 2) {
                logger.error("Invalid stored hash format");
                return false;
            }
            
            String salt = parts[0];
            String expectedHash = parts[1];
            
            // Hash the input password with the same salt
            String actualHash = hashWithSalt(plainPassword, salt);
            
            // Compare hashes using constant-time comparison to prevent timing attacks
            return constantTimeEquals(expectedHash, actualHash);
        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }
    
    /**
     * Constant-time string comparison to prevent timing attacks.
     * 
     * @param a first string
     * @param b second string
     * @return true if strings are equal
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
    
    /**
     * Hashes a security answer for secure storage.
     * Security answers are case-insensitive and trimmed.
     * 
     * @param answer the plain text answer
     * @return the hashed answer
     */
    public static String hashSecurityAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return null;
        }
        // Normalize: trim and lowercase for case-insensitive comparison
        return hashPassword(answer.trim().toLowerCase());
    }
    
    /**
     * Verifies a security answer against a stored hash.
     * Security answers are case-insensitive.
     * 
     * @param plainAnswer the plain text answer to verify
     * @param storedHash the stored hash
     * @return true if the answer matches
     */
    public static boolean verifySecurityAnswer(String plainAnswer, String storedHash) {
        if (plainAnswer == null) {
            return false;
        }
        // Normalize: trim and lowercase for case-insensitive comparison
        return verifyPassword(plainAnswer.trim().toLowerCase(), storedHash);
    }
}

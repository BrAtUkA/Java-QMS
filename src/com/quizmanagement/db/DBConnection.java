package com.quizmanagement.db;

import com.quizmanagement.util.ConfigLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection manager using HikariCP connection pooling.
 * Configuration is loaded from config.properties file.
 */
public class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private static final HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            
            // Database connection settings from config file
            config.setJdbcUrl(ConfigLoader.get("db.url"));
            config.setUsername(ConfigLoader.get("db.user"));
            config.setPassword(ConfigLoader.get("db.password"));
            
            // Connection pool settings
            config.setMaximumPoolSize(ConfigLoader.getInt("db.pool.size", 10));
            config.setMinimumIdle(ConfigLoader.getInt("db.pool.minIdle", 5));
            config.setIdleTimeout(ConfigLoader.getLong("db.pool.idleTimeout", 300000));
            config.setConnectionTimeout(ConfigLoader.getLong("db.pool.connectionTimeout", 20000));
            config.setMaxLifetime(ConfigLoader.getLong("db.pool.maxLifetime", 1200000));
            
            // Performance optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            
            config.setPoolName("QuizSystemPool");
            
            dataSource = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    /**
     * Get a connection from the pool.
     * @return a database connection
     * @throws SQLException if a connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Shutdown the connection pool gracefully.
     * Should be called when the application is shutting down.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP connection pool shut down");
        }
    }
}
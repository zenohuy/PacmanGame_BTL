package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "pacman_game";
    private static final String USER = "root"; // Default XAMPP user
    private static final String PASS = "";     // Default XAMPP password

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initDatabase() {
        try {
            // Register JDBC driver (optional in newer JDBC, but good for safety)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to MySQL server without specifying DB first to create it
            Connection initConn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = initConn.createStatement();

            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.close();
            initConn.close();

            // Connect to the specific database
            connection = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);

            // Create users table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password_hash VARCHAR(64) NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            Statement createTableStmt = connection.createStatement();
            createTableStmt.execute(createTableSQL);
            createTableStmt.close();

            // Create user_progress table
            String createProgressTableSQL = "CREATE TABLE IF NOT EXISTS user_progress ("
                    + "user_id INT,"
                    + "level_id INT,"
                    + "high_score INT DEFAULT 0,"
                    + "PRIMARY KEY (user_id, level_id),"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")";
            Statement createProgressStmt = connection.createStatement();
            createProgressStmt.execute(createProgressTableSQL);
            createProgressStmt.close();

            System.out.println("Database and table initialized successfully.");
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Register error: " + e.getMessage());
            return false; // Often indicates duplicate username
        }
    }

    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        String hashedPassword = hashPassword(password);
        String sql = "SELECT id FROM users WHERE username = ? AND password_hash = ?";

        if (connection == null) return false;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If there is a matching row, login is successful
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }

    public int getUnlockedMaxLevel(String username) {
        if (connection == null) return 1;
        String sql = "SELECT MAX(level_id) as max_lvl FROM user_progress JOIN users ON users.id = user_progress.user_id WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int maxLvl = rs.getInt("max_lvl");
                return maxLvl < 1 ? 1 : maxLvl;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int getHighScore(String username, int levelId) {
        if (connection == null) return -1;
        String sql = "SELECT high_score FROM user_progress JOIN users ON users.id = user_progress.user_id WHERE username = ? AND level_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, levelId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("high_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateScoreAndUnlockNext(String username, int currentLevelId, int score) {
        if (connection == null) return;
        try {
            int userId = -1;
            String getUserIdSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(getUserIdSql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
            if (userId == -1) return;

            String upsertProgress = "INSERT INTO user_progress (user_id, level_id, high_score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE high_score = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(upsertProgress)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, currentLevelId);
                pstmt.setInt(3, score);
                pstmt.setInt(4, score);
                pstmt.executeUpdate();
            }

            if (currentLevelId < 9) {
                String unlockNext = "INSERT IGNORE INTO user_progress (user_id, level_id, high_score) VALUES (?, ?, 0)";
                try (PreparedStatement pstmt = connection.prepareStatement(unlockNext)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, currentLevelId + 1);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

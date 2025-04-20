package server;

import java.sql.*;

/**
 * Database connection and operations for the chess server
 */
public class Database {
    private Connection connection;
    
    /**
     * Create a database connection
     * 
     * @param url JDBC URL for the database
     * @param username Database username
     * @param password Database password
     * @throws SQLException If connection fails
     */
    public Database(String url, String username, String password) throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to the database
            connection = DriverManager.getConnection(url, username, password);
            
            System.out.println("Connected to database: " + url);
            
            // Initialize the database if needed
            initializeDatabase();
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found! Make sure the JAR is in the classpath.");
            throw new SQLException("JDBC driver not found", e);
        }
    }
    
    /**
     * Initialize the database schema if needed
     */
    private void initializeDatabase() {
        try {
            // Check if the users table exists
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "users", null);
            
            if (!tables.next()) {
                // Create the users table
                String createTableSQL = 
                    "CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL)";
                
                Statement stmt = connection.createStatement();
                stmt.execute(createTableSQL);
                
                // Add some test users
                String insertUserSQL = 
                    "INSERT INTO users (username, password) VALUES " +
                    "('player1', 'password1'), " +
                    "('player2', 'password2')";
                
                stmt.execute(insertUserSQL);
                
                System.out.println("Database initialized with test users");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate a user
     * 
     * @param username Username to check
     * @param password Password to verify
     * @return True if authentication successful, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Register a new user
     * 
     * @param username New username
     * @param password New password
     * @return True if registration successful, false otherwise
     */
    public boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Close the database connection
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
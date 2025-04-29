import java.sql.*;

public class AuthenticationManager {
    // JDBC connection variables
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234"; // Change this to your MySQL password
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
    
    /**
     * Check if the given credentials are valid for an admin user
     */
    public boolean authenticateAdmin(String username, String password) {
        // In a real application, you would check against a users/admins table
        // For now, we'll use hardcoded credentials
        return username.equals("admin") && password.equals("admin123");
    }
    
    /**
     * Check if the given credentials are valid for a student user
     * @return student_id if authenticated, -1 otherwise
     */
    public int authenticateStudent(String username, String password) {
        try (Connection conn = getConnection()) {
            // In a real application, you would hash passwords and store them securely
            // Here we're assuming students log in with email and last name
            String query = "SELECT student_id FROM students WHERE email = ? AND last_name = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("student_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1; // Authentication failed
    }
    
    /**
     * Initialize the admin user in the database (call this when setting up the database)
     */
    public void setupAdminUser() {
        try (Connection conn = getConnection()) {
            // Check if users table exists, if not create it
            try (Statement stmt = conn.createStatement()) {
                // Create users table if it doesn't exist
                String createTable = "CREATE TABLE IF NOT EXISTS users (" +
                                     "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                     "username VARCHAR(50) NOT NULL UNIQUE, " +
                                     "password VARCHAR(255) NOT NULL, " +
                                     "user_type VARCHAR(20) NOT NULL, " +
                                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.executeUpdate(createTable);
                
                // Check if admin user exists
                String checkAdmin = "SELECT * FROM users WHERE username = 'admin'";
                ResultSet rs = stmt.executeQuery(checkAdmin);
                
                if (!rs.next()) {
                    // Add admin user
                    String insertAdmin = "INSERT INTO users (username, password, user_type) VALUES ('admin', 'admin123', 'admin')";
                    stmt.executeUpdate(insertAdmin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
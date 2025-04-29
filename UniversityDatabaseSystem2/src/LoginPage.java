import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn, backBtn;
    private String userType;
    
    // JDBC connection variables - make sure these match your main application
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234"; // Change this to your MySQL password
    
    public LoginPage(String userType) {
        this.userType = userType;
        
        setTitle("University Database System - " + userType + " Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add header label
        JLabel headerLabel = new JLabel(userType + " Login", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(headerLabel, gbc);
        
        // Add username field
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        
        // Add password field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(passwordField, gbc);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        loginBtn = new JButton("Login");
        backBtn = new JButton("Back");
        
        // Style buttons
        styleButton(loginBtn);
        styleButton(backBtn);
        
        // Add action listeners
        loginBtn.addActionListener(e -> handleLogin());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginSelection();
        });
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add panels to frame
        add(mainPanel);
        
        // Set default button (press Enter to login)
        getRootPane().setDefaultButton(loginBtn);
        
        setVisible(true);
    }
    
    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(100, 30));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 230, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIManager.getColor("Button.background"));
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                                         "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check credentials
        if (authenticateUser(username, password)) {
            dispose(); // Close login window
            
            // Start main application with appropriate access level
            SwingUtilities.invokeLater(() -> {
                UniversityDatabaseSystem mainSystem = new UniversityDatabaseSystem();
                
                // Set access restrictions based on user type
                if (userType.equals("Student")) {
                    restrictStudentAccess(mainSystem);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                                         "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean authenticateUser(String username, String password) {
        // For demonstration purposes: preset credentials
        if (userType.equals("Admin")) {
            return username.equals("admin") && password.equals("admin123");
        } else {
            // Check if this is a valid student in the database
            try (Connection conn = getConnection()) {
                // Here we're assuming students can log in with their email as username and last name as password
                // In a real application, you should use a proper users table with hashed passwords
                String query = "SELECT * FROM students WHERE email = ? AND last_name = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        return rs.next(); // Returns true if a matching student was found
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(),
                                             "Login Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return false;
            }
        }
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
    
    private void restrictStudentAccess(UniversityDatabaseSystem mainSystem) {
        // Disable all add, update, delete buttons across all tabs
        
        // Students tab
        mainSystem.disableStudentEditing();
        
        // Courses tab
        mainSystem.disableCourseEditing();
        
        // Marks tab
        mainSystem.disableMarksEditing();
    }
}
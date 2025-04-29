import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginSelection extends JFrame {
    private JButton adminBtn, studentBtn;
    
    public LoginSelection() {
        setTitle("University Database System - Login Selection");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create panel with grid layout
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add header label
        JLabel headerLabel = new JLabel("Select Login Type", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create buttons
        adminBtn = new JButton("Administrator Login");
        studentBtn = new JButton("Student Login");
        
        // Add hover effect and styling
        styleButton(adminBtn);
        styleButton(studentBtn);
        
        // Add action listeners
        adminBtn.addActionListener(e -> openLoginPage("Admin"));
        studentBtn.addActionListener(e -> openLoginPage("Student"));
        
        // Add components to panel
        panel.add(headerLabel);
        panel.add(adminBtn);
        panel.add(studentBtn);
        
        // Add panel to frame
        add(panel);
        
        setVisible(true);
    }
    
    private void styleButton(JButton button) {
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
    
    private void openLoginPage(String userType) {
        dispose(); // Close selection window
        new LoginPage(userType);
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create application
            SwingUtilities.invokeLater(() -> new LoginSelection());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(),
                                         "Application Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
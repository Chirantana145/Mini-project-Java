// Optional: Custom Report Generation Class
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
class ReportGenerator {
    private Connection conn;
    
    public ReportGenerator(Connection conn) {
        this.conn = conn;
    }
    
    public void generateStudentReport(int studentId) {
        try {
            // Query to get student details and grades
            String query = "SELECT s.first_name, s.last_name, c.course_name, m.semester, m.marks_obtained, m.grade " +
                          "FROM students s " +
                          "JOIN marks m ON s.student_id = m.student_id " +
                          "JOIN courses c ON m.course_id = c.course_id " +
                          "WHERE s.student_id = ? " +
                          "ORDER BY m.semester, c.course_name";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Create report
                        JFrame reportFrame = new JFrame("Student Report");
                        reportFrame.setSize(600, 500);
                        reportFrame.setLocationRelativeTo(null);
                        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        // Student info panel
                        JPanel infoPanel = new JPanel(new GridLayout(2, 2));
                        infoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
                        infoPanel.add(new JLabel("Name:"));
                        infoPanel.add(new JLabel(rs.getString("first_name") + " " + rs.getString("last_name")));
                        
                        // Reset result set pointer
                        rs.beforeFirst();
                        
                        // Create marks table
                        Vector<String> columnNames = new Vector<>();
                        columnNames.add("Course");
                        columnNames.add("Semester");
                        columnNames.add("Marks");
                        columnNames.add("Grade");
                        
                        Vector<Vector<Object>> data = new Vector<>();
                        while (rs.next()) {
                            Vector<Object> row = new Vector<>();
                            row.add(rs.getString("course_name"));
                            row.add(rs.getString("semester"));
                            row.add(rs.getDouble("marks_obtained"));
                            row.add(rs.getString("grade"));
                            data.add(row);
                        }
                        
                        DefaultTableModel model = new DefaultTableModel(data, columnNames);
                        JTable marksTable = new JTable(model);
                        JScrollPane scrollPane = new JScrollPane(marksTable);
                        
                        panel.add(infoPanel, BorderLayout.NORTH);
                        panel.add(scrollPane, BorderLayout.CENTER);
                        
                        reportFrame.add(panel);
                        reportFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "No data found for the student.", 
                                                     "Report Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage(), 
                                         "Report Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public void generateCourseReport(int courseId) {
        try {
            // Query to get course details and student performances
            String query = "SELECT c.course_name, c.course_code, c.credit_hours, " +
                          "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                          "m.semester, m.marks_obtained, m.grade " +
                          "FROM courses c " +
                          "JOIN marks m ON c.course_id = m.course_id " +
                          "JOIN students s ON m.student_id = s.student_id " +
                          "WHERE c.course_id = ? " +
                          "ORDER BY m.semester, s.last_name, s.first_name";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, courseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Create report
                        JFrame reportFrame = new JFrame("Course Report");
                        reportFrame.setSize(600, 500);
                        reportFrame.setLocationRelativeTo(null);
                        
                        JPanel panel = new JPanel(new BorderLayout());
                        
                        // Course info panel
                        JPanel infoPanel = new JPanel(new GridLayout(3, 2));
                        infoPanel.setBorder(BorderFactory.createTitledBorder("Course Information"));
                        infoPanel.add(new JLabel("Course Name:"));
                        infoPanel.add(new JLabel(rs.getString("course_name")));
                        infoPanel.add(new JLabel("Course Code:"));
                        infoPanel.add(new JLabel(rs.getString("course_code")));
                        infoPanel.add(new JLabel("Credit Hours:"));
                        infoPanel.add(new JLabel(String.valueOf(rs.getInt("credit_hours"))));
                        
                        // Reset result set pointer
                        rs.beforeFirst();
                        
                        // Create student marks table
                        Vector<String> columnNames = new Vector<>();
                        columnNames.add("Student");
                        columnNames.add("Semester");
                        columnNames.add("Marks");
                        columnNames.add("Grade");
                        
                        Vector<Vector<Object>> data = new Vector<>();
                        while (rs.next()) {
                            Vector<Object> row = new Vector<>();
                            row.add(rs.getString("student_name"));
                            row.add(rs.getString("semester"));
                            row.add(rs.getDouble("marks_obtained"));
                            row.add(rs.getString("grade"));
                            data.add(row);
                        }
                        
                        DefaultTableModel model = new DefaultTableModel(data, columnNames);
                        JTable marksTable = new JTable(model);
                        JScrollPane scrollPane = new JScrollPane(marksTable);
                        
                        panel.add(infoPanel, BorderLayout.NORTH);
                        panel.add(scrollPane, BorderLayout.CENTER);
                        
                        reportFrame.add(panel);
                        reportFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "No data found for the course.", 
                                                     "Report Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error generating report: " + e.getMessage(), 
                                         "Report Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
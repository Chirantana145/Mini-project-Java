import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UniversityDatabaseSystem extends JFrame {
    // JDBC connection variables
    private boolean isStudentMode = false;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234"; // Change this to your MySQL password
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JPanel studentsPanel, coursesPanel, marksPanel;
    
    // Students components
    private JTable studentTable;
    private JTextField studentIdField, firstNameField, lastNameField, emailField, dobField, programField;
    private JButton addStudentBtn, updateStudentBtn, deleteStudentBtn, clearStudentBtn;
    
    // Courses components
    private JTable courseTable;
    private JTextField courseIdField, courseCodeField, courseNameField, creditHoursField, departmentField;
    private JButton addCourseBtn, updateCourseBtn, deleteCourseBtn, clearCourseBtn;
    
    // Marks components
    private JTable marksTable;
    private JComboBox<String> studentComboBox, courseComboBox;
    private JTextField markIdField, semesterField, marksObtainedField, gradeField;
    private JButton addMarkBtn, updateMarkBtn, deleteMarkBtn, clearMarkBtn;

    public UniversityDatabaseSystem(boolean isStudentMode) {
        this();
        this.isStudentMode = isStudentMode;
        
        if (isStudentMode) {
            disableStudentEditing();
            disableCourseEditing();
            disableMarksEditing();
            
            // Update the title to reflect student mode
            setTitle("University Database Management System - Student View (Read Only)");
        }
    }
    public void disableStudentEditing() {
        addStudentBtn.setEnabled(false);
        updateStudentBtn.setEnabled(false);
        deleteStudentBtn.setEnabled(false);
        clearStudentBtn.setEnabled(false);
        
        // Disable text fields
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        emailField.setEditable(false);
        dobField.setEditable(false);
        programField.setEditable(false);
    }

    public void disableCourseEditing() {
        addCourseBtn.setEnabled(false);
        updateCourseBtn.setEnabled(false);
        deleteCourseBtn.setEnabled(false);
        clearCourseBtn.setEnabled(false);
        
        // Disable text fields
        courseCodeField.setEditable(false);
        courseNameField.setEditable(false);
        creditHoursField.setEditable(false);
        departmentField.setEditable(false);
    }

    public void disableMarksEditing() {
        addMarkBtn.setEnabled(false);
        updateMarkBtn.setEnabled(false);
        deleteMarkBtn.setEnabled(false);
        clearMarkBtn.setEnabled(false);
        
        // Disable fields
        semesterField.setEditable(false);
        marksObtainedField.setEditable(false);
        gradeField.setEditable(false);
        
        // Disable combo boxes
        studentComboBox.setEnabled(false);
        courseComboBox.setEnabled(false);
    }
    
    public UniversityDatabaseSystem() {
        setTitle("University Database Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        createStudentsPanel();
        createCoursesPanel();
        createMarksPanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("Students", studentsPanel);
        tabbedPane.addTab("Courses", coursesPanel);
        tabbedPane.addTab("Marks", marksPanel);
        
        // Add tabbed pane to frame
        add(tabbedPane);
        
        // Load data
        loadStudentData();
        loadCourseData();
        loadMarksData();
        loadComboBoxes();
        
        setVisible(true);
    }
    
    private void createStudentsPanel() {
        studentsPanel = new JPanel(new BorderLayout());
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        studentTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(studentTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        studentIdField.setEditable(false);
        formPanel.add(studentIdField);
        
        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);
        
        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        dobField = new JTextField();
        formPanel.add(dobField);
        
        formPanel.add(new JLabel("Program:"));
        programField = new JTextField();
        formPanel.add(programField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addStudentBtn = new JButton("Add");
        updateStudentBtn = new JButton("Update");
        deleteStudentBtn = new JButton("Delete");
        clearStudentBtn = new JButton("Clear");
        
        buttonPanel.add(addStudentBtn);
        buttonPanel.add(updateStudentBtn);
        buttonPanel.add(deleteStudentBtn);
        buttonPanel.add(clearStudentBtn);
        
        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);
        
        // Add panels to students panel
        studentsPanel.add(tablePanel, BorderLayout.CENTER);
        studentsPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Add event listeners
        addStudentBtn.addActionListener(e -> addStudent());
        updateStudentBtn.addActionListener(e -> updateStudent());
        deleteStudentBtn.addActionListener(e -> deleteStudent());
        clearStudentBtn.addActionListener(e -> clearStudentFields());
        
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = studentTable.getSelectedRow();
                if (row != -1) {
                    studentIdField.setText(studentTable.getValueAt(row, 0).toString());
                    firstNameField.setText(studentTable.getValueAt(row, 1).toString());
                    lastNameField.setText(studentTable.getValueAt(row, 2).toString());
                    emailField.setText(studentTable.getValueAt(row, 3).toString());
                    dobField.setText(studentTable.getValueAt(row, 4).toString());
                    programField.setText(studentTable.getValueAt(row, 6).toString());
                }
            }
        });
    }
    
    private void createCoursesPanel() {
        coursesPanel = new JPanel(new BorderLayout());
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        courseTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(courseTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Course ID:"));
        courseIdField = new JTextField();
        courseIdField.setEditable(false);
        formPanel.add(courseIdField);
        
        formPanel.add(new JLabel("Course Code:"));
        courseCodeField = new JTextField();
        formPanel.add(courseCodeField);
        
        formPanel.add(new JLabel("Course Name:"));
        courseNameField = new JTextField();
        formPanel.add(courseNameField);
        
        formPanel.add(new JLabel("Credit Hours:"));
        creditHoursField = new JTextField();
        formPanel.add(creditHoursField);
        
        formPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        formPanel.add(departmentField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addCourseBtn = new JButton("Add");
        updateCourseBtn = new JButton("Update");
        deleteCourseBtn = new JButton("Delete");
        clearCourseBtn = new JButton("Clear");
        
        buttonPanel.add(addCourseBtn);
        buttonPanel.add(updateCourseBtn);
        buttonPanel.add(deleteCourseBtn);
        buttonPanel.add(clearCourseBtn);
        
        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);
        
        // Add panels to courses panel
        coursesPanel.add(tablePanel, BorderLayout.CENTER);
        coursesPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Add event listeners
        addCourseBtn.addActionListener(e -> addCourse());
        updateCourseBtn.addActionListener(e -> updateCourse());
        deleteCourseBtn.addActionListener(e -> deleteCourse());
        clearCourseBtn.addActionListener(e -> clearCourseFields());
        
        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = courseTable.getSelectedRow();
                if (row != -1) {
                    courseIdField.setText(courseTable.getValueAt(row, 0).toString());
                    courseCodeField.setText(courseTable.getValueAt(row, 1).toString());
                    courseNameField.setText(courseTable.getValueAt(row, 2).toString());
                    creditHoursField.setText(courseTable.getValueAt(row, 3).toString());
                    departmentField.setText(courseTable.getValueAt(row, 4).toString());
                }
            }
        });
    }
    
    private void createMarksPanel() {
        marksPanel = new JPanel(new BorderLayout());
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        marksTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(marksTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Mark ID:"));
        markIdField = new JTextField();
        markIdField.setEditable(false);
        formPanel.add(markIdField);
        
        formPanel.add(new JLabel("Student:"));
        studentComboBox = new JComboBox<>();
        formPanel.add(studentComboBox);
        
        formPanel.add(new JLabel("Course:"));
        courseComboBox = new JComboBox<>();
        formPanel.add(courseComboBox);
        
        formPanel.add(new JLabel("Semester:"));
        semesterField = new JTextField();
        formPanel.add(semesterField);
        
        formPanel.add(new JLabel("Marks Obtained:"));
        marksObtainedField = new JTextField();
        formPanel.add(marksObtainedField);
        
        formPanel.add(new JLabel("Grade:"));
        gradeField = new JTextField();
        formPanel.add(gradeField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addMarkBtn = new JButton("Add");
        updateMarkBtn = new JButton("Update");
        deleteMarkBtn = new JButton("Delete");
        clearMarkBtn = new JButton("Clear");
        
        buttonPanel.add(addMarkBtn);
        buttonPanel.add(updateMarkBtn);
        buttonPanel.add(deleteMarkBtn);
        buttonPanel.add(clearMarkBtn);
        
        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);
        
        // Add panels to marks panel
        marksPanel.add(tablePanel, BorderLayout.CENTER);
        marksPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Add event listeners
        addMarkBtn.addActionListener(e -> addMark());
        updateMarkBtn.addActionListener(e -> updateMark());
        deleteMarkBtn.addActionListener(e -> deleteMark());
        clearMarkBtn.addActionListener(e -> clearMarkFields());
        
        marksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = marksTable.getSelectedRow();
                if (row != -1) {
                    markIdField.setText(marksTable.getValueAt(row, 0).toString());
                    
                    String studentName = marksTable.getValueAt(row, 1).toString();
                    for (int i = 0; i < studentComboBox.getItemCount(); i++) {
                        if (studentComboBox.getItemAt(i).equals(studentName)) {
                            studentComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    String courseName = marksTable.getValueAt(row, 2).toString();
                    for (int i = 0; i < courseComboBox.getItemCount(); i++) {
                        if (courseComboBox.getItemAt(i).equals(courseName)) {
                            courseComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    
                    semesterField.setText(marksTable.getValueAt(row, 3).toString());
                    marksObtainedField.setText(marksTable.getValueAt(row, 4).toString());
                    gradeField.setText(marksTable.getValueAt(row, 5).toString());
                }
            }
        });
    }
    
    // Database Connection Method
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
    
    // Load Student Data
    private void loadStudentData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            
            // Get metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Create column names
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            
            // Create data rows
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }
            
            // Set table model
            DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            studentTable.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Load Course Data
    private void loadCourseData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {
            
            // Get metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Create column names
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            
            // Create data rows
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }
            
            // Set table model
            DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            courseTable.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading course data: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Load Marks Data
    private void loadMarksData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT m.mark_id, CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                 "c.course_name, m.semester, m.marks_obtained, m.grade " +
                 "FROM marks m " +
                 "JOIN students s ON m.student_id = s.student_id " +
                 "JOIN courses c ON m.course_id = c.course_id")) {
            
            // Create column names
            Vector<String> columnNames = new Vector<>();
            columnNames.add("Mark ID");
            columnNames.add("Student");
            columnNames.add("Course");
            columnNames.add("Semester");
            columnNames.add("Marks Obtained");
            columnNames.add("Grade");
            
            // Create data rows
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("mark_id"));
                row.add(rs.getString("student_name"));
                row.add(rs.getString("course_name"));
                row.add(rs.getString("semester"));
                row.add(rs.getDouble("marks_obtained"));
                row.add(rs.getString("grade"));
                data.add(row);
            }
            
            // Set table model
            DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            marksTable.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading marks data: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Load Combo Boxes
    private void loadComboBoxes() {
        try (Connection conn = getConnection()) {
            // Load Student Combo Box
            studentComboBox.removeAllItems();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT student_id, CONCAT(first_name, ' ', last_name) AS student_name FROM students")) {
                while (rs.next()) {
                    String item = rs.getString("student_name");
                    studentComboBox.addItem(item);
                }
            }
            
            // Load Course Combo Box
            courseComboBox.removeAllItems();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT course_id, course_name FROM courses")) {
                while (rs.next()) {
                    String item = rs.getString("course_name");
                    courseComboBox.addItem(item);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading combo box data: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Student CRUD operations
    private void addStudent() {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String dob = dobField.getText().trim();
            String program = programField.getText().trim();
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name, last name, and email are required fields.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO students (first_name, last_name, email, date_of_birth, program) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, dob);
                pstmt.setString(5, program);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Student added successfully.", 
                                                 "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearStudentFields();
                    loadStudentData();
                    loadComboBoxes();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add student.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateStudent() {
        try {
            String studentId = studentIdField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String dob = dobField.getText().trim();
            String program = programField.getText().trim();
            
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student to update.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name, last name, and email are required fields.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE students SET first_name = ?, last_name = ?, email = ?, date_of_birth = ?, program = ? WHERE student_id = ?")) {
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, dob);
                pstmt.setString(5, program);
                pstmt.setInt(6, Integer.parseInt(studentId));
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Student updated successfully.", 
                                                 "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearStudentFields();
                    loadStudentData();
                    loadComboBoxes();
                    loadMarksData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update student.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid student ID.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        try {
            String studentId = studentIdField.getText().trim();
            
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this student? This will also delete all their marks.", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                         "DELETE FROM students WHERE student_id = ?")) {
                    pstmt.setInt(1, Integer.parseInt(studentId));
                    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Student deleted successfully.", 
                                                     "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearStudentFields();
                        loadStudentData();
                        loadComboBoxes();
                        loadMarksData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete student.", 
                                                     "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid student ID.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearStudentFields() {
        studentIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        dobField.setText("");
        programField.setText("");
        studentTable.clearSelection();
    }
    
    // Course CRUD operations
    private void addCourse() {
        try {
            String courseCode = courseCodeField.getText().trim();
            String courseName = courseNameField.getText().trim();
            String creditHoursText = creditHoursField.getText().trim();
            String department = departmentField.getText().trim();
            
            if (courseCode.isEmpty() || courseName.isEmpty() || creditHoursText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course code, name, and credit hours are required fields.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int creditHours;
            try {
                creditHours = Integer.parseInt(creditHoursText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Credit hours must be a valid number.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO courses (course_code, course_name, credit_hours, department) VALUES (?, ?, ?, ?)")) {
                pstmt.setString(1, courseCode);
                pstmt.setString(2, courseName);
                pstmt.setInt(3, creditHours);
                pstmt.setString(4, department);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Course added successfully.", 
                                                 "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearCourseFields();
                    loadCourseData();
                    loadComboBoxes();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add course.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateCourse() {
        try {
            String courseId = courseIdField.getText().trim();
            String courseCode = courseCodeField.getText().trim();
            String courseName = courseNameField.getText().trim();
            String creditHoursText = creditHoursField.getText().trim();
            String department = departmentField.getText().trim();
            
            if (courseId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a course to update.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (courseCode.isEmpty() || courseName.isEmpty() || creditHoursText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course code, name, and credit hours are required fields.","Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int creditHours;
            try {
                creditHours = Integer.parseInt(creditHoursText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Credit hours must be a valid number.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE courses SET course_code = ?, course_name = ?, credit_hours = ?, department = ? WHERE course_id = ?")) {
                pstmt.setString(1, courseCode);
                pstmt.setString(2, courseName);
                pstmt.setInt(3, creditHours);
                pstmt.setString(4, department);
                pstmt.setInt(5, Integer.parseInt(courseId));
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Course updated successfully.", 
                                                 "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearCourseFields();
                    loadCourseData();
                    loadComboBoxes();
                    loadMarksData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update course.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid course ID.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCourse() {
        try {
            String courseId = courseIdField.getText().trim();
            
            if (courseId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a course to delete.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this course? This will also delete all related marks.", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                         "DELETE FROM courses WHERE course_id = ?")) {
                    pstmt.setInt(1, Integer.parseInt(courseId));
                    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Course deleted successfully.", 
                                                     "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearCourseFields();
                        loadCourseData();
                        loadComboBoxes();
                        loadMarksData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete course.", 
                                                     "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid course ID.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearCourseFields() {
        courseIdField.setText("");
        courseCodeField.setText("");
        courseNameField.setText("");
        creditHoursField.setText("");
        departmentField.setText("");
        courseTable.clearSelection();
    }
    
    // Marks CRUD operations
    private void addMark() {
        try {
            if (studentComboBox.getSelectedIndex() == -1 || courseComboBox.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student and course.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String semester = semesterField.getText().trim();
            String marksObtainedText = marksObtainedField.getText().trim();
            String grade = gradeField.getText().trim();
            
            if (semester.isEmpty() || marksObtainedText.isEmpty() || grade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semester, marks obtained, and grade are required fields.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double marksObtained;
            try {
                marksObtained = Double.parseDouble(marksObtainedText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Marks obtained must be a valid number.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get student ID and course ID from selected items
            int studentId = getStudentIdFromName(studentComboBox.getSelectedItem().toString());
            int courseId = getCourseIdFromName(courseComboBox.getSelectedItem().toString());
            
            if (studentId == -1 || courseId == -1) {
                JOptionPane.showMessageDialog(this, "Error retrieving student or course ID.",
                                             "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO marks (student_id, course_id, semester, marks_obtained, grade) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                pstmt.setString(3, semester);
                pstmt.setDouble(4, marksObtained);
                pstmt.setString(5, grade);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Mark added successfully.", 
                                                 "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearMarkFields();
                    loadMarksData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add mark.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateMark() {
        try {
            String markId = markIdField.getText().trim();
            
            if (markId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a mark to update.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (studentComboBox.getSelectedIndex() == -1 || courseComboBox.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student and course.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String semester = semesterField.getText().trim();
            String marksObtainedText = marksObtainedField.getText().trim();
            String grade = gradeField.getText().trim();
            
            if (semester.isEmpty() || marksObtainedText.isEmpty() || grade.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semester, marks obtained, and grade are required fields.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double marksObtained;
            try {
                marksObtained = Double.parseDouble(marksObtainedText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Marks obtained must be a valid number.",
                                             "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get student ID and course ID from selected items
            int studentId = getStudentIdFromName(studentComboBox.getSelectedItem().toString());
            int courseId = getCourseIdFromName(courseComboBox.getSelectedItem().toString());
            
            if (studentId == -1 || courseId == -1) {
                JOptionPane.showMessageDialog(this, "Error retrieving student or course ID.",
                                             "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE marks SET student_id = ?, course_id = ?, semester = ?, marks_obtained = ?, grade = ? WHERE mark_id = ?")) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, courseId);
                pstmt.setString(3, semester);
                pstmt.setDouble(4, marksObtained);
                pstmt.setString(5, grade);
                pstmt.setInt(6, Integer.parseInt(markId));
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Mark updated successfully.", 
                                                 "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearMarkFields();
                    loadMarksData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update mark.", 
                                                 "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid mark ID.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteMark() {
        try {
            String markId = markIdField.getText().trim();
            
            if (markId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a mark to delete.",
                                             "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this mark?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                         "DELETE FROM marks WHERE mark_id = ?")) {
                    pstmt.setInt(1, Integer.parseInt(markId));
                    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Mark deleted successfully.", 
                                                     "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearMarkFields();
                        loadMarksData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete mark.", 
                                                     "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid mark ID.", 
                                         "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearMarkFields() {
        markIdField.setText("");
        studentComboBox.setSelectedIndex(-1);
        courseComboBox.setSelectedIndex(-1);
        semesterField.setText("");
        marksObtainedField.setText("");
        gradeField.setText("");
        marksTable.clearSelection();
    }
    
    // Helper methods
    private int getStudentIdFromName(String fullName) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT student_id FROM students WHERE CONCAT(first_name, ' ', last_name) = ?")) {
            pstmt.setString(1, fullName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("student_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    private int getCourseIdFromName(String courseName) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT course_id FROM courses WHERE course_name = ?")) {
            pstmt.setString(1, courseName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Main method
    public static void main(String[] args) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Set look and feel to system's look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create login selection screen instead of main application
            SwingUtilities.invokeLater(() -> new LoginSelection());
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC driver not found. Please add the MySQL connector JAR to your project.",
                                          "Driver Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(),
                                          "Application Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}


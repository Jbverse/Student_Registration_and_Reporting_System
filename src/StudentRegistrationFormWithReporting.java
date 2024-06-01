import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.util.regex.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StudentRegistrationFormWithReporting {
    private JFrame frame;
    private JTextField academicIdField;
    private JTextField nameField;
    private JTextField birthDateField;
    private JTextField addressField;
    private JTextField contactDetailsField;
    private JComboBox<String> courseComboBox;
    private List<Student> students;
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private JTextArea studentDetailsTextArea;
    private JTextField searchField;
    private JComboBox<String> searchCriteriaComboBox;
    private TableRowSorter<DefaultTableModel> sorter;
    private Connection connection;

    public StudentRegistrationFormWithReporting() {
        students = new ArrayList<>();

        frame = new JFrame("Student Registration Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new BorderLayout());

        // Establish database connection
        connection = DatabaseConnection.getConnection();

        // Create a table if it doesn't exist
        DatabaseConnection.createTable(connection);

        // Create a panel for registration fields
        JPanel registrationPanel = new JPanel(new GridLayout(7, 2));
        JLabel academicIdLabel = new JLabel("Academic ID:");
        academicIdField = new JTextField();
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel birthDateLabel = new JLabel("Birth Date (YYYY-MM-DD):");
        birthDateField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();
        JLabel contactDetailsLabel = new JLabel("Contact Details:");
        contactDetailsField = new JTextField();
        JLabel courseLabel = new JLabel("Course:");
        String[] courses = {"Course A", "Course B", "Course C"};
        courseComboBox = new JComboBox<>(courses);
        JButton registerButton = new JButton("Register");
        registerButton.setToolTipText("Click to register a new student");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerStudent();
            }
        });
        registrationPanel.add(academicIdLabel);
        registrationPanel.add(academicIdField);
        registrationPanel.add(nameLabel);
        registrationPanel.add(nameField);
        registrationPanel.add(birthDateLabel);
        registrationPanel.add(birthDateField);
        registrationPanel.add(addressLabel);
        registrationPanel.add(addressField);
        registrationPanel.add(contactDetailsLabel);
        registrationPanel.add(contactDetailsField);
        registrationPanel.add(courseLabel);
        registrationPanel.add(courseComboBox);
        registrationPanel.add(registerButton);

        // Create a panel for student list and details
        JPanel studentPanel = new JPanel(new BorderLayout());

        // Create a table to display student data
        String[] columnNames = {"Academic ID", "Name", "Course"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(sorter);

        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                showStudentDetails();
            }
        });
        JScrollPane tableScrollPane = new JScrollPane(studentTable);

        // Create a text area to display student details
        studentDetailsTextArea = new JTextArea();
        studentDetailsTextArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(studentDetailsTextArea);

        // Create search and filter components
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchCriteriaComboBox = new JComboBox<>(columnNames);
        searchField = new JTextField(15);
        JButton clearSearchButton = new JButton("Clear Search");
        clearSearchButton.setToolTipText("Search for students by selected criteria");
        clearSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSearch();
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
                                                          public void changedUpdate(DocumentEvent e) {
                                                              performSearch();
                                                          }

                                                          public void removeUpdate(DocumentEvent e) {
                                                              performSearch();
                                                          }

                                                          public void insertUpdate(DocumentEvent e) {
                                                              performSearch();
                                                          }
                                                      });
        searchCriteriaComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchCriteriaComboBox);
        searchPanel.add(searchField);
        searchPanel.add(clearSearchButton);

        // Create edit and delete buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        editButton.setToolTipText("Edit the selected student's information");
        deleteButton.setToolTipText("Delete the selected student");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editStudent();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Create a panel for reporting
        JPanel reportingPanel = new JPanel(new FlowLayout());
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setToolTipText("Generate and export a report of enrolled students");
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateStudentReport();
            }
        });
        reportingPanel.add(generateReportButton);

        studentPanel.add(searchPanel, BorderLayout.NORTH);
        studentPanel.add(tableScrollPane, BorderLayout.WEST);
        studentPanel.add(detailsScrollPane, BorderLayout.CENTER);
        studentPanel.add(buttonPanel, BorderLayout.SOUTH);
        studentPanel.add(reportingPanel, BorderLayout.EAST);

        frame.add(registrationPanel, BorderLayout.NORTH);
        frame.add(studentPanel, BorderLayout.CENTER);

        // Load data from the database
        loadStudentsFromDatabase();

        frame.setVisible(true);
    }

    private void registerStudent() {
        String academicId = academicIdField.getText();
        String name = nameField.getText();
        String birthDate = birthDateField.getText();
        String address = addressField.getText();
        String contactDetails = contactDetailsField.getText();
        String selectedCourse = (String) courseComboBox.getSelectedItem();

        if (!validateDate(birthDate))
        {
            JOptionPane.showMessageDialog(frame, "Invalid date of birth",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert student data into the database
        insertStudentIntoDatabase(academicId, name, birthDate, address, contactDetails, selectedCourse);

        // Create a new Student object and add it to the list
        Student student = new Student(academicId, name, birthDate, address, contactDetails, selectedCourse);
        students.add(student);

        // Add the student to the table
        Object[] rowData = {academicId, name, selectedCourse};
        tableModel.addRow(rowData);

        // Clear the input fields
        academicIdField.setText("");
        nameField.setText("");
        birthDateField.setText("");
        addressField.setText("");
        contactDetailsField.setText("");
    }

    private void showStudentDetails() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            Student student = students.get(studentTable.convertRowIndexToModel(selectedRow));
            String details = "Academic ID: " + student.getAcademicId() + "\n"
                    + "Name: " + student.getName() + "\n"
                    + "Birth Date: " + student.getBirthDate() + "\n"
                    + "Address: " + student.getAddress() + "\n"
                    + "Contact Details: " + student.getContactDetails() + "\n"
                    + "Course: " + student.getCourse();
            studentDetailsTextArea.setText(details);
        }
    }

    private void clearSearch() {
        searchField.setText("");
    }

    private void performSearch() {
        int searchCriteria = searchCriteriaComboBox.getSelectedIndex();
        String searchText = searchField.getText();

        RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter(searchText, searchCriteria);
        sorter.setRowFilter(rowFilter);
    }

    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            Student student = students.get(studentTable.convertRowIndexToModel(selectedRow));

            // Create a dialog for editing student information
            JTextField editedNameField = new JTextField(student.getName());
            JTextField editedBirthDateField = new JTextField(student.getBirthDate());
            JTextField editedAddressField = new JTextField(student.getAddress());
            JTextField editedContactDetailsField = new JTextField(student.getContactDetails());

            JPanel editPanel = new JPanel(new GridLayout(4, 2));
            editPanel.add(new JLabel("Name:"));
            editPanel.add(editedNameField);
            editPanel.add(new JLabel("Birth Date:"));
            editPanel.add(editedBirthDateField);
            editPanel.add(new JLabel("Address:"));
            editPanel.add(editedAddressField);
            editPanel.add(new JLabel("Contact Details:"));
            editPanel.add(editedContactDetailsField);

            int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit Student Information",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (!validateDate(editedBirthDateField.getText()))
                {
                    JOptionPane.showMessageDialog(frame, "Invalid date of birth",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update student information in the database
                updateStudentInDatabase(student.getAcademicId(),
                        editedNameField.getText(),
                        editedBirthDateField.getText(),
                        editedAddressField.getText(),
                        editedContactDetailsField.getText());

                // Update student information in the list
                student.setName(editedNameField.getText());
                student.setBirthDate(editedBirthDateField.getText());
                student.setAddress(editedAddressField.getText());
                student.setContactDetails(editedContactDetailsField.getText());

                // Update the table
                tableModel.setValueAt(student.getName(), selectedRow, 1);
                showStudentDetails();
            }
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            Student student = students.get(studentTable.convertRowIndexToModel(selectedRow));

            // Confirm the deletion
            int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?",
                    "Delete Student", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                // Delete the student from the database
                deleteStudentFromDatabase(student.getAcademicId());

                // Remove the student from the list and table
                students.remove(student);
                tableModel.removeRow(selectedRow);
                studentDetailsTextArea.setText("");
            }
        }
    }

    private void insertStudentIntoDatabase(String academicId, String name, String birthDate, String address,
                                           String contactDetails, String course) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO students (academic_id, name, birth_date, address, contact_details, course) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            );
            preparedStatement.setString(1, academicId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, birthDate);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, contactDetails);
            preparedStatement.setString(6, course);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to insert student data into the database.");
        }
    }

    private void updateStudentInDatabase(String academicId, String name, String birthDate, String address,
                                         String contactDetails) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE students SET name = ?, birth_date = ?, address = ?, contact_details = ? " +
                            "WHERE academic_id = ?"
            );
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, birthDate);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, contactDetails);
            preparedStatement.setString(5, academicId);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to update student data in the database.");
        }
    }

    private void deleteStudentFromDatabase(String academicId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM students WHERE academic_id = ?"
            );
            preparedStatement.setString(1, academicId);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to delete student data from the database.");
        }
    }

    private void loadStudentsFromDatabase() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM students");

            while (resultSet.next()) {
                String academicId = resultSet.getString("academic_id");
                String name = resultSet.getString("name");
                String birthDate = resultSet.getString("birth_date");
                String address = resultSet.getString("address");
                String contactDetails = resultSet.getString("contact_details");
                String course = resultSet.getString("course");

                Student student = new Student(academicId, name, birthDate, address, contactDetails, course);
                students.add(student);

                // Add the student to the table
                Object[] rowData = {academicId, name, course};
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load student data from the database.");
        }
    }

    private void generateStudentReport() {
        // Create a report based on user-selected criteria
        String report = generateReportText();

        // Show a file dialog for selecting the export location
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Export Location");
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            // Add .txt extension if missing
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }

            try {
                // Write the report to the selected file
                FileWriter fileWriter = new FileWriter(filePath);
                fileWriter.write(report);
                fileWriter.close();

                JOptionPane.showMessageDialog(frame, "Report exported successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to export the report.");
            }
        }
    }

    private String generateReportText() {
        StringBuilder report = new StringBuilder("Enrolled Students Report\n\n");

        // Append student data to the report
        for (Student student : students) {
            report.append("Academic ID: ").append(student.getAcademicId()).append("\n");
            report.append("Name: ").append(student.getName()).append("\n");
            report.append("Birth Date: ").append(student.getBirthDate()).append("\n");
            report.append("Address: ").append(student.getAddress()).append("\n");
            report.append("Contact Details: ").append(student.getContactDetails()).append("\n");
            report.append("Course: ").append(student.getCourse()).append("\n");
            report.append("\n");
        }

        return report.toString();
    }

    private boolean validateDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(date, formatter);
        }
        catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentRegistrationFormWithReporting();
            }
        });
    }
}

class DatabaseConnection {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/student_db";

    private static final String USER = "root";
    private static final String PASSWORD = "Ybano1997!";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            String jdbcUrl = "jdbc:mysql://localhost:3306/student_db?serverTimezone=UTC";
            connection = DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
        }
        return connection;
    }

    public static void createTable(Connection connection) {

        try {
            Statement statement = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "academic_id VARCHAR(10) UNIQUE NOT NULL," +
                    "name VARCHAR(255) NOT NULL," +
                    "birth_date DATE," +
                    "address VARCHAR(255)," +
                    "contact_details VARCHAR(255)," +
                    "course VARCHAR(50)" +
                    ")";
            statement.executeUpdate(createTableSQL);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to create the 'students' table.");
        }
    }
}

### Project Description: Student Registration and Reporting System

#### Overview
The Student Registration and Reporting System is a Java-based application designed to facilitate the registration and management of student information. This project includes a graphical user interface (GUI) for easy interaction and a MySQL database for persistent data storage. It is developed using Java Swing for the front-end interface and JDBC for database connectivity.

#### Features
1. **Student Registration**:
   - Users can register new students by entering their academic ID, name, birth date, address, contact details, and course.
   - The system validates the input data and adds the new student to the database and the display table.

2. **Student Information Display**:
   - A dynamic table lists all registered students with columns for academic ID, name, and course.
   - Selecting a student from the table displays detailed information about the student.

3. **Database Integration**:
   - The application connects to a MySQL database to store and retrieve student information.
   - It ensures data persistence, enabling users to maintain a record of students across sessions.

4. **User-Friendly Interface**:
   - The GUI is built using Java Swing, providing a straightforward and interactive user experience.
   - Tooltips and input validations enhance usability and data accuracy.

#### Components
1. **Student.java**:
   - Defines a `Student` class with attributes for academic ID, name, birth date, address, contact details, and course.
   - Includes getter and setter methods for accessing and modifying student details.

2. **StudentRegistrationFormWithReporting.java**:
   - Implements the main GUI for the application.
   - Includes fields and buttons for entering student details, a table for displaying registered students, and methods for handling user actions and data validation.

3. **DatabaseConnection.java**:
   - Manages database connectivity using JDBC.
   - Provides methods for establishing a connection to the MySQL database and creating the `students` table if it does not exist.

#### How to Use
1. **Set Up the Database**:
   - Ensure MySQL is installed and running.
   - Create a database named `student_db` and update the connection details in `DatabaseConnection.java` if necessary.

2. **Run the Application**:
   - Compile and execute the `StudentRegistrationFormWithReporting.java` file.
   - The GUI will launch, allowing users to register students and view their details.

3. **Registering Students**:
   - Enter the required student information in the provided fields and click the "Register" button.
   - The student will be added to the database and displayed in the table.

4. **Viewing Student Details**:
   - Select a student from the table to view their detailed information in the display panel.

This project is ideal for educational institutions and small organizations looking to streamline their student registration and management processes with a simple yet effective software solution.

package com.scrum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    // Change these values to match your local MySQL setup.
    private static final String URL =
            "jdbc:mysql://localhost:3306/scrum_cost_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeTables() throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS projects (
                    project_id INT AUTO_INCREMENT PRIMARY KEY,
                    project_name VARCHAR(200) NOT NULL,
                    srs_text LONGTEXT NOT NULL,
                    hourly_rate DECIMAL(10,2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tasks (
                    task_id INT AUTO_INCREMENT PRIMARY KEY,
                    project_id INT NOT NULL,
                    task_name VARCHAR(255) NOT NULL,
                    extracted_verb VARCHAR(100),
                    extracted_noun VARCHAR(100),
                    assigned_agent VARCHAR(100),
                    estimated_hours DECIMAL(10,2) NOT NULL,
                    estimated_cost DECIMAL(12,2) NOT NULL,
                    sprint_number INT NOT NULL,
                    FOREIGN KEY (project_id) REFERENCES projects(project_id)
                        ON DELETE CASCADE
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS lessons_learned (
                    lesson_id INT AUTO_INCREMENT PRIMARY KEY,
                    project_id INT NOT NULL,
                    lesson_text TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (project_id) REFERENCES projects(project_id)
                        ON DELETE CASCADE
                )
                """);
        }
    }
}

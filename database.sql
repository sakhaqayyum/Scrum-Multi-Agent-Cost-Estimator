CREATE DATABASE IF NOT EXISTS scrum_cost_db;
USE scrum_cost_db;

CREATE TABLE IF NOT EXISTS projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(200) NOT NULL,
    srs_text LONGTEXT NOT NULL,
    hourly_rate DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lessons_learned (
    lesson_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    lesson_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
);

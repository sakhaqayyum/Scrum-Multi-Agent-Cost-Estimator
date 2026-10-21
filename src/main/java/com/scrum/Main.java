package com.scrum;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Database.initializeTables();

            System.out.println("=== Scrum Multi-Agent Cost Estimation System ===");
            System.out.print("Project name: ");
            String projectName = scanner.nextLine().trim();
            if (projectName.isEmpty()) projectName = "Untitled Project";

            System.out.print("Hourly development rate (e.g. 40.00): ");
            double hourlyRate = Double.parseDouble(scanner.nextLine().trim());
            if (hourlyRate < 0) {
                System.out.println("Hourly rate cannot be negative.");
                return;
            }

            System.out.println("Paste the SRS text. Enter a line containing only END to finish:");
            StringBuilder srs = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("END")) break;
                srs.append(line).append(System.lineSeparator());
            }
            if (srs.toString().isBlank()) {
                System.out.println("No SRS text provided.");
                return;
            }

            RequirementAgent requirementAgent = new RequirementAgent();
            TaskAgent taskAgent = new TaskAgent();
            EstimationAgent estimationAgent = new EstimationAgent();

            List<RequirementAgent.Requirement> requirements =
                    requirementAgent.extract(srs.toString());
            List<TaskAgent.PlannedTask> tasks =
                    taskAgent.distribute(requirements, hourlyRate);
            EstimationAgent.Estimate estimate = estimationAgent.summarize(tasks);

            int projectId = saveProject(projectName, srs.toString(), hourlyRate);
            saveTasks(projectId, tasks);

            System.out.println("\n--- Extracted requirements and task distribution ---");
            int i = 1;
            for (TaskAgent.PlannedTask task : tasks) {
                System.out.printf(
                        "%n%d. %s%n   Verb: %s | Noun: %s | Agent: %s%n" +
                        "   Hours: %.2f | Cost: %.2f | Sprint: %d%n",
                        i++, task.taskName(), task.verb(), task.noun(),
                        task.assignedAgent(), task.estimatedHours(),
                        task.estimatedCost(), task.sprintNumber());
            }

            System.out.println("\n--- Estimation summary ---");
            System.out.println("Project ID: " + projectId);
            System.out.printf("Total tasks: %d%n", tasks.size());
            System.out.printf("Total estimated hours: %.2f%n", estimate.totalHours());
            System.out.printf("Total estimated cost: %.2f%n", estimate.totalCost());
            System.out.printf("Estimated sprints: %d (planning assumption: 5 tasks/sprint)%n",
                    estimate.sprintCount());
            System.out.println("Project and tasks saved to MySQL.");

            System.out.print("\nSave a lesson learned? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.print("Lesson: ");
                String lesson = scanner.nextLine().trim();
                if (!lesson.isEmpty()) saveLesson(projectId, lesson);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number. Enter a valid hourly rate.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.err.println("Check that MySQL is running, the database exists, and Database.java credentials are correct.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int saveProject(String name, String srs, double rate)
            throws SQLException {
        String sql = "INSERT INTO projects(project_name, srs_text, hourly_rate) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, srs);
            ps.setDouble(3, rate);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                throw new SQLException("Project insert succeeded but no project ID was returned.");
            }
        }
    }

    private static void saveTasks(int projectId, List<TaskAgent.PlannedTask> tasks)
            throws SQLException {
        String sql = """
            INSERT INTO tasks(project_id, task_name, extracted_verb, extracted_noun,
                              assigned_agent, estimated_hours, estimated_cost, sprint_number)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (TaskAgent.PlannedTask task : tasks) {
                ps.setInt(1, projectId);
                ps.setString(2, task.taskName());
                ps.setString(3, task.verb());
                ps.setString(4, task.noun());
                ps.setString(5, task.assignedAgent());
                ps.setDouble(6, task.estimatedHours());
                ps.setDouble(7, task.estimatedCost());
                ps.setInt(8, task.sprintNumber());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void saveLesson(int projectId, String lesson)
            throws SQLException {
        String sql = "INSERT INTO lessons_learned(project_id, lesson_text) VALUES (?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setString(2, lesson);
            ps.executeUpdate();
            System.out.println("Lesson learned saved.");
        }
    }
}

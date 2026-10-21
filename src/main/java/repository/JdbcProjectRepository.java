package com.scrum.estimator.repository;

import com.scrum.estimator.domain.PlannedTask;
import com.scrum.estimator.domain.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class JdbcProjectRepository implements ProjectRepository {
    private static final String INSERT_PROJECT =
            "INSERT INTO projects(project_name, srs_text, hourly_rate) VALUES (?, ?, ?)";
    private static final String INSERT_TASK = """
            INSERT INTO tasks(project_id, task_name, extracted_verb, extracted_noun,
                              assigned_agent, estimated_hours, estimated_cost, sprint_number)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String INSERT_LESSON =
            "INSERT INTO lessons_learned(project_id, lesson_text) VALUES (?, ?)";

    private final ConnectionFactory connectionFactory;

    public JdbcProjectRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public long save(Project project, List<PlannedTask> tasks) {
        try (Connection connection = connectionFactory.open()) {
            connection.setAutoCommit(false);
            try {
                long projectId = insertProject(connection, project);
                insertTasks(connection, projectId, tasks);
                connection.commit();
                return projectId;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not save project '" + project.name() + "'.", e);
        }
    }

    @Override
    public void saveLesson(long projectId, String lessonText) {
        try (Connection connection = connectionFactory.open();
             PreparedStatement ps = connection.prepareStatement(INSERT_LESSON)) {
            ps.setLong(1, projectId);
            ps.setString(2, lessonText);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Could not save lesson for project " + projectId + ".", e);
        }
    }

    @Override
    public String description() {
        return "MySQL";
    }

    private static long insertProject(Connection connection, Project project) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                INSERT_PROJECT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.name());
            ps.setString(2, project.srsText());
            ps.setDouble(3, project.hourlyRate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Project insert succeeded but no project ID was returned.");
            }
        }
    }

    private static void insertTasks(Connection connection, long projectId, List<PlannedTask> tasks)
            throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(INSERT_TASK)) {
            for (PlannedTask task : tasks) {
                ps.setLong(1, projectId);
                ps.setString(2, task.taskName());
                ps.setString(3, task.verb());
                ps.setString(4, task.noun());
                ps.setString(5, task.assignedRole().displayName());
                ps.setDouble(6, task.estimatedHours());
                ps.setDouble(7, task.estimatedCost());
                ps.setInt(8, task.sprintNumber());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}

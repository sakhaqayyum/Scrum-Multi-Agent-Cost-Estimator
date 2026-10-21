package com.scrum.estimator;

import com.scrum.estimator.agent.EffortEstimator;
import com.scrum.estimator.agent.EstimationAgent;
import com.scrum.estimator.agent.RequirementAgent;
import com.scrum.estimator.agent.RoleAssigner;
import com.scrum.estimator.agent.TaskAgent;
import com.scrum.estimator.cli.ConsoleApp;
import com.scrum.estimator.config.AppConfig;
import com.scrum.estimator.repository.ConnectionFactory;
import com.scrum.estimator.repository.InMemoryProjectRepository;
import com.scrum.estimator.repository.JdbcProjectRepository;
import com.scrum.estimator.repository.ProjectRepository;
import com.scrum.estimator.repository.SchemaInitializer;
import com.scrum.estimator.service.EstimationService;

import java.sql.SQLException;
import java.util.Scanner;

/** Entry point. Wires the application together from configuration. */
public final class App {
    private App() {}

    public static void main(String[] args) {
        AppConfig config = AppConfig.load();
        ProjectRepository repository = createRepository(config.database());

        TaskAgent taskAgent = new TaskAgent(
                new RoleAssigner(), new EffortEstimator(), config.estimation().tasksPerSprint());
        EstimationService service = new EstimationService(
                new RequirementAgent(), taskAgent, new EstimationAgent(), repository);

        int exitCode;
        try (Scanner scanner = new Scanner(System.in)) {
            exitCode = new ConsoleApp(service, scanner, System.out, System.err,
                    config.estimation().tasksPerSprint()).run();
        }
        System.exit(exitCode);
    }

    private static ProjectRepository createRepository(AppConfig.Database db) {
        if (!db.enabled()) {
            return new InMemoryProjectRepository();
        }
        ConnectionFactory connections = new ConnectionFactory(db);
        try {
            new SchemaInitializer(connections).initialize();
            return new JdbcProjectRepository(connections);
        } catch (SQLException e) {
            System.err.println("WARNING: MySQL is not available (" + e.getMessage() + ").");
            System.err.println("         Continuing with in-memory storage; results will not be saved.");
            System.err.println("         Set SCRUM_DB_PASSWORD / SCRUM_DB_URL, or SCRUM_DB_ENABLED=false to hide this.");
            return new InMemoryProjectRepository();
        }
    }
}

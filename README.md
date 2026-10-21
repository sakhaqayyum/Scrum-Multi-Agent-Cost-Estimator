# Scrum Multi-Agent Cost Estimator

A Java 17 console application that reads Software Requirements Specification (SRS) text and produces a
Scrum plan: one task per requirement, the team role that owns it, estimated hours and cost, and the
sprint it falls into. Results are saved to MySQL, or kept in memory when no database is available.

> Educational prototype. Verb/noun extraction is rule based and effort values are illustrative
> heuristics, not validated cost predictions.

## How it works

```
SRS text ──► RequirementAgent ──► TaskAgent ──────────────► EstimationAgent ──► report
             (sentences,          (RoleAssigner: verb→role,   (total hours,
              verb + noun)         EffortEstimator: hours,     cost, sprints)
                                   cost, sprint number)
                                                     │
                                                     ▼
                                            ProjectRepository
                                      (MySQL via JDBC, or in-memory)
```

| Agent / component  | Responsibility |
|--------------------|----------------|
| `RequirementAgent` | Splits SRS into sentences; picks the first known verb and first meaningful noun. |
| `RoleAssigner`     | Maps verbs to roles: Security/Test, Database, Analysis, Development. |
| `EffortEstimator`  | 4 h base, +8 h for integration/security/payment, +4 h for reports/dashboards, +4 h for long requirements. |
| `TaskAgent`        | Builds planned tasks, cost = hours × hourly rate, groups N tasks per sprint (default 5). |
| `EstimationAgent`  | Sums hours and cost and counts sprints. |

## Project structure

```
src/main/java/com/scrum/estimator/
├── App.java                     Entry point: loads config and wires components together
├── agent/                       Estimation logic (no I/O, easy to unit test)
│   ├── RequirementAgent.java
│   ├── RoleAssigner.java
│   ├── EffortEstimator.java
│   ├── TaskAgent.java
│   └── EstimationAgent.java
├── domain/                      Immutable data types (records / enums)
│   ├── Project.java             Validated input: name, SRS text, hourly rate
│   ├── Requirement.java
│   ├── PlannedTask.java
│   ├── AgentRole.java
│   ├── Estimate.java
│   └── EstimationReport.java
├── service/
│   └── EstimationService.java   Runs the agent pipeline and persists the result
├── repository/                  Persistence
│   ├── ProjectRepository.java   Interface
│   ├── JdbcProjectRepository.java   MySQL implementation (transactional save)
│   ├── InMemoryProjectRepository.java   Fallback / test implementation
│   ├── ConnectionFactory.java
│   ├── SchemaInitializer.java   Applies db/schema.sql on startup
│   └── RepositoryException.java
├── config/
│   └── AppConfig.java           Reads application.properties + env var overrides
└── cli/
    ├── ConsoleApp.java          Interactive prompts
    └── ReportPrinter.java       Report formatting

src/main/resources/
├── application.properties       Default settings (no secrets)
└── db/schema.sql                Table definitions

src/test/java/com/scrum/estimator/   JUnit 5 tests for agents and service
```

## Requirements

- Java 17 or newer
- Maven 3.9+ (bundled in Eclipse and IntelliJ)
- MySQL 8+ (optional — the app falls back to in-memory storage without it)

## Configuration

Defaults live in `src/main/resources/application.properties`. Override any key with an environment
variable (`SCRUM_` + key in upper case, dots and dashes as underscores) or a `-D` JVM flag.

| Key                           | Env variable                    | Default |
|-------------------------------|---------------------------------|---------|
| `db.enabled`                  | `SCRUM_DB_ENABLED`              | `true`  |
| `db.url`                      | `SCRUM_DB_URL`                  | `jdbc:mysql://localhost:3306/scrum_cost_db?createDatabaseIfNotExist=true&…` |
| `db.user`                     | `SCRUM_DB_USER`                 | `root`  |
| `db.password`                 | `SCRUM_DB_PASSWORD`             | *(empty)* |
| `estimation.tasks-per-sprint` | `SCRUM_ESTIMATION_TASKS_PER_SPRINT` | `5` |

Keep real passwords out of the properties file; set `SCRUM_DB_PASSWORD` instead.
The database and tables are created automatically on first run.

PowerShell example:

```powershell
$env:SCRUM_DB_PASSWORD = "your-password"
mvn -q compile exec:java
```

## Build and run

```bash
mvn test                 # run unit tests
mvn compile exec:java    # run the console app
mvn package              # build target/scrum-cost-estimator.jar (MySQL driver included)
java -jar target/scrum-cost-estimator.jar
```

**Eclipse:** File → Import → Maven → Existing Maven Projects, select this folder, then run
`com.scrum.estimator.App` as a Java Application. Set `SCRUM_DB_PASSWORD` under
Run Configurations → Environment.

## Example session

```
Project name: Demo Project
Hourly development rate (e.g. 40.00): 40
Paste the SRS text. Enter a line containing only END to finish:
The system shall allow users to register and login.
The system shall store project requirements.
The system shall generate a cost estimation report.
END
```

Writing one requirement per line in "The system shall …" form gives the best results.

## Limitations

- Verbs are matched exactly against a fixed list (`provides` does not match `provide`).
- One sentence becomes one task, so a paragraph listing several features yields a single task.
- Effort and sprint grouping are heuristics. For real estimation use story points, team velocity and
  historical project data.

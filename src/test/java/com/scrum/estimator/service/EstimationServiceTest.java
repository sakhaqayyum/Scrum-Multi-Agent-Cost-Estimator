package com.scrum.estimator.service;

import com.scrum.estimator.agent.EffortEstimator;
import com.scrum.estimator.agent.EstimationAgent;
import com.scrum.estimator.agent.RequirementAgent;
import com.scrum.estimator.agent.RoleAssigner;
import com.scrum.estimator.agent.TaskAgent;
import com.scrum.estimator.domain.EstimationReport;
import com.scrum.estimator.domain.Project;
import com.scrum.estimator.repository.InMemoryProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstimationServiceTest {
    private InMemoryProjectRepository repository;
    private EstimationService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProjectRepository();
        service = new EstimationService(
                new RequirementAgent(),
                new TaskAgent(new RoleAssigner(), new EffortEstimator(), 5),
                new EstimationAgent(),
                repository);
    }

    @Test
    void estimatesAndSavesProject() {
        Project project = new Project("Demo", """
                The system shall allow users to register and login.
                The system shall store project requirements.
                The system shall generate a cost estimation report.
                """, 40);

        EstimationReport report = service.estimate(project);

        assertEquals(3, report.tasks().size());
        assertEquals(16.0, report.estimate().totalHours());
        assertEquals(640.0, report.estimate().totalCost());
        assertEquals(1, report.estimate().sprintCount());
        assertEquals(3, repository.find(report.projectId()).tasks().size());
    }

    @Test
    void recordsLessonAgainstProject() {
        long id = service.estimate(new Project("Demo", "Store data.", 10)).projectId();

        service.recordLesson(id, "  Split large stories.  ");

        assertEquals("Split large stories.", repository.find(id).lessons().get(0));
    }

    @Test
    void rejectsInvalidProjectInput() {
        assertThrows(IllegalArgumentException.class, () -> new Project("Demo", "Store data.", -1));
        assertThrows(IllegalArgumentException.class, () -> new Project("Demo", "  ", 10));
    }
}

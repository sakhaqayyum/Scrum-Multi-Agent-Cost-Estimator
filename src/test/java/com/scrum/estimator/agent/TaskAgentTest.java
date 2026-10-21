package com.scrum.estimator.agent;

import com.scrum.estimator.domain.AgentRole;
import com.scrum.estimator.domain.PlannedTask;
import com.scrum.estimator.domain.Requirement;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskAgentTest {
    private final TaskAgent agent = new TaskAgent(new RoleAssigner(), new EffortEstimator(), 5);

    @Test
    void assignsRoleByVerb() {
        List<PlannedTask> tasks = agent.distribute(List.of(
                new Requirement("Validate input.", "validate", "input"),
                new Requirement("Store data.", "store", "data"),
                new Requirement("Calculate totals.", "calculate", "totals"),
                new Requirement("View items.", "view", "items")), 10);

        assertEquals(AgentRole.SECURITY_TEST, tasks.get(0).assignedRole());
        assertEquals(AgentRole.DATABASE, tasks.get(1).assignedRole());
        assertEquals(AgentRole.ANALYSIS, tasks.get(2).assignedRole());
        assertEquals(AgentRole.DEVELOPMENT, tasks.get(3).assignedRole());
    }

    @Test
    void costIsHoursTimesRate() {
        PlannedTask task = agent.distribute(
                List.of(new Requirement("Integrate payment gateway.", "process", "payment")), 25).get(0);

        assertEquals(12.0, task.estimatedHours());
        assertEquals(300.0, task.estimatedCost());
    }

    @Test
    void groupsTasksIntoSprints() {
        List<Requirement> six = Collections.nCopies(6, new Requirement("View items.", "view", "items"));
        List<PlannedTask> tasks = agent.distribute(six, 10);

        assertEquals(1, tasks.get(4).sprintNumber());
        assertEquals(2, tasks.get(5).sprintNumber());
    }

    @Test
    void rejectsNonPositiveSprintSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new TaskAgent(new RoleAssigner(), new EffortEstimator(), 0));
    }
}

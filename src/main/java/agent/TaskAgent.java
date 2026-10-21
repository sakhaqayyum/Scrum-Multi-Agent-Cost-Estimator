package com.scrum.estimator.agent;

import com.scrum.estimator.domain.AgentRole;
import com.scrum.estimator.domain.PlannedTask;
import com.scrum.estimator.domain.Requirement;

import java.util.ArrayList;
import java.util.List;

/** Turns requirements into planned tasks: assigns a role, estimates effort and cost, and slots each into a sprint. */
public class TaskAgent {
    private final RoleAssigner roleAssigner;
    private final EffortEstimator effortEstimator;
    private final int tasksPerSprint;

    public TaskAgent(RoleAssigner roleAssigner, EffortEstimator effortEstimator, int tasksPerSprint) {
        if (tasksPerSprint < 1) {
            throw new IllegalArgumentException("tasksPerSprint must be at least 1.");
        }
        this.roleAssigner = roleAssigner;
        this.effortEstimator = effortEstimator;
        this.tasksPerSprint = tasksPerSprint;
    }

    public List<PlannedTask> distribute(List<Requirement> requirements, double hourlyRate) {
        List<PlannedTask> tasks = new ArrayList<>();
        for (int i = 0; i < requirements.size(); i++) {
            Requirement requirement = requirements.get(i);
            AgentRole role = roleAssigner.assign(requirement.verb());
            double hours = effortEstimator.estimateHours(requirement.sentence());
            int sprint = (i / tasksPerSprint) + 1;

            tasks.add(new PlannedTask(
                    requirement.sentence(), requirement.verb(), requirement.noun(),
                    role, hours, hours * hourlyRate, sprint));
        }
        return tasks;
    }
}

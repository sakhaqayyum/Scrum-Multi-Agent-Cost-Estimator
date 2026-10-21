package com.scrum.estimator.agent;

import com.scrum.estimator.domain.Estimate;
import com.scrum.estimator.domain.PlannedTask;

import java.util.List;

/** Rolls planned tasks up into project totals. */
public class EstimationAgent {
    public Estimate summarize(List<PlannedTask> tasks) {
        double hours = tasks.stream().mapToDouble(PlannedTask::estimatedHours).sum();
        double cost = tasks.stream().mapToDouble(PlannedTask::estimatedCost).sum();
        int sprints = tasks.stream().mapToInt(PlannedTask::sprintNumber).max().orElse(0);
        return new Estimate(hours, cost, sprints);
    }
}

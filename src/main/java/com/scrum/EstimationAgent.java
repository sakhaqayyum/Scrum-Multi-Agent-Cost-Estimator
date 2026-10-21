package com.scrum;

import java.util.List;

public class EstimationAgent {
    public Estimate summarize(List<TaskAgent.PlannedTask> tasks) {
        double hours = tasks.stream()
                .mapToDouble(TaskAgent.PlannedTask::estimatedHours).sum();
        double cost = tasks.stream()
                .mapToDouble(TaskAgent.PlannedTask::estimatedCost).sum();
        int sprints = tasks.stream()
                .mapToInt(TaskAgent.PlannedTask::sprintNumber).max().orElse(0);
        return new Estimate(hours, cost, sprints);
    }

    public record Estimate(double totalHours, double totalCost, int sprintCount) {}
}

package com.scrum.estimator.domain;

import java.util.List;

/** Result of one estimation run, including the ID it was saved under. */
public record EstimationReport(
        long projectId, Project project, List<PlannedTask> tasks, Estimate estimate) {

    public EstimationReport {
        tasks = List.copyOf(tasks);
    }
}

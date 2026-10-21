package com.scrum.estimator.service;

import com.scrum.estimator.agent.EstimationAgent;
import com.scrum.estimator.agent.RequirementAgent;
import com.scrum.estimator.agent.TaskAgent;
import com.scrum.estimator.domain.Estimate;
import com.scrum.estimator.domain.EstimationReport;
import com.scrum.estimator.domain.PlannedTask;
import com.scrum.estimator.domain.Project;
import com.scrum.estimator.domain.Requirement;
import com.scrum.estimator.repository.ProjectRepository;

import java.util.List;

/** Runs the requirement → task → estimation agent pipeline and persists the result. */
public class EstimationService {
    private final RequirementAgent requirementAgent;
    private final TaskAgent taskAgent;
    private final EstimationAgent estimationAgent;
    private final ProjectRepository repository;

    public EstimationService(RequirementAgent requirementAgent, TaskAgent taskAgent,
                             EstimationAgent estimationAgent, ProjectRepository repository) {
        this.requirementAgent = requirementAgent;
        this.taskAgent = taskAgent;
        this.estimationAgent = estimationAgent;
        this.repository = repository;
    }

    public EstimationReport estimate(Project project) {
        List<Requirement> requirements = requirementAgent.extract(project.srsText());
        List<PlannedTask> tasks = taskAgent.distribute(requirements, project.hourlyRate());
        Estimate estimate = estimationAgent.summarize(tasks);
        long projectId = repository.save(project, tasks);
        return new EstimationReport(projectId, project, tasks, estimate);
    }

    public void recordLesson(long projectId, String lessonText) {
        if (lessonText == null || lessonText.isBlank()) {
            throw new IllegalArgumentException("Lesson text must not be blank.");
        }
        repository.saveLesson(projectId, lessonText.trim());
    }

    public String storageDescription() {
        return repository.description();
    }
}

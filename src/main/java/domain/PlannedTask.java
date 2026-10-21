package com.scrum.estimator.domain;

public record PlannedTask(
        String taskName, String verb, String noun, AgentRole assignedRole,
        double estimatedHours, double estimatedCost, int sprintNumber) {}

package com.scrum.estimator.cli;

import com.scrum.estimator.domain.Estimate;
import com.scrum.estimator.domain.EstimationReport;
import com.scrum.estimator.domain.PlannedTask;

import java.io.PrintStream;

public class ReportPrinter {
    private final PrintStream out;

    public ReportPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(EstimationReport report, int tasksPerSprint, String storage) {
        out.println();
        out.println("--- Extracted requirements and task distribution ---");
        int i = 1;
        for (PlannedTask task : report.tasks()) {
            out.printf(
                    "%n%d. %s%n   Verb: %s | Noun: %s | Agent: %s%n" +
                    "   Hours: %.2f | Cost: %.2f | Sprint: %d%n",
                    i++, task.taskName(), task.verb(), task.noun(),
                    task.assignedRole().displayName(), task.estimatedHours(),
                    task.estimatedCost(), task.sprintNumber());
        }

        Estimate estimate = report.estimate();
        out.println();
        out.println("--- Estimation summary ---");
        out.println("Project ID: " + report.projectId());
        out.printf("Total tasks: %d%n", report.tasks().size());
        out.printf("Total estimated hours: %.2f%n", estimate.totalHours());
        out.printf("Total estimated cost: %.2f%n", estimate.totalCost());
        out.printf("Estimated sprints: %d (planning assumption: %d tasks/sprint)%n",
                estimate.sprintCount(), tasksPerSprint);
        out.println("Project and tasks saved to " + storage + ".");
    }
}

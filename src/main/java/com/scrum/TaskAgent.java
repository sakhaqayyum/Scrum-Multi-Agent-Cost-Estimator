package com.scrum;

import java.util.ArrayList;
import java.util.List;

public class TaskAgent {
    public List<PlannedTask> distribute(
            List<RequirementAgent.Requirement> requirements,
            double hourlyRate) {

        List<PlannedTask> tasks = new ArrayList<>();
        int number = 1;

        for (RequirementAgent.Requirement requirement : requirements) {
            String verb = requirement.verb();
            String noun = requirement.noun();

            // Example rule-based role assignment; customize for your team.
            String assignedAgent;
            if (verb.equals("validate") || verb.equals("authenticate")
                    || verb.equals("login")) {
                assignedAgent = "Security/Test Agent";
            } else if (verb.equals("save") || verb.equals("store")
                    || verb.equals("record") || verb.equals("update")
                    || verb.equals("delete")) {
                assignedAgent = "Database Agent";
            } else if (verb.equals("estimate") || verb.equals("calculate")
                    || verb.equals("analyze") || verb.equals("analyse")
                    || verb.equals("extract")) {
                assignedAgent = "Analysis Agent";
            } else {
                assignedAgent = "Development Agent";
            }

            // Heuristic only. Replace with team velocity or planning-poker values.
            double hours = estimateHours(requirement.sentence());
            double cost = hours * hourlyRate;
            int sprint = ((number - 1) / 5) + 1;

            tasks.add(new PlannedTask(
                    requirement.sentence(), verb, noun, assignedAgent,
                    hours, cost, sprint));
            number++;
        }
        return tasks;
    }

    private double estimateHours(String sentence) {
        String text = sentence.toLowerCase();
        double hours = 4.0;
        if (text.contains("integrat") || text.contains("security")
                || text.contains("authenticate") || text.contains("payment")) {
            hours += 8.0;
        }
        if (text.contains("report") || text.contains("dashboard")
                || text.contains("generate")) {
            hours += 4.0;
        }
        if (text.length() > 180) hours += 4.0;
        return hours;
    }

    public record PlannedTask(
            String taskName, String verb, String noun, String assignedAgent,
            double estimatedHours, double estimatedCost, int sprintNumber) {}
}

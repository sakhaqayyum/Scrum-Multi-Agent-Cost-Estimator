package com.scrum.estimator.cli;

import com.scrum.estimator.domain.EstimationReport;
import com.scrum.estimator.domain.Project;
import com.scrum.estimator.repository.RepositoryException;
import com.scrum.estimator.service.EstimationService;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;

/** Interactive console front end: collects input, runs the service, prints the report. */
public class ConsoleApp {
    private static final String END_MARKER = "END";
    private static final String DEFAULT_PROJECT_NAME = "Untitled Project";

    private final EstimationService service;
    private final Scanner in;
    private final PrintStream out;
    private final PrintStream err;
    private final ReportPrinter printer;
    private final int tasksPerSprint;

    public ConsoleApp(EstimationService service, Scanner in, PrintStream out, PrintStream err,
                      int tasksPerSprint) {
        this.service = service;
        this.in = in;
        this.out = out;
        this.err = err;
        this.printer = new ReportPrinter(out);
        this.tasksPerSprint = tasksPerSprint;
    }

    /** Returns a process exit code: 0 on success, 1 on bad input or a storage failure. */
    public int run() {
        out.println("=== Scrum Multi-Agent Cost Estimation System ===");
        out.println("Storage: " + service.storageDescription());

        String name = prompt("Project name: ").orElse("");
        if (name.isEmpty()) name = DEFAULT_PROJECT_NAME;

        Optional<Double> rate = readHourlyRate();
        if (rate.isEmpty()) return 1;

        String srs = readSrs();
        if (srs.isBlank()) {
            err.println("No SRS text provided.");
            return 1;
        }

        try {
            EstimationReport report = service.estimate(new Project(name, srs, rate.get()));
            printer.print(report, tasksPerSprint, service.storageDescription());
            offerLesson(report.projectId());
            return 0;
        } catch (RepositoryException e) {
            err.println("Database error: " + e.getMessage()
                    + (e.getCause() != null ? " (" + e.getCause().getMessage() + ")" : ""));
            return 1;
        }
    }

    private Optional<Double> readHourlyRate() {
        String raw = prompt("Hourly development rate (e.g. 40.00): ").orElse("");
        try {
            double rate = Double.parseDouble(raw);
            if (rate < 0 || Double.isNaN(rate) || Double.isInfinite(rate)) {
                err.println("Hourly rate must be a non-negative number.");
                return Optional.empty();
            }
            return Optional.of(rate);
        } catch (NumberFormatException e) {
            err.println("Invalid number. Enter a valid hourly rate.");
            return Optional.empty();
        }
    }

    private String readSrs() {
        out.println("Paste the SRS text. Enter a line containing only " + END_MARKER + " to finish:");
        StringBuilder srs = new StringBuilder();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.trim().equalsIgnoreCase(END_MARKER)) break;
            srs.append(line).append(System.lineSeparator());
        }
        return srs.toString();
    }

    private void offerLesson(long projectId) {
        String answer = prompt("\nSave a lesson learned? (y/n): ").orElse("n");
        if (!answer.equalsIgnoreCase("y")) return;

        String lesson = prompt("Lesson: ").orElse("");
        if (lesson.isEmpty()) return;
        service.recordLesson(projectId, lesson);
        out.println("Lesson learned saved.");
    }

    private Optional<String> prompt(String message) {
        out.print(message);
        out.flush();
        return in.hasNextLine() ? Optional.of(in.nextLine().trim()) : Optional.empty();
    }
}

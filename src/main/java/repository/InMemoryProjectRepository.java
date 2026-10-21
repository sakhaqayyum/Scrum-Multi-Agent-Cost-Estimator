package com.scrum.estimator.repository;

import com.scrum.estimator.domain.PlannedTask;
import com.scrum.estimator.domain.Project;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Non-persistent repository used for tests and when MySQL is disabled or unreachable. */
public class InMemoryProjectRepository implements ProjectRepository {
    public record StoredProject(Project project, List<PlannedTask> tasks, List<String> lessons) {}

    private final Map<Long, StoredProject> projects = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public synchronized long save(Project project, List<PlannedTask> tasks) {
        long id = nextId++;
        projects.put(id, new StoredProject(project, List.copyOf(tasks), new ArrayList<>()));
        return id;
    }

    @Override
    public synchronized void saveLesson(long projectId, String lessonText) {
        StoredProject stored = projects.get(projectId);
        if (stored == null) {
            throw new IllegalArgumentException("Unknown project ID " + projectId);
        }
        stored.lessons().add(lessonText);
    }

    @Override
    public String description() {
        return "in-memory (not persisted)";
    }

    public synchronized StoredProject find(long projectId) {
        return projects.get(projectId);
    }
}

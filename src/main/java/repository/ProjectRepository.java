package com.scrum.estimator.repository;

import com.scrum.estimator.domain.PlannedTask;
import com.scrum.estimator.domain.Project;

import java.util.List;

/** Persistence port for estimation runs. */
public interface ProjectRepository {
    /** Saves the project and its tasks together and returns the new project ID. */
    long save(Project project, List<PlannedTask> tasks);

    void saveLesson(long projectId, String lessonText);

    /** Short label shown to the user, e.g. "MySQL" or "in-memory". */
    String description();
}

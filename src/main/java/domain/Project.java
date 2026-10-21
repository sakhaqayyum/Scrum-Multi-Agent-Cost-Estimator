package com.scrum.estimator.domain;

import java.util.Objects;

/** Input for one estimation run. */
public record Project(String name, String srsText, double hourlyRate) {
    public Project {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(srsText, "srsText");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Project name must not be blank.");
        }
        if (srsText.isBlank()) {
            throw new IllegalArgumentException("SRS text must not be blank.");
        }
        if (hourlyRate < 0 || Double.isNaN(hourlyRate) || Double.isInfinite(hourlyRate)) {
            throw new IllegalArgumentException("Hourly rate must be a non-negative number.");
        }
    }
}

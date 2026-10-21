package com.scrum.estimator.domain;

/** Team role a planned task is assigned to. */
public enum AgentRole {
    SECURITY_TEST("Security/Test Agent"),
    DATABASE("Database Agent"),
    ANALYSIS("Analysis Agent"),
    DEVELOPMENT("Development Agent");

    private final String displayName;

    AgentRole(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}

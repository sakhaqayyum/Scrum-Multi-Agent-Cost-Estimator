package com.scrum.estimator.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EffortEstimatorTest {
    private final EffortEstimator estimator = new EffortEstimator();

    @Test
    void baseHoursForSimpleRequirement() {
        assertEquals(4.0, estimator.estimateHours("The system shall store data."));
    }

    @Test
    void addsHoursForComplexAndReportingKeywords() {
        assertEquals(12.0, estimator.estimateHours("Authenticate users."));
        assertEquals(8.0, estimator.estimateHours("Show a dashboard."));
        assertEquals(16.0, estimator.estimateHours("Generate a payment report."));
    }

    @Test
    void addsHoursForLongRequirement() {
        assertEquals(8.0, estimator.estimateHours("x".repeat(181)));
    }
}

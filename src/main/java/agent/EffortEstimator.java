package com.scrum.estimator.agent;

import java.util.List;
import java.util.Locale;

/**
 * Keyword-based effort heuristic.
 * Heuristic only. Replace with team velocity or planning-poker values.
 */
public class EffortEstimator {
    static final double BASE_HOURS = 4.0;
    static final double COMPLEX_FEATURE_HOURS = 8.0;
    static final double REPORTING_HOURS = 4.0;
    static final double LONG_REQUIREMENT_HOURS = 4.0;
    static final int LONG_REQUIREMENT_CHARS = 180;

    private static final List<String> COMPLEX_KEYWORDS =
            List.of("integrat", "security", "authenticate", "payment");
    private static final List<String> REPORTING_KEYWORDS =
            List.of("report", "dashboard", "generate");

    public double estimateHours(String sentence) {
        String text = sentence.toLowerCase(Locale.ROOT);
        double hours = BASE_HOURS;
        if (containsAny(text, COMPLEX_KEYWORDS)) hours += COMPLEX_FEATURE_HOURS;
        if (containsAny(text, REPORTING_KEYWORDS)) hours += REPORTING_HOURS;
        if (text.length() > LONG_REQUIREMENT_CHARS) hours += LONG_REQUIREMENT_HOURS;
        return hours;
    }

    private static boolean containsAny(String text, List<String> keywords) {
        return keywords.stream().anyMatch(text::contains);
    }
}

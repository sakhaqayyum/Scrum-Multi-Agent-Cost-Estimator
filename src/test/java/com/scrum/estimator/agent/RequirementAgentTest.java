package com.scrum.estimator.agent;

import com.scrum.estimator.domain.Requirement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequirementAgentTest {
    private final RequirementAgent agent = new RequirementAgent();

    @Test
    void splitsSentencesAndLines() {
        List<Requirement> result = agent.extract("""
                The system shall store project requirements. The system shall track tasks.
                The system shall generate a report.
                """);

        assertEquals(3, result.size());
        assertEquals("The system shall store project requirements.", result.get(0).sentence());
    }

    @Test
    void extractsFirstKnownVerbAndFirstMeaningfulNoun() {
        Requirement r = agent.extract("The system shall store project requirements.").get(0);

        assertEquals("store", r.verb());
        assertEquals("project", r.noun());
    }

    @Test
    void fallsBackToDefaultsWhenNothingMatches() {
        Requirement r = agent.extract("It is.").get(0);

        assertEquals(RequirementAgent.DEFAULT_VERB, r.verb());
        assertEquals(RequirementAgent.DEFAULT_NOUN, r.noun());
    }

    @Test
    void ignoresBlankInput() {
        assertTrue(agent.extract("   \n\n  ").isEmpty());
    }
}

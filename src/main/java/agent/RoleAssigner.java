package com.scrum.estimator.agent;

import com.scrum.estimator.domain.AgentRole;

import java.util.Map;

/** Maps an extracted verb to the team role that owns the work. Customize for your team. */
public class RoleAssigner {
    private static final Map<String, AgentRole> ROLE_BY_VERB = Map.ofEntries(
            Map.entry("validate", AgentRole.SECURITY_TEST),
            Map.entry("authenticate", AgentRole.SECURITY_TEST),
            Map.entry("login", AgentRole.SECURITY_TEST),
            Map.entry("save", AgentRole.DATABASE),
            Map.entry("store", AgentRole.DATABASE),
            Map.entry("record", AgentRole.DATABASE),
            Map.entry("update", AgentRole.DATABASE),
            Map.entry("delete", AgentRole.DATABASE),
            Map.entry("estimate", AgentRole.ANALYSIS),
            Map.entry("calculate", AgentRole.ANALYSIS),
            Map.entry("analyze", AgentRole.ANALYSIS),
            Map.entry("analyse", AgentRole.ANALYSIS),
            Map.entry("extract", AgentRole.ANALYSIS)
    );

    public AgentRole assign(String verb) {
        return ROLE_BY_VERB.getOrDefault(verb, AgentRole.DEVELOPMENT);
    }
}

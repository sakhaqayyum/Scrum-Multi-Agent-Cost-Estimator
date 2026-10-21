package com.scrum;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Lightweight rule-based requirement analysis agent.
 * This is a prototype: it does not perform full linguistic NLP.
 */
public class RequirementAgent {
    private static final Set<String> VERBS = Set.of(
            "manage", "create", "add", "update", "delete", "remove", "view",
            "display", "generate", "calculate", "estimate", "save", "store",
            "upload", "download", "search", "register", "login", "authenticate",
            "assign", "track", "send", "validate", "process", "extract",
            "distribute", "produce", "maintain", "allow", "provide", "record",
            "analyze", "analyse", "export", "import", "monitor", "schedule"
    );

    private static final Set<String> NOUN_STOP_WORDS = Set.of(
            "the", "a", "an", "and", "or", "to", "of", "for", "in", "on",
            "with", "by", "from", "as", "is", "are", "be", "will", "shall",
            "should", "must", "can", "may", "system", "user", "users",
            "software", "application", "able", "allow", "allows", "need",
            "needs", "required", "requirement", "requirements", "that", "this",
            "it", "its", "their", "they", "we", "our", "which", "when"
    );

    public List<Requirement> extract(String srsText) {
        List<Requirement> results = new ArrayList<>();
        String[] sentences = srsText.split("(?<=[.!?])\\s+|\\R+");

        for (String raw : sentences) {
            String sentence = raw.trim();
            if (sentence.isEmpty()) continue;

            List<String> words = Arrays.stream(
                    sentence.toLowerCase(Locale.ROOT)
                            .replaceAll("[^a-z0-9\\s-]", " ")
                            .split("\\s+"))
                    .filter(w -> !w.isBlank())
                    .toList();

            String verb = words.stream()
                    .filter(VERBS::contains)
                    .findFirst().orElse("analyze");

            String noun = words.stream()
                    .filter(w -> w.matches("[a-z][a-z0-9-]{2,}"))
                    .filter(w -> !NOUN_STOP_WORDS.contains(w))
                    .filter(w -> !VERBS.contains(w))
                    .findFirst().orElse("requirement");

            // Keep the original sentence as the task description.
            results.add(new Requirement(sentence, verb, noun));
        }
        return results;
    }

    public record Requirement(String sentence, String verb, String noun) {}
}

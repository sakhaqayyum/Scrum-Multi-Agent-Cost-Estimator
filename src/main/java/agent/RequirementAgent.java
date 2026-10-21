package com.scrum.estimator.agent;

import com.scrum.estimator.domain.Requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Lightweight rule-based requirement analysis agent.
 * This is a prototype: it does not perform full linguistic NLP.
 */
public class RequirementAgent {
    static final String DEFAULT_VERB = "analyze";
    static final String DEFAULT_NOUN = "requirement";

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

            List<String> words = tokenize(sentence);
            // Keep the original sentence as the task description.
            results.add(new Requirement(sentence, findVerb(words), findNoun(words)));
        }
        return results;
    }

    private static List<String> tokenize(String sentence) {
        return Arrays.stream(
                        sentence.toLowerCase(Locale.ROOT)
                                .replaceAll("[^a-z0-9\\s-]", " ")
                                .split("\\s+"))
                .filter(w -> !w.isBlank())
                .toList();
    }

    private static String findVerb(List<String> words) {
        return words.stream()
                .filter(VERBS::contains)
                .findFirst().orElse(DEFAULT_VERB);
    }

    private static String findNoun(List<String> words) {
        return words.stream()
                .filter(w -> w.matches("[a-z][a-z0-9-]{2,}"))
                .filter(w -> !NOUN_STOP_WORDS.contains(w))
                .filter(w -> !VERBS.contains(w))
                .findFirst().orElse(DEFAULT_NOUN);
    }
}

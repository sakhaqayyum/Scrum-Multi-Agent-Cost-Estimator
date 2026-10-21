package com.scrum.estimator.domain;

/** A single SRS sentence with the verb and noun the requirement agent picked out. */
public record Requirement(String sentence, String verb, String noun) {}

package com.scrum.estimator.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Application settings. Values come from {@code application.properties} on the classpath and can be
 * overridden by a JVM system property of the same key ({@code -Ddb.password=...}) or an environment
 * variable named {@code SCRUM_} + the key in upper case with dots/dashes as underscores
 * ({@code SCRUM_DB_PASSWORD}).
 */
public record AppConfig(Database database, Estimation estimation) {
    private static final String RESOURCE = "/application.properties";

    public record Database(boolean enabled, String url, String user, String password) {
        @Override
        public String toString() {
            return "Database[enabled=" + enabled + ", url=" + url + ", user=" + user + ", password=***]";
        }
    }

    public record Estimation(int tasksPerSprint) {}

    public static AppConfig load() {
        return from(loadDefaults(), System.getProperties(), System.getenv());
    }

    static AppConfig from(Properties defaults, Properties systemProperties, Map<String, String> env) {
        Resolver r = new Resolver(defaults, systemProperties, env);
        Database database = new Database(
                Boolean.parseBoolean(r.get("db.enabled", "true")),
                r.get("db.url", "jdbc:mysql://localhost:3306/scrum_cost_db"),
                r.get("db.user", "root"),
                r.get("db.password", ""));
        Estimation estimation = new Estimation(
                Integer.parseInt(r.get("estimation.tasks-per-sprint", "5")));
        return new AppConfig(database, estimation);
    }

    private static Properties loadDefaults() {
        Properties properties = new Properties();
        try (InputStream in = AppConfig.class.getResourceAsStream(RESOURCE)) {
            if (in != null) properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + RESOURCE, e);
        }
        return properties;
    }

    private record Resolver(Properties defaults, Properties systemProperties, Map<String, String> env) {
        String get(String key, String fallback) {
            String envValue = env.get(envName(key));
            if (envValue != null) return envValue.trim();
            String sysValue = systemProperties.getProperty(key);
            if (sysValue != null) return sysValue.trim();
            return defaults.getProperty(key, fallback).trim();
        }

        static String envName(String key) {
            return "SCRUM_" + key.toUpperCase(Locale.ROOT).replace('.', '_').replace('-', '_');
        }
    }
}

package com.scrum.estimator.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/** Creates the tables from {@code db/schema.sql} on the classpath if they do not exist. */
public class SchemaInitializer {
    private static final String SCHEMA_RESOURCE = "/db/schema.sql";

    private final ConnectionFactory connectionFactory;

    public SchemaInitializer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void initialize() throws SQLException {
        try (Connection connection = connectionFactory.open();
             Statement statement = connection.createStatement()) {
            for (String sql : loadStatements()) {
                statement.executeUpdate(sql);
            }
        }
    }

    static List<String> loadStatements() {
        try (InputStream in = SchemaInitializer.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource " + SCHEMA_RESOURCE);
            }
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("(?m)^\\s*--.*$", "");
            return Arrays.stream(script.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package com.scrum.estimator.repository;

import com.scrum.estimator.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private final AppConfig.Database config;

    public ConnectionFactory(AppConfig.Database config) {
        this.config = config;
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection(config.url(), config.user(), config.password());
    }
}

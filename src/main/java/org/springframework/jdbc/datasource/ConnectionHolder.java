package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHolder {
    private final Connection connection;
    private boolean transactionActive = false;

    public ConnectionHolder(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
    }

    public boolean isTransactionActive() {
        return transactionActive;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
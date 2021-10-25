package com.example.datasource.mlw;

import java.sql.Connection;

public class MlwConnectionHolder {
    private Connection connection;
    private boolean isActive;
    private boolean isClosed;

    private MlwDataSource mlwDataSource;

    public MlwConnectionHolder(Connection connection, MlwDataSource dataSource){
        this.connection = connection;
        this.mlwDataSource = dataSource;
    }

    public Connection getConnection() {
        return connection;
    }

    public MlwDataSource getDataSource() {
        return mlwDataSource;
    }

    public void setDataSource(MlwDataSource mlwDataSource) {
        this.mlwDataSource = mlwDataSource;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}

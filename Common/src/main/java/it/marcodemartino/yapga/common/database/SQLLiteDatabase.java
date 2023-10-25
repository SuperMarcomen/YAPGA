package it.marcodemartino.yapga.common.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public abstract class SQLLiteDatabase implements Database {

    private final Logger logger = LogManager.getLogger(SQLLiteDatabase.class);
    private final String name;
    private Connection connection;

    public SQLLiteDatabase(String name) {
        this.name = name;
    }

    @Override
    public void initDatabase() {
        connection = getConnection();
        createTable();
    }

    @Override
    public Connection getConnection() {
        if (connection != null) return connection;
        connection = createConnection();
        return connection;
    }

    @Override
    public PreparedStatement createPreparedStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            logger.error("There was an error creating a prepared statement for the query {}", sql, e);
            return null;
        }
    }

    public abstract void createTable();

    protected void createTable(String sql) {
        try (Statement preparedStatement = getConnection().createStatement();) {
            if (preparedStatement == null) {
                logger.fatal("Could not create the table, quitting the app");
                System.exit(1);
            }

            preparedStatement.executeUpdate(sql);
        } catch (SQLException e) {
            logger.fatal("Could not create the table, quitting the app", e);
            System.exit(1);
        }
    }

    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
        } catch (SQLException e) {
            logger.fatal("There was an error connecting to the database. Error message: " + e.getMessage());
            return null;
        }
    }
}

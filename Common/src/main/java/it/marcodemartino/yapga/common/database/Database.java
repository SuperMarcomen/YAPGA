package it.marcodemartino.yapga.common.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface Database {

    void initDatabase();
    Connection getConnection();
    PreparedStatement createPreparedStatement(String sql);
    void closeConnection();

}

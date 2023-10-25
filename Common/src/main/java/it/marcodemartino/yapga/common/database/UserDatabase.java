package it.marcodemartino.yapga.common.database;

public class UserDatabase extends SQLLiteDatabase {

    public UserDatabase() {
        super("users");
    }

    @Override
    public void createTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS users (
                    user_id VARCHAR(40) NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    rsa_public_key TEXT,
                    PRIMARY KEY (user_id)
                );""";
        super.createTable(sql);
    }
}

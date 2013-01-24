package net.hcfactions.bigbrother.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseAction implements IDatabaseAction {

    private String query;

    public DatabaseAction(String query)
    {
        this.query = query;
    }

    public void execute(Connection conn) throws SQLException {
        conn.createStatement().execute(query);
    }
}

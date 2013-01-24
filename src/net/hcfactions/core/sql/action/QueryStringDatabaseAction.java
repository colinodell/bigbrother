package net.hcfactions.core.sql.action;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A simple SQL query to execute on the database
 */
public class QueryStringDatabaseAction implements IDatabaseAction {

    private String query;

    /**
     * Creates a new QueryStringDatabaseAction
     * @param query The SQL to execute, as a String
     */
    public QueryStringDatabaseAction(String query)
    {
        this.query = query;
    }

    public void execute(Connection conn) throws SQLException {
        conn.createStatement().execute(query);
    }
}

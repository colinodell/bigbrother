package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.sql.IDatabaseAction;
import net.hcfactions.bigbrother.sql.ILogFailure;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateServerLastSeenAction implements IDatabaseAction, ILogFailure {

    private static final String QUERY_UPDATE_SERVER_LAST_SEEN = "INSERT INTO bigbrother_server_lastseen (foo, last_seen) VALUES (1, UNIX_TIMESTAMP()) ON DUPLICATE KEY UPDATE last_seen = UNIX_TIMESTAMP();";

    public static final String FAILURE_MESSAGE = "Failed to update server last-seen timestamp";

    @Override
    public void execute(Connection conn) throws SQLException {
        conn.createStatement().execute(QUERY_UPDATE_SERVER_LAST_SEEN);
    }

    @Override
    public String getOnFailureMessage() {
        return FAILURE_MESSAGE;
    }
}

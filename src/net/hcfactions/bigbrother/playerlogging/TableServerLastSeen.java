package net.hcfactions.bigbrother.playerlogging;

import net.hcfactions.core.sql.action.IDatabaseAction;
import net.hcfactions.core.sql.action.ILogFailure;
import net.hcfactions.core.sql.action.ILogSuccess;

import java.sql.Connection;
import java.sql.SQLException;

public class TableServerLastSeen {
    protected static final String CREATE_TABLE = "" +
        "CREATE TABLE IF NOT EXISTS `bigbrother_server_lastseen` (\n" +
        "  `foo` tinyint(1) unsigned NOT NULL,\n" +
        "  `last_seen` int(8) unsigned NOT NULL DEFAULT '0',\n" +
        "  PRIMARY KEY (`foo`)\n" +
        ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    /**
     * Updates the server's last_seen timestamp
     */
    protected static class UpdateServerLastSeenAction implements IDatabaseAction, ILogFailure/*, ILogSuccess*/ {

        private static final String QUERY_UPDATE_SERVER_LAST_SEEN = "INSERT INTO bigbrother_server_lastseen (foo, last_seen) VALUES (1, UNIX_TIMESTAMP()) ON DUPLICATE KEY UPDATE last_seen = UNIX_TIMESTAMP();";

        //private static final String SUCCESS_MESSAGE = "Successfully updated server last-seen timestamp";
        private static final String FAILURE_MESSAGE = "Failed to update server last-seen timestamp";

        @Override
        public void execute(Connection conn) throws SQLException {
            conn.createStatement().execute(QUERY_UPDATE_SERVER_LAST_SEEN);
        }

        @Override
        public String getOnFailureMessage() {
            return FAILURE_MESSAGE;
        }
    }
}

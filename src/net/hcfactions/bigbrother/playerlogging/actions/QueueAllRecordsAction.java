package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.sql.IDatabaseAction;
import net.hcfactions.bigbrother.sql.ILogFailure;
import net.hcfactions.bigbrother.sql.ILogSuccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Queue all player records for processing.
 * This should NEVER be called while players are on the server.
 */
public class QueueAllRecordsAction implements IDatabaseAction, ILogSuccess, ILogFailure {

    private static final String QUERY_QUEUE_RECORDS_ALL = "UPDATE bigbrother_player_log SET status = ? WHERE status = ?;";

    private static final String FAILURE_MESSAGE = "Failed to queue all records";
    private static final String SUCCESS_MESSAGE = "Successfully queued all records";

    @Override
    public void execute(Connection conn) throws SQLException {
        // Queue all completed records that may have been missed
        PreparedStatement stmt = conn.prepareStatement(QUERY_QUEUE_RECORDS_ALL);
        stmt.setInt(1, PlayerDbHelper.STATUS_QUEUED);
        stmt.setInt(2, PlayerDbHelper.STATUS_ACTIVE);
        stmt.execute();
    }

    @Override
    public String getOnFailureMessage() {
        return FAILURE_MESSAGE;
    }

    @Override
    public String getOnSuccessMessage() {
        return SUCCESS_MESSAGE;
    }
}

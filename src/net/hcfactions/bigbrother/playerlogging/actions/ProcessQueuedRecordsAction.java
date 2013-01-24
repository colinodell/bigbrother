package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.sql.ILogFailure;
import net.hcfactions.bigbrother.sql.ILogSuccess;
import net.hcfactions.bigbrother.sql.TransactionalDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProcessQueuedRecordsAction extends TransactionalDatabaseAction implements ILogSuccess, ILogFailure {

    private static final String QUERY_LOCK_QUEUED_LOGS = "SELECT l.status FROM bigbrother_player_log l WHERE l.status = ? FOR UPDATE;";
    private static final String QUERY_PROCESS_QUEUED = "" +
            "UPDATE bigbrother_player_time pt \n" +
            "INNER JOIN (\n " +
            "    SELECT player, SUM(logout - login) AS timesum \n" +
            "    FROM bigbrother_player_log \n" +
            "    WHERE status = ? \n" +
            "    GROUP BY player \n" +
            ") AS l ON pt.player = l.player \n" +
            "INNER JOIN bigbrother_player_log l2 ON" +
            "    pt.player = l2.player AND status = ? \n" +
            "SET \n" +
            "    pt.seconds = pt.seconds + l.timesum, \n" +
            "    l2.status = ? \n" +
            "WHERE pt.map = 0;";

    private static final String SUCCESS_MESSAGE = "Processed all queued player logs";
    private static final String FAILURE_MESSAGE = "Failed processing of queued player logs";

    @Override
    protected void _execute(Connection conn) throws SQLException {
        // Lock the log records we're about to process
        PreparedStatement lockStatement = conn.prepareStatement(QUERY_LOCK_QUEUED_LOGS);
        lockStatement.setInt(1, PlayerDbHelper.STATUS_QUEUED);
        lockStatement.execute();

        // Process the queued records
        PreparedStatement updateStatement = conn.prepareStatement(QUERY_PROCESS_QUEUED);
        updateStatement.setInt(1, PlayerDbHelper.STATUS_QUEUED);
        updateStatement.setInt(2, PlayerDbHelper.STATUS_QUEUED);
        updateStatement.setInt(3, PlayerDbHelper.STATUS_PROCESSED);
        updateStatement.execute();
    }

    @Override
    public String getOnSuccessMessage() {
        return SUCCESS_MESSAGE;
    }

    @Override
    public String getOnFailureMessage() {
        return FAILURE_MESSAGE;
    }
}

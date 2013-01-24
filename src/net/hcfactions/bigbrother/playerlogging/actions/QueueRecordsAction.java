package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.sql.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class QueueRecordsAction implements IDatabaseAction {

    private static final String QUERY_QUEUE_RECORDS_PLAYER = "UPDATE bigbrother_player_log SET status = ? WHERE status = ? AND player = ?;";

    private String playerName;

    public QueueRecordsAction(String p)
    {
        playerName = p;
    }
    @Override
    public void execute(Connection conn) throws SQLException {
        // Queue all completed records that may have been missed
        PreparedStatement stmt = conn.prepareStatement(QUERY_QUEUE_RECORDS_PLAYER);
        stmt.setInt(1, PlayerDbHelper.STATUS_QUEUED);
        stmt.setInt(2, PlayerDbHelper.STATUS_ACTIVE);
        stmt.setString(3, playerName);
        stmt.execute();
    }
}

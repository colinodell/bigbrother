package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.sql.IDatabaseAction;
import net.hcfactions.bigbrother.sql.ILogSuccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecordLogoutAction implements IDatabaseAction, ILogSuccess {

    private static final String QUERY_LOG_PLAYER_LOGOUT = "UPDATE bigbrother_player_log SET logout = UNIX_TIMESTAMP(), status = ? WHERE status = ? AND player = ? AND login IS NOT NULL AND login <= UNIX_TIMESTAMP() LIMIT 1;";

    private static final String SUCCESS_MESSAGE = "Recorded logout for player %s";

    private String playerName;

    public RecordLogoutAction(String p)
    {
        playerName = p;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        // Update the existing _time record with the logout time and mark it as queued
        PreparedStatement stmt = conn.prepareStatement(QUERY_LOG_PLAYER_LOGOUT);
        stmt.setInt(1, PlayerDbHelper.STATUS_QUEUED);
        stmt.setInt(2, PlayerDbHelper.STATUS_ACTIVE);
        stmt.setString(3, playerName);
        stmt.execute();
    }

    @Override
    public String getOnSuccessMessage() {
        return String.format(SUCCESS_MESSAGE, playerName);
    }
}

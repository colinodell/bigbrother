package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.sql.IDatabaseAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FixLogsWithNoLogoutAction implements IDatabaseAction {

    private static final String QUERY_FIX_LOGS_WITH_NO_LOGOUT = "UPDATE bigbrother_player_log SET logout = (SELECT last_seen FROM bigbrother_server_lastseen) WHERE status = ? AND logout IS NULL;";

    @Override
    public void execute(Connection conn) throws SQLException {
        PreparedStatement lockStatement = conn.prepareStatement(QUERY_FIX_LOGS_WITH_NO_LOGOUT);
        lockStatement.setInt(1, PlayerDbHelper.STATUS_ACTIVE);
        lockStatement.execute();
    }
}

package net.hcfactions.bigbrother.playerlogging.actions;

import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.sql.IDatabaseAction;
import net.hcfactions.bigbrother.sql.ILogSuccess;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecordLoginAction implements IDatabaseAction, ILogSuccess {

    private static final String QUERY_CREATE_TIME_ENTRY = "INSERT IGNORE INTO bigbrother_player_time (player, map, seconds) VALUES (?, 0, 0);";
    private static final String QUERY_LOG_PLAYER_LOGIN = "INSERT INTO bigbrother_player_log (player, login, logout, ip, status) VALUES (?, UNIX_TIMESTAMP(), NULL, INET_ATON(?), ?);";

    private static final String SUCCESS_MESSAGE = "Recorded login for player %s";

    private String playerName;
    private String ip;

    public RecordLoginAction(String p, String ip)
    {
        this.playerName = p;
        this.ip = ip;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        // Create a new _time record, if they don't have on already
        PreparedStatement stmt = conn.prepareStatement(QUERY_CREATE_TIME_ENTRY);
        stmt.setString(1, playerName);
        stmt.execute();

        // Save their login to the db
        PreparedStatement stmt2 = conn.prepareStatement(QUERY_LOG_PLAYER_LOGIN);
        stmt2.setString(1, playerName);
        stmt2.setString(2, ip);
        stmt2.setInt(3, PlayerDbHelper.STATUS_ACTIVE);
        stmt2.execute();
    }

    @Override
    public String getOnSuccessMessage() {
        return String.format(SUCCESS_MESSAGE, playerName);
    }
}

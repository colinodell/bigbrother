package net.hcfactions.bigbrother.playerlogging;

import net.hcfactions.core.sql.action.*;
import net.hcfactions.core.util.FuzzyTime;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class TablePlayerLog {

    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_PROCESSED = 1;

    protected static final String CREATE_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_player_log` (\n" +
            "  `player` varchar(32) NOT NULL,\n" +
            "  `login` int(8) unsigned NOT NULL,\n" +
            "  `logout` int(8) unsigned NULL DEFAULT NULL,\n" +
            "  `ip` int(4) unsigned NOT NULL,\n" +
            "  `status` tinyint(1) NOT NULL,\n" +
            "  KEY `IDX_PLAYER_STATUS` (`player`, `status`),\n" +
            "  KEY `IDX_STATUS` (`status`)\n" +
            ") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";

    protected static final String CREATE_TABLE_TIME = "" +
            "CREATE TABLE IF NOT EXISTS `bigbrother_player_time` (\n" +
            "  `player` varchar(32) NOT NULL,\n" +
            "  `map` tinyint(1) unsigned NOT NULL DEFAULT '0',\n" +
            "  `seconds` int(8) unsigned NOT NULL DEFAULT '0',\n" +
            "  PRIMARY KEY (`player`,`map`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    /**
     * Locates logs with no logout time and sets them to the server's last_seen time
     */
    protected static class FixLogsWithNoLogoutAction implements IDatabaseAction, ILogSuccess {

        private static final String QUERY_FIX_LOGS_WITH_MESSED_UP_LOGOUT = "UPDATE bigbrother_player_log SET logout = login WHERE status = ? AND logout IS NOT NULL AND logout < login;";
        private static final String QUERY_FIX_LOGS_WITH_NO_LOGOUT = "UPDATE bigbrother_player_log SET logout = GREATEST((SELECT last_seen FROM bigbrother_server_lastseen), login) WHERE status = ? AND logout IS NULL;";

        private static final String SUCCESS_MESSAGE = "Fixed any logs which may have lacked a logout timestamp";

        @Override
        public void execute(Connection conn) throws SQLException {
            PreparedStatement fixMessedUp = conn.prepareStatement(QUERY_FIX_LOGS_WITH_MESSED_UP_LOGOUT);
            fixMessedUp.setInt(1, STATUS_ACTIVE);
            fixMessedUp.execute();

            PreparedStatement lockStatement = conn.prepareStatement(QUERY_FIX_LOGS_WITH_NO_LOGOUT);
            lockStatement.setInt(1, STATUS_ACTIVE);
            lockStatement.execute();
        }

        @Override
        public String getOnSuccessMessage() {
            return SUCCESS_MESSAGE;
        }
    }

    /**
     * Records a player logging in
     * Note:
     *   The IP is converted to a 4 byte integer, instead of using a 15 byte string. This cuts down on storage space
     *   You can leave it as-is to do comparissons and check for alts. If you need a string representation, use the
     *   MySQl function "INET_NTOA(ip)" to convert it from an integer
     */
    protected static class RecordLoginAction implements IDatabaseAction, ILogSuccess {

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
            stmt2.setInt(3, STATUS_ACTIVE);
            stmt2.execute();
        }

        @Override
        public String getOnSuccessMessage() {
            return String.format(SUCCESS_MESSAGE, playerName);
        }
    }

    /**
     * Records a player logging out
     */
    protected static class RecordLogoutAction implements IDatabaseAction, ILogSuccess {

        private static final String QUERY_LOG_PLAYER_LOGOUT = "UPDATE bigbrother_player_log SET logout = UNIX_TIMESTAMP() WHERE status = ? AND player = ? AND login IS NOT NULL AND login <= UNIX_TIMESTAMP() LIMIT 1;";

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
            stmt.setInt(1, STATUS_ACTIVE);
            stmt.setString(2, playerName);
            stmt.execute();
        }

        @Override
        public String getOnSuccessMessage() {
            return String.format(SUCCESS_MESSAGE, playerName);
        }
    }

    /**
     * Processes all queued records for the given player
     * We first identify the log records which haven't been processed yet. They'll be in a queued stated.
     * With these records, we subtract the login time from logout time to figure out how long their session was
     * We then sum those session times and add them to the player's running total
     * Once that's done, we mark the log records as processed so we don't count them twice
     */
    protected static class ProcessPlayerQueuedRecordsAction extends TransactionalDatabaseAction implements ILogSuccess, ILogFailure, ISQLDebug<Connection> {

        private static final String QUERY_LOCK_QUEUED_LOGS = "SELECT l.status FROM bigbrother_player_log l WHERE l.status = ? AND l.player = ? FOR UPDATE;";
        private static final String QUERY_PROCESS_QUEUED = "" +
                "UPDATE bigbrother_player_time pt \n" +
                "INNER JOIN (\n " +
                "    SELECT SUM(logout - login) AS timesum \n" +
                "    FROM bigbrother_player_log \n" +
                "    WHERE status = ? AND player = ? AND logout IS NOT NULL \n" +
                ") AS l \n" +
                "INNER JOIN bigbrother_player_log l2 ON" +
                "    status = ? AND l2.player = ? AND logout IS NOT NULL \n" +
                "SET \n" +
                "    pt.seconds = pt.seconds + l.timesum, \n" +
                "    l2.status = ? \n" +
                "WHERE pt.map = 0 AND pt.player = ?;";

        private static final String SUCCESS_MESSAGE = "Processed queued logs for player %s";
        private static final String FAILURE_MESSAGE = "Failed processing queued logs for player %s";

        private String playerName;
        public ProcessPlayerQueuedRecordsAction(String player)
        {
            this.playerName = player;
        }

        @Override
        protected void _execute(Connection conn) throws SQLException {

            // Lock the log records we're about to process
            PreparedStatement lockStatement = conn.prepareStatement(QUERY_LOCK_QUEUED_LOGS);
            lockStatement.setInt(1, STATUS_ACTIVE);
            lockStatement.setString(2, playerName);
            lockStatement.execute();

            // Process the queued records
            PreparedStatement updateStatement = conn.prepareStatement(QUERY_PROCESS_QUEUED);
            updateStatement.setInt(1, STATUS_ACTIVE);
            updateStatement.setString(2, playerName);
            updateStatement.setInt(3, STATUS_ACTIVE);
            updateStatement.setString(4, playerName);
            updateStatement.setInt(5, STATUS_PROCESSED);
            updateStatement.setString(6, playerName);
            updateStatement.execute();
        }

        @Override
        public String getOnSuccessMessage() {
            return String.format(SUCCESS_MESSAGE, playerName);
        }

        @Override
        public String getOnFailureMessage() {
            return String.format(FAILURE_MESSAGE, playerName);
        }

        @Override
        public void debug(Connection conn, Logger logger) throws SQLException {
            PreparedStatement ps = conn.prepareStatement(""+
                    "SELECT player, login, logout, status \n" +
                    "    FROM bigbrother_player_log \n" +
                    "    WHERE player = ?;");
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
                logger.info(String.format("p: %s | i: %d | o: %d | s: %d", rs.getString(1), rs.getLong(2), rs.getLong(3), rs.getInt(4)));

            rs.close();
            ps.close();

            PreparedStatement ps2 = conn.prepareStatement(""+
                    "SELECT player, map, seconds FROM bigbrother_player_time" +
                    "   WHERE player = ?");
            ps2.setString(1, playerName);
            ResultSet rs2 = ps2.executeQuery();

            while(rs2.next())
                logger.info(String.format("p: %s | m: %d | s: %d", rs2.getString(1), rs2.getInt(2), rs2.getLong(3)));
            rs2.close();
            ps2.close();

            ResultSet rs3 = conn.createStatement().executeQuery("SELECT last_seen FROM bigbrother_server_lastseen;");
            while(rs3.next())
                logger.info(String.format("ls: %d", rs3.getLong(1)));
            rs3.close();
        }
    }

    /**
     * Processes all queued records for the all players
     * This works identically to ProcessPlayerQueuedRecordsAction, except that it executes for all players
     * @see ProcessPlayerQueuedRecordsAction
     */
    protected static class ProcessAllQueuedRecordsAction extends TransactionalDatabaseAction implements ILogSuccess, ILogFailure, ISQLDebug<Connection> {

        private static final String QUERY_LOCK_QUEUED_LOGS = "SELECT l.status FROM bigbrother_player_log l WHERE l.status = ? AND logout IS NOT NULL FOR UPDATE;";
        private static final String QUERY_PROCESS_QUEUED = "" +
                "UPDATE bigbrother_player_time pt \n" +
                "INNER JOIN (\n " +
                "    SELECT player, SUM(logout - login) AS timesum \n" +
                "    FROM bigbrother_player_log \n" +
                "    WHERE status = ? AND logout IS NOT NULL \n" +
                "    GROUP BY player \n" +
                ") AS l ON pt.player = l.player \n" +
                "INNER JOIN bigbrother_player_log l2 ON" +
                "    pt.player = l2.player AND status = ? AND logout IS NOT NULL \n" +
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
            lockStatement.setInt(1, STATUS_ACTIVE);
            lockStatement.execute();

            // Process the queued records
            PreparedStatement updateStatement = conn.prepareStatement(QUERY_PROCESS_QUEUED);
            updateStatement.setInt(1, STATUS_ACTIVE);
            updateStatement.setInt(2, STATUS_ACTIVE);
            updateStatement.setInt(3, STATUS_PROCESSED);
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

        @Override
        public void debug(Connection conn, Logger logger) throws SQLException {
            PreparedStatement ps = conn.prepareStatement(""+
                    "SELECT player, login, logout, status \n" +
                    "    FROM bigbrother_player_log \n" +
                    "    WHERE login = 0 OR logout IS NULL OR logout = 0 OR login >= logout OR logout > UNIX_TIMESTAMP() OR login > UNIX_TIMESTAMP();");
            ResultSet rs = ps.executeQuery();

            int limit = 10;
            while(rs.next())
            {
                limit--;
                logger.info(String.format("p: %s | i: %d | o: %d | s: %d", rs.getString(1), rs.getLong(2), rs.getLong(3), rs.getInt(4)));
                if(limit <= 0)
                    break;
            }

            rs.close();
            ps.close();

            ResultSet rs2 = conn.createStatement().executeQuery("SELECT last_seen FROM bigbrother_server_lastseen;");
            while(rs2.next())
                logger.info(String.format("ls: %d", rs2.getLong(1)));
            rs2.close();
        }
    }

    public static class GetPlayTime implements IDatabaseAction
    {
        private static final String QUERY_GET_PREVIOUS_PLAYTIME = "SELECT pt.seconds FROM bigbrother_player_time pt WHERE pt.player = ? AND pt.map = 0;";
        private static final String QUERY_GET_CURRENT_PLAYTIME = "SELECT UNIX_TIMESTAMP() - login FROM bigbrother_player_log WHERE player = ? AND status = ?;";

        private CommandSender sender;
        private String playerName;
        public GetPlayTime(CommandSender sender, String player)
        {
            this.sender = sender;
            this.playerName = player;
        }
        @Override
        public void execute(Connection conn) throws SQLException {
            long prevPlayTime = 0;
            long currPlayTime = 0;

            PreparedStatement prev = conn.prepareStatement(QUERY_GET_PREVIOUS_PLAYTIME);
            prev.setString(1, playerName);
            ResultSet prevRS = prev.executeQuery();

            if(prevRS.first())
                prevPlayTime = prevRS.getLong(1);
            prevRS.close();
            prev.close();

            PreparedStatement curr = conn.prepareStatement(QUERY_GET_CURRENT_PLAYTIME);
            curr.setString(1, playerName);
            curr.setInt(2, STATUS_ACTIVE);
            ResultSet currRS = curr.executeQuery();

            if(currRS.first())
                currPlayTime = currRS.getLong(1);
            currRS.close();
            curr.close();

            if(prevPlayTime + currPlayTime == 0)
            {
                // Player not found
                if(sender != null)
                    sender.sendMessage("Unable to find player " + ChatColor.BLUE + playerName);
            }
            else
            {
                if(sender != null)
                    sender.sendMessage(new StringBuilder()
                            .append(ChatColor.BLUE)
                            .append(playerName)
                            .append(ChatColor.WHITE)
                            .append(" has played for ")
                            .append(ChatColor.GOLD)
                            .append(FuzzyTime.durationFromSeconds(prevPlayTime + currPlayTime))
                            .append(ChatColor.WHITE)
                            .append(" this map")
                            .toString()
                    );
            }
        }
    }
}
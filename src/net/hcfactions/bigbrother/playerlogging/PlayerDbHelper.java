package net.hcfactions.bigbrother.playerlogging;

import net.hcfactions.core.sql.DbHelper;
import net.hcfactions.core.sql.action.IDatabaseAction;
import net.hcfactions.core.threading.BackgroundQueue;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles logic for creating player-related tables and queueing queries
 * This class sits between the event listener and the tables/queries/queue
 */
public class PlayerDbHelper extends DbHelper {

    public PlayerDbHelper(BackgroundQueue q)
    {
        super(q);
        getDb().enqueue(new IDatabaseAction() {
            @Override
            public void execute(Connection conn) throws SQLException {
                Statement stmt = conn.createStatement();
                stmt.execute(TablePlayerLog.CREATE_TABLE);
                stmt.execute(TablePlayerLog.CREATE_TABLE_TIME);
                stmt.execute(TableServerLastSeen.CREATE_TABLE);
            }
        });
    }

    /**
     * Record the player logging in
     * @param player The player who logged in
     */
    public void recordLogin(Player player)
    {
        getDb().enqueue(new TablePlayerLog.RecordLoginAction(player.getName(), player.getAddress().getAddress().getHostAddress()));
    }

    /**
     * Record the player logging out and immediately update their total play time
     * @param player The player who logged out
     */
    public void recordLogout(Player player) {
        // Update the existing _time record with the logout time to mark it as queued
        getDb().enqueue(new TablePlayerLog.RecordLogoutAction(player.getName()));

        // Process record immediately
        getDb().enqueue(new TablePlayerLog.ProcessPlayerQueuedRecordsAction(player.getName()));
    }

    /**
     * Process any queued records that were somehow missed
     * This should ONLY be called on server startup!!
     */
    public void processQueuedRecords() {
        getDb().enqueue(new TablePlayerLog.ProcessAllQueuedRecordsAction());
    }

    /**
     * Sets the logout value to the server's last seen timestamp for all active records
     * This should ONLY be called on server startup!!
     */
    public void fixLogsWithNoLogout() {
        getDb().enqueue(new TablePlayerLog.FixLogsWithNoLogoutAction());
    }

    /**
     * Updates the server's last_seen timestamp
     * This value is used for tracking player time in case the server crashes before they logout
     */
    public void updateServerLastSeen() {
        getDb().enqueue(new TableServerLastSeen.UpdateServerLastSeenAction());
    }

    public void getPlayTime(CommandSender requester, String player){
        getDb().enqueue(new TablePlayerLog.GetPlayTime(requester, player));
    }

}

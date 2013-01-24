package net.hcfactions.bigbrother.playerlogging;

import net.hcfactions.bigbrother.playerlogging.actions.*;
import net.hcfactions.bigbrother.sql.QueuedDatabaseRunnable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class PlayerDbHelper extends QueuedDatabaseRunnable {
    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_QUEUED = 1;
    public static final int STATUS_PROCESSED = 2;

    public PlayerDbHelper(Connection conn, Logger log)
    {
        super(conn, log);
        this.enqueue(new CreateTablesAction());
    }

    public void recordLogin(String playerName, String ip)
    {
        // Just in case we haven't marked their previous log for processing, do that now
        // I doubt it will happen, but you never know...
        this.enqueue(new QueueRecordsAction(playerName));
        this.enqueue(new RecordLoginAction(playerName, ip));
    }
    public void recordLogin(String playerName, InetAddress addr)
    {
        this.recordLogin(playerName, addr.getHostAddress());
    }
    public void recordLogin(String playerName, InetSocketAddress addr) {
        this.recordLogin(playerName, addr.getAddress());
    }

    public void recordLogout(String playerName) {
        // Update the existing _time record with the logout time and mark it as queued
        this.enqueue(new RecordLogoutAction(playerName));

        // Queue record
        this.enqueue(new QueueRecordsAction(playerName));
    }

    public void processQueuedRecords() {
        this.enqueue(new ProcessQueuedRecordsAction());
    }

    /**
     * Queues and processes all pending records.
     * This should ONLY be called on server startup!!
     */
    public void queueAllRecords() {
        this.enqueue(new QueueAllRecordsAction());
    }

    /**
     * Sets the logout value to the server's last seen timestamp for all active records
     * This should ONLY be called on server startup!!
     */
    public void fixLogsWithNoLogout() {
        this.enqueue(new FixLogsWithNoLogoutAction());
    }

    public void updateServerLastSeen() {
        this.enqueue(new UpdateServerLastSeenAction());
    }

}

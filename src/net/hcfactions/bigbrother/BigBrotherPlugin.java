package net.hcfactions.bigbrother;

import net.hcfactions.bigbrother.blocklogging.BlockDbHelper;
import net.hcfactions.bigbrother.blocklogging.BlockEventListener;
import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.playerlogging.PlayerEventListener;
import net.hcfactions.bigbrother.sql.MySQLProvider;
import net.hcfactions.bigbrother.util.LogUtils;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;

import java.sql.SQLException;

public class BigBrotherPlugin extends JavaPlugin {

    private Connection dbconn;
    private PlayerDbHelper playerDbHelper;
    private BlockDbHelper blockDbHelper;

    @Override
    public void onEnable() {
        super.onEnable();

        // Start listening for events
        getServer().getPluginManager().registerEvents(new BlockEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);

        // Start the async background threads for logging blocks and players
        // Check for and execute queries every other tick
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, getPlayerDbHelper(), 0, 2);
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, getBlockDbHelper(), 0, 2);

        // In case of crash, immediately process any pending records before people join again
        getPlayerDbHelper().fixLogsWithNoLogout();
        getPlayerDbHelper().queueAllRecords();

        // Start the background task to update server_last_seen timestamp (delay 1 tick, run every 30 seconds)
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getPlayerDbHelper().updateServerLastSeen();
                getPlayerDbHelper().processQueuedRecords();
            }
        }, 1, 20 * 30);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // Close the MySQL connection
        try
        {
            getConnection().close();
        }
        catch (SQLException ex)
        {
            getLogger().warning("Failed to close MySQL connection");
            getLogger().throwing("BigBrotherPlugin", "onDisable", ex);
        }
    }

    public PlayerDbHelper getPlayerDbHelper()
    {
        if(this.playerDbHelper == null)
        {
            this.playerDbHelper = new PlayerDbHelper(this.getConnection(), this.getLogger());
        }

        return this.playerDbHelper;
    }

    public BlockDbHelper getBlockDbHelper()
    {
        if(this.blockDbHelper == null)
        {
            this.blockDbHelper = new BlockDbHelper(this.getConnection(), this.getLogger());
        }

        return this.blockDbHelper;
    }
    
    
    protected Connection getConnection() {
        if(this.dbconn == null)
        {
            try
            {
                MySQLProvider.setParent(this);
                MySQLProvider.load();
                this.dbconn = MySQLProvider.getConnection();
            }
            catch (SQLException ex) {
                getLogger().throwing("BigBrotherPlugin", "getConnection", ex);
                getLogger().severe(ex.getMessage());
            }
        }
        return this.dbconn;
    }
}

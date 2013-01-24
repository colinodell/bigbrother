package net.hcfactions.bigbrother;

import net.hcfactions.bigbrother.blocklogging.BlockDbHelper;
import net.hcfactions.bigbrother.blocklogging.BlockEventListener;
import net.hcfactions.bigbrother.blocklogging.DropRecorder;
import net.hcfactions.bigbrother.commands.CmdPlaytime;
import net.hcfactions.core.BasePlugin;
import net.hcfactions.bigbrother.playerlogging.PlayerDbHelper;
import net.hcfactions.bigbrother.playerlogging.PlayerEventListener;
import net.hcfactions.core.commands.BaseCommandManager;
import net.hcfactions.core.commands.CommandDeclaration;

/**
 * The core of the BigBrother plugin
 */
public class BigBrotherPlugin extends BasePlugin {
    private PlayerDbHelper playerDbHelper;
    private BlockDbHelper blockDbHelper;
    private DropRecorder dropRecorder;

    @Override
    public void onEnable() {
        super.onEnable();

        // Start listening for events
        getServer().getPluginManager().registerEvents(new BlockEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);

        // In case of crash, immediately process any pending records before people join again
        getPlayerDbHelper().fixLogsWithNoLogout();
        getPlayerDbHelper().processQueuedRecords();

        // Start the background task to update server_last_seen timestamp (delay 1 tick, run every 30 seconds)
        startUpdateLastSeenTask();

        // Purge any ore block breaks that we couldn't connect to a player/drop
        // This should run frequently to decrease memory usage and the potential for abuse
        // Currently it's set to run every second (20 ticks) after a 20 tick delay
        getServer().getScheduler().runTaskTimerAsynchronously(this, getDropRecorer(), 20, 20);
    }

    @Override
    public void onDisable() {
        // Stop the background task; we'll call it manually once more though
        stopUpdateLastSeenTask();
        getPlayerDbHelper().updateServerLastSeen();

        // Call parent logic LAST to complete queued tasks and close the db connection
        super.onDisable();
    }

    @Override
    protected void onRegisterCustomCommands(BaseCommandManager manager) {
        // All the config commands are registered in the base plugin
        // If you need to register other commands, you would do something like: manager.register();
        manager.register(new CommandDeclaration(CmdPlaytime.class, getBaseCommandPrefix(), "playtime", "time"));
    }

    private int updateServerLastSeenTaskId = -1;
    private void startUpdateLastSeenTask()
    {
        if(updateServerLastSeenTaskId == -1)
            updateServerLastSeenTaskId = getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    getPlayerDbHelper().updateServerLastSeen();
                }
            }, 1, getConfig().getInt("lastseen.frequency", 30*20)).getTaskId();
    }
    private void stopUpdateLastSeenTask()
    {
        if(updateServerLastSeenTaskId != -1)
        {
            getServer().getScheduler().cancelTask(updateServerLastSeenTaskId);
            updateServerLastSeenTaskId = -1;
        }
    }

    /**
     * Lazy-loads the Player helper as needed.
     * The helper will bind itself to the current db queue
     * @return The single instance of the helper
     */
    public PlayerDbHelper getPlayerDbHelper()
    {
        if(this.playerDbHelper == null)
        {
            this.playerDbHelper = new PlayerDbHelper(getDatabaseQueue());
        }

        return this.playerDbHelper;
    }

    /**
     * Lazy-loads the Block helper as needed
     * The helper will bind itself to the current db queue
     * @return
     */
    public BlockDbHelper getBlockDbHelper()
    {
        if(this.blockDbHelper == null)
        {
            this.blockDbHelper = new BlockDbHelper(getDatabaseQueue());
        }

        return this.blockDbHelper;
    }

    public DropRecorder getDropRecorer()
    {
        if(this.dropRecorder == null)
        {
            this.dropRecorder = new DropRecorder(this);
        }

        return this.dropRecorder;
    }

    @Override
    public void reloadConfig()
    {
        super.reloadConfig();
        stopUpdateLastSeenTask();
        startUpdateLastSeenTask();
    }
}
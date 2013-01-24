package net.hcfactions.core;

import net.hcfactions.core.commands.PermissionCommandDeclaration;
import net.hcfactions.core.config.CmdConfig;
import net.hcfactions.core.config.CmdConfigView;
import net.hcfactions.core.config.CmdConfigReload;
import net.hcfactions.core.config.CmdConfigSet;
import net.hcfactions.core.log.EnhancedLogger;
import net.hcfactions.core.util.ChatUtils;
import net.hcfactions.core.commands.BaseCommandManager;
import net.hcfactions.core.log.IHasLogger;
import net.hcfactions.core.sql.MySQLProvider;
import net.hcfactions.core.sql.action.IDatabaseAction;
import net.hcfactions.core.threading.MonitoredBackgroundQueue;
import net.hcfactions.core.util.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public abstract class BasePlugin extends JavaPlugin implements IHasLogger {
    private Connection dbconn;
    private MonitoredBackgroundQueue<IDatabaseAction, Connection> dbQueue;
    private BaseCommandManager cmdManager;
    private File configFile;
    private int dbQueueTaskId = -1;
    private EnhancedLogger logger;

    // This should be the first part of the plugin permissions, up to the first dot separator
    private static String PERMISSION_PREFIX = "";
    public static String getPermissionPrefix()
    {
        return PERMISSION_PREFIX;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        PERMISSION_PREFIX = this.getName().toLowerCase();

        // Register our command
        //getCommand(COMMAND_PREFIX).setExecutor(this);

        this.loadConfig();
        this.applyConfig();

        // Start the background queue which handles all database actions
        // It will check for and execute queries every so often
        startDbQueueTask();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // Stop, close and clean things
        try
        {
            // Stop the background task
            stopDbQueueTask();

            // Execute any remaining actions before we shut down
            getDatabaseQueue().runAll();
            getELogger().info("Completed execution of any queued actions remaining");

            // Close the db connection
            getConnection().close();
            this.dbconn = null;
            getELogger().info("Closed MySQL connection");
        }
        catch (SQLException ex)
        {
            getELogger().warning("Failed to close MySQL connection");
            getELogger().logException(ex);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return getCommandManager().execute(sender, cmd.getName(), label, args);
    }

    protected void startDbQueueTask()
    {
        if(dbQueueTaskId == -1)
            dbQueueTaskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, getDatabaseQueue(), 0, getConfig().getInt("queue.frequency", 2));
    }

    protected void stopDbQueueTask()
    {
        if(dbQueueTaskId != -1)
        {
            getServer().getScheduler().cancelTask(dbQueueTaskId);
            dbQueueTaskId = -1;
        }
    }

    /**
     * Lazy-loads the queue which handles database queries
     * This will obtain a single connection from our plugin
     * @return
     */
    protected MonitoredBackgroundQueue getDatabaseQueue()
    {
        if(this.dbQueue == null)
        {
            this.dbQueue = new MonitoredBackgroundQueue<IDatabaseAction, Connection>(getConnection(), getELogger(), "dbqueue") {
                @Override
                public boolean onMonitorAlarm(int level) {
                    if(level == WARNING)
                    {
                        // Broadcast warning message
                        ChatUtils.broadcastWarningOps(BasePlugin.this, String.format("%sThe %s background queue has exceeded the warning level (%d/%d)", ChatColor.GOLD, this.getQueueIdentifier(), this.queue.size(), this.warnLevel));

                        // Return true, indicating that we may resume processing
                        return true;
                    }
                    else // level == ERROR
                    {
                        // Broadcast error message
                        ChatUtils.broadcastWarningOps(BasePlugin.this, String.format("%sThe %s background queue has exceeded the error level (%d/%d). All queued records will be processed immediately.", ChatColor.RED, this.getQueueIdentifier(), this.queue.size(), this.errorLevel));

                        // Return true, indicating that we may resume processing
                        return true;
                    }
                }

                @Override
                public void onMonitorAlarmReset(int level) {
                    // The error response above should clear the queue, which would also cause the warning alarm to clear. Therefore we'll only show the warning cleared message
                    ChatUtils.broadcastWarningOps(BasePlugin.this, String.format("%sThe %s background queue is now below the warning threshold", ChatColor.DARK_GREEN, this.getQueueIdentifier()));
                }
            };
        }
        return this.dbQueue;
    }

    /**
     * Lazy-loads a single connection for our plugin to use
     * @return
     */
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
                getELogger().logException(ex);
            }
        }
        return this.dbconn;
    }

    protected abstract void onRegisterCustomCommands(BaseCommandManager manager);

    public BaseCommandManager getCommandManager()
    {
        if(this.cmdManager == null)
        {
            this.cmdManager = new BaseCommandManager(this, getELogger()) {
                @Override
                public void onInit() {
                    this.register(new PermissionCommandDeclaration(CmdConfig.class, getBaseCommandPrefix(), "*.config", "config", "conf"));
                    this.register(new PermissionCommandDeclaration(CmdConfigView.class, getBaseCommandPrefix(), "*.config", "config view", "conf view"));
                    this.register(new PermissionCommandDeclaration(CmdConfigSet.class, getBaseCommandPrefix(), "*.config", "config set", "conf set"));
                    this.register(new PermissionCommandDeclaration(CmdConfigReload.class, getBaseCommandPrefix(), "*.config", "config reload", "conf reload", "config load", "conf load"));
                    onRegisterCustomCommands(this);
                }
            };
        }
        return this.cmdManager;
    }

    private String baseCommandPrefix;
    public String getBaseCommandPrefix() {
        if(baseCommandPrefix == null)
        {
            Set<String> commands = this.getDescription().getCommands().keySet();
            baseCommandPrefix = (String) commands.toArray()[0];
        }
        return baseCommandPrefix;
    }

    // Get a custom logger instead of the built-in one
    public EnhancedLogger getELogger()
    {
        if(this.logger == null)
        {
            this.logger = new EnhancedLogger(this);
        }
        return this.logger;
    }

    public void loadConfig()
    {

        try {
            if(this.configFile == null)
            {
                this.configFile = new File(getDataFolder(), "config.yml");
                if(!this.configFile.exists())
                {
                    this.configFile.getParentFile().mkdirs();
                    FileUtils.copy(getResource("config.yml"), this.configFile);
                }
            }

            this.getConfig().load(this.configFile);
            this.getConfig().options().copyDefaults(true);
            getELogger().info("Loaded configuration");
        } catch (Exception e) {
            getELogger().severe("Failed to load configuration");
            getELogger().logException(e);
        }
    }

    // Apply current config options to important things
    private void applyConfig()
    {
        getDatabaseQueue().setMaxItems(getConfig().getInt("queue.maxitems"));
        getDatabaseQueue().setWarnLevel(getConfig().getInt("queue.warnlevel"));
        getDatabaseQueue().setErrorLevel(getConfig().getInt("queue.errorlevel"));

        // Restart the background queue only if it was running
        if(dbQueueTaskId != -1)
        {
            stopDbQueueTask();
            startDbQueueTask();
        }
    }

    public void saveConfig()
    {
        super.saveConfig();
    }

    @Override
    public void reloadConfig()
    {
        super.reloadConfig();
        this.applyConfig();
    }

    /**
     * Checks player permissions. Any leading wildcard is replaced with the plugin name before checking
     * @param player The player to check
     * @param permission The permission to check
     * @return Whether they have that permission or not
     */
    public static boolean playerHasPermission(Permissible player, String permission) {
        if(permission.startsWith("*"))
            permission = getPermissionPrefix() + permission.substring(1);

        return player.hasPermission(permission);
    }
}

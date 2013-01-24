package net.hcfactions.bigbrother;

import net.hcfactions.bigbrother.sql.MySQLProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;

import java.sql.SQLException;

public class BigBrotherPlugin extends JavaPlugin {

    private Connection dbconn;

    @Override
    public void onEnable() {
        super.onEnable();

        // Start listening for events
        getServer().getPluginManager().registerEvents(new BlockChangeListener(this), this);
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

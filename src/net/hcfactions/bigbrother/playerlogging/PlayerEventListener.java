package net.hcfactions.bigbrother.playerlogging;

import net.hcfactions.bigbrother.BigBrotherPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
    private BigBrotherPlugin plugin;

    public PlayerEventListener(BigBrotherPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if(event.getPlayer() == null)
            return;

        plugin.getPlayerDbHelper().recordLogin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if(event.getPlayer() == null)
            return;

        plugin.getPlayerDbHelper().recordLogout(event.getPlayer());
    }
}

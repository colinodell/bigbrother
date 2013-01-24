package net.hcfactions.core.util;

import net.hcfactions.core.BasePlugin;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static void broadcastWarningOps(BasePlugin plugin, String message)
    {
        plugin.getLogger().warning(message);
        for(Player p : plugin.getServer().getOnlinePlayers())
        {
            if(plugin.playerHasPermission(p, "*.queuewarnings"))
                p.sendMessage(message);
        }
    }
}

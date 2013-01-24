package net.hcfactions.bigbrother.commands;

import net.hcfactions.bigbrother.BigBrotherPlugin;
import net.hcfactions.core.BasePlugin;
import net.hcfactions.core.commands.BaseCommand;
import net.hcfactions.core.commands.CommandDeclaration;
import net.hcfactions.core.commands.InvalidArgumentsException;
import net.hcfactions.core.commands.UnauthorizedException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdPlaytime extends BaseCommand {
    /**
     * @param sender      The person who is executing the command
     * @param declaration The declaration of the command; useful for getting information about how the command is called
     * @param args        Any command arguments
     * @param plugin      Reference to the plugin instance
     */
    public CmdPlaytime(CommandSender sender, CommandDeclaration declaration, String[] args, BasePlugin plugin) {
        super(sender, declaration, args, plugin);
    }

    @Override
    protected void execute() throws UnauthorizedException, InvalidArgumentsException {
        String player = "";

        if(getArgs() == null || getArgs().length == 0)
        {
            if (!(getSender() instanceof Player))
                throw new InvalidArgumentsException("Player name is required");

            // No player name given; assume current player
            Player p = (Player)getSender();
            // Can they use this on themselves?
            if(!p.hasPermission("bigbrother.playtime.self"))
                throw new UnauthorizedException();

            player = p.getName();
        }
        else
        {
            player = getArgs()[0];

            // Check their permissions
            if(getSender() instanceof Player)
            {
                Player p = (Player)getSender();
                if(p.getName().equalsIgnoreCase(player))
                {
                    // Can the player view their own time?
                    if(!p.hasPermission("bigbrother.playtime.self"))
                        throw new UnauthorizedException();
                }
                else
                {
                    // Can the player view others' time?
                    if(!p.hasPermission("bigbrother.playtime.others"))
                        throw new UnauthorizedException();
                }
            }
        }

        ((BigBrotherPlugin)getPlugin()).getPlayerDbHelper().getPlayTime(getSender(), player);
    }

    @Override
    protected void showHelp() {
        getSender().sendMessage(getHelpMessage(getCommandDeclaration()));
    }

    protected static String getHelpMessage(CommandDeclaration declaration)
    {
        return new StringBuilder()
                .append(ChatColor.GOLD)
                .append("/")
                .append(declaration.getFullName())
                .append(" [")
                .append(ChatColor.YELLOW)
                .append("player")
                .append(ChatColor.GOLD)
                .append("]").toString();
    }
}

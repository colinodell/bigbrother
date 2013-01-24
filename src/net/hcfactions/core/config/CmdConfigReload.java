package net.hcfactions.core.config;

import net.hcfactions.core.BasePlugin;
import net.hcfactions.core.commands.BaseCommand;
import net.hcfactions.core.commands.CommandDeclaration;
import net.hcfactions.core.commands.InvalidArgumentsException;
import net.hcfactions.core.commands.UnauthorizedException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdConfigReload extends BaseCommand {


    /**
     * @param sender      The person who is executing the command
     * @param declaration The declaration of the command; useful for getting information about how the command is called
     * @param args        Any command arguments
     * @param plugin      Reference to the plugin instance
     */
    public CmdConfigReload(CommandSender sender, CommandDeclaration declaration, String[] args, BasePlugin plugin) {
        super(sender, declaration, args, plugin);
    }

    @Override
    protected void execute() throws UnauthorizedException, InvalidArgumentsException {
        getPlugin().saveConfig();
        getPlugin().reloadConfig();
        getSender().sendMessage(ChatColor.GREEN + "Reloaded and applied the configuration");
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
                .toString();
    }
}

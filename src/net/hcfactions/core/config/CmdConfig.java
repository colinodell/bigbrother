package net.hcfactions.core.config;

import net.hcfactions.core.BasePlugin;
import net.hcfactions.core.commands.BaseCommand;
import net.hcfactions.core.commands.CommandDeclaration;
import net.hcfactions.core.commands.InvalidArgumentsException;
import net.hcfactions.core.commands.UnauthorizedException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdConfig extends BaseCommand {


    /**
     * @param sender      The person who is executing the command
     * @param declaration The declaration of the command; useful for getting information about how the command is called
     * @param args        Any command arguments
     * @param plugin      Reference to the plugin instance
     */
    public CmdConfig(CommandSender sender, CommandDeclaration declaration, String[] args, BasePlugin plugin) {
        super(sender, declaration, args, plugin);
    }

    @Override
    protected void execute() throws UnauthorizedException, InvalidArgumentsException {
        this.showHelp();
    }

    @Override
    protected void showHelp() {
        getSender().sendMessage(ChatColor.BLUE + "Manage the plugin's configuration");

        CommandDeclaration get = getPlugin().getCommandManager().getDeclarationForCommand(CmdConfigView.class);
        CommandDeclaration set = getPlugin().getCommandManager().getDeclarationForCommand(CmdConfigSet.class);
        CommandDeclaration reload = getPlugin().getCommandManager().getDeclarationForCommand(CmdConfigReload.class);

        if(get != null)
            getSender().sendMessage(CmdConfigView.getHelpMessage(get));
        if(set != null)
            getSender().sendMessage(CmdConfigSet.getHelpMessage(set));
        if(reload != null)
            getSender().sendMessage(CmdConfigReload.getHelpMessage(reload));
    }

}

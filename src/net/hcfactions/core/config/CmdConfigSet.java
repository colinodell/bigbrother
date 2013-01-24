package net.hcfactions.core.config;

import net.hcfactions.core.BasePlugin;
import net.hcfactions.core.commands.BaseCommand;
import net.hcfactions.core.commands.CommandDeclaration;
import net.hcfactions.core.commands.InvalidArgumentsException;
import net.hcfactions.core.commands.UnauthorizedException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

public class CmdConfigSet extends BaseCommand {

    /**
     * @param sender      The person who is executing the command
     * @param declaration The declaration of the command; useful for getting information about how the command is called
     * @param args        Any command arguments
     * @param plugin      Reference to the plugin instance
     */
    public CmdConfigSet(CommandSender sender, CommandDeclaration declaration, String[] args, BasePlugin plugin) {
        super(sender, declaration, args, plugin);
    }

    public static String getHelpMessage(CommandDeclaration declaration)
    {
        return new StringBuilder()
                .append(ChatColor.GOLD)
                .append("/")
                .append(declaration.getFullName())
                .append(" [")
                .append(ChatColor.YELLOW)
                .append("key")
                .append(ChatColor.GOLD)
                .append("] [")
                .append(ChatColor.YELLOW)
                .append("value")
                .append(ChatColor.GOLD)
                .append("]")
                .toString();
    }


    @Override
    protected void execute() throws UnauthorizedException, InvalidArgumentsException {
        if(getArgs() == null || getArgs().length != 2)
            throw new InvalidArgumentsException();

        String key = getArgs()[0];
        String newVal = getArgs()[1];

        Configuration config = getPlugin().getConfig();
        Object currentValue = config.get(key);
        if(currentValue == null)
            throw new InvalidArgumentsException("No such configuration key");

        try
        {
            if(config.isBoolean(key))
            {
                config.set(key, Boolean.parseBoolean(newVal));
            }
            else if(config.isDouble(key))
            {
                config.set(key, Double.parseDouble(newVal));
            }
            else if(config.isInt(key))
            {
                config.set(key, Integer.parseInt(newVal));
            }
            else if(config.isLong(key))
            {
                config.set(key, Long.parseLong(newVal));
            }
            else if(config.isString(key))
            {
                config.set(key, newVal);
            }
            else
            {
                throw new InvalidArgumentsException("You cannot modify this configuration key");
            }
            getPlugin().saveConfig();
            getSender().sendMessage(new StringBuilder()
                .append(ChatColor.GREEN)
                .append("Successfully set ")
                .append(ChatColor.DARK_GREEN)
                .append(key)
                .append(ChatColor.GREEN)
                .append(" to: ")
                .append(ChatColor.DARK_GREEN)
                .append(config.get(key))
                .toString()
            );

            // Get the reload command name
            CommandDeclaration reloadDeclaration = getPlugin().getCommandManager().getDeclarationForCommand(CmdConfigReload.class);
            if(reloadDeclaration != null)
            {
                getSender().sendMessage(new StringBuilder()
                    .append(ChatColor.GREEN)
                    .append("You may need to run ")
                    .append(ChatColor.GOLD)
                    .append("/")
                    .append(reloadDeclaration.getFullName())
                    .append(" ")
                    .append(ChatColor.GREEN)
                    .append("for changes to take effect")
                    .toString()
                );
            }
            else
            {
                getSender().sendMessage(new StringBuilder()
                    .append(ChatColor.GREEN)
                    .append("You may need to reload the configuration for changes to take effect")
                    .toString()
                );
            }
        }
        catch(Exception ex) {
            throw new InvalidArgumentsException("Invalid value type");
        }

    }

    @Override
    protected void showHelp() {
        getSender().sendMessage(getHelpMessage(getCommandDeclaration()));
    }
}

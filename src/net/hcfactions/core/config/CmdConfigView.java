package net.hcfactions.core.config;

import net.hcfactions.core.BasePlugin;
import net.hcfactions.core.commands.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class CmdConfigView extends BaseCommand {

    /**
     * @param sender      The person who is executing the command
     * @param declaration The declaration of the command; useful for getting information about how the command is called
     * @param args        Any command arguments
     * @param plugin      Reference to the plugin instance
     */
    public CmdConfigView(CommandSender sender, CommandDeclaration declaration, String[] args, BasePlugin plugin) {
        super(sender, declaration, args, plugin);
    }

    @Override
    protected void execute() throws UnauthorizedException, InvalidArgumentsException {
        if(getArgs() == null || getArgs().length > 1)
            throw new InvalidArgumentsException();

        if(getArgs().length == 0)
        {
            // Show the whole config
            StringBuilder sb = new StringBuilder();
            this.traverseConfig(getPlugin().getConfig(), "Configuration", -1, sb);
            getSender().sendMessage(sb.toString().split("\n"));

            return;
        }

        String key = getArgs()[0];
        Object val = getPlugin().getConfig().get(key);

        if(val instanceof ConfigurationSection)
        {
            // Show the whole config
            StringBuilder sb = new StringBuilder();
            this.traverseConfig(getPlugin().getConfig().getConfigurationSection(key), key, 0, sb);
            getSender().sendMessage(sb.toString().split("\n"));

            return;
        }
        else if(val == null)
        {
            getSender().sendMessage("Unknown configuration key.");
        }
        else
        {
            getSender().sendMessage(String.format("%s%s: %s%s", ChatColor.GOLD, key, ChatColor.YELLOW, val.toString()));
        }


    }

    private void traverseConfig(ConfigurationSection section, String sectionTitle, int depth, StringBuilder sb)
    {
        if(depth == -1)
        {
            // We want to see the full config; let's make the title blue
            sb.append(ChatColor.BLUE).append(sectionTitle).append("\n");
        }
        else
        {
            // We're looking at a subset of the config
            sb.append(ChatColor.GOLD).append(StringUtils.repeat(" ", depth * 2));
            if(depth > 0)
                sb.append(".");
            sb.append(sectionTitle).append("\n");
        }
        //section.
        for(String key : section.getKeys(false))
        {
            if(section.isConfigurationSection(key))
            {
                traverseConfig(section.getConfigurationSection(key), key, depth+1, sb);
            }
            else
            {
                sb.append(ChatColor.GOLD).append(StringUtils.repeat(" ", (depth+1)*2)).append(".").append(ChatColor.YELLOW).append(key).append(": ").append(section.get(key).toString()).append("\n");
            }
        }
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
                .append("key")
                .append(ChatColor.GOLD)
                .append("]").toString();
    }
}
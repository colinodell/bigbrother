package net.hcfactions.core.commands;


import net.hcfactions.core.BasePlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CommandDeclaration {
    protected Class<? extends BaseCommand> commandClass;
    protected String basePluginCommandPrefix;
    protected String[] name;
    protected List<String[]> aliases = new ArrayList<String[]>();

    public CommandDeclaration(Class<? extends BaseCommand> cmdClass, String basePluginCommandPrefix, String... names)
    {
        this.commandClass = cmdClass;
        this.basePluginCommandPrefix = basePluginCommandPrefix;
        this.name = names[0].split(" ");
        for(int i = 1; i < names.length; i++)
            this.aliases.add(names[i].split(" "));
    }

    protected String getBasePluginCommandPrefix()
    {
        return basePluginCommandPrefix;
    }

    protected String[] getName()
    {
        return name;
    }

    protected List<String[]> getAliases()
    {
        return aliases;
    }

    public BaseCommand createCommandInstance(CommandSender sender, String[] args, BasePlugin plugin)
    {
        BaseCommand cmd = null;
        try
        {
            Constructor ctor = commandClass.getConstructor(CommandSender.class, CommandDeclaration.class, String[].class, BasePlugin.class);
            cmd = (BaseCommand)(ctor.newInstance(sender, this, args, plugin));
        }
        catch(Exception ex) {
            sender.sendMessage(ChatColor.RED + "Failed to create instance of command: " + ChatColor.GOLD + StringUtils.join(name, " "));
            plugin.getLogger().logException(ex);
        }

        return cmd;
    }

    /**
     * Get the full command name as a space-separated string, including base plugin's command prefix
     * (i.e. "f show" or "myplugin config set")
     * @return
     */
    public String getFullName() {
        return getBasePluginCommandPrefix() + " " + StringUtils.join(getName(), " ");
    }
}

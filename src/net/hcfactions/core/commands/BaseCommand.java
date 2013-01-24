package net.hcfactions.core.commands;

import net.hcfactions.core.BasePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class BaseCommand implements Runnable {
    private CommandSender sender;
    private String[] args;
    private CommandDeclaration declaration;
    private BasePlugin plugin;

    /**
     *
     * @param sender The person who is executing the command
     * @param declaration The declaration of the command; useful for getting information about how the command is called
     * @param args Any command arguments
     * @param plugin Reference to the plugin instance
     */
    public BaseCommand(CommandSender sender, CommandDeclaration declaration, String[] args, BasePlugin plugin)
    {
        this.sender = sender;
        this.declaration = declaration;
        this.args = args;
        this.plugin = plugin;
    }

    protected CommandSender getSender()
    {
        return sender;
    }

    protected CommandDeclaration getCommandDeclaration()
    {
        return declaration;
    }

    protected String[] getArgs()
    {
        return args;
    }

    protected BasePlugin getPlugin()
    {
        return plugin;
    }

    protected abstract void execute() throws UnauthorizedException, InvalidArgumentsException;

    public void run()
    {
        try
        {
            if(args != null && args.length == 1 && args[0].equals("help"))
                showHelp();
            else
                this.execute();
        } catch (InvalidArgumentsException ex) {
            if(ex != null && ex.getMessage() != null)
                getSender().sendMessage(ChatColor.RED + ex.getMessage());
            else
                getSender().sendMessage(ChatColor.RED + "Invalid arugments");

            this.showHelp();
        } catch (UnauthorizedException ex) {
            getSender().sendMessage(ChatColor.RED + "You don't have permission to use this command");
        } catch (Exception ex) {
            getSender().sendMessage(ChatColor.RED + "An unknown error occurred");

            getPlugin().getELogger().logException(ex);
        }
    }

    /**
     * Shows help/usage information
     */
    protected abstract void showHelp();
}

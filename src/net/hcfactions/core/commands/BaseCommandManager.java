package net.hcfactions.core.commands;

import net.hcfactions.core.BasePlugin;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.logging.Logger;

public abstract class BaseCommandManager {
    // This is used for determining which command should be executed
    protected HashMap<List<String>, CommandDeclaration> declarationsByCommandName;
    // This is for other classes to access declaration information per-command
    protected HashMap<Class<? extends BaseCommand>, CommandDeclaration> declarationsByClass;
    // This is a simple list of command names; useful for help messages
    protected SortedSet<String> baseCommandNames;

    protected BasePlugin plugin;
    protected Logger logger;

    public BaseCommandManager(BasePlugin plugin, Logger logger) {
        this.logger = logger;
        this.plugin = plugin;
        this.declarationsByCommandName = new HashMap<List<String>, CommandDeclaration>();
        this.declarationsByClass = new HashMap<Class<? extends BaseCommand>, CommandDeclaration>();
        this.baseCommandNames = new TreeSet<String>();
        this.onInit();
    }

    // Register your commands in here
    public abstract void onInit();

    public boolean execute(CommandSender sender, String cmd, String label, String[] args)
    {
        // Do some null checks
        if(declarationsByCommandName == null || declarationsByCommandName.size() == 0)
            return false;

        // If no args passed, we don't know which custom command to show, so list them all out
        if(args == null || args.length == 0 || (args.length == 1 && args[0].equals("help")))
        {
            for(String s : baseCommandNames)
            {
                sender.sendMessage(String.format("%s/%s %s%s", ChatColor.GOLD, cmd, ChatColor.YELLOW, s));
            }
            return true;
        }

        // Is the player looking for help information?
        boolean showHelpInsteadOfRunning = false;
        if(args[0].equals("help"))
        {
            showHelpInsteadOfRunning = true;
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        // Locate the command
        LinkedList<String> cmdNameToTry = new LinkedList<String>(Arrays.asList(args));
        for(int i = args.length; !cmdNameToTry.isEmpty(); i--)
        {
            if(declarationsByCommandName.containsKey(cmdNameToTry))
            {
                CommandDeclaration declaration = declarationsByCommandName.get(cmdNameToTry);
                if(declaration instanceof IRequiresPermission)
                {
                    if(!((IRequiresPermission)declaration).canBeExecutedBy(sender))
                    {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                        return true;
                    }
                }

                // Get the real args
                String[] newargs = Arrays.copyOfRange(args, i, args.length);
                String cmdName = StringUtils.join(cmdNameToTry, " ");
                // Run command on a background thread
                BaseCommand matchedCommand = declaration.createCommandInstance(sender, newargs, plugin);
                if(matchedCommand == null)
                    continue;

                if(showHelpInsteadOfRunning)
                    matchedCommand.showHelp();
                else
                    matchedCommand.run();

                return true;

            }
            // Take the last arg off and try again
            cmdNameToTry.removeLast();
        }

        return false;
    }

    public void register(CommandDeclaration cd)
    {
        // Register the main command name
        registerCommandForExecution(cd.getName(), cd);
        // Register the aliases
        if(cd.getAliases() != null)
            for(String[] alias : cd.getAliases())
                registerCommandForExecution(alias, cd);

        // Also list the main name in the subcommand list, but not aliases
        registerPrimaryNameForHelpInfo(cd.getName());

        // Lastly, store the declaration where we can easily reference it by the command class
        storeCommandDeclarationByClass(cd);
    }

    private void storeCommandDeclarationByClass(CommandDeclaration cd)
    {
        declarationsByClass.put(cd.commandClass, cd);
    }

    private void registerCommandForExecution(String[] nameOrAlias, CommandDeclaration cd)
    {
        if(!declarationsByCommandName.containsKey(nameOrAlias))
            declarationsByCommandName.put(Arrays.asList(nameOrAlias), cd);
    }

    private void registerPrimaryNameForHelpInfo(String[] name)
    {
        // We only care about the first part
        if(name != null && name.length > 0)
            if(!baseCommandNames.contains(name[0]))
                baseCommandNames.add(name[0]);
    }

    public CommandDeclaration getDeclarationForCommand(Class<? extends BaseCommand> cmdClass) {
        return declarationsByClass.get(cmdClass);
    }
}

package net.hcfactions.core.commands;

import net.hcfactions.core.BasePlugin;
import org.bukkit.command.CommandSender;

public class PermissionCommandDeclaration extends CommandDeclaration implements IRequiresPermission {
    private String permission;

    public PermissionCommandDeclaration(Class<? extends BaseCommand> cmdClass, String basePluginCommandPrefix, String permission, String... names) {
        super(cmdClass, basePluginCommandPrefix, names);
        this.permission = permission;
    }

    @Override
    public boolean canBeExecutedBy(CommandSender sender) {
        return BasePlugin.playerHasPermission(sender, permission);
    }
}

package net.hcfactions.core.commands;

import org.bukkit.command.CommandSender;

public interface IRequiresPermission {
    public boolean canBeExecutedBy(CommandSender sender);
}

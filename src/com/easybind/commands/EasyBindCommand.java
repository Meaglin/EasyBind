package com.easybind.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.easybind.EasyBind;

public abstract class EasyBindCommand extends Command {
    private EasyBind plugin;
    private String   requiredAccess = null;

    public EasyBindCommand(String name, EasyBind plugin) {
        super(name);
        this.plugin = plugin;
    }

    protected EasyBind getPlugin() {
        return plugin;
    }

    protected boolean canUseCommand(Player p, String command) {
        return getPlugin().getPermissions().canUse(p, command);
    }

    protected void setRequiredAccess(String access) {
        requiredAccess = access;
    }

    protected boolean requiresAccess() {
        return requiredAccess != null;
    }

    protected String getRequiredAccess() {
        return requiredAccess;
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] vars) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (requiresAccess() && !canUseCommand(p, getRequiredAccess())) {
                p.sendMessage(ChatColor.RED + "You don't have the required permissions to use this command.");
            } else {
                run((Player) sender, vars);
            }

            return true;
        } else if (sender instanceof ConsoleCommandSender) {
            return runConsole(sender, vars);
        } else {
            return false;
        }
    }

    public abstract boolean run(Player player, String[] vars);

    public boolean runConsole(CommandSender sender, String[] vars) {
        sender.sendMessage(ChatColor.RED + "This command doesn't support console usage.");
        return true;
    }
}

package com.easybind.commands.general;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.easybind.EasyBind;
import com.easybind.commands.EasyBindCommand;
import com.easybind.model.BindList;
import com.easybind.model.Key;
import com.easybind.persistence.Bind;

public class BindCommand extends EasyBindCommand {

    public BindCommand(EasyBind plugin) {
        super("bind", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        if (vars.length < 1) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /bind <command>");
            return true;
        }

        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You need to have something equipped to be able to bind.");
            return true;
        }
        Key key = Key.byName(vars[0]);
        String command = null;
        if (key != null) {
            if (vars.length < 2) {
                player.sendMessage(ChatColor.YELLOW + "Usage: /bind r|l|lb|la|rb|ra <command>");
                return true;
            }
            command = join(" ", Arrays.copyOfRange(vars, 1, vars.length));
        } else {
            key = Key.ALL;
            command = join(" ", vars);
        }
        if (!canUseKey(player, key)) {
            player.sendMessage(ChatColor.RED + "You are not allowed to use this type of bind.");
            return true;
        }
        BindList list = getPlugin().getBinds(player);
        Bind oldbind = list.getBind(key, player.getItemInHand());
        if (oldbind != null) {
            getPlugin().getDatabase().delete(oldbind);
        }
        Bind bind = new Bind(player.getName(), key, command, player.getItemInHand());
        getPlugin().getDatabase().save(bind);
        list.add(bind);
        player.sendMessage(ChatColor.GREEN + "To item " + player.getItemInHand().getType().name().toLowerCase() + (key != Key.ALL ? "[" + key.name().toLowerCase() + "]" : "") + " command '" + command + "' was binded.");
        return true;
    }

    private boolean canUseKey(Player player, Key key) {
        switch (key) {
            case RIGHT_CLICK:
                if (canUseCommand(player, "easybind.bind.rightclick"))
                    return true;
                else
                    return false;
            case RIGHT_CLICK_BLOCK:
                if (canUseCommand(player, "easybind.bind.rightclickblock"))
                    return true;
                else
                    return false;
            case RIGHT_CLICK_AIR:
                if (canUseCommand(player, "easybind.bind.rightclickair"))
                    return true;
                else
                    return false;
            case LEFT_CLICK:
                if (canUseCommand(player, "easybind.bind.leftclick"))
                    return true;
                else
                    return false;
            case LEFT_CLICK_BLOCK:
                if (canUseCommand(player, "easybind.bind.leftclickblock"))
                    return true;
                else
                    return false;
            case LEFT_CLICK_AIR:
                if (canUseCommand(player, "easybind.bind.leftclickair"))
                    return true;
                else
                    return false;
            case ALL:
                if (canUseCommand(player, "easybind.bind.all"))
                    return true;
                else
                    return false;
        }
        return false;
    }

    private static String join(String del, String[] set) {
        String rt = "";
        for (String i : set) {
            rt += del + i;
        }
        if (rt.length() > del.length())
            ;
        rt = rt.substring(del.length());

        return rt;
    }

}

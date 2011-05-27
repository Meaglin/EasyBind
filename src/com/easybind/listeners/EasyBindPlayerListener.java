package com.easybind.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;

import com.easybind.EasyBind;
import com.easybind.EasyBindConfig;
import com.easybind.model.BindList;
import com.easybind.model.Key;
import com.easybind.persistence.Bind;

public class EasyBindPlayerListener extends PlayerListener {

    private final EasyBind plugin;

    public EasyBindPlayerListener(EasyBind plugin) {
        this.plugin = plugin;
    }

    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        if (event.getItem() == null)
            return;
        Player player = event.getPlayer();
        BindList list = plugin.getBinds(player);

        Bind bind = null;
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                bind = list.getBind(Key.RIGHT_CLICK_BLOCK, event.getItem());
                break;
            case LEFT_CLICK_BLOCK:
                bind = list.getBind(Key.LEFT_CLICK_BLOCK, event.getItem());
                break;
            case RIGHT_CLICK_AIR:
                bind = list.getBind(Key.RIGHT_CLICK_AIR, event.getItem());
                break;
            case LEFT_CLICK_AIR:
                bind = list.getBind(Key.LEFT_CLICK_AIR, event.getItem());
                break;
            default:
                return; // Stop anything if it is a other action.
        }
        if (bind == null) {
            switch (event.getAction()) {
                case RIGHT_CLICK_BLOCK:
                case RIGHT_CLICK_AIR:
                    bind = list.getBind(Key.RIGHT_CLICK, event.getItem());
                    break;
                case LEFT_CLICK_BLOCK:
                case LEFT_CLICK_AIR:
                    bind = list.getBind(Key.LEFT_CLICK, event.getItem());
                    break;
            }
        }
        if (bind == null) {
            bind = list.getBind(Key.ALL, event.getItem());
        }

        if (bind != null) {
            long delay = list.getLastUse() + EasyBindConfig.USE_DELAY - System.currentTimeMillis();
            if (delay > 0 && !plugin.getPermissions().canUse(player, "easybind.delayoverride")) {
                if (delay > 1000) {
                    int seconds = (int) Math.ceil(delay / 1000);
                    player.sendMessage(ChatColor.RED + "Please wait " + seconds + " second" + (seconds != 1 ? "s" : "") + " before using a bind again.");
                } else {
                    player.sendMessage(ChatColor.RED + "Please wait a moment until you can use a bind again");
                }
                return;
            }
            if (EasyBindConfig.ALLOW_MULTILINE_COMMANDS) {
                for (String cmd : bind.getCommand().split(EasyBindConfig.LINE_SEPERATOR))
                    player.chat(cmd);
            } else {
                player.chat(bind.getCommand());
            }
            list.setLastUse(System.currentTimeMillis());
            event.setCancelled(true);
        }
    }

    public void onPlayerLogin(org.bukkit.event.player.PlayerLoginEvent event) {
        plugin.registerPlayer(event.getPlayer());
    }

    public void onPlayerQuit(org.bukkit.event.player.PlayerEvent event) {
        plugin.forgetPlayer(event.getPlayer());
    }
}

package com.easybind.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.easybind.EasyBind;
import com.easybind.model.BindList;
import com.easybind.model.Key;
import com.easybind.persistence.Bind;

public class ListBindsCommand extends EasyBindCommand {

    public ListBindsCommand(EasyBind plugin) {
        super("listbinds", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {
        BindList list = getPlugin().getBinds(player);
        if(vars.length > 0) {
            if(vars[0].equalsIgnoreCase("current")) {
                if(player.getItemInHand() == null) {
                    player.sendMessage(ChatColor.RED + "You have no item in your hand.");
                    return true;
                }
                Bind[] binds = new Bind[Key.values().length];
                for(int i = 0; i < Key.values().length;i++) {
                    binds[i] = list.getBind(Key.values()[i], player.getItemInHand());
                }
                sendList(player, binds, 0);
            } else {
                int i = -1;
                try {
                    i = Integer.parseInt(vars[0]);
                } catch(NumberFormatException e) {
                }
                if(i < 0) {
                    player.sendMessage(ChatColor.RED + "Invalid page number.");
                    return true;
                }
                sendList(player, list.getBinds(), i*7);
            }
            return true;
        }
        sendList(player, list.getBinds(), 0);
        return true;
    }
    
    private void sendList(Player player, Bind[] binds, int start) {
        if(binds.length < start) {
            player.sendMessage(ChatColor.DARK_GREEN + "Binds " + start + "/" + binds.length + ":");
            player.sendMessage(ChatColor.GREEN + "None.");
            return;
        }
        int end = start + 7;
        if(end > binds.length) end = binds.length;
        Bind[] displayList = Arrays.copyOfRange(binds, start, end);
        player.sendMessage(ChatColor.DARK_GREEN + "Binds " + start + "/" + binds.length + ":");
        for(Bind b : displayList) {
            if(b != null)
                player.sendMessage(b.toString());
        }
    }

}

package com.easybind.commands.general;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.easybind.EasyBind;
import com.easybind.commands.EasyBindCommand;
import com.easybind.model.BindList;
import com.easybind.model.Key;
import com.easybind.persistence.Bind;

public class UnBindCommand extends EasyBindCommand {

    public UnBindCommand(EasyBind plugin) {
        super("unbind", plugin);
    }

    @Override
    public boolean run(Player player, String[] vars) {

        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You need to have something equipped to be able to unbind.");
            return true;
        }

        Key key = null;
        if (vars.length > 0)
            key = Key.byName(vars[0]);
        if (key == null)
            key = Key.ALL;
        BindList list = getPlugin().getBinds(player);
        Bind bind = list.getBind(key, player.getItemInHand());
        if (bind == null) {
            player.sendMessage(ChatColor.YELLOW + "No bind found.");
            return true;
        }
        getPlugin().getDatabase().delete(bind);
        list.removeBind(bind);
        player.sendMessage(ChatColor.GREEN + "Unbind succesfull.");
        return true;
    }

}

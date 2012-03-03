package com.easybind;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;

import com.easybind.commands.EasyBindCommand;
import com.easybind.commands.EasyBindCommandMap;
import com.easybind.model.BindList;
import com.easybind.model.Key;
import com.easybind.permissions.Permissions;
import com.easybind.permissions.PermissionsResolver;
import com.easybind.persistence.Bind;
import com.easybind.util.FileUtil;

public class EasyBind extends JavaPlugin implements Listener {

    public static final int              Rev            = 14;
    protected static final Logger        log            = Logger.getLogger("Minecraft");
    private final EasyBindCommandMap     commandMap     = new EasyBindCommandMap(this);

    private Permissions                  permissionsManager;
    private Map<Integer, BindList>       bindMap        = new HashMap<Integer, BindList>();


    private void setupDatabase() {
        try {
            getDatabase().find(Bind.class).findRowCount();
        } catch (PersistenceException ex) {
            System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Bind.class);
        return list;
    }

    @Override
    public void onDisable() {
        log.info("[EasyBind]plugin disabled!");
    }

    @Override
    public void onEnable() {

        File configFile = new File(getDataFolder().getPath() + "/" + EasyBindConfig.EASYBIND_CONFIG_FILE);
        if (!configFile.exists()) {
            getDataFolder().mkdirs();
            if (FileUtil.copyFile(EasyBind.class.getResourceAsStream("/com/easybind/config/EasyBind.properties"), configFile)) {
                log.info("[EasyBind]Missing configuration file restored.");
            } else {
                log.info("[EasyBind]Error while restorting configuration file.");
            }
        }
        EasyBindConfig.load(configFile);
        resolvePermissions();
        setupDatabase();

        getServer().getPluginManager().registerEvents(this, this);
        
        bindMap.clear();
        for(Player player : getServer().getOnlinePlayers()) registerPlayer(player);
        log.info("[EasyBind]Rev " + Rev + " Loaded, Permissions: " + permissionsManager.getName() + ".");
    }

    private void resolvePermissions() {
        permissionsManager = PermissionsResolver.resolve(this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        Action action = event.getAction();
        if(action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) event.setCancelled(false);
        
        if (event.isCancelled())        return;
        if (event.getItem() == null)    return;
        
        Player player = event.getPlayer();
        BindList list = getBinds(player);

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
            if (delay > 0 && !getPermissions().canUse(player, "easybind.override.delay")) {
                if (delay > 1000) {
                    int seconds = (int) Math.ceil(delay / 1000);
                    player.sendMessage(ChatColor.RED + "Please wait " + seconds + " second" + (seconds != 1 ? "s" : "") + " before using a bind again.");
                } else {
                    player.sendMessage(ChatColor.RED + "Please wait a moment until you can use a bind again");
                }
                return;
            }
            if (EasyBindConfig.ALLOW_MULTILINE_COMMANDS) {
                for (String cmd : bind.getCommand().split(EasyBindConfig.LINE_SEPERATOR)) {
                    if(cmd.startsWith("event:")) {
                        getServer().getPluginManager().callEvent(new EasyBindEvent(event, cmd.substring(6)));
                        return;
                    } else {
                        player.chat(cmd);
                    }
                }
            } else {
                if(bind.getCommand().startsWith("event:")) {
                    getServer().getPluginManager().callEvent(new EasyBindEvent(event, bind.getCommand().substring(6)));
                    return;
                } else {
                    player.chat(bind.getCommand());
                }
            }
            list.setLastUse(System.currentTimeMillis());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(org.bukkit.event.player.PlayerLoginEvent event) {
        registerPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        forgetPlayer(event.getPlayer());
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EasyBindCommand cmd = commandMap.getCommand(command.getName());
        if (cmd != null) {
            return cmd.execute(sender, label, args);
        }
        return false;
    }

    public Permissions getPermissions() {
        return permissionsManager;
    }

    public BindList registerPlayer(Player player) {
        List<Bind> binds = getDatabase().find(Bind.class).where().ieq("playername", player.getName()).findList();
        BindList bindlist = new BindList(player);
        bindlist.addAll(binds);
        bindMap.put(player.getEntityId(), bindlist);
        return bindlist;
    }

    public void forgetPlayer(Player player) {
        bindMap.remove(player.getEntityId());
    }

    public BindList getBinds(Player player) {
        BindList list = bindMap.get(player.getEntityId());
        if (list == null) {
            list = registerPlayer(player);
        }
        return list;
    }

}

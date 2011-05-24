package com.easybind;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.easybind.commands.EasyBindCommand;
import com.easybind.commands.EasyBindCommandMap;
import com.easybind.listeners.EasyBindPlayerListener;
import com.easybind.model.BindList;
import com.easybind.permissions.BukkitPermissions;
import com.easybind.permissions.NijiPermissions;
import com.easybind.permissions.Permissions;
import com.easybind.persistence.Bind;
import com.easybind.util.FileUtil;

public class EasyBind extends JavaPlugin {

    public static final int              Rev            = 1;
    protected static final Logger        log            = Logger.getLogger("Minecraft");
    private final EasyBindPlayerListener playerListener = new EasyBindPlayerListener(this);
    private final EasyBindCommandMap     commandMap     = new EasyBindCommandMap(this);

    private Permissions                  permissionsManager;
    private Map<Integer, BindList>       bindMap        = new HashMap<Integer, BindList>();

    private void registerEvents() {
        registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.High);
    }

    /**
     * Register an event.
     * 
     * @param type
     * @param listener
     * @param priority
     */
    private void registerEvent(Event.Type type, Listener listener, Priority priority) {
        getServer().getPluginManager().registerEvent(type, listener, priority, this);
    }

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
        log.info("[EasyBind]Rev " + Rev + "  Loading...");

        File configFile = new File(getDataFolder().getPath() + "/" + EasyBindConfig.EASYBIND_CONFIG_FILE);
        if (!configFile.exists()) {
            getDataFolder().mkdirs();
            if (FileUtil.copyFile(EasyBind.class.getResourceAsStream("/com/easybind/config/EasyBind.properties"), configFile)) {
                log.info("[EasyBind]Missing configuration file restored.");
            } else {
                log.info("[EasyBind]Error while restorting configuration file.");
            }
        }
        resolvePermissions();
        setupDatabase();
        EasyBindConfig.load(configFile);
        registerEvents();
        log.info("[EasyBind]finished Loading.");

    }

    private void resolvePermissions() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Permissions");
        if (plugin != null && plugin instanceof com.nijikokun.bukkit.Permissions.Permissions) {
            if (!plugin.isEnabled()) {
                getPluginLoader().enablePlugin(plugin);
            }
            permissionsManager = new NijiPermissions((com.nijikokun.bukkit.Permissions.Permissions) plugin);
            log.info("[Zones]Using Nijikokun Permissions for permissions managing.");
        } else {
            permissionsManager = new BukkitPermissions();
            log.info("[Zones]Using built in isOp() for permissions managing.");
        }
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

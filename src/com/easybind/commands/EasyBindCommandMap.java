package com.easybind.commands;

import java.util.HashMap;
import java.util.logging.Logger;

import com.easybind.EasyBind;
import com.easybind.commands.general.*;

public class EasyBindCommandMap {
    private HashMap<String, EasyBindCommand> commands = new HashMap<String, EasyBindCommand>(); ;
    private EasyBind                         plugin;
    protected static final Logger            log      = Logger.getLogger("Minecraft");

    public EasyBindCommandMap(EasyBind plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        commands.clear();
        registerCommand(new BindCommand(plugin));
        registerCommand(new UnBindCommand(plugin));
        registerCommand(new ListBindsCommand(plugin));
    }

    public void registerCommand(EasyBindCommand cmd) {
        commands.put(cmd.getName(), cmd);
        if (cmd.getAliases() != null && cmd.getAliases().size() > 0) {
            for (String str : cmd.getAliases()) {
                if (commands.containsKey(str))
                    log.info("[EasyBind] " + cmd.getName() + " tryes to register " + str + " but it's already taken by " + commands.get(str).getName());
                else
                    commands.put(str, cmd);
            }
        }
    }

    public EasyBindCommand getCommand(String name) {
        return commands.get(name);
    }

    public boolean commandExists(String name) {
        return commandExists(name, true);
    }

    public boolean commandExists(String name, boolean aliasAllowed) {
        if (aliasAllowed)
            return commands.containsKey(name);
        else
            return (commands.containsKey(name) && commands.get(name).getName().equals(name));
    }
}

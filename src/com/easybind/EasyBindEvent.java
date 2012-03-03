package com.easybind;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@SuppressWarnings("serial")
public class EasyBindEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    
    private PlayerInteractEvent event;
    private String name;
    
    public EasyBindEvent(PlayerInteractEvent event, String name) {
        this.event = event;
        this.name = name;
    }

    public PlayerInteractEvent getTriggerEvent() {
        return event;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Action getAction() {
        return getTriggerEvent().getAction();
    }
    
    public Player getPlayer() {
        return getTriggerEvent().getPlayer();
    }
    
    @Override
    public boolean isCancelled() {
        return getTriggerEvent().isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        getTriggerEvent().setCancelled(cancel);
    }

    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

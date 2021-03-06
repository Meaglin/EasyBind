package com.easybind.model;

import gnu.trove.TIntObjectHashMap;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.easybind.persistence.Bind;

public class BindList {

    private TIntObjectHashMap<Bind> binds = new TIntObjectHashMap<Bind>();
    private final Player            player;
    private long                    lastUse;

    public BindList(Player player) {
        this.player = player;
    }

    public long getLastUse() {
        return lastUse;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }

    public void add(Bind bind) {
        binds.put(getIndex(bind), bind);
    }

    public void addAll(List<Bind> binds) {
        for (Bind bind : binds)
            add(bind);
    }

    public void removeBind(Bind bind) {
        binds.remove(getIndex(bind));
    }

    public Bind getBind(Key key, ItemStack item) {
        return binds.get(getIndex(key, item));
    }
    
    public Bind[] getBinds() {
        return binds.getValues(new Bind[binds.size()]);
    }

    private int getIndex(Key key, ItemStack item) {
        return getIndex(key, item.getTypeId(), item.getDurability());
    }

    private int getIndex(Bind bind) {
        return getIndex(bind.getKeybind(), bind.getItemid(), bind.getItemdata());
    }

    /*
     * Due to the fact that the maximum size of itemid and itemdata is 16 bit
     * and 4 bits respectively we can blend them all together into 1 integer.
     */
    private int getIndex(Key key, int itemid, int itemdata) {
        int index = itemid;
        index += ((itemdata & 0xF) << 16);
        index += (key.ordinal() << 24);
        return index;
    }

    public boolean equals(Object o) {
        if (!(o instanceof BindList)) {
            return false;
        }

        return (player.getEntityId() == ((BindList) o).player.getEntityId());
    }
}

package com.easybind.model;

import org.bukkit.inventory.ItemStack;

import com.easybind.persistence.Bind;

public class BindKey {

    private final Key key;

    private final int itemid;
    private final int itemdata;

    public BindKey(Key key, int itemid, int itemdata) {
        this.key = key;
        this.itemid = itemid;
        this.itemdata = itemdata;
    }

    public BindKey(Key key, ItemStack item) {
        this(key, item.getTypeId(), item.getDurability());
    }

    public BindKey(Bind bind) {
        this(bind.getKeybind(), bind.getItemid(), bind.getItemdata());
    }

    public Key getKey() {
        return key;
    }

    public int getItemid() {
        return itemid;
    }

    public int getItemdata() {
        return itemdata;
    }

    public boolean equals(Object o) {
        if (!(o instanceof BindKey))
            return false;
        BindKey k = (BindKey) o;

        return k.getKey() == getKey() && k.getItemid() == getItemid() && k.getItemdata() == getItemdata();
    }

    public int hashcode() {
        int hash = 23;
        hash = hash * 37 + getKey().ordinal();
        hash = hash * 37 + getItemid();
        hash = hash * 37 + getItemdata();
        return hash;
    }

}

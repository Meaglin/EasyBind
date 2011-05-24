package com.easybind.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.validation.NotEmpty;
import com.easybind.model.Key;

@Entity
@Table(name = "easybind_binds")
public class Bind {

    @Id
    private int    id;

    @NotEmpty
    private String playername;

    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String command;

    @Enumerated(value = EnumType.STRING)
    private Key    key;

    private int    itemid;
    private int    itemdata;

    public Bind() {

    }

    public Bind(String name, Key key, String command, ItemStack itemInHand) {
        setPlayername(name);
        setKey(key);
        setCommand(command);
        setItemid(itemInHand.getTypeId());
        /*
         * Bukkit has no proper way to do this :/.
         */
        switch (itemInHand.getTypeId()) {
            case 6: // Sapplings
            case 17: // Logs
            case 35: // Wool
            case 43: // DoubleSteps
            case 44: // HalfSteps
            case 263: // Coal
            case 351: // Dye
                setItemdata(itemInHand.getDurability());
            default:
                setItemdata(0);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public int getItemdata() {
        return itemdata;
    }

    public void setItemdata(int itemdata) {
        this.itemdata = itemdata;
    }

}

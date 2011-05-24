package com.easybind.model;

public enum Key {
    ALL, // Full bind.
    RIGHT_CLICK, // ...
    LEFT_CLICK, // ...
    RIGHT_CLICK_BLOCK, // ...
    LEFT_CLICK_BLOCK, // ...
    RIGHT_CLICK_AIR, // ...
    LEFT_CLICK_AIR;// ...

    public static Key byName(String name) {
        name = name.toLowerCase();
        if (name.equals("r") || name.equals("right"))
            return Key.RIGHT_CLICK;
        else if (name.equals("l") || name.equals("left"))
            return Key.LEFT_CLICK;
        else if (name.equals("lb") || name.equals("leftblock"))
            return Key.LEFT_CLICK_BLOCK;
        else if (name.equals("la") || name.equals("leftair"))
            return Key.LEFT_CLICK_AIR;
        else if (name.equals("rb") || name.equals("rightblock"))
            return Key.RIGHT_CLICK_BLOCK;
        else if (name.equals("ra") || name.equals("rightair"))
            return Key.RIGHT_CLICK_AIR;
        else
            return null;
    }
}

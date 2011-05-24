package com.easybind;

import java.io.File;
import java.util.logging.Logger;

import com.easybind.util.Properties;

public class EasyBindConfig {
    public static final String EASYBIND_CONFIG_FILE = "EasyBind.properties";

    private static Logger      log                  = Logger.getLogger(EasyBindConfig.class.getName());

    public static boolean      ALLOW_MULTILINE_COMMANDS;
    public static String       LINE_SEPERATOR;
    public static int          USE_DELAY;

    public static void load(File f) {
        try {
            Properties ebp = new Properties(f);
            ALLOW_MULTILINE_COMMANDS = ebp.getBool("AllowMultiLineCommands", true);
            LINE_SEPERATOR = ebp.getProperty("LineSeperator", "\n");
            USE_DELAY = ebp.getInt("UseDelay", 1000);
        } catch (Exception e) {
            log.warning("[EasyBind]Error loading configurations.");
            e.printStackTrace();
        }
    }
}

package ru.justnero.minecraft.bukkit.mprotect;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

/**
 *
 * @author Nero
 */
public class UtilLog {
    
    public static boolean infoEnabled = true;
    public static boolean warningEnabled = true;
    public static boolean errorEnabled = true;
    public static boolean debugEnabled = true;
    private static final java.util.logging.Logger _log = java.util.logging.Logger.getLogger("Minecraft");
    
    public static void info(String... list) {
        if(infoEnabled) {
            _log.info(getString(list));
        }
    }
    
    public static void warning(String... list) {
        if(warningEnabled) {
            _log.warning(getString(list));
        }
    }
    
    public static void error(String... list) {
        if(errorEnabled) {
            _log.severe(getString(list));
        }
    }
    
    public static void error(Throwable ex) {
        if(errorEnabled) {
            StringWriter error = new StringWriter();
            ex.printStackTrace(new PrintWriter(error));
            Scanner scan = new Scanner(error.toString());
            scan.useDelimiter("\n");
            while(scan.hasNext()) {
                _log.severe(scan.nextLine());
            }
        }
    }
    
    public static void debug(String... list) {
        if(debugEnabled) {
            _log.info("---DEBUG---");
            _log.info(getString(list));
            _log.info("---DEBUG---");
        }
    }
    
    public static void debug(Throwable ex) {
        if(debugEnabled) {
            StringWriter error = new StringWriter();
            ex.printStackTrace(new PrintWriter(error));
            Scanner scan = new Scanner(error.toString());
            scan.useDelimiter("\n");
            _log.info("---DEBUG---");
            while(scan.hasNext()) {
                _log.info(scan.nextLine());
            }
            _log.info("---DEBUG---");
        }
    }
    
    public static String getString(String[] list) {
        StringBuilder sb = new StringBuilder("[mProtect] ");
        for(String part : list) {
            sb.append(part);
        }
        return sb.toString();
    }
    
    
}

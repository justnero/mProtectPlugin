package ru.justnero.minecraft.bukkit.mprotect.protect;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Nero
 */
public class ProtectConfig {
    
    public static Map<String,ProtectConfig> list = new TreeMap<String,ProtectConfig>();
    
    public final String name;
    public String prefix;
    public int resizeTill;
    public ProtectSize size;
    public ProtectSize resize;
    public String[] flags;
    public String[] worlds;
    
    public ProtectConfig(String name) {
        this.name = name.toLowerCase();
        this.prefix = this.name+"_";
        this.resizeTill = 0;
        this.resize = new ProtectSize();
        this.size = new ProtectSize();
        this.flags = new String[]{};
        this.worlds = new String[]{};
        list.put(this.name,this);
    }
    
    public ProtectConfig(String name, String prefix, int resizeTill, ProtectSize resize, ProtectSize size, String[] flags, String[] worlds) {
        this.name = name.toLowerCase();
        this.prefix = prefix.toLowerCase();
        this.resizeTill = resizeTill;
        this.resize = resize;
        this.size = size;
        this.flags = flags;
        this.worlds = worlds;
        list.put(this.name,this);
    }
    
}

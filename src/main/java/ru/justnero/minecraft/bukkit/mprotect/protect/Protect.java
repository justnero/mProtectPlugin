package ru.justnero.minecraft.bukkit.mprotect.protect;

/**
 *
 * @author Nero
 */
public class Protect {
    
    public final String name;
    public ProtectConfig config;
    public ProtectData data;
    
    public Protect(String name, ProtectConfig config) {
        this.name = name;
        this.config = config;
        this.data = new ProtectData();
    }
    
    public Protect(String name, ProtectConfig config, ProtectData data) {
        this.name = name;
        this.config = config;
        this.data = data;
    }
    
}

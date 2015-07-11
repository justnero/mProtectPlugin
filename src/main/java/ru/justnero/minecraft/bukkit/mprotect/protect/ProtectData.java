package ru.justnero.minecraft.bukkit.mprotect.protect;

/**
 *
 * @author Nero
 */
public class ProtectData {
    
    public boolean isSet;
    public String world;
    public Point3D center;
    public ProtectSize size;
    public String prefix;
    
    public ProtectData() {
        this.isSet = false;
        this.world = null;
        this.center = null;
        this.size = null;
        this.prefix = null;
    }
    
    public ProtectData(boolean isSet, String world, Point3D center, ProtectSize size, String prefix) {
        this.isSet = isSet;
        this.world = world;
        this.center = center;
        this.size = size;
        this.prefix = prefix;
    }
    
}

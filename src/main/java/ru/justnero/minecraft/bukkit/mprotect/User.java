package ru.justnero.minecraft.bukkit.mprotect;

import java.util.Map;
import java.util.TreeMap;

import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectConfig;

/**
 *
 * @author Nero
 */
public class User {
    
    public  final String name;
    public final Map<String,Protect> protects = new TreeMap<String,Protect>();
    
    public User(String name) {
        this.name = name;
        protects.put("default",new Protect("default",ProtectConfig.list.get("default")));
    }
    
    public User(String name, Map<String,Protect> protects) {
        this.name = name;
        this.protects.putAll(protects);
    }
    
    public void clearProtects() {
        protects.clear();
        protects.put("default",new Protect("default",ProtectConfig.list.get("default")));
    }
    
}

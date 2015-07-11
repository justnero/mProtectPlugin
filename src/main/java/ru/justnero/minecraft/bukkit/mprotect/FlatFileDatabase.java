package ru.justnero.minecraft.bukkit.mprotect;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.configuration.file.YamlConfiguration;

import ru.justnero.minecraft.bukkit.mprotect.protect.Point3D;
import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectConfig;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectData;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectSize;

import static ru.justnero.minecraft.bukkit.mprotect.UtilLog.*;

/**
 *
 * @author Nero
 */
public class FlatFileDatabase extends IDatabase {
    
    protected File file;
    protected YamlConfiguration list;
    protected Map<String,User> cache = new TreeMap<String,User>();
    
    @Override
    public boolean instalize() {
        try {
            this.file = new File("plugins/mProtect/users.yml");
            this.list = new YamlConfiguration();
            fileCheck();
        } catch(Exception ex) {
            error(ex);
            return true;
        }
        return true;
    }
    
    @Override
    public User get(String name) {
        name = name.toLowerCase();
        if(cache.containsKey(name)) {
            return cache.get(name);
        }
        try {
            fileCheck();
            list = new YamlConfiguration();
            list.load(file);
        } catch (Exception ex) {
            error(ex);
            return null;
        }
        if(!list.isSet(name)) {
            User user = new User(name);
            if(!set(new User(name))){
                return null;
            } else {
                return user;
            }
        }
        Map<String,Protect> protects = new TreeMap<String,Protect>();
        Set<String> userSection = list.getConfigurationSection(name).getKeys(false);
        for(String protect : userSection) {
            String config = list.getString(name+"."+protect+".config","default");
            ProtectConfig pConfig;
            if(ProtectConfig.list.containsKey(config)) {
                pConfig = ProtectConfig.list.get(config);
            } else {
                pConfig = new ProtectConfig(config,config+"_",0,new ProtectSize(),new ProtectSize(),new String[]{},new String[]{});
            }
            boolean isSet = list.getBoolean(name+"."+protect+".data.isSet",false);
            String world = list.getString(name+"."+protect+".data.world",null);
            Point3D point = parsePoint(list.getString(name+"."+protect+".data.center","null"));
            ProtectSize size = parseSize(list.getString(name+"."+protect+".data.size","null"));
            String prefix = list.getString(name+"."+protect+".data.prefix",null);
            protects.put(protect,new Protect(protect,pConfig,new ProtectData(isSet,world,point,size,prefix)));
        }
        if(!protects.containsKey("default")) {
            protects.put("default",new Protect("default",ProtectConfig.list.get("default"),new ProtectData()));
        }
        return new User(name,protects);
    }
    
    @Override
    public boolean set(User user) {
        if(cache.containsKey(user.name)) {
            cache.put(user.name,user);
        }
        try {
            fileCheck();
            list = new YamlConfiguration();
            list.load(file);
        } catch (Exception ex) {
            error(ex);
            return false;
        }
        list.set(user.name,null);
        for(Entry<String,Protect> entry : user.protects.entrySet()) {
            String pName = entry.getKey();
            Protect protect = entry.getValue();
            list.set(user.name+"."+pName+".config",protect.config.name);
            list.set(user.name+"."+pName+".data.prefix",protect.data.prefix);
            list.set(user.name+"."+pName+".data.isSet",protect.data.isSet);
            if(protect.data.world != null) {
                list.set(user.name+"."+pName+".data.world",protect.data.world);
            }
            if(protect.data.center != null) {
                list.set(user.name+"."+pName+".data.center",buildPoint(protect.data.center));
            }
            if(protect.data.size != null) {
                list.set(user.name+"."+pName+".data.size",buildSize(protect.data.size));
            }
        }
        cache.put(user.name,user);
        try {
            list.save(file);
            return true;
	} catch(Exception ex) {
            error("Error saving user to database");
            error(ex);
            return false;
	}   
    }
    
    @Override
    public void removeFromCache(String name) {
        cache.remove(name.toLowerCase());
    }
    
    @Override
    public void clearCache() {
        cache.clear();
    }
    
    protected void fileCheck() throws Exception {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
	}
    }
    
}

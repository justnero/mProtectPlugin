package ru.justnero.minecraft.bukkit.mprotect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.InvalidConfigurationException;

import org.bukkit.configuration.file.YamlConfiguration;

import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectConfig;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectSize;

/**
 *
 * @author Nero
 */
public class Config {

    private static final File file = new File("plugins/mProtect/config.yml");
    private static final YamlConfiguration config = new YamlConfiguration();
    
    public static boolean isOnlineMod = false;
    public static boolean isChatMod = false;
    public static List<String> autogive = Collections.EMPTY_LIST;
    public static String language = "en";
    
    public static void load() throws IOException, InvalidConfigurationException {
        fileCheck();
        config.load(file);
        
        autogive = config.getStringList("autogive");
        language = config.getString("language",language);
        isChatMod = config.getBoolean("modules.chat",isChatMod);
        isOnlineMod = config.getBoolean("modules.online",isOnlineMod);
        ProtectConfig.list.clear();
        Set<String> configSelection = config.getConfigurationSection("protects").getKeys(false);
        for(String protect : configSelection) {
            String protectPath = "protects."+protect;
            String prefix = config.getString(protectPath+".prefix",protectPath+"_");
            int resizeTill = config.getInt(protectPath+".resizeTill",0);
            ProtectSize resize  = new ProtectSize(new int[]{0,0,0});
            if(resizeTill > 0) {
                resize = getSize(protectPath+".resize");
            }
            ProtectSize size = getSize(protectPath+".size");
            String[] flags = getStrings(protectPath+".flags");
            String[] worlds = getStrings(protectPath+".worlds");
            new ProtectConfig(protect,prefix,resizeTill,resize,size,flags,worlds);
        }
    }
    
    protected static ProtectSize getSize(String path) {
        List<Integer> tmp = config.getIntegerList(path);
        return new ProtectSize(new int[]{tmp.get(0),tmp.get(1),tmp.get(2)});
    }
    
    protected static String[] getStrings(String path) {
        List<String> tmp = config.getStringList(path);
        return tmp.toArray(new String[tmp.size()]);
    }
    
    protected static void fileCheck() throws IOException {
        if(!file.exists()){
            InputStream resourceAsStream = Bootstrap.class.getResourceAsStream("/config.yml");
            file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buff = new byte[65536];
            int n;
            while((n = resourceAsStream.read(buff)) > 0){
                fos.write(buff,0,n);
                fos.flush();
            }
            fos.close();
            buff = null;
        }
    }
    
}

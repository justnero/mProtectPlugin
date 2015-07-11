package ru.justnero.minecraft.bukkit.mprotect;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.IOException;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ru.justnero.minecraft.bukkit.mprotect.command.*;

import static ru.justnero.minecraft.bukkit.mprotect.Language.*;
import static ru.justnero.minecraft.bukkit.mprotect.UtilLog.*;

/**
 *
 * @author Nero
 */
public class Bootstrap extends JavaPlugin {
    
    public static Permission permission = null;
    public static WorldEditPlugin WE;
    public static WorldGuardPlugin WG;
    public static Server server;
    public static IDatabase db;
    public static JavaPlugin instance;
    
    @Override
    public void onEnable(){
        instance = this;
        server = getServer();
        if(!registerPerms() || !registerWE() || !registerWG() || !loadConfig() || !loadLanguage()) {
            server.getPluginManager().disablePlugin(this);
            return;
        }
        db = new FlatFileDatabase(); //@TODO more databases, I need moreeeeee
        db.instalize();
        startMetrics();
        server.getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("rset").setExecutor(new SetCommand());
        getCommand("radd").setExecutor(new AddCommand());
        getCommand("rdel").setExecutor(new DeleteCommand());
        getCommand("rflag").setExecutor(new FlagCommand());
        getCommand("rrem").setExecutor(new RemoveCommand());
        getCommand("rinfo").setExecutor(new InfoCommand());
        getCommand("mc").setExecutor(new ChatCommand());
        getCommand("mprotect").setExecutor(new AdminCommand());
        info("Everything is ready, capitan. Engage!");
    }
    
    @Override
    public void onDisable(){
        info("I am disabling, see you next time");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    public static boolean hasPerm(CommandSender sender, String perm){
        return permission.has(sender,"mprotect."+perm);
    }
    
    private boolean registerPerms() {
        RegisteredServiceProvider<Permission> permissionProvider = server.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = (Permission) permissionProvider.getProvider();
            info("Vault loaded.");
            return true;
        }
        error("Vault does not appear to be installed");
        server.getPluginManager().disablePlugin(this);
        return false;
    }
    
    private boolean registerWE() {
        Plugin wePlugin = server.getPluginManager().getPlugin("WorldEdit");
        if(wePlugin == null) {
            error("WorldEdit does not appear to be installed.");
        } else if(wePlugin instanceof WorldEditPlugin) {
            Bootstrap.WE = (WorldEditPlugin) wePlugin;
            info("WorldEdit loaded.");
            return true;
        } else {
            error("WorldEdit detection failed (report error).");
        }
        return false;
    }
    
    private boolean registerWG() {
        Plugin wgPlugin = server.getPluginManager().getPlugin("WorldGuard");
        if(wgPlugin == null) {
            error("WorldGuard does not appear to be installed.");
        } else if(wgPlugin instanceof WorldGuardPlugin) {
            Bootstrap.WG = (WorldGuardPlugin) wgPlugin;
            info("WorldGuard loaded.");
            return true;
        } else {
            error("WorldGuard detection failed (report error).");
        }
        return false;
    }
    
    private boolean loadConfig() {
        try {
            Config.load();
        } catch(Exception ex) {
            error("Error loadign configuration, check it please or remove and I will restore default");
            error(ex);
            return false;
        }
        return true;
    }
    
    private boolean loadLanguage() {
        try {
            langLoad(Config.language);
        } catch(Exception ex) {
            error("Error loadign languange, check plugin is valid and report developer if it is");
            error(ex);
            return false;
        }
        return true;
    }
    
    private boolean startMetrics() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            info("Metrics loaded.");
        } catch (IOException ex) {
            error("Metrics load error.");
            error(ex);
            return false;
        }
        return true;
    }
    
}

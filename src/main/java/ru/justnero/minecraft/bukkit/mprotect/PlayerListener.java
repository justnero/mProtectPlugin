package ru.justnero.minecraft.bukkit.mprotect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectConfig;

import static ru.justnero.minecraft.bukkit.mprotect.UtilLog.*;

/**
 * 
 * @author Nero
 */
public class PlayerListener implements Listener {
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        User cachedUser = Bootstrap.db.get(event.getPlayer().getName());
        for(String protect : Config.autogive) {
            if(!cachedUser.protects.containsKey(protect) && ProtectConfig.list.containsKey(protect)) {
                cachedUser.protects.put(protect,new Protect(protect,ProtectConfig.list.get(protect)));
            }
        }
        info("Cacheed user "+event.getPlayer().getName());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bootstrap.db.removeFromCache(event.getPlayer().getName());
        info("Uncacheed user "+event.getPlayer().getName());
    }
    
}

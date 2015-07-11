package ru.justnero.minecraft.bukkit.mprotect.command;

import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;

import org.bukkit.entity.Player;

import ru.justnero.minecraft.bukkit.mprotect.Bootstrap;
import ru.justnero.minecraft.bukkit.mprotect.User;
import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;

import static ru.justnero.minecraft.bukkit.mprotect.Language.*;
import static ru.justnero.minecraft.bukkit.mprotect.UtilLog.*;

/**
 *
 * @author Nero
 */
public class RemoveCommand extends BasicCommand {
    
    static {
        requiredPermissions = new String[]{"user","remove"};
    }
    
    @Override
    public boolean execute(final Player player, String[] args) {
        String protectName = "default";
        if(args.length == 1) {
            protectName = args[0].toLowerCase();
        } else if(args.length > 1) {
            player.sendMessage(langGet("wrongArguments"));
            player.sendMessage(langGet("commandRemove"));
            return true;
        }
        User user = Bootstrap.db.get(player.getName());
        if(!user.protects.containsKey(protectName)) {
            player.sendMessage(langGet("notAvailableProtect"));
            return true;
        }
        Protect protect = user.protects.get(protectName);
        if(!protect.data.isSet) {
            player.sendMessage(langGet("protectNotSet"));
            return true;
        }
        if(!player.getWorld().getName().equalsIgnoreCase(protect.data.world)) {
            player.sendMessage(langGet("protectWrongWorld",protect.data.world));
            return true;
        }
        String regionName = protect.data.prefix+player.getName().toLowerCase();
        RegionManager mgr = Bootstrap.WG.getGlobalRegionManager().get(player.getWorld());
        if(!mgr.hasRegion(regionName)) {
            player.sendMessage(langGet("protectNotSet"));
            player.sendMessage(langGet("protectDataError"));
            return true;
        }
        protect.data.isSet = false;
        protect.data.world = null;
        protect.data.center = null;
        protect.data.size = null;
        mgr.removeRegion(regionName);
        try {
            mgr.save();
            Bootstrap.db.set(user);
            player.sendMessage(langGet("protectRemoved"));
        } catch (ProtectionDatabaseException ex) {
            player.sendMessage(langGet("protectSaveError"));
            error("Database save error");
            error(ex);
            return true;
        }
        return true;
    }
    
}

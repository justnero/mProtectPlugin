package ru.justnero.minecraft.bukkit.mprotect.command;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.justnero.minecraft.bukkit.mprotect.Bootstrap;
import ru.justnero.minecraft.bukkit.mprotect.User;
import ru.justnero.minecraft.bukkit.mprotect.protect.Point3D;
import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;

import static ru.justnero.minecraft.bukkit.mprotect.Language.*;
import static ru.justnero.minecraft.bukkit.mprotect.UtilLog.*;

/**
 *
 * @author Nero
 */
public class SetCommand extends BasicCommand {
    
    static {
        requiredPermissions = new String[]{"user","set"};
    }
    
    @Override
    public boolean execute(final Player player, String[] args) {
        String protectName = "default";
        if(args.length == 1) {
            protectName = args[0].toLowerCase();
        } else if(args.length > 1) {
            player.sendMessage(langGet("wrongArguments"));
            player.sendMessage(langGet("commandSet"));
            return true;
        }
        User user = Bootstrap.db.get(player.getName());
        if(!user.protects.containsKey(protectName)) {
            player.sendMessage(langGet("notAvailableProtect"));
            return true;
        }
        Protect protect = user.protects.get(protectName);
        if(protect.data.isSet) {
            player.sendMessage(langGet("protectAlreadySet"));
            return true;
        }
        if(!isPlayerInWorld(player,protect.config.worlds)) {
            player.sendMessage(langGet("protectWrongWorld",concatenate(protect.config.worlds)));
            return true;
        }
        String regionName = protect.config.prefix+player.getName().toLowerCase();
        LocalPlayer localPlayer = Bootstrap.WG.wrapPlayer(player);
        RegionManager mgr = Bootstrap.WG.getGlobalRegionManager().get(player.getWorld());
        if(mgr.hasRegion(regionName)) {
            player.sendMessage(langGet("protectAlreadySet"));
            player.sendMessage(langGet("protectDataError"));
            return true;
        }
        Location centerWE = player.getLocation();
        protect.data.isSet = true;
        protect.data.world = player.getWorld().getName().toLowerCase();
        protect.data.center = new Point3D(centerWE.getBlockX(),centerWE.getBlockZ(),centerWE.getBlockY());
        protect.data.size = protect.config.size.clone();
        protect.data.prefix = protect.config.prefix;
        int maxX = protect.data.center.pointX+protect.data.size.north;
        int maxY = protect.data.center.pointY+protect.data.size.east;
        int maxZ = 255;
        int minX = protect.data.center.pointX-protect.data.size.south;
        int minY = protect.data.center.pointY-protect.data.size.west;
        int minZ = 0;
        if(1+protect.data.size.up+protect.data.size.down < 256) {
            if(protect.data.center.pointZ-protect.data.size.down < 0) {
                maxZ = Math.min(1+protect.data.size.up+protect.data.size.down,255);
            } else if(protect.data.center.pointZ+protect.data.size.up >= 256) {
                minZ = Math.max(254-protect.data.size.up-protect.data.size.down,0);
            }
        }
        final BlockVector max = new BlockVector(maxX,maxZ,maxY);
        final BlockVector min = new BlockVector(minX,minZ,minY);
        ProtectedRegion region = new ProtectedCuboidRegion(regionName,min,max);
        ApplicableRegionSet regions = mgr.getApplicableRegions(region);
        if(regions.size() > 0) {
            if(!regions.canBuild(localPlayer) || containsProtect(regions)) {
                player.sendMessage(langGet("protectOverlaps"));
                return true;
            }
        }
        DefaultDomain owner = new DefaultDomain();
        owner.addPlayer(player.getName());
        region.setOwners(owner);
        mgr.addRegion(region);
        try {
            mgr.save();
            Bootstrap.db.set(user);
            player.sendMessage(langGet("protectCreated"));
        } catch (ProtectionDatabaseException ex) {
            player.sendMessage(langGet("protectSaveError"));
            error("Database save error");
            error(ex);
            return true;
        }
        return true;
    }
    
    private boolean containsProtect(ApplicableRegionSet regions) {
        ProtectedRegion region;
        while(regions.iterator().hasNext()) {
            region = regions.iterator().next();
            if(region.getPriority() >= 0) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isPlayerInWorld(Player player, String[] worlds) {
        if(worlds.length == 0) {
            return true;
        }
        String currentWorld = player.getWorld().getName();
        for(String world : worlds) {
            if(world.equalsIgnoreCase(currentWorld))
                return true;
        }
        return false;
    }
    
    private String concatenate(String[] list) {
        StringBuilder sb = new StringBuilder();
        for(String element : list) {
            if(sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(element);
        }
        return sb.toString();
    }

}

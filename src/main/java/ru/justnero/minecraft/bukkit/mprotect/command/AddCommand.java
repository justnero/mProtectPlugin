package ru.justnero.minecraft.bukkit.mprotect.command;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Iterator;

import org.bukkit.entity.Player;

import ru.justnero.minecraft.bukkit.mprotect.Bootstrap;
import ru.justnero.minecraft.bukkit.mprotect.Config;
import ru.justnero.minecraft.bukkit.mprotect.User;
import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectSize;

import static ru.justnero.minecraft.bukkit.mprotect.Language.*;
import static ru.justnero.minecraft.bukkit.mprotect.UtilLog.*;

/**
 *
 * @author Nero
 */
public class AddCommand extends BasicCommand {
    
    static {
        requiredPermissions = new String[]{"user","add"};
    }
    
    @Override
    public boolean execute(final Player player, String[] args) {
        String protectName = "default";
        String playerToAdd;
        if(args.length == 1) {
            playerToAdd = args[0].toLowerCase();
        } else if(args.length == 2) {
            protectName = args[0].toLowerCase();
            playerToAdd = args[1].toLowerCase();
        } else {
            player.sendMessage(langGet("wrongArguments"));
            player.sendMessage(langGet("commandAdd"));
            return true;
        }
        if(playerToAdd.equalsIgnoreCase(player.getName())) {
            player.sendMessage(langGet("protectAddHimSelf"));
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
        boolean isFakePlayer = playerToAdd.startsWith("[") && playerToAdd.endsWith("]");
        if(Config.isOnlineMod) {
            Player member = Bootstrap.server.getOfflinePlayer(playerToAdd).getPlayer();
            if((member == null || !member.isOnline()) && !(isFakePlayer)) {
                player.sendMessage(langGet("protectMemberOffline"));
                return true;
            }
        }
        ProtectedRegion region = mgr.getRegion(regionName);
        DefaultDomain members = isFakePlayer ? region.getOwners() : region.getMembers();
        if(members.contains(playerToAdd)) {
            player.sendMessage(langGet("protectMemberAlready"));
            return true;
        }
        members.addPlayer(playerToAdd);
        if(isFakePlayer) {
            region.setOwners(members);
        } else {
            region.setMembers(members);
        }
        try {
            mgr.save();
            if(!isFakePlayer && members.size() <= protect.config.resizeTill) { 
                protect.data.size = resizeProtect(player,protect,members.size(),regionName,mgr);
            }
            Bootstrap.db.set(user);
            player.sendMessage(langGet("protectAdded"));
        } catch (ProtectionDatabaseException ex) {
            player.sendMessage(langGet("protectSaveError"));
            error("Database save error");
            error(ex);
            return true;
        }
        return true;
    }
    
    private ProtectSize resizeProtect(Player player, Protect protect, int members, String regionName, RegionManager mgr) {
        int maxX = protect.data.center.pointX+protect.config.size.north+protect.config.resize.north*members;
        int maxY = protect.data.center.pointY+protect.config.size.east+protect.config.resize.east*members;
        int maxZ = 255;
        int minX = protect.data.center.pointX-protect.config.size.south-protect.config.resize.south*members;
        int minY = protect.data.center.pointY-protect.config.size.west-protect.config.resize.west*members;
        int minZ = 0;
        if(1+protect.config.size.up+protect.config.size.down+(protect.config.resize.up+protect.config.resize.down)*members < 256) {
            if(protect.data.center.pointZ-protect.config.size.down-protect.config.resize.down*members < 0) {
                maxZ = Math.min(1+protect.config.size.up+protect.config.size.down+(protect.config.resize.up+protect.config.resize.down)*members,255);
            } else if(protect.data.center.pointZ+protect.config.size.up+protect.config.resize.up*members >= 256) {
                minZ = Math.max(254-protect.config.size.up-protect.config.size.down-(protect.config.resize.up+protect.config.resize.down)*members,0);
            }
        }
        final BlockVector max = new BlockVector(maxX,maxZ,maxY);
        final BlockVector min = new BlockVector(minX,minZ,minY);
        ProtectedRegion existing = mgr.getRegion(regionName);
        ProtectedRegion region = new ProtectedCuboidRegion(regionName,min,max);
        ApplicableRegionSet regions = mgr.getApplicableRegions(region);
        LocalPlayer localPlayer = Bootstrap.WG.wrapPlayer(player);
        if(regions.size() > 0) {
            if(!regions.canBuild(localPlayer) || containsProtect(regions,regionName)) {
                player.sendMessage(langGet("protectOverlapsCantResize"));
                return protect.data.size;
            }
        }
        region.setMembers(existing.getMembers());
        region.setOwners(existing.getOwners());
        region.setFlags(existing.getFlags());
        region.setPriority(existing.getPriority());
        mgr.addRegion(region);
        try {
            mgr.save();
            player.sendMessage(langGet("protectResized"));
        } catch (ProtectionDatabaseException e) {
            player.sendMessage(langGet("protectResizeSaveError"));
            error("Database save error");
            return protect.data.size;
        }
        int[] size = new int[] {
            protect.config.size.north+protect.config.resize.north*members,
            protect.config.size.south+protect.config.resize.south*members,
            protect.config.size.west+protect.config.resize.west*members,
            protect.config.size.east+protect.config.resize.east*members,
            protect.config.size.up+protect.config.resize.up*members,
            protect.config.size.down+protect.config.resize.down*members,
        };
        return new ProtectSize(size);
    }
    
    private boolean containsProtect(ApplicableRegionSet regions, String regionName) {
        ProtectedRegion region;
        Iterator<ProtectedRegion> iterator = regions.iterator();
        while(iterator.hasNext()) {
            region = iterator.next();
            if(!region.getId().toLowerCase().equals(regionName.toLowerCase()) && region.getPriority() >= 0) {
                return true;
            }
        }
        return false;
    }
    
}

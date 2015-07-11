package ru.justnero.minecraft.bukkit.mprotect.command;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Set;

import org.bukkit.entity.Player;

import ru.justnero.minecraft.bukkit.mprotect.Bootstrap;
import ru.justnero.minecraft.bukkit.mprotect.User;
import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;

import static ru.justnero.minecraft.bukkit.mprotect.Language.*;

/**
 *
 * @author Nero
 */
public class InfoCommand extends BasicCommand {
    
    static {
        requiredPermissions = new String[]{"user","info"};
    }
    
    @Override
    public boolean execute(final Player player, String[] args) {
        String protectName = "default";
        if(args.length == 1) {
            protectName = args[0].toLowerCase();
        } else if(args.length != 0) {
            player.sendMessage(langGet("wrongArguments"));
            player.sendMessage(langGet("commandInfo"));
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
        ProtectedRegion region = mgr.getRegion(regionName);
        
        final DefaultDomain members = region.getMembers();
        final DefaultDomain fakeplayers = region.getOwners();
        boolean hasFlags = false;
        final StringBuilder s = new StringBuilder();
        for (Flag<?> flag : DefaultFlag.getFlags()) {
            Object val = region.getFlag(flag);
            if(val == null) 
                continue;
            if(s.length() > 0) 
                s.append(", ");
            s.append(flag.getName()).append(": ").append(String.valueOf(val));
            hasFlags = true;
        }
        player.sendMessage(langGet("protectInfoName",regionName));
        if (hasFlags) {
            player.sendMessage(langGet("protectInfoFlags",s.toString()));
        }
        if (members.size() != 0) {
            player.sendMessage(langGet("protectInfoMembers",members.toPlayersString()));
        } else {
            player.sendMessage(langGet("protectInfoNoMembers"));
        }
        if (fakeplayers.size() > 1) {
            player.sendMessage(langGet("protectInfoFakePlayers",getFakePlayersList(fakeplayers.getPlayers(),player.getName())));
        } else {
            player.sendMessage(langGet("protectInfoNoFakePlayers"));
        }
        final BlockVector min = region.getMinimumPoint();
        final BlockVector max = region.getMaximumPoint();
        player.sendMessage(langGet("protectInfoBounds", 
            min.getBlockX()+","+min.getBlockY()+","+min.getBlockZ(),
            max.getBlockX()+","+max.getBlockY()+","+max.getBlockZ()));
        player.sendMessage(langGet("protectInfoSize",protect.data.size.toString()));
        return true;
    }
    
    private String getFakePlayersList(Set<String> players, String owner){
        String str = "";
        String[] array = toStringArray(players.toArray());
        for(String fakeplayer : array) {
            if(!fakeplayer.equalsIgnoreCase(owner)) {
                if(!"".equals(str))
                    str += ", ";
                str += fakeplayer;
            }
        }
        
        return str;
    }
    
    private String[] toStringArray(Object[] array){
        String[] res = new String[array.length];
        for(int i=0;i<array.length;i++) {
            res[i] = String.valueOf(array[i]);
        }
        return res;
    }
    
}

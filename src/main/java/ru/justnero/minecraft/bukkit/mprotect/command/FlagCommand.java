package ru.justnero.minecraft.bukkit.mprotect.command;

import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.command.CommandSender;
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
public class FlagCommand extends BasicCommand {
    
    static {
        requiredPermissions = new String[]{"user","flag"};
    }
    
    @Override
    public boolean execute(final Player player, String[] args) {
        String protectName = "default";
        String flag = null;
        String state = null;
        if(args.length == 2) {
            protectName = args[0].toLowerCase();
            flag = args[1].toLowerCase();
        } else if(args.length >= 3) {
            protectName = args[0].toLowerCase();
            flag = args[1].toLowerCase();
            state = sliceToString(args,2,args.length-1);
        } else {
            player.sendMessage(langGet("wrongArguments"));
            player.sendMessage(langGet("commandFlag"));
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
        if(protect.config.flags.length == 0) {
            player.sendMessage(langGet("protectNoAvailableFlags"));
            return true;
        }
        if(!contains(flag,protect.config.flags)) {
            player.sendMessage(langGet("protectFlagNotAvailable"));
            return true;
        }
        if(!player.getWorld().getName().equalsIgnoreCase(protect.data.world)) {
            player.sendMessage(langGet("protectWrongWorld",protect.data.world));
            return true;
        }
        Flag<?> foundFlag = null;
        for(Flag<?> flg : DefaultFlag.getFlags()) {
            if(flg.getName().replace("-", "").equalsIgnoreCase(flag.replace("-", ""))) {
                foundFlag = flg;
                break;
            }
        }
        if(foundFlag == null) {
            player.sendMessage(langGet("protectFlagNotAvailable"));
            return true;
        }
        String regionName = protect.config.prefix+player.getName().toLowerCase();
        RegionManager mgr = Bootstrap.WG.getGlobalRegionManager().get(player.getWorld());
        if(!mgr.hasRegion(regionName)) {
            player.sendMessage(langGet("protectNotSet"));
            player.sendMessage(langGet("protectDataError"));
            return true;
        }
        ProtectedRegion region = mgr.getRegion(regionName);
        
        if(state != null) {
            if(state.startsWith("G:")) {
                String group = state.replaceAll("G:", "");
                if(foundFlag.getRegionGroupFlag() == null) {
                    player.sendMessage(langGet("protectFlagNotGroup"));
                    return true;
                }
                try {
                    setFlag(region,foundFlag.getRegionGroupFlag(),sender, group);
                } catch(InvalidFlagFormat e) {
                    player.sendMessage(langGet("protectFlagWrongFormat"));
                    return true;
                }
            } else {
                try {
                    setFlag(region,foundFlag,sender,state);
                } catch(InvalidFlagFormat e) {
                    player.sendMessage(langGet("protectFlagWrongFormat"));
                    return true;
                }
            }
        } else {
            region.setFlag(foundFlag, null);
        }
        
        try {
            mgr.save();
            Bootstrap.db.set(user);
            player.sendMessage(langGet(state == null ? "protectFlagCleared" : "protectFlagSet"));
        } catch (ProtectionDatabaseException ex) {
            player.sendMessage(langGet("protectSaveError"));
            error("Database save error");
            error(ex);
            return true;
        }
        return true;
    }
    
    private <V> void setFlag(ProtectedRegion region, Flag<V> flag, CommandSender sender, String value) throws InvalidFlagFormat {
        region.setFlag(flag,flag.parseInput(Bootstrap.WG,sender,value));
    }
    
    private String sliceToString(String[] list, int start, int finish) {
        StringBuilder sb = new StringBuilder();
        for(int i=start;i<=finish;i++) {
            if(sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(list[i]);
        }
        return sb.toString();
    }
    
    private boolean contains(String needle, String[] list) {
        for(String element : list) {
            if(element.equalsIgnoreCase(needle)) {
                return true;
            }
        }
        return false;
    }

}

package ru.justnero.minecraft.bukkit.mprotect.command;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import ru.justnero.minecraft.bukkit.mprotect.Bootstrap;
import ru.justnero.minecraft.bukkit.mprotect.Config;
import ru.justnero.minecraft.bukkit.mprotect.Language;
import ru.justnero.minecraft.bukkit.mprotect.User;
import ru.justnero.minecraft.bukkit.mprotect.UtilLog;
import ru.justnero.minecraft.bukkit.mprotect.protect.Protect;

import static ru.justnero.minecraft.bukkit.mprotect.Language.*;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectConfig;

/**
 *
 * @author Nero
 */
public class AdminCommand extends BasicCommand {
    
    static {
        requiredPermissions = new String[]{"admin"};
    }

    @Override
    public boolean execute(final Player player, String[] args) {
        switch(args.length) {
            case 0:
                actionVersion(player);
                break;
            case 1:
                switch(args[0]) {
                    case "reload":
                    case "r":
                        actionReload(player);
                        break;
                    default:
                        player.sendMessage(langGet("wrongArguments"));
                        break;
                }
                break;
            case 2:
                switch(args[0]) {
                    case "user":
                    case "u":
                        actionUser(player,args[1],null);
                        break;
                    default:
                        player.sendMessage(langGet("wrongArguments"));
                        break;
                }
                break;
            case 3:
                switch(args[0]) {
                    case "user":
                    case "u":
                        actionUser(player,args[1],args[2]);
                        break;
                    case "give":
                    case "g":
                        actionGive(player,args[1],args[2],null);
                        break;
                    case "take":
                    case "t":
                        actionTake(player,args[1],args[2]);
                        break;
                    default:
                        player.sendMessage(langGet("wrongArguments"));
                        break;
                }
                break;
            case 4:
                switch(args[0]) {
                    case "give":
                    case "g":
                        actionGive(player,args[1],args[2],args[3]);
                        break;
                    default:
                        player.sendMessage(langGet("wrongArguments"));
                        break;
                }
                break;
            default:
                player.sendMessage(langGet("wrongArguments"));
                break;
        }
        return true;
    }
    
    private void actionVersion(final Player player) {
        player.sendMessage(langGet("separator","VERSION"));
        PluginDescriptionFile desc = Bootstrap.instance.getDescription();
        player.sendMessage(langGet("adminVersionName",desc.getName(),desc.getVersion()));
        player.sendMessage(langGet("adminVersionSite",desc.getWebsite()));
        if (!desc.getAuthors().isEmpty()) {
            if (desc.getAuthors().size() == 1) {
                player.sendMessage(langGet("adminVersionAuthor",getAuthors(desc.getAuthors())));
            } else {
                player.sendMessage(langGet("adminVersionAuthors",getAuthors(desc.getAuthors())));
            }
        }
    }
    
    private void actionReload(final Player player) {
        player.sendMessage(langGet("separator","RELOAD"));
        try {
            Config.load();
            player.sendMessage(langGet("adminReloadConfigSuccess"));
        } catch(Exception ex) {
            player.sendMessage(langGet("adminReloadConfigError"));
            UtilLog.error("Error reloading configurations");
            UtilLog.error(ex);
            return;
        }
        
        try {
            Language.langLoad(Config.language);
            player.sendMessage(langGet("adminReloadLanguageSuccess"));
        } catch(Exception ex) {
            player.sendMessage(langGet("adminReloadLanguageError"));
            UtilLog.error("Error reloading language");
            UtilLog.error(ex);
            return;
        }
        
        try {
            Bootstrap.db.clearCache();
            for(Player user : Bootstrap.server.getOnlinePlayers()) {
                Bootstrap.db.get(user.getName());
            }
            player.sendMessage(langGet("adminReloadCacheSuccess"));
        } catch(Exception ex) {
            player.sendMessage(langGet("adminReloadCacheError"));
            UtilLog.error("Error reloading cache");
            UtilLog.error(ex);
        }
    }
    
    private void actionUser(final Player player, String userName, String protectName) {
        User user = Bootstrap.db.get(userName);
        if(protectName == null) {
            player.sendMessage(langGet("separator","USER"));
            player.sendMessage(langGet("adminUserName",user.name,user.protects.size()));
            for(Protect protect : user.protects.values()) {
                player.sendMessage(langGet("adminUserProtect"+(protect.data.isSet ? "Is" : "Not")+"Set",protect.name));
            }
        } else {
            player.sendMessage(langGet("separator","PROTECT"));
            if(user.protects.containsKey(protectName)) {
                Protect protect = user.protects.get(protectName);
                player.sendMessage(langGet("adminUserProtect"+(protect.data.isSet ? "Is" : "Not")+"Set",protect.name));
                if(protect.data.isSet) {
                    player.sendMessage(langGet("adminUserProtectRegion",protect.data.prefix+userName.toLowerCase()));
                    player.sendMessage(langGet("adminUserProtectCenter",protect.data.center));
                    player.sendMessage(langGet("adminUserProtectSize",protect.data.size));
                    player.sendMessage(langGet("adminUserProtectWorld",protect.data.world));
                }
            } else {
                player.sendMessage(langGet("adminUserNoProtect"));
            }
        }
    }
    
    private void actionGive(final Player player, String userName, String protectName, String configName) {
        if(configName == null) {
            configName = protectName;
        }
        User user = Bootstrap.db.get(userName);
        if(!user.protects.containsKey(protectName)) {
            if(ProtectConfig.list.containsKey(configName)) {
                user.protects.put(protectName,new Protect(protectName,ProtectConfig.list.get(configName)));
                Bootstrap.db.set(user);
                player.sendMessage(langGet("adminGiveSuccess"));
            } else {
                player.sendMessage(langGet("adminGiveNoConfig"));
            }
        } else {
            player.sendMessage(langGet("adminGiveHave"));
        }
    }
    
    private void actionTake(final Player player, String userName, String protectName) {
        if(protectName.equalsIgnoreCase("default")) {
            player.sendMessage(langGet("adminTakeDefault"));
            return;
        }
        User user = Bootstrap.db.get(userName);
        if(user.protects.containsKey(protectName)) {
            if(!user.protects.get(protectName).data.isSet) {
                user.protects.remove(protectName);
                Bootstrap.db.set(user);
                player.sendMessage(langGet("adminTakeSuccess"));
            } else {
                player.sendMessage(langGet("adminTakeSet"));
            }
        } else {
            player.sendMessage(langGet("adminTakeNo"));
        }
    }
    
    private String getAuthors(final List<String> authors) {
        StringBuilder result = new StringBuilder();
        for(String author : authors) {
            if (result.length() > 0) {
                result.append(ChatColor.WHITE);
                result.append(", ");
            }
            result.append(ChatColor.GRAY);
            result.append(author);
        }
        return result.toString();
    }
    
}

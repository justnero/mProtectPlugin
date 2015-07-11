package ru.justnero.minecraft.bukkit.mprotect.command;

import org.bukkit.entity.Player;

import ru.justnero.minecraft.bukkit.mprotect.Bootstrap;
import ru.justnero.minecraft.bukkit.mprotect.Config;

import static ru.justnero.minecraft.bukkit.mprotect.UtilLog.*;
import static ru.justnero.minecraft.bukkit.mprotect.Language.*;

/**
 *
 * @author Nero
 */
public class ChatCommand extends BasicCommand {
    
    static {
        requiredPermissions = new String[]{"chat"};
    }
    
    @Override
    public boolean execute(final Player player, String[] args) {
        if(!Config.isChatMod) {
            player.sendMessage(langGet("chatDisabled"));
            return true;
        }
        if(args.length == 0) {
            player.sendMessage(langGet("chatEmpty"));
            return true;
        }
        String text = concatenate(args);
        String message = langGet("chatMessage",player.getName(),text);
        for(Player p : Bootstrap.server.getOnlinePlayers()) {
            if(Bootstrap.hasPerm(p,"chat")) {
                p.sendMessage(message);
            }
        }
        info("Player ",player.getName()," sent message to chat: ",text);
        return true;
    }
    
    private String concatenate(String[] list) {
        StringBuilder sb = new StringBuilder();
        for(String element : list) {
            if(sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(element);
        }
        return sb.toString();
    }

}

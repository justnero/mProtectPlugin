package ru.justnero.minecraft.bukkit.mprotect;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.bukkit.ChatColor;

/**
 *
 * @author Nero
 */
public class Language {
    
    private static ResourceBundle lang;
    private static final transient Map<String, MessageFormat> messageFormatCache = new HashMap<String, MessageFormat>();
    private static final String baseName = "message";
    
    public static boolean langLoad(String code) throws Exception {
        messageFormatCache.clear();
        try {
            lang = ResourceBundle.getBundle(baseName,Locale.forLanguageTag(code));
        } catch (MissingResourceException e) {
            lang = ResourceBundle.getBundle(baseName);
        }
        return true;
    }

    public static String langGet(final String key){
        return ChatColor.DARK_AQUA+"[mProtect] "+ChatColor.WHITE+langGetRaw(key);
    }
    
    public static String langGet(final String key, final Object... objects) {
        return ChatColor.DARK_AQUA+"[mProtect] "+ChatColor.WHITE+langGetRaw(key,objects);
    }

    public static String langGetRaw(final String key){
        try {
            return lang.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
    
    public static String langGetRaw(final String key, final Object... objects) {
        final String format = langGetRaw(key);
        MessageFormat messageFormat = messageFormatCache.get(format);
        if(messageFormat == null) {
            messageFormat = new MessageFormat(format);
            messageFormatCache.put(format, messageFormat);
        }
        return messageFormat.format(objects);
    }

}
    

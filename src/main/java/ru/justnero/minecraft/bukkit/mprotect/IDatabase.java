package ru.justnero.minecraft.bukkit.mprotect;

import ru.justnero.minecraft.bukkit.mprotect.protect.Point3D;
import ru.justnero.minecraft.bukkit.mprotect.protect.ProtectSize;

/**
 *
 * @author Nero
 */
public abstract class IDatabase {
    
    public abstract boolean instalize();
    
    public abstract User get(String name);
    
    public abstract boolean set(User user);
    
    public abstract void removeFromCache(String name);
    
    public abstract void clearCache();
    
    public String buildPoint(Point3D point) {
        if(point == null) {
            return "null";
        }
        StringBuilder returnValue = new StringBuilder();
        returnValue.append(point.pointX);
        returnValue.append(".");
        returnValue.append(point.pointY);
        returnValue.append(".");
        returnValue.append(point.pointZ);
        return returnValue.toString();
    }
    
    public Point3D parsePoint(String point) {
        if(point.equals("null")) {
            return null;
        }
        String[] list = point.split("\\.");
        return new Point3D(Integer.valueOf(list[0]),Integer.valueOf(list[1]),Integer.valueOf(list[2]));
    }
    
    protected String buildSize(ProtectSize size) {
        if(size == null) {
            return "null";
        }
        StringBuilder returnValue = new StringBuilder();
        returnValue.append(size.north);
        returnValue.append(".");
        returnValue.append(size.south);
        returnValue.append(".");
        returnValue.append(size.west);
        returnValue.append(".");
        returnValue.append(size.east);
        returnValue.append(".");
        returnValue.append(size.up);
        returnValue.append(".");
        returnValue.append(size.down);
        return returnValue.toString();
    }
    
    public ProtectSize parseSize(String size) {
        if(size.equals("null")) {
            return null;
        }
        String[] list = size.split("\\.");
        int[] converted = new int[list.length];
        for(int i=0;i<list.length;i++) {
            converted[i] = Integer.parseInt(list[i]);
        }
        return new ProtectSize(converted);
    }
    
}

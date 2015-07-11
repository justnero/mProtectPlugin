package ru.justnero.minecraft.bukkit.mprotect.protect;

/**
 *
 * @author Nero
 */
public class Point3D {
    
    public final int pointX;
    public final int pointY;
    public final int pointZ;
    
    public Point3D(int x, int y, int z) {
        pointX = x;
        pointY = y;
        pointZ = z;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(pointX);
        sb.append(", ");
        sb.append(pointY);
        sb.append(", ");
        sb.append(pointZ);
        return sb.toString();
    }
    
    @Override
    public Point3D clone() {
        return new Point3D(pointX,pointY,pointZ);
    }
    
}

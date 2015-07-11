package ru.justnero.minecraft.bukkit.mprotect.protect;

/**
 *
 * @author Nero
 */
public class ProtectSize {
    
    public int north    = 0;
    public int south    = 0;
    public int west     = 0;
    public int east     = 0;
    public int up       = 0;
    public int down     = 0;
    
    public ProtectSize() {
        
    }
    
    public ProtectSize(int[] size) {
        if(size.length == 6) {
            north = size[0];
            south = size[1];
            west = size[2];
            east = size[3];
            up = size[4];
            down = size[5];
        } else if(size.length == 3) {
            north = size[0]/2+size[0]%2;
            south = size[0]/2;
            west = size[1]/2+size[1]%2;
            east = size[1]/2;
            up = size[2]/2+size[2]%2;
            down = size[2]/2;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(north+south);
        sb.append(", ");
        sb.append(west+east);
        sb.append(", ");
        sb.append(up+down);
        return sb.toString();
    }
    
    @Override
    public ProtectSize clone() {
        return new ProtectSize(new int[]{north,south,west,east,up,down});
    }
    
}

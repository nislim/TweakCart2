package net.tweakcraft.tweakcart.util;

import net.tweakcraft.tweakcart.model.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;


public class VehicleUtil {

    public static void moveCart(Minecart cart, int x, int y, int z) {
        Location location = cart.getLocation();
        if(x != 0){
            location.setX(x);
        }
        
        if(y != 0){
            location.setY(y);
        }
        
        if(z != 0){
            location.setZ(z);
        }
        
        cart.teleport(location);
    }
    
    public static void moveCartRelative(Minecart cart, double x, double y, double z){
        cart.teleport(cart.getLocation().add(x, y, z));
    }
    
    public static void moveCart(Minecart cart, Location loc){
        cart.teleport(loc);
    }

    //TODO: should a cart be spawnable on something different then a track?
    //Long live method overloading

    private static boolean spawnCartWithVelocity(Location location, Material type, Direction dir, double velocity) {
        if (isMinecart(type) && canSpawn(location.getBlock())) {
            Minecart cart;
            switch (type) {
                case MINECART:
                    cart = location.getWorld().spawn(location, Minecart.class);
                    break;
                case STORAGE_MINECART:
                    cart = location.getWorld().spawn(location, StorageMinecart.class);
                    break;
                case POWERED_MINECART:
                    cart = location.getWorld().spawn(location, PoweredMinecart.class);
                    break;
                default:
                    return false;
            }

            cart.setVelocity(dir.mod(velocity > cart.getMaxSpeed() ? cart.getMaxSpeed() : velocity));
        } else {
            return false;
        }
        return true;
    }

    /**
     * Spawns a cart on a block, with a given direction
     * wont check for rails, just spawns the cart
     *
     * @param b
     * @param type
     * @param dir
     */
    public static boolean spawnCart(Block b, Material type, Direction dir) {
        return spawnCartWithVelocity(b.getLocation(), type, dir, Double.MAX_VALUE);
    }

    public static boolean spawnCartFromDispenser(Dispenser d, Material type, double velocity) {
        Block track;
        Direction dir;
        switch (d.getData().getData()) {
            case 0x2:
                track = d.getBlock().getRelative(BlockFace.EAST);
                dir = Direction.EAST;
                break;
            case 0x3:
                track = d.getBlock().getRelative(BlockFace.WEST);
                dir = Direction.WEST;
                break;
            case 0x4:
                track = d.getBlock().getRelative(BlockFace.NORTH);
                dir = Direction.NORTH;
                break;
            case 0x5:
                track = d.getBlock().getRelative(BlockFace.SOUTH);
                dir = Direction.SOUTH;
                break;
            default:
                dir = Direction.SELF;
                track = d.getBlock();
                break;
        }
        
        // als een cart op een poweredrail staat die niet aan is, zet de snelheid dan op 0
        if(track.getType() == Material.POWERED_RAIL && !track.isBlockPowered()){
            velocity = 0.0d;
        }
        
        return spawnCartWithVelocity(track.getLocation(), type, dir, velocity);
        
    }

    public static boolean spawnCartFromDispenser(Dispenser d, Material type) {
        return spawnCartFromDispenser(d, type, Double.MAX_VALUE);
    }

    public static boolean isMinecart(Material type) {
        return type == Material.MINECART || type == Material.POWERED_MINECART || type == Material.STORAGE_MINECART;
    }
    
    public static boolean canSpawn(Block b){
        switch(b.getType()){
        case RAILS:
        case POWERED_RAIL:
        case DETECTOR_RAIL:
            return true;
        default:
            return false;
        }
    }
}

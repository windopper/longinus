package utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class StandHandler {
    public ArmorStand getArmorStand(Location location) {
        ArmorStand ar = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        ar.setSmall(true);
        ar.setSilent(true);
        ar.setInvisible(true);
        ar.setCollidable(false);
        ar.setInvulnerable(true);
        return ar;
    }
    public boolean isCollideWithBlock(ArmorStand armorStand) {
        Location eye = armorStand.getEyeLocation();
        Vector v = eye.getDirection().normalize().multiply(0.5);
        if(eye.add(v).getBlock().getType().isSolid()) return true;
        return false;
    }
}

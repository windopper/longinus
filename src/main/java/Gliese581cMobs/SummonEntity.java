package Gliese581cMobs;

import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;

public class SummonEntity {

    public void summon(Player player) {
        Location loc = player.getEyeLocation();
        World nmsworld = ((CraftWorld) loc.getWorld()).getHandle();
        MouseFoot mouseFoot = new MouseFoot(nmsworld);
        mouseFoot.setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

}

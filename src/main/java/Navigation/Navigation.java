package Navigation;

import net.minecraft.world.entity.monster.EntityZombie;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftZombie;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class Navigation {

    private Location depart;
    private Location dest;
    private Player player;

    public Navigation(Location dest, Location depart) {
        this.dest = dest;
        this.depart = depart;
        this.player = player;
    }

    public void startNavigating() {
        Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
        EntityZombie eZ = ((CraftZombie) zombie).getHandle();
        eZ.getNavigation().q().a();
    }

}

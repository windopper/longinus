package EntityPlayerManager;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MoveController {

    EntityPlayer entityPlayer;
    MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
    WorldServer nmsWorld;

    public MoveController(EntityPlayer entityPlayer, String name) {

        World world = entityPlayer.getBukkitEntity().getWorld();

        nmsWorld = ((CraftWorld) world).getHandle();
        this.entityPlayer = entityPlayer;

    }

    public void setFollowingAI(Player player) {

        Location originalloc = entityPlayer.getBukkitEntity().getLocation();
        Location ploc = player.getLocation();
        Vector vec = ploc.clone().subtract(originalloc.clone()).toVector().normalize();
    }



}

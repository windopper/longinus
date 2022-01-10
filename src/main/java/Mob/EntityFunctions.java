package Mob;

import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityFunctions {

    public static void hideFromPlayer(Entity entity) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
            conn.sendPacket(new PacketPlayOutEntityDestroy(entity.getEntityId()));
        }
    }

}

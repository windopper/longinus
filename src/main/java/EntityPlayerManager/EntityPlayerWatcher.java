package EntityPlayerManager;

import Mob.EntityManager;
import Mob.MobListManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EntityPlayerWatcher {

    public static EntityManager EntityWrapper(EntityPlayer entityPlayer, LivingEntity WrappedEntity, MobListManager.MobList mobList) {
        HashMap<Entity, Location> disguises = new HashMap<>();
        disguises.put(entityPlayer.getBukkitEntity(), new Location(WrappedEntity.getWorld(), 0, 0, 0));
        return EntityManager.getinstance(WrappedEntity, mobList, disguises);
    }

    public static void Remove(EntityPlayer entityPlayer) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer);
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());

        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
            playerConnection.sendPacket(packetPlayOutPlayerInfo);
            playerConnection.sendPacket(packetPlayOutEntityDestroy);
        }

        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
                playerConnection.sendPacket(packetPlayOutPlayerInfo);
                playerConnection.sendPacket(packetPlayOutEntityDestroy);
            }
        }, 8);
        
    }

    public static void Remove(EntityPlayer entityPlayer, Player player) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, entityPlayer);
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());

        final PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
        playerConnection.sendPacket(packetPlayOutPlayerInfo);
        playerConnection.sendPacket(packetPlayOutEntityDestroy);

        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            playerConnection.sendPacket(packetPlayOutPlayerInfo);
            playerConnection.sendPacket(packetPlayOutEntityDestroy);
        }, 8);

    }

    public static void sendPacket(EntityPlayer entityPlayer, Packet<?> packet) {

        for(Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
            playerConnection.sendPacket(packet);
        }
    }
}

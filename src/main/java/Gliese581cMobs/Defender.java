package Gliese581cMobs;

import DynamicData.EntityManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import spellinteracttest.DummyNetworkManager;

import java.util.HashMap;
import java.util.UUID;

public class Defender extends EntityPlayer {

    public static HashMap<EntityPlayer, Villager> npclist = new HashMap<>();

    public Defender(MinecraftServer nmsServer, WorldServer nmsWorld, GameProfile profile) {
        super(nmsServer, nmsWorld, profile);

        //Player defender = (Player) this.getBukkitEntity();

        //this.getWorld().addEntity(this);

    }



    public static Defender createNPC(Player p, World world , Location location) {

        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "Defender");
        //PlayerInteractManager interactManager = new PlayerInteractManager(nmsWorld);
        Defender entityPlayer = new Defender(nmsServer, nmsWorld, profile);
        entityPlayer.b = new PlayerConnection(nmsServer, new DummyNetworkManager(EnumProtocolDirection.a), entityPlayer);

        Player defender = (Player) entityPlayer.getBukkitEntity();
        defender.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999, 10, false, false));
        defender.setGravity(true);

        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());

        nmsWorld.addEntity(entityPlayer);

        PacketPlayOutPlayerInfo playerInfoAdd = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, entityPlayer);
        PacketPlayOutNamedEntitySpawn namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((location.getYaw() * 256f) / 360f));
        PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.d, entityPlayer);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            connection.sendPacket(playerInfoAdd);
            connection.sendPacket(namedEntitySpawn);
            connection.sendPacket(headRotation);
            connection.sendPacket(playerInfoRemove);
        }

        EntityManager.getinstance(entityPlayer.getBukkitEntity());


        Villager v = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
        v.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 100, true, true));

        npclist.put(entityPlayer, v);


        new BukkitRunnable() {

            //Location original = entityPlayer.getBukkitEntity().getLocation();

            int count = 0;

            @Override
            public void run() {

                Location original = npclist.get(entityPlayer).getLocation();
                entityPlayer.setPosition(original.getX(), original.getY(), original.getZ());

                Location ploc = p.getLocation();

                Vector vec = ploc.clone().subtract(original.clone()).toVector().normalize().multiply(0.3);

                short x = (short) (4096 * vec.getX());
                short y = (short) (4096 * vec.getY());
                short z = (short) (4096 * vec.getZ());

                Location location = original.clone().setDirection(p.getLocation().subtract(original.clone()).toVector());

                byte yaw = (byte) (location.getYaw() * 256 / 360);
                byte pitch = (byte) (location.getPitch() * 256 / 360);

                double distance = original.distance(ploc);

                for (Player player : Bukkit.getOnlinePlayers()) {

                    PlayerConnection connection = ((CraftPlayer) player).getHandle().b;

                    PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(entityPlayer, yaw);
                    connection.sendPacket(headRotationPacket);

                    PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(
                            entityPlayer.getId(),
                            yaw,
                            pitch,
                            false);

                    connection.sendPacket(lookPacket);

//                    if(distance >= 5) {
//                        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(entityPlayer.getId(),
//                                x, y, z, yaw, pitch, true));
//
//                    }

                }

                entityPlayer.setPosition(original.getX(), original.getY(), original.getZ());


                if(count==2000) {
                    cancel();
                }
                count++;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

        return entityPlayer;
    }
}

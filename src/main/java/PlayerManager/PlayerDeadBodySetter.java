package PlayerManager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;

import java.util.UUID;

public class PlayerDeadBodySetter {

    private final int REMAINTICK = 600;

    private final String texture;
    private final String signature;
    private final Player player;
    private final GameProfile gameProfile;

    public PlayerDeadBodySetter(Player player) {
        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        this.texture = property.getValue();
        this.signature = property.getSignature();
        this.player = player;
        this.gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
    }

    public void init() {
        EntityPlayer Body = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(),
                gameProfile);

        Body.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()
        ,player.getLocation().getYaw(), player.getLocation().getPitch());
        Location bed = player.getLocation().add(0, 0, 0);
        Body.e(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));

        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard())
                .getHandle(), player.getName());
        PacketPlayOutScoreboardTeam score1 = PacketPlayOutScoreboardTeam.a(team);
        PacketPlayOutScoreboardTeam score2 = PacketPlayOutScoreboardTeam.a(team, true);
        PacketPlayOutScoreboardTeam score3 = PacketPlayOutScoreboardTeam.a(team, Body.getName(), PacketPlayOutScoreboardTeam.a.a);

        Body.setPose(EntityPose.c);

        DataWatcher watcher = Body.getDataWatcher();
        byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        watcher.set(DataWatcherRegistry.a.a(17), b);

        PacketPlayOutEntity.PacketPlayOutRelEntityMove move =
                new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                        Body.getId(), (byte) 0, (byte) ((player.getLocation().getY() - 1.7 - player.getLocation().getY()) * 32),
                        (byte) 0, false
                );

        byte yaw = (byte) (bed.getYaw() * 256/360);
        byte pitch = (byte) (bed.getPitch() * 256/360);
        PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(
                Body.getId(),
                yaw,
                pitch,
                false);

        PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(Body, yaw);


        for(Player on : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) on).getHandle().b;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, Body));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(Body));
            connection.sendPacket(score1);
            connection.sendPacket(score2);
            connection.sendPacket(score3);

            connection.sendPacket(new PacketPlayOutEntityMetadata(Body.getId(), watcher, true));
            connection.sendPacket(move);
//            connection.sendPacket(lookPacket);
//            connection.sendPacket(headRotationPacket);

            new BukkitRunnable() {
                @Override
                public void run() {
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, Body));
                }
            }.runTaskAsynchronously(Main.getPlugin(Main.class));
        }

        final EntityArmorStand bodyMarker = new BodyMarker(EntityTypes.c, ((CraftWorld) player.getWorld()).getHandle(),
                player.getName(), player.getLocation());

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            for(Player on : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) on).getHandle().b;
                connection.sendPacket(new PacketPlayOutEntityDestroy(Body.getId()));
            }
            Body.setRemoved(Entity.RemovalReason.a);
            bodyMarker.setRemoved(Entity.RemovalReason.a);
        }, REMAINTICK);


    }

    public class BodyMarker extends EntityArmorStand {

        public BodyMarker(EntityTypes<? extends EntityArmorStand> entitytypes, World world, String Name, Location loc) {
            super(entitytypes, world);

            ArmorStand bodymarker = (ArmorStand) this.getBukkitEntity();
            bodymarker.setCustomNameVisible(true);
            bodymarker.setInvisible(true);
            bodymarker.setInvulnerable(true);
            bodymarker.setCollidable(false);
            bodymarker.setCustomName("§c§o"+Name+"의 시체");
            bodymarker.setSmall(true);
            bodymarker.setGravity(false);
            bodymarker.addScoreboardTag(Name);
            world.addEntity(this);

            this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        }
    }
}

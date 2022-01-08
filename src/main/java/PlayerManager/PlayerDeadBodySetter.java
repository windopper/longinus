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
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.level.World;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDeadBodySetter {

    private final int REMAINTICK = 600;
    public static final ConcurrentHashMap<org.bukkit.entity.Entity, Player> playerMarker = new ConcurrentHashMap<>();

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
                .getHandle(), player.getName()+"a");
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

            new BukkitRunnable() {
                @Override
                public void run() {
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, Body));
                }
            }.runTaskAsynchronously(Main.getPlugin(Main.class));
        }

        final EntitySlime bodyMarker = new BodyMarker(EntityTypes.aD, ((CraftWorld) player.getWorld()).getHandle(),
                player.getName(), player.getLocation(), texture, signature, player);
        final EntityArmorStand bodyMarker_ = new BodyMarker1(EntityTypes.c, ((CraftWorld) player.getWorld()).getHandle(),
                player.getName(), player.getLocation(), texture, signature, player);
        Slime slime = (Slime) bodyMarker.getBukkitEntity();

        playerMarker.put(slime, player);

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                if(!playerMarker.containsKey(slime)) {
                    for(Player on : Bukkit.getOnlinePlayers()) {
                        PlayerConnection connection = ((CraftPlayer) on).getHandle().b;
                        connection.sendPacket(new PacketPlayOutEntityDestroy(Body.getId()));
                    }
                    Body.setRemoved(Entity.RemovalReason.a);
                    bodyMarker.setRemoved(Entity.RemovalReason.a);
                    bodyMarker_.setRemoved(Entity.RemovalReason.a);
                    slime.remove();
                    cancel();
                }
                if(time >= REMAINTICK) {
                    for(Player on : Bukkit.getOnlinePlayers()) {
                        PlayerConnection connection = ((CraftPlayer) on).getHandle().b;
                        connection.sendPacket(new PacketPlayOutEntityDestroy(Body.getId()));
                    }
                    Body.setRemoved(Entity.RemovalReason.a);
                    bodyMarker.setRemoved(Entity.RemovalReason.a);
                    bodyMarker_.setRemoved(Entity.RemovalReason.a);
                    playerMarker.remove(slime);
                    cancel();
                }
                time++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
    }

    public class BodyMarker extends EntitySlime {

        String texture = "";
        String signature = "";

        public BodyMarker(EntityTypes<? extends EntitySlime> entitytypes, World world, String Name, Location loc
         ,   String texture, String signature, Player player) {
            super(entitytypes, world);

            this.texture = texture;
            this.signature = signature;

            Slime bodymarker = (Slime) this.getBukkitEntity();
            bodymarker.setCustomNameVisible(true);
            bodymarker.setCustomName("§c§o"+Name+"의 시체");
            bodymarker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
            bodymarker.setInvulnerable(false);
            bodymarker.setCollidable(false);
            bodymarker.setAI(false);

            bodymarker.setSilent(true);
            bodymarker.setGravity(false);
            bodymarker.addScoreboardTag(Name);
            bodymarker.addScoreboardTag("texture:"+texture);
            bodymarker.addScoreboardTag("signature:"+signature);
            ItemStack weapon = player.getInventory().getItemInMainHand();
            ItemStack chest = player.getInventory().getItem(EquipmentSlot.CHEST);
            ItemStack helm = player.getInventory().getItem(EquipmentSlot.HEAD);
            ItemStack leggings = player.getInventory().getItem(EquipmentSlot.LEGS);
            ItemStack boots = player.getInventory().getItem(EquipmentSlot.FEET);
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("weapon", weapon);
            yaml.set("chest", chest);
            yaml.set("helmet", helm);
            yaml.set("leggings", leggings);
            yaml.set("boots", boots);
            String encoded = (new SQL.Converter()).encodeYaml(yaml);
            bodymarker.addScoreboardTag("encoded:"+encoded);

            world.addEntity(this);

            this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        }
    }

    public class BodyMarker1 extends EntityArmorStand {

        String texture = "";
        String signature = "";

        public BodyMarker1(EntityTypes<? extends EntityArmorStand> entitytypes, World world, String Name, Location loc
                ,   String texture, String signature, Player player) {
            super(entitytypes, world);

            this.texture = texture;
            this.signature = signature;

            ArmorStand bodymarker = (ArmorStand) this.getBukkitEntity();
            bodymarker.setCustomNameVisible(true);
            bodymarker.setCustomName("§c§o"+Name+"의 시체");
            bodymarker.setInvisible(true);
            bodymarker.setInvulnerable(false);
            bodymarker.setCollidable(false);
            bodymarker.setAI(false);

            bodymarker.setSilent(true);
            bodymarker.setGravity(false);
            bodymarker.addScoreboardTag(Name);
            bodymarker.addScoreboardTag("texture:"+texture);
            bodymarker.addScoreboardTag("signature:"+signature);
            world.addEntity(this);

            this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        }
    }
}

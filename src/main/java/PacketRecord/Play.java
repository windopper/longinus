package PacketRecord;

import ClassAbility.Combination;
import PacketRecord.Skill.PacketAetherMelee;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.DummyNetworkManager;

import java.io.File;
import java.util.*;

public class Play {

    private HashMap<String, EntityPlayer> entityPlayers = new HashMap<>();
    private static final Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> TELEPORT_FLAGS
            = Collections.unmodifiableSet(EnumSet.of(PacketPlayOutPosition.EnumPlayerTeleportFlags.e,
            PacketPlayOutPosition.EnumPlayerTeleportFlags.d));
    private Player player;
    private String filename;
    private PlayerConnection conn;

    public Play(Player player, String filename) {
        this.player = player;
        this.filename = filename;
        this.conn = ((CraftPlayer) player).getHandle().b;
    }

    public void Play() {

        File file = new File(filename+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

        for(String name : config.getConfigurationSection(filename+".1.").getKeys(false)) {
            //Bukkit.broadcastMessage(name);

            String uuid = config.getString(filename+".1."+name+"."+"uuid");
            String world = config.getString(filename+".1."+name+"."+"world");
            double x = config.getDouble(filename+".1."+name+"."+"x");
            double y = config.getDouble(filename+".1."+name+"."+"y");
            double z = config.getDouble(filename+".1."+name+"."+"z");
            float yaw = (float) config.getDouble(filename+".1."+name+"."+"yaw");
            float pitch = (float) config.getDouble(filename+".1."+name+"."+"pitch");
//            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
//            GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
//            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = config.getString(filename+".info."+name+".texture");
            String signature = config.getString(filename+".info."+name+".signature");
            ItemStack handitem = config.getItemStack(filename+".info."+name+".handitem");

            WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorld(world)).getHandle();
            GameProfile egameProfile = new GameProfile(UUID.randomUUID(), name);
            Property eproperty = new Property("textures", texture, signature);
            egameProfile.getProperties().put("textures", eproperty);

            EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, egameProfile);

            entityPlayer.b = new PlayerConnection(nmsServer, new DummyNetworkManager(EnumProtocolDirection.a), entityPlayer);
            entityPlayer.setInvulnerable(true);
            entityPlayer.setLocation(x, y, z, yaw, pitch);
            nmsWorld.addEntity(entityPlayer);

            if(handitem != null) {
                CraftItemStack cis = CraftItemStack.asCraftCopy(handitem);
                Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair =
                        new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(cis));
                sendpacket(player, new PacketPlayOutEntityEquipment(entityPlayer.getId(), Arrays.asList(pair)));
            }

            entityPlayers.put(name, entityPlayer);
        }

        showEntity();

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                for(String name : config.getConfigurationSection(filename+".1.").getKeys(false)) {

                    String filepath = filename+"."+Integer.toString(time)+"."+name;

                    String world = config.getString(filepath+".world");
                    double x = config.getDouble(filepath+".x");
                    double y = config.getDouble(filepath+".y");
                    double z = config.getDouble(filepath+".z");
                    float yaw = (float) config.getDouble(filepath+".yaw");
                    float pitch = (float) config.getDouble(filepath+".pitch");
                    boolean swing = config.getBoolean(filepath+".armswing");
                    boolean sneaking = config.getBoolean(filepath+".sneaking");
                    int combo = config.getInt(filepath+".combo");
                    String skill = config.getString(filepath+".skill");
                    String Class = config.getString(filepath+".class");



                    EntityPlayer entityPlayer = entityPlayers.get(name);
                    entityPlayer.setPositionRotation(x, y, z, yaw, pitch);
                    org.bukkit.entity.Entity eP = entityPlayer.getBukkitEntity();
                    eP.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));

                    if(swing) {
                        sendpacket(player, new PacketPlayOutAnimation(entityPlayer, 0));
                    }
                    if(sneaking) {
                        entityPlayer.setSneaking(true);
                        DataWatcher dataWatcher = new DataWatcher(null);
                        dataWatcher.register(new DataWatcherObject<>(6, DataWatcherRegistry.s), EntityPose.f);
                        sendpacket(player, new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher, true));
                    }
                    else {
                        entityPlayer.setSneaking(false);
                        DataWatcher dataWatcher = new DataWatcher(null);
                        dataWatcher.register(new DataWatcherObject<>(6, DataWatcherRegistry.s), EntityPose.a);
                        sendpacket(player, new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher, true));
                    }
                    if(skill != null) {
                        if(!skill.equals(""))
                            CallSkill(Class, skill, combo, entityPlayer);
                    }



                }

                if(!config.contains(filename+"."+Integer.toString(time))) {
                    PlayerConnection conn = ((CraftPlayer) player).getHandle().b;

                    for(EntityPlayer eP : entityPlayers.values()) {
                        conn.sendPacket(new PacketPlayOutEntityDestroy(eP.getId()));
                        eP.setRemoved(Entity.RemovalReason.a);
                    }
                    cancel();
                }
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 10, 1);
    }


    private void showEntity() {

        for(EntityPlayer npc : entityPlayers.values()) {


            PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                    npc
            );
            sendpacket(player, packetPlayOutPlayerInfo);

            PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(
                    npc
            );
            sendpacket(player, packetPlayOutNamedEntitySpawn);

            CraftScoreboardManager scoreboardManager = ((CraftServer) Bukkit.getServer()).getScoreboardManager();
            assert scoreboardManager != null;

            CraftScoreboard mainScoreboard = scoreboardManager.getNewScoreboard();
            Scoreboard scoreboard = mainScoreboard.getHandle();

            ScoreboardTeam scoreboardTeam = scoreboard.getPlayerTeam(npc.getName());


            if (scoreboardTeam == null) {
                scoreboardTeam = scoreboard.createTeam("Actors");
                scoreboard.addPlayerToTeam(npc.getName(), scoreboardTeam);
            }
            else {
                scoreboard.addPlayerToTeam(npc.getName(), scoreboardTeam);
            }


            Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                try {
                    PacketPlayOutPlayerInfo removeFromTabPacket = new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
                            npc
                    );
                    sendpacket(player, removeFromTabPacket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, 20);

            Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                fixSkinHelmetLayerForPlayer(npc, player);
            }, 8);

        }

    }

    private void fixSkinHelmetLayerForPlayer(EntityPlayer npc, Player player) {

        DataWatcher dataWatcher = npc.getDataWatcher();
        dataWatcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), (byte) 127);

        conn.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher, true));
    }


    private void sendpacket(Player player, Packet<?> packet) {

        PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
        conn.sendPacket(packet);
    }

    private void CallSkill(String Class, String Skill, int Combo, EntityPlayer entityPlayer) {

        if(Class.equals(Combination.Classes.아이테르.name())) {
            (new PacketAetherMelee(entityPlayer, player)).Melee(Skill, Combo);
        }


    }

}

package EntityPlayerManager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.DummyNetworkManager;
import spellinteracttest.Main;

import java.util.UUID;

public class EntityPlayerManager {

    private static EntityPlayerManager entityPlayerManager = null;

    private EntityPlayerManager() {

    }

    public static EntityPlayerManager getInstance() {
        if(entityPlayerManager==null) entityPlayerManager = new EntityPlayerManager();
        return entityPlayerManager;
    }

    public EntityPlayer dummyNetworkNPC(World world, Location location, String texture, String signature) {

        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), " ");
        DummyEntityPlayer entityPlayer = new DummyEntityPlayer(nmsServer, nmsWorld, profile);
        Property property = new Property("textures", texture, signature);
        profile.getProperties().put("textures", property);

        entityPlayer.b = new PlayerConnection(nmsServer, new DummyNetworkManager(EnumProtocolDirection.a), entityPlayer);

        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());
        entityPlayer.setInvulnerable(true);
        entityPlayer.getBukkitEntity().setCollidable(false);

        nmsWorld.addEntity(entityPlayer);

        for(Player player : Bukkit.getOnlinePlayers()) {
            (new EntityPlayerManager()).showTo(entityPlayer, player);
        }

        return entityPlayer;
    }

    public void fixSkinHelmetLayerForPlayer(EntityPlayer npc, Player player) {

        DataWatcher dataWatcher = npc.getDataWatcher();
        dataWatcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), (byte) 127);

        PlayerConnection conn = ((CraftPlayer)player).getHandle().b;
        conn.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher, true));
    }

    private void sendpacket(Player player, Packet<?> packet) {

        PlayerConnection conn = ((CraftPlayer)player).getHandle().b;
        conn.sendPacket(packet);
    }

    public void showTo(EntityPlayer npc, Player player) {

//        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(
//                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
//                npc
//        );
//
//        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(
//                npc
//        );
//
//        sendpacket(player, packetPlayOutPlayerInfo);
//        sendpacket(player, packetPlayOutNamedEntitySpawn);
//
//        CraftScoreboardManager scoreboardManager = ((CraftServer) Bukkit.getServer()).getScoreboardManager();
//        assert scoreboardManager != null;
//
//        CraftScoreboard mainScoreboard = scoreboardManager.getNewScoreboard();
//        Scoreboard scoreboard = mainScoreboard.getHandle();
//
//        ScoreboardTeam scoreboardTeam = scoreboard.getPlayerTeam(npc.getName());
//
//
//        if (scoreboardTeam == null) {
//            scoreboardTeam = scoreboard.createTeam("NPC");
//            scoreboard.addPlayerToTeam(npc.getName(), scoreboardTeam);
//        }
//        else {
//            scoreboard.addPlayerToTeam(npc.getName(), scoreboardTeam);
//        }
//
//        scoreboardTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.b);
//
//        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
//            try {
//                PacketPlayOutPlayerInfo removeFromTabPacket = new PacketPlayOutPlayerInfo(
//                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
//                        npc
//                );
//                sendpacket(player, removeFromTabPacket);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }, 10);
//
//        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
//            fixSkinHelmetLayerForPlayer(npc, player);
//        }, 8);

        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard())
                .getHandle(), player.getName()+"a");
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.b);
        PacketPlayOutScoreboardTeam score1 = PacketPlayOutScoreboardTeam.a(team);
        PacketPlayOutScoreboardTeam score2 = PacketPlayOutScoreboardTeam.a(team, true);
        PacketPlayOutScoreboardTeam score3 = PacketPlayOutScoreboardTeam.a(team, npc.getName(), PacketPlayOutScoreboardTeam.a.a);

        DataWatcher watcher = npc.getDataWatcher();
        byte b = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        watcher.set(DataWatcherRegistry.a.a(17), b);

            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(score1);
            connection.sendPacket(score2);
            connection.sendPacket(score3);

            connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), watcher, true));

            new BukkitRunnable() {
                @Override
                public void run() {
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc));
                }
            }.runTaskAsynchronously(Main.getPlugin(Main.class));


    }


}

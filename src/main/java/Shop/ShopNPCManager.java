package Shop;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ShopNPCManager {
	
	private static ShopNPCManager ShopNPCManager;
	
	private final List<String> npcname = Arrays.asList("test0", "test1", "test2", "test3");
	private final static List<EntityPlayer> NPC = new ArrayList<EntityPlayer>();
	
	private ShopNPCManager() {
		
	}
	
	public static ShopNPCManager getinstance() {
		if(ShopNPCManager == null) ShopNPCManager = new ShopNPCManager();
		return ShopNPCManager;
	}

	public static EntityPlayer getNPCID(int EntityID) {
		for(EntityPlayer NPCs : NPC) {
			if(NPCs.getId() == EntityID) {
				return NPCs;
			}
		}
		return null;
	}
	
	public void createNPC(Location location, String npcName, String texture, String signature) {
		
		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), npcName);	
		Property property = new Property("textures", texture, signature);
		gameProfile.getProperties().put("textures", property);
		
		EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile);
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		npc.displayName = "";
		
		NPC.add(npc);
		
		//addNPCPacket(npc);
		
	}
	
	public void addNPCPacket(EntityPlayer npc) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getWorld().getName().equals(npc.getBukkitEntity().getWorld().getName())) {
				 showTo(npc, player);
			}

		}
	}
	
	public void addJoinPacket(Player player) {
		for(EntityPlayer npc : NPC) {
			if(player.getWorld().getName().equals(npc.getBukkitEntity().getWorld().getName())) {
				 showTo(npc, player);
			}
		    }
			
			
		}
	
	public void removeNPCPacketallplayer() {

		for(Player p : Bukkit.getOnlinePlayers()) {
			for(EntityPlayer npc : NPC) {
				PlayerConnection connection = ((CraftPlayer)p).getHandle().b;
				connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
			}
		}
	}
	
	
	public void removeNPCPacket(Player p) {

		for(EntityPlayer npc : NPC) {
			PlayerConnection connection = ((CraftPlayer)p).getHandle().b;
			connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		}

	}
	
	public List<EntityPlayer> getNPCs() {
		return NPC;
	}
	
	public void addnpctolist() {

		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "NPC.yml");
		FileConfiguration NPCFile = YamlConfiguration.loadConfiguration(file);

		for(String name : npcname) {
			createNPC(new Location(Bukkit.getWorld(NPCFile.getString(name+".world")),
					 NPCFile.getDouble(name+".x"),
					 NPCFile.getDouble(name+".y"),
					 NPCFile.getDouble(name+".z")),
					 NPCFile.getString(name+".name"),
					 NPCFile.getString(name+".texture"),
					 NPCFile.getString(name+".signature"));
			Bukkit.broadcastMessage("hehe");
		}

	}
	
	
	public void sendHeadRotationPacket() {
		for(Player p : Bukkit.getOnlinePlayers()) {			
			for(EntityPlayer npc : NPC) {
				
				if(!npc.getBukkitEntity().getWorld().getName().equals(p.getWorld().getName())) continue;
				
				
				
				Location original = npc.getBukkitEntity().getLocation();
				Location ploc = p.getLocation();
				
				double dist = ploc.distance(original);
				
				if(dist<9) {
					
					Location location = original.clone().setDirection(p.getLocation().subtract(original.clone()).toVector());
					
					byte yaw = (byte) (location.getYaw() * 256/360);
					byte pitch = (byte) (location.getPitch() * 256/360);
					
					PacketPlayOutEntityHeadRotation headRotationPacket = new PacketPlayOutEntityHeadRotation(npc, yaw);
					sendpacket(p, headRotationPacket);
					
					PacketPlayOutEntity.PacketPlayOutEntityLook lookPacket = new PacketPlayOutEntity.PacketPlayOutEntityLook(
							npc.getId(),
							yaw,
							pitch,
							false);
					
					sendpacket(p, lookPacket);
				}

			}
		}
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
			scoreboardTeam = scoreboard.createTeam("Shop");
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

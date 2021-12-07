package QuestFunctions;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Player;

import java.util.*;

public class QuestNPCManager {

	private static QuestNPCManager NPCManager;

	private final static HashMap<EntityPlayer, EntityArmorStand> NPC = new HashMap<>();

	private QuestNPCManager() {
		
	}
	
	public static QuestNPCManager getinstance() {
		if(NPCManager == null) NPCManager = new QuestNPCManager();
		return NPCManager;
	}
	
	public void createNPC(Location location, String npcName, String texture, String signature) {
		
		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "§5Quest NPC");
		Property property = new Property("textures", texture, signature);
		gameProfile.getProperties().put("textures", property);
		
		EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));	
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		npc.displayName = "";

		EntityArmorStand armorstand = new EntityArmorStand(EntityTypes.ARMOR_STAND, nmsWorld);
		armorstand.setLocation(location.getX(), location.clone().add(0, 1.1, 0).getY(), location.getZ(), 0, 0);
		armorstand.setCustomName(new ChatMessage("§b"+npcName));
		armorstand.setCustomNameVisible(true);
		armorstand.setInvisible(true);
		armorstand.setSmall(true);
		armorstand.setInvulnerable(true);
		armorstand.setBasePlate(false);
		armorstand.setNoGravity(true);
		armorstand.setSilent(true);


		NPC.put(npc, armorstand);
		
		//addNPCPacket(npc);
		
	}
	
	public void addNPCPacket(EntityPlayer npc) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getWorld().getName().equals(npc.getBukkitEntity().getWorld().getName())) {
				 showQuestNPC(npc, player);
			}

		}
	}
	
	public void addJoinPacket(Player player) {
		for(EntityPlayer npc : NPC.keySet()) {
			if(player.getWorld().getName().equals(npc.getBukkitEntity().getWorld().getName())) {
				 showQuestNPC(npc, player);
			}
		}
			
			
	}
	
	public void removeNPCPacketallplayer() {

		for(Player p : Bukkit.getOnlinePlayers()) {
			for(EntityPlayer npc : NPC.keySet()) {
				PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
				connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
				connection.sendPacket(new PacketPlayOutEntityDestroy(NPC.get(npc).getId()));

			}
//			for(EntityArmorStand armorstands : NPC.values()) {
//				PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
//				connection.sendPacket(new PacketPlayOutEntityDestroy(armorstands.getId()));
//			}
		}
	}
	
	
	public void removeNPCPacket(Player p) {

		for(EntityPlayer npc : NPC.keySet()) {
			PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
			connection.sendPacket(new PacketPlayOutEntityDestroy(NPC.get(npc).getId()));
		}
//		for(EntityArmorStand stand : NPC.values()) {
//			PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
//			connection.sendPacket(new PacketPlayOutEntityDestroy(stand.getId()));
//		}

	}
	
	public HashMap<EntityPlayer, EntityArmorStand> getNPCSets() {
		return NPC;
	}




	public void addnpctolist() {

		QuestFunctions QN = new QuestFunctions();
		QN.addQuestNPCs();

	}
	
	
	public void sendHeadRotationPacket() {
		for(Player p : Bukkit.getOnlinePlayers()) {			
			for(EntityPlayer npc : NPC.keySet()) {
				
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
		dataWatcher.set(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);
		
		PlayerConnection conn = ((CraftPlayer)player).getHandle().playerConnection;
		conn.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), dataWatcher, true));
	}
	
	
	private void sendpacket(Player player, Packet<?> packet) {
		
		PlayerConnection conn = ((CraftPlayer)player).getHandle().playerConnection;
		conn.sendPacket(packet);
	}
	
	
	public void showQuestNPC(EntityPlayer npc, Player player) {

		showNPCNames(NPC.get(npc), player);

		PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
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

		ScoreboardTeam scoreboardTeam = scoreboard.getTeam(npc.getName());
		if (scoreboardTeam == null) {
			scoreboardTeam = new ScoreboardTeam(scoreboard, npc.getName());
		}

		sendpacket(player, new PacketPlayOutScoreboardTeam(scoreboardTeam, 1)); // Create team
		sendpacket(player, new PacketPlayOutScoreboardTeam(scoreboardTeam, 0)); // Setup team options
		sendpacket(player, new PacketPlayOutScoreboardTeam(scoreboardTeam, Collections.singletonList(npc.getName()), 3)); // Add entityPlayer to team entries

		Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
			try {
				PacketPlayOutPlayerInfo removeFromTabPacket = new PacketPlayOutPlayerInfo(
						PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
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

	public void showNPCNames(EntityArmorStand stand, Player p) {

		sendpacket(p, new PacketPlayOutSpawnEntity(stand));

		DataWatcher datawatcher = stand.getDataWatcher();
		datawatcher.set(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) 0x20);

		sendpacket(p, new PacketPlayOutEntityMetadata(stand.getId(), datawatcher, true));

	}

}

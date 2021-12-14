package Mob;

import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.monster.EntityZombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import QuestClasses.Tutorial;
import DynamicData.EntityStatusManager;

public class mob {
	
	static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	
	public void mobdelete() {
		for(LivingEntity e : Bukkit.getWorld("world").getLivingEntities()) {
			int i=0;
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(e.getWorld() == p.getWorld()) {
					if(e.getLocation().distance(p.getLocation())<100) {
						i=1;
						break;
					}
				}

			}
			if(i==0) { // 아무도 없으면
				e.remove();
			}
			
		}
	}
	
//	public static void mobsummoncondition(Location loc) {
//		int i=0;
//		for(Player p : Bukkit.getOnlinePlayers()) {
//			if(loc.distance(p.getLocation())<100) {
//				i=1;
//				break;
//			}
//		}
//		if(i==0) return;
//	}
	
	public void loop() {
		
		trainerbot();
		trainerbot2();
		Tutorial.exambot();
				
	}
	
	public static void fastmobloop() {
		
				
		
				
	}
	
	@SuppressWarnings("deprecation")
	public void trainerbot() {
		
		//Bukkit.broadcastMessage("hi1");
		
		Location loc = new Location(Bukkit.getServer().getWorld("world"), -74.5, 52, 62.5);
		
		for(LivingEntity le : Bukkit.getServer().getWorld("world").getLivingEntities()) {
			if(le.getCustomName() != null) {
				if(le.getCustomName().equals("샌드백")) {
					return;
				}
			}

		}
		
		int i=0;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(loc.getWorld() == p.getWorld()) {
				if(loc.distance(p.getLocation())<100) {
					i=1;
					break;
				}
			}
		}
		if(i==0) return;
		
		
		//Bukkit.broadcastMessage("hi");
		
		Skeleton skeleton = (Skeleton) Bukkit.getServer().getWorld("world").spawnEntity(loc, EntityType.SKELETON);
		skeleton.setMaxHealth(2048);
		skeleton.setHealth(2048);
		skeleton.setCustomName("샌드백");
		skeleton.setCustomNameVisible(true);
		skeleton.setAI(false);
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 200), true);
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 200), true);
		skeleton.setCollidable(true);
		skeleton.setSilent(true);
		
		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(1);
		item.setItemMeta(meta);
		
		skeleton.getEquipment().setItemInMainHand(item);
		
		EntityStatusManager.getinstance(skeleton).setCanKnockback(false);
		
		//Bukkit.broadcastMessage("hi2");
	}
	
	
	public void trainerbot2() {
		
		

		//Bukkit.broadcastMessage("hi1");
		
		Location loc = new Location(Bukkit.getServer().getWorld("world"), -40.5, 57, 95.5);
		
		for(LivingEntity le : Bukkit.getServer().getWorld("world").getLivingEntities()) {
			if(le.getCustomName() != null) {
				if(le.getCustomName().equals("과녁")) {
					return;
				}
			}

		}
		
		int i=0;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(loc.getWorld() == p.getWorld()) {
				if(loc.distance(p.getLocation())<100) {
					i=1;
					break;
				}
			}
		}
		if(i==0) return;
		//Bukkit.broadcastMessage("hi");
		
		Skeleton skeleton = (Skeleton) Bukkit.getServer().getWorld("world").spawnEntity(loc, EntityType.SKELETON);
		skeleton.setMaxHealth(2048);
		skeleton.setHealth(2048);
		skeleton.setCustomName("과녁");
		skeleton.setCustomNameVisible(true);
		skeleton.setAI(false);
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 200), true);
		skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 200), true);
		skeleton.setSilent(true);
		
		
		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(1);
		item.setItemMeta(meta);
		
		skeleton.getEquipment().setItemInMainHand(item);
		
		EntityStatusManager.getinstance(skeleton).setCanKnockback(false);
		
		//Bukkit.broadcastMessage("hi2");
	}
	
	public static void trainerbot3(final Player p) {
		
		
		
		
		 PlayerConnection connection = ((CraftPlayer) p).getHandle().b;
		 WorldServer nmsWorld = ((CraftWorld) p.getWorld()).getHandle();
		 
		 EntityZombie entity = new EntityZombie(nmsWorld);
		 entity.setLocation(
				 p.getLocation().getX(),
				 p.getLocation().getY(),
				 p.getLocation().getZ(),
				 0,
				 0
				 );
		 
		 
		 //nmsWorld.addEntity(entity);
		 
		 
		 connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entity));
		 
		 
		 Bukkit.broadcastMessage("summon");
			
	}
	
	
	
}

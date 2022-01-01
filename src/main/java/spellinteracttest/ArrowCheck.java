package spellinteracttest;

import CustomEvents.ArrowFlyingEvent;
import CustomEvents.ArrowOnGroundEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;

public class ArrowCheck {
	
	
	
	public static void ArrowWatcher() {

		for(World worlds : Bukkit.getWorlds()) {
			
			for(Entity entity : Bukkit.getWorld(worlds.getName()).getEntities()) {
				
				if(entity instanceof Arrow) {				
					Arrow ar = (Arrow) entity;
					if(ar.isOnGround()) {
						ar.remove();

						if(ar.getCustomName() == null) continue;

						Bukkit.getPluginManager().callEvent(new ArrowOnGroundEvent(ar, ar.getCustomName()));

//						if(ar.getCustomName() == null) continue;
//						String ename = ar.getCustomName();
//						String split[] = ename.split(":");
//						String skillname = split[0];
//						String username = split[1];
//
//
//						for(Player p : Bukkit.getOnlinePlayers()) {
//							if(skillname.equals("bomb") && p.getName().equals(username)) {
//
//								Blaster.getinstance().grenadelauncherbomb(ar.getLocation(),p);
//								break;
//							}
//
//
//						}

					}
					else {

						if(ar.getCustomName() == null) continue;

						Bukkit.getPluginManager().callEvent(new ArrowFlyingEvent(ar, ar.getCustomName()));

//						if(ar.getCustomName() == null) continue;
//						String ename = ar.getCustomName();
//						String split[] = ename.split(":");
//						String skillname = split[0];
//						String username = split[1];
//
//						if(skillname.equals("bomb")) {
//
//							Location eloc = ar.getLocation();
//							eloc.getWorld().spawnParticle(Particle.FLAME, eloc, 7, 0.3, 0.3, 0.3, 0, null);
//							eloc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, eloc, 4, 0.5, 0.5, 0.5, 0, null);
//						}
					}
				}
			}
		}
	}
}

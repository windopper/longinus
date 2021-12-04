package spellinteracttest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import ClassAbility.Blaster;

public class ArrowCheck {
	
	
	
	public static void onGround() {
		
		
		for(World worlds : Bukkit.getWorlds()) {
			
			for(Entity entity : Bukkit.getWorld(worlds.getName()).getEntities()) {
				
				if(entity instanceof Arrow) {				
					Arrow ar = (Arrow) entity;
					if(ar.isOnGround()) {
						
						
						if(ar.getCustomName() == null) return;						
						String ename = ar.getCustomName();						
						String split[] = ename.split(":");
						String skillname = split[0];
						String username = split[1];
						
						
						for(Player p : Bukkit.getOnlinePlayers()) {
							if(skillname.equals("bomb") && p.getName().equals(username)) {
								
								Blaster.getinstance().grenadelauncherbomb(ar.getLocation(),p);
								break;
							}
			
							
						}	
						ar.remove();
					}
					else {
						
						if(ar.getCustomName() == null) return;						
						String ename = ar.getCustomName();						
						String split[] = ename.split(":");
						String skillname = split[0];
						String username = split[1];
						
						if(skillname.equals("bomb")) {
							
							Location eloc = ar.getLocation();
							eloc.getWorld().spawnParticle(Particle.FLAME, eloc, 7, 0.3, 0.3, 0.3, 0, null);
							eloc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, eloc, 4, 0.5, 0.5, 0.5, 0, null);
						}
						
						
						
					}
					
					
					
					
				}
			}
			

		}
	}
}

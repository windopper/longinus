package DynamicData;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramIndicator {
	
	private static HologramIndicator HologramIndicator;
	
	private HologramIndicator() {
		
	}
	
	public static HologramIndicator getinstance() {
		if(HologramIndicator == null) HologramIndicator = new HologramIndicator();
		return HologramIndicator;
	}
	
	
	
	public void DamageIndicator(final int r, final LivingEntity e) { // 데미지 뜨게 하기
		
				

		

		new BukkitRunnable() {

			int time = 0;
			ArmorStand as;
			
			
			@Override
			public void run() {
				
				if(time == 0) {
					
					Location loc = e.getLocation();
					Random random = new Random();
					double x = random.nextDouble() *2;
					double y = random.nextDouble() *2;
					double z = random.nextDouble() *2;
					x -= 1;
					z -= 1;
					loc.setX(loc.getX()+x);
					loc.setY(loc.getY()+y);
					loc.setZ(loc.getZ()+z);
					as = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
					as.setCustomName("§c- ♥"+r+"§c");
					as.setVisible(false);
					as.setCollidable(false);
					as.setGravity(false);
					as.setSmall(true);
					as.setInvulnerable(true);
					as.setCustomNameVisible(true);
					
				}
				
				
					if(time > 20) {
						
						as.remove();
						cancel();
						return;
						
					}
					
				time++;

				
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}
	
	public void ShieldBroken(final LivingEntity e) { // 데미지 뜨게 하기
		
				

		

		new BukkitRunnable() {

			int time = 0;
			ArmorStand as;
			
			
			@Override
			public void run() {
				
				if(time == 0) {
					
					Location loc = e.getLocation();
					for(Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(loc, Sound.ITEM_TOTEM_USE, 0.5f, 2);
					}
					
					Random random = new Random();
					double x = random.nextDouble() *2;
					double y = random.nextDouble() *2;
					double z = random.nextDouble() *2;
					x -= 1;
					z -= 1;
					loc.setX(loc.getX()+x);
					loc.setY(loc.getY()+y);
					loc.setZ(loc.getZ()+z);
					as = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
					as.setCustomName("§5§l-§kii§k§r§5§l🛡§kii§k§l§5");
					as.setVisible(false);
					as.setGravity(false);
					as.setSmall(true);
					as.setInvulnerable(true);
					as.setCustomNameVisible(true);
					
				}
				
					

					
					if(time > 30) {
						
						as.remove();
						cancel();
						return;
						
					}


				
				time++;

				
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}
	
	

	
	public void HealIndicator(final int r, final Player p) {
		
		new BukkitRunnable() {

			int time = 0;
			ArmorStand as;
			
			
			@Override
			public void run() {
				
				if(time == 0) {
					
					Location loc = p.getLocation();
					Random random = new Random();
					double x = random.nextDouble() *2;
					double y = random.nextDouble() *2;
					double z = random.nextDouble() *2;
					x -= 1;
					z -= 1;
					loc.setX(loc.getX()+x);
					loc.setY(loc.getY()+y);
					loc.setZ(loc.getZ()+z);
					as = (ArmorStand) loc.getWorld().spawn(loc, ArmorStand.class);
					as.setCustomName("§a+"+r+"§a");
					as.setVisible(false);
					as.setCollidable(false);
					as.setGravity(false);
					as.setSmall(true);
					as.setInvulnerable(true);
					as.setCustomNameVisible(true);
					
				}
				
					

					
					if(time > 20) {
						
						as.remove();
						cancel();
						return;

						
					}


				
				time++;

				
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}

}

package Mob;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class mobhitsound {

		public void sound(LivingEntity e) {
			
			if(e.getCustomName() != null) {
				trainer(e);
			}
			

		}
		
		public void trainer(LivingEntity e) {
			
			if(e.getCustomName().equals("샌드백") || e.getCustomName().equals("과녁")) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(e.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 2);
				}
			}
			
		}
}

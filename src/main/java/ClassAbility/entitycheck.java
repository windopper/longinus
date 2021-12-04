package ClassAbility;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;

public class entitycheck {
	
	public static boolean entitycheck(Entity e) {
		
		if((e instanceof Player || e instanceof LivingEntity) && !(e instanceof ArmorStand) && !(e instanceof ShulkerBullet) && !(e instanceof FallingBlock)) { 
			return true;
		}
		return false;
	}
	
	public static boolean duelcheck(Entity p, Entity me) {
		if(p instanceof Player && me instanceof Player) {
			// 사람이고 듀얼목록이 있으면
			
			if(userdata.UserManager.dual.containsKey(p) || userdata.UserManager.dual.containsKey(me)) {
				if(userdata.UserManager.dual.get(p)==me || userdata.UserManager.dual.get(me)==p) {
					return true;
				}
			}	
		}
		else if(p instanceof LivingEntity && !(p instanceof ArmorStand) && !(p instanceof Player) && !(p instanceof ShulkerBullet)){
			// 살아있는 엔티티 이고 아머스탠드가 아니고 플레이어가 아니면
			return true;
		}
		return false;
	}
}

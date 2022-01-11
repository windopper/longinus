package ClassAbility;

import Duel.DuelManager;
import org.bukkit.entity.*;

public class entitycheck {
	
	public static boolean entitycheck(Entity e) {
		
		if((e instanceof Player || e instanceof LivingEntity) && !(e instanceof ArmorStand) && !(e instanceof ShulkerBullet)
				&& !(e instanceof FallingBlock)) {

			if(e.isInvulnerable()) return false;

			return true;
		}
		return false;
	}
	
	public static boolean duelcheck(Entity p, Entity me) {
		if(p instanceof Player && me instanceof Player && !p.equals(me)) {
			// 사람이고 듀얼목록이 있으면
			
			if(DuelManager.checkInSameDuel((Player) me, (Player) p)) {
				return true;
			}	
		}
		else if(p instanceof LivingEntity && !(p instanceof ArmorStand) && !(p instanceof Player)
				&& !(p instanceof ShulkerBullet) && p != me && !p.equals(me)){
			// 살아있는 엔티티 이고 아머스탠드가 아니고 플레이어가 아니면
			return true;
		}
		return false;
	}


	public static boolean Invulnerableentitycheck(Entity e) {
		if((e instanceof Player || e instanceof LivingEntity) && !(e instanceof ArmorStand) && !(e instanceof ShulkerBullet)
				&& !(e instanceof FallingBlock)) {

			return true;
		}
		return false;
	}

}

package dynamicdata;

import java.util.HashMap;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class EntityStatus {

	private static final HashMap<LivingEntity, EntityStatus> instance = new HashMap<>();
	
	private LivingEntity e;
	
	private boolean canStun = true;
	private boolean canKnockback = true;
	
	private int burnstick = 0;
	private int burnsdmg = 0;
	
	private EntityStatus(LivingEntity e) {
		this.e = e;
	}
	
	public static EntityStatus getinstance(LivingEntity e) {
		if(!instance.containsKey(e)) instance.put(e, new EntityStatus(e));
		return instance.get(e);
	}
	
	public void removeinstance() {
		instance.remove(e);
	}
	
	public boolean canStun() {
		return canStun;
	}
	public boolean canKnockback() {
		return canKnockback;
	}
	
	public void setBurnsdmg(int burnsdmg) {
		this.burnsdmg = burnsdmg;
	}
	public void setBurnstick(int burnstick) {
		this.burnstick = burnstick;
	}
	public void setCanKnockback(boolean canKnockback) {
		this.canKnockback = canKnockback;
	}
	public void setCanStun(boolean canStun) {
		this.canStun = canStun;
	}
	
	
	public void KnockBack(Entity damager, double knockbackvector) {
		
		if(canKnockback == true) {
			Vector playerdir = damager.getLocation().getDirection();
			playerdir.normalize();
			Vector knockback = playerdir.multiply(knockbackvector);
			e.setVelocity(knockback);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void Stun(Entity damager, int tick) {
		
		if(canStun == true) {
			
			PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, tick, 100);
			e.addPotionEffect(potion, true);
			
		}
	}
	
	public void burns(Entity p , int tick, int dmg) {
		
		if(burnstick == 0) {
			burnstick = tick;
			burnsdmg = dmg;
		}
		else {
			burnstick = tick;
			if(burnsdmg<dmg) {
				burnsdmg = dmg;
			}
		}
	}
	
	
	public void BurnsLoop()	{
		
		if(burnstick == 0) return;
		
		burnstick--;
		
		if(burnstick % 20 == 0) {
			e.getWorld().spawnParticle(Particle.FALLING_LAVA, e.getLocation(), 20, 0.5, 0.5, 0.5, 1, null);
			Damage.getinstance().taken(burnsdmg, e);
		}
				
		if(burnstick <=0) {
			burnsdmg = 0;
			burnstick = 0;			
		}

		
	}
	
	
	
	
}

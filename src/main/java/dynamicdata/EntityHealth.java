package dynamicdata;

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class EntityHealth {
	
	private static HashMap<LivingEntity, EntityHealth> instance = new HashMap<>();
	
	private LivingEntity e;
	private int CurrentHealth;
	private int MaxHealth;
	
	private EntityHealth(@Nonnull LivingEntity e) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = (int)(e.getHealth()*500);
		MaxHealth = (int)(e.getMaxHealth()*500);
	}
	
	
	public static EntityHealth getinstance(LivingEntity e) {
		
		if(!instance.containsKey(e)) instance.put(e, new EntityHealth(e));
		return instance.get(e);
	}
	
	public static boolean checkinstasnce(LivingEntity e) {
		if(!instance.containsKey(e)) return false;
		return true;
	}
	
	public void removeinstance() {
		instance.remove(e);
	}
	
	public int getCurrentHealth() {
		return CurrentHealth;
	}
	
	public int getMaxHealth() {
		return MaxHealth;
	}
	
	public void setCurrentHealth(int currentHealth) {
		CurrentHealth = currentHealth;
	}
	
	
	@SuppressWarnings("deprecation")
	public void EntityHealthWatcher() {
		
		
		if(!(e instanceof Player) && (e instanceof LivingEntity) && !(e instanceof ArmorStand)) {
			
			double heart = e.getMaxHealth() * ((double)CurrentHealth/(int)(e.getMaxHealth())*500);
			if(heart > e.getMaxHealth()) {
				heart = e.getMaxHealth();
			}
			if(heart > 0) {
				e.setHealth(heart);
			}
			else {
				e.setHealth(0);
			}
			
			if(CurrentHealth < 0) {
				EntityStatus.getinstance(e).removeinstance();
				removeinstance();
				
			}
		}

	}

}

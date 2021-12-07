package dynamicdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;


public class EntityHealthManager {
	
	private static HashMap<LivingEntity, EntityHealthManager> instance = new HashMap<>();
	
	private LivingEntity e;
	private ArmorStand ar;
	private int CurrentHealth;
	private int MaxHealth;
	
	private EntityHealthManager(@Nonnull LivingEntity e) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = (int)(e.getHealth()*500);
		MaxHealth = (int)(e.getMaxHealth()*500);
	}
	
	
	public static EntityHealthManager getinstance(LivingEntity e) {
		
		if(!instance.containsKey(e)) instance.put(e, new EntityHealthManager(e));
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

	public ArmorStand getArmorStand() {
		return ar;
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

				// 한방컷 날때 오류 방지
				// 코드 밑으로 내리기 귀찮음
				if(ar!=null) {
					ar.remove();
				}

				e.setCustomName(" ");
				EntityStatus.getinstance(e).removeinstance();
				removeinstance();
				return;
			}


			// 맞았으면 아머스탠드 소환
			if(ar==null && CurrentHealth < MaxHealth) {

				ar = (ArmorStand) e.getWorld().spawnEntity(e.getLocation().add(0, e.getHeight()+0.25, 0), EntityType.ARMOR_STAND);
				ar.setCustomName(e.getCustomName());
				ar.setCustomNameVisible(true);
				ar.setInvisible(true);
				ar.setSmall(true);
				ar.setMarker(true);
				ar.setInvulnerable(true);
				ar.setBasePlate(false);
				ar.setGravity(false);
				ar.setSilent(true);

				e.setCustomName("§b[||||"+CurrentHealth+"||||]");
				e.setCustomNameVisible(true);
			}

			// 아머스탠드 텔레포트
			if(ar!=null) {
				String arname = "§b[||||"+CurrentHealth+"||||]";
				List<Character> arrlist = new ArrayList<>();
				char[] arr = arname.toCharArray();
				for(int i=0; i<arr.length; i++) {
					arrlist.add(arr[i]);
				}
				double rate = (double) CurrentHealth / (double) MaxHealth;
				int index = (int)(arr.length * rate);
				if(index<=1) index = 2;
				arrlist.add(index+1, '§');
				arrlist.add(index+2, '3');

				String customname = "";
				for(char ch : arrlist) {
					customname += ch;
				}

				e.setCustomName(customname);
				ar.teleport(e.getLocation().add(0, e.getHeight()+0.25, 0));
			}


		}

	}

}

package DynamicData;

import CustomEvents.CustomMobDeathEvent;
import Mob.MobListManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import spellinteracttest.Main;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EntityManager {
	
	private static HashMap<LivingEntity, EntityManager> instance = new HashMap<>();
	
	private LivingEntity e;
	private ArmorStand ar;
	private String CustomName;
	private int CurrentHealth;
	private int MaxHealth;
	private int patterntime = 0;
	private MobListManager.MobList mobList;
	
	private EntityManager(@Nonnull LivingEntity e) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = (int)(e.getHealth()*500);
		MaxHealth = (int)(e.getMaxHealth()*500);
	}

	private EntityManager(@Nonnull LivingEntity e, int maxhealth) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = maxhealth;
		MaxHealth = maxhealth;
	}

	private EntityManager(@Nonnull LivingEntity e, int maxhealth, MobListManager.MobList mobList) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = maxhealth;
		MaxHealth = maxhealth;
		this.mobList = mobList;
	}

	public static HashMap<LivingEntity, EntityManager> getEntityHealthManager() {
		return instance;
	}
	
	public static EntityManager getinstance(LivingEntity e) {
		
		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e));
		return instance.get(e);
	}

	public static EntityManager getinstance(LivingEntity e, MobListManager.MobList mobList) {

		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, mobList.getHealth(), mobList));
		return instance.get(e);
	}
	
	public static boolean checkinstasnce(LivingEntity e) {
		if(!instance.containsKey(e)) return false;
		return true;
	}
	
	public void removeinstance() {
		instance.remove(e);
	}

	public int getPatterntime() {
		return patterntime;
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

	public String getCustomName() { return CustomName;}

	public void setCurrentHealth(int currentHealth) {
		CurrentHealth = currentHealth;
	}

	public MobListManager.MobList getMobList() {
		return mobList;
	}
	
	@SuppressWarnings("deprecation")
	public void EntityWatcher() {
		
		if(!(e instanceof Player) && (e instanceof LivingEntity) && !(e instanceof ArmorStand)) {

			if(CustomName == null && e.getCustomName() != null) {
				CustomName = e.getCustomName();
				//EntityNamePacketSender.getInstance().SendPacket(e);
			}
			
//			double heart = e.getMaxHealth() * ((double)CurrentHealth/(int)(e.getMaxHealth())*500);
//			if(heart > e.getMaxHealth()) {
//				heart = e.getMaxHealth();
//			}
//			if(heart > 0) {
//				e.setHealth(heart);
//			}
//			else {
//				e.setHealth(0);
//			}
//
			if(CurrentHealth < 0 || e.getHealth() <= 0) {


				if(ar!=null) {
					ar.remove();
				}

				e.setCustomName(" ");
				e.setHealth(0);
				EntityStatusManager.getinstance(e).removeinstance();
				removeinstance();


				if(mobList == null) return;

				// event call
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
					@Override
					public void run() {
						Bukkit.getPluginManager().callEvent(new CustomMobDeathEvent(e, mobList));
					}
				}, 0);


				return;
			}


			// summon armorstand when damaged by who
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

			// teleporting armorstand
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

		if(patterntime == 3600) patterntime = 0;

		patterntime++;

	}

}

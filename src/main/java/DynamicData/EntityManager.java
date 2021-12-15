package DynamicData;

import CustomEvents.CustomMobDeathEvent;
import Mob.MobListManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import spellinteracttest.Main;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class EntityManager {
	
	private static HashMap<LivingEntity, EntityManager> instance = new HashMap<>();
	private static HashMap<LivingEntity, HashMap<Entity, Location>> disguise = new HashMap<>();
	
	private LivingEntity e;
	private ArmorStand Namear;
	private ArmorStand Healthar;
	private String CustomName;
	private int CurrentHealth;
	private int PreviousHealth;
	private int MaxHealth;
	private int patterntime = 0;
	private boolean isinChunk = false;
	private int DamageAfterDelay = 0;
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
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
	}

	private EntityManager(@Nonnull LivingEntity e, int maxhealth, MobListManager.MobList mobList) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = maxhealth;
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
		this.mobList = mobList;
		CustomName = mobList.getName();
	}

	private EntityManager(@Nonnull LivingEntity e, int maxhealth, MobListManager.MobList mobList, HashMap<Entity, Location> disguises) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = maxhealth;
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
		this.mobList = mobList;
		this.disguise.put(e, disguises);
		CustomName = mobList.getName();
	}

	public static HashMap<LivingEntity, EntityManager> getEntityHealthManager() {
		return instance;
	}

	// 일반적인 몹
	public static EntityManager getinstance(LivingEntity e) {
		
		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e));
		return instance.get(e);
	}

	// 변신 하지 않는 몹들 중 등록된 몹
	public static EntityManager getinstance(LivingEntity e, MobListManager.MobList mobList) {

		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, mobList.getHealth(), mobList));
		return instance.get(e);
	}

	// 변신하고 등록된 몹
	public static EntityManager getinstance(LivingEntity e, MobListManager.MobList mobList, HashMap<Entity, Location> disguises) {

		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, mobList.getHealth(), mobList, disguises));
		return instance.get(e);
	}
	
	public static boolean checkinstasnce(LivingEntity e) {
		if(!instance.containsKey(e)) return false;
		return true;
	}
	
	public void removeinstance() {

		instance.remove(e);
		disguise.remove(e);
	}

	public Set<Entity> getDisguises() {
		if(disguise.containsKey(e)) {
			return disguise.get(e).keySet();
		}
		return null;
	}

	public int getPatterntime() {
		return patterntime;
	}
	
	public int getCurrentHealth() {
		return CurrentHealth;
	}

	public void addHealth(int var0) {
		if(CurrentHealth + var0 > MaxHealth) {
			CurrentHealth = MaxHealth;
		}
		else CurrentHealth += var0;

		HologramIndicator.getinstance().HealIndicator(var0, e.getLocation());
	}

	public void setDamageValue(int var0) {
		CurrentHealth -= var0;
		DamageAfterDelay = 100;
	}
	
	public int getMaxHealth() {
		return MaxHealth;
	}

	public ArmorStand getNameArmorStand() {
		return Namear;
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

			// 엔티티 청크 체커
			for(Player player : Bukkit.getOnlinePlayers()) {
				World world = e.getWorld();
				Location location = e.getLocation();

				if(!world.equals(player.getWorld())) continue;

				if(player.getLocation().distance(location) < 100) {
					isinChunk = true;
					break;
				}
			}

			// 죽었을때
			if(CurrentHealth < 0 || e.getHealth() <= 0 || e.isDead() || !e.isValid() || isinChunk == false) {


				if(Namear !=null) {
					Namear.remove();
				}
				if(Healthar != null) {
					Healthar.remove();
				}

				e.setCustomName(" ");
				e.setHealth(0);

				// 변신한 몹들 가져오기
				if(disguise.containsKey(e)) {
					for(Entity disguise : this.getDisguises()) {
						disguise.remove();
					}
				}

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

			// 피해를 받았을때
			if((Namear == null && CurrentHealth < MaxHealth) && DamageAfterDelay == 100) {

				// 아머스탠드 소환
				Namear = (ArmorStand) e.getWorld().spawnEntity(e.getLocation().add(0, e.getHeight()+0.25, 0), EntityType.ARMOR_STAND);
				Namear.setCustomName(CustomName);
				Namear.setCustomNameVisible(true);
				Namear.setInvisible(true);
				Namear.setSmall(true);
				Namear.setMarker(true);
				Namear.setInvulnerable(true);
				Namear.setBasePlate(false);
				Namear.setGravity(false);
				Namear.setSilent(true);

				e.setCustomName(" ");
				e.setCustomNameVisible(false);

			}
			if((Healthar == null && CurrentHealth < MaxHealth) && DamageAfterDelay == 100) {

				Healthar = (ArmorStand) e.getWorld().spawnEntity(e.getLocation().add(0, e.getHeight(), 0), EntityType.ARMOR_STAND);
				Healthar.setCustomNameVisible(true);
				Healthar.setInvisible(true);
				Healthar.setSmall(true);
				Healthar.setMarker(true);
				Healthar.setInvulnerable(true);
				Healthar.setBasePlate(false);
				Healthar.setGravity(false);
				Healthar.setSilent(true);

				Healthar.setCustomName("§b[||||"+CurrentHealth+"||||]");
				Healthar.setCustomNameVisible(true);
			}

			// teleporting armorstand and set Healthar's name
			if(Healthar != null && DamageAfterDelay != 0) {
				String arname = "§b[||||"+CurrentHealth+"||||]";
				List<Character> arrlist = new ArrayList<>();
				char[] arr = arname.toCharArray();
				for(int i=0; i<arr.length; i++) {
					arrlist.add(arr[i]);
				}
				double rate = (double) CurrentHealth / (double) MaxHealth;
				int index = (int)(arr.length * rate);
				if(index<=1) index = 2;
				arrlist.add(index, '§');
				arrlist.add(index+1, '3');

				String customname = "";
				for(char ch : arrlist) {
					customname += ch;
				}

				Healthar.setCustomName(customname);
				Healthar.teleport(e.getLocation().add(0, e.getHeight(), 0));
			}
			if(Namear != null) {
				Namear.teleport(e.getLocation().add(0, e.getHeight()+0.25, 0));
			}
			else if(Namear != null && DamageAfterDelay == 0) {
				Namear.teleport(e.getLocation().add(0, e.getHeight(), 0));
			}
		}

		// 패턴 시간 사이클
		if(patterntime == 3600) patterntime = 0;

		patterntime++;


		// disguise 엔티티 텔레포트
		for(LivingEntity entity : disguise.keySet()) {

			// 기준 엔티티의 좌표
			final Location location = entity.getLocation();

			// 엔티티가 변신한 엔티티의 목록과 좌표
			HashMap<Entity, Location> disguise = this.disguise.get(entity);

			// 변신할 엔티티를 순환하며 좌표로 이동
			for(Entity disentity : disguise.keySet()) {

				// set fallingblock's time to 1
				if(disentity instanceof FallingBlock) {
					((FallingBlock) disentity).setTicksLived(1);
				}

				disentity.teleport(location.clone().add(disguise.get(disentity)));
			}
		}


		// 피해를 받은후 5초가 지나면
		if(DamageAfterDelay > 0) DamageAfterDelay--;

		// 투명화 된 엔티티는 이름과 체력 없애기
		if(DamageAfterDelay == 0) {




			if(e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {

				if(Namear != null) Namear.remove();
				Namear = null;
			}

			if(Healthar != null) Healthar.remove();
			Healthar = null;
		}


	}
}

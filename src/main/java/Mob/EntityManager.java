package Mob;

import CustomEvents.CustomMobDeathEvent;
import DynamicData.HologramIndicator;
import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityPose;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import spellinteracttest.Main;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class EntityManager {
	
	private static ConcurrentHashMap<Entity, EntityManager> instance = new ConcurrentHashMap<Entity, EntityManager>();
	private static HashMap<Entity, HashMap<Entity, Location>> disguise = new HashMap<>();

	private final HashMap<Player, Integer> contribute = new HashMap<>(); // 플레이어 기여도
	private LivingEntity teleportTo;  //텔레포트 하고자 하는 엔티티
	private Location teleportToLoc; // 상대적인 좌표
	private Entity e; // 마스터 엔티티
	private ArmorStand Namear; // 이름 아머스탠드
	private ArmorStand Healthar; // 체력 아머스탠드
	public String CustomName = ""; // 마스터 엔티티 이름
	private int CurrentHealth; // 현재 체력
	private int PreviousHealth; // 이전 체력
	private int MaxHealth; // 최대 체력
	public int Level = 1;
	private int patterntime = 0; // 패턴 시간
	private boolean isinChunk = false;
	private int DamageAfterDelay = 0;
	private MobListManager.MobList mobList;
	private double Height = 0;
	private double HeightTempAdd = 0; // 아머스탠드 이름 위치 맞지 않는거 보정 값
	private boolean showNameTag = true;
	public int minDmg = 1;
	public int maxDmg = 1;
	public Entity latestDamager;

	private Object EntityInstance;
	private Method particleMethod;
	private Method ambientAbilityMethod;
	private Method deathMethod;
	private Method attackMethod;
	private Method attackedByMethod;
	//private Method dropTable;
	private final HashMap<ItemStack, Double> dropTable = new HashMap<>();
	
	private EntityManager(@Nonnull Entity e) {
		this.e = e;
		CurrentHealth = 1000;
		MaxHealth = 1000;
		Height = e.getHeight();
	}

	private EntityManager(@Nonnull Entity e, int maxhealth, String CustomName, HashMap<Entity, Location> disguises) {
		this.e = e;
		CurrentHealth = maxhealth;
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
		this.CustomName = CustomName;
		if(isDisguiseEntityPlayer()) Height = getDisguiseEntityPlayer().getHeight();
		else Height = e.getHeight();
	}

	private EntityManager(@Nonnull Entity e, int maxhealth, MobListManager.MobList mobList) {
		this.e = e;
		CurrentHealth = maxhealth;
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
		this.mobList = mobList;
		CustomName = mobList.getName();
		Height = e.getHeight();
		minDmg = mobList.getMindamage();
		maxDmg = mobList.getMaxdamage();
		Level = mobList.getLevel();
	}

	private EntityManager(@Nonnull Entity e, int maxhealth, MobListManager.MobList mobList, HashMap<Entity, Location> disguises) {
		this.e = e;
		CurrentHealth = maxhealth;
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
		this.mobList = mobList;
		disguise.put(e, disguises);
		CustomName = mobList.getName();
		if(isDisguiseEntityPlayer()) Height = getDisguiseEntityPlayer().getHeight();
		else Height = e.getHeight();
		minDmg = mobList.getMindamage();
		maxDmg = mobList.getMaxdamage();
		Level = mobList.getLevel();
	}

	public static Map<Entity, EntityManager> getEntityHealthManager() {
		return instance;
	}

	public static int getEntityManagerInstanceSize() { return instance.size(); }

	public static Set<Entity> getEntityManagerEntities() { return instance.keySet(); }

	public static List<String> getEntityClassList() {
		List<String> string = new ArrayList<>();
		for(Entity entity : instance.keySet()) {
			string.add(entity.getClass().getName());
		}
		return string;
	}

	public static boolean isRegister(Entity e) {
		if(instance.containsKey(e)) return true;
		return false;
	}

	// 일반적인 몹
	public static EntityManager getinstance(Entity e) {
		
		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e));
		return instance.get(e);
	}

	// 변신 하지 않는 몹들 중 등록된 몹
	public static EntityManager getinstance(Entity e, MobListManager.MobList mobList) {

		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, mobList.getHealth(), mobList));
		return instance.get(e);
	}

	public static EntityManager getinstance(Entity e, HashMap<Entity, Location> disguises, String CustomName, int Maxhealth) {
		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, Maxhealth, CustomName, disguises));
		return instance.get(e);
	}

	// 변신하고 등록된 몹
	public static EntityManager getinstance(Entity e, MobListManager.MobList mobList, HashMap<Entity, Location> disguises) {

		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, mobList.getHealth(), mobList, disguises));
		return instance.get(e);
	}
	
	public static boolean checkinstance(Entity e) {
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

	public boolean isDisguiseEntityPlayer() {
		if(disguise.get(e) != null) {
			if (disguise.get(e).keySet().toArray()[0] instanceof Player) {
				return true;
			}
		}
		return false;
	}
	public Player getDisguiseEntityPlayer() {
		if(isDisguiseEntityPlayer()) {
			return (Player) disguise.get(e).keySet().toArray()[0];
		}
		return null;
	}

	public static Entity getDisguiseEntity(Entity e) {
		for(Entity entity : disguise.keySet()) {
			for(Entity disguise : disguise.get(entity).keySet()) {
				if(disguise == e) {
					return entity;
				}
			}
		}
		return null;
	}

	public static List<EntityPlayer> getDisguiseEntitiesPlayer() {

		List<EntityPlayer> eP = new ArrayList<>();

		for(Entity aL : disguise.keySet()) {
			if(EntityManager.getinstance(aL).isDisguiseEntityPlayer()) {
				eP.add(((CraftPlayer) EntityManager.getinstance(aL).getDisguiseEntityPlayer()).getHandle());
			}
		}
		return eP;
	}

	public EntityManager setShowNameTag(boolean value) {
		this.showNameTag = value;
		return this;
	}

	public EntityManager setHeight(double Height) {
		this.Height = Height;
		return this;
	}

	public EntityManager setAmbientParticle(Method method, Object instance) {

		particleMethod = method;
		EntityInstance = instance;

		return this;
	}

	public EntityManager setAmbientAbility(Method method, Object entityInstance) {
		ambientAbilityMethod = method;
		EntityInstance = entityInstance;

		return this;
	}

	public EntityManager setDeathAbility(Method method, Object entityInstance) {
		deathMethod = method;
		EntityInstance = entityInstance;

		return this;
	}

	public EntityManager setAttackAbility(Method method, Object entityInstance)	{
		attackMethod = method;
		EntityInstance = entityInstance;
		return this;
	}

	public EntityManager addDropTable(ItemStack itemStack, double percent) {
		dropTable.put(itemStack, percent);
		return this;
	}

	public EntityManager setTeleportToEntity(LivingEntity teleportTo) {
		this.teleportTo = teleportTo;
		return this;
	}

	public EntityManager setTeleportToEntityLoc(Location location) {
		this.teleportToLoc = location;
		return this;
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

	public void setDamageValue(int var0, Entity damager) {

		Location loc = e.getBoundingBox().getCenter().toLocation(e.getWorld());
		loc.getWorld().spawnParticle(Particle.BLOCK_DUST, loc,10, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.createBlockData());

		if(var0 > getCurrentHealth()) var0 = getCurrentHealth();

		if(damager instanceof Player player) {
			if(contribute.containsKey(player)) {
				contribute.replace(player, contribute.get(player) + var0);
			}
			else {
				contribute.put(player, var0);
			}
		}

		if(isDisguiseEntityPlayer()) {
			Player wrapper = getDisguiseEntityPlayer();

			for(Player player : Bukkit.getOnlinePlayers()) {
				PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
				playerConnection.sendPacket(new PacketPlayOutAnimation(((CraftPlayer) wrapper).getHandle(), 1));
			}

		}

		CurrentHealth -= var0;
		DamageAfterDelay = 100;
		AttackedByAbility();
		latestDamager = damager;
	}

	public void setDamageValue(int var0) {
		CurrentHealth -= var0;

		if(isDisguiseEntityPlayer()) {
			Player wrapper = getDisguiseEntityPlayer();

			for(Player player : Bukkit.getOnlinePlayers()) {
				PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
				playerConnection.sendPacket(new PacketPlayOutAnimation(((CraftPlayer) wrapper).getHandle()
						, 1));
			}
		}
		DamageAfterDelay = 100;
	}

	
	public int getMaxHealth() {
		return MaxHealth;
	}
	public int setMaxHealth(int Health) { return MaxHealth = Health; }

	public ArmorStand getNameArmorStand() {
		return Namear;
	}

	public String getCustomName() { return CustomName;}
	public void setCustomName(String customName) { this.CustomName = customName; }

	public void updateCustomName() {
		CustomName = "§6[Lv."+Level+"] §c "+mobList.name().replaceAll("_", " ");
		if(Namear != null) Namear.remove();
		Namear = null;
	}

	public void setCurrentHealth(int currentHealth) {
		CurrentHealth = currentHealth;
	}

	public MobListManager.MobList getMobList() {
		return mobList;
	}

	public HashMap<Player, Integer> getContribute() { return contribute; }
	public List<Player> getDropItemContribute() {
		List<Player> list = new ArrayList<>();
		for(Player p : contribute.keySet()) {
			if((double)contribute.get(p) / (double)MaxHealth > 0.05) list.add(p);
		}
		return list;
	}
	
	@SuppressWarnings("deprecation")
	public synchronized void EntityWatcher() {
		
		if(!(e instanceof Player) && (e instanceof LivingEntity) && !(e instanceof ArmorStand)) {

			e.setCustomName(" ");
			e.setCustomNameVisible(false);
			//((LivingEntity) e).setHealth(((LivingEntity) e).getMaxHealth());

			isinChunk = false;
			// 엔티티 청크 체커
			for(Player player : Bukkit.getOnlinePlayers()) {
				World world = e.getWorld();
				Location location = e.getLocation();

				if(!world.equals(player.getWorld())) continue;

				if(player.getLocation().distance(location) < 70) {
					isinChunk = true;
					break;
				}
			}

			// 죽었을때
			if(CurrentHealth <= 0 || e.isDead() || !isinChunk) {

				if(mobList != null) {
					// event call
					Bukkit.getPluginManager().callEvent(new CustomMobDeathEvent(e, mobList));
				}

				if(Namear !=null) {
					Namear.remove();
				}
				if(Healthar != null) {
					Healthar.remove();
				}

				e.setCustomName(" ");
				if(e instanceof LivingEntity) {
					((LivingEntity) e).setHealth(0);
				}
				else
					e.remove();

				// 변신한 몹들 가져오기
				if(disguise.containsKey(e)) {
					for(Entity disguise : this.getDisguises()) {

						if(disguise instanceof Player) {
							EntityPlayer entityPlayer = ((CraftPlayer) disguise).getHandle();

							((CraftPlayer) disguise).getHandle().setHealth(0);
							Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
								EntityPlayerWatcher.Remove(entityPlayer);
								((CraftPlayer) disguise).getHandle().setRemoved(net.minecraft.world.entity.Entity.RemovalReason.a);
							}, 20);
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
							disguise.remove();
						}, 20);
					}
				}

				if(isDisguiseEntityPlayer()) {
					Player wrapper = getDisguiseEntityPlayer();
					EntityPlayer eP = ((CraftPlayer) wrapper).getHandle();
					eP.setPose(EntityPose.h);
				}

				if(teleportTo != null) {
					teleportTo.setHealth(0);
				}

				DeathAbility();
				invokeDropTable();

				if(e instanceof LivingEntity)
					EntityStatusManager.getinstance((LivingEntity) e).removeinstance();
				removeinstance();
				return;
			}

			if(Namear == null && showNameTag && !CustomName.equals("")) {

				Namear = (ArmorStand) e.getWorld().spawnEntity(e.getLocation().add(0, Height+HeightTempAdd, 0), EntityType.ARMOR_STAND);

				Namear.setCustomName(CustomName);
				Namear.setCustomNameVisible(true);
				Namear.setInvisible(true);
				Namear.setSmall(true);
				Namear.setMarker(true);
				Namear.setInvulnerable(true);
				Namear.setBasePlate(false);
				Namear.setGravity(false);
				Namear.setSilent(true);
			}

			// 피해를 받았을때
			if(((CurrentHealth < MaxHealth) && DamageAfterDelay == 100) && !CustomName.equals("")) {

				if(Namear != null) Namear.remove();

				// 아머스탠드 소환
				Namear = (ArmorStand) e.getWorld().spawnEntity(e.getLocation().add(0, Height+0.25, 0), EntityType.ARMOR_STAND);

				Namear.setCustomName(CustomName);
				Namear.setCustomNameVisible(true);
				Namear.setInvisible(true);
				Namear.setSmall(true);
				Namear.setMarker(true);
				Namear.setInvulnerable(true);
				Namear.setBasePlate(false);
				Namear.setGravity(false);
				Namear.setSilent(true);

			}
			if((Healthar == null && CurrentHealth < MaxHealth) && DamageAfterDelay == 100 && CustomName != "") {

				Healthar = (ArmorStand) e.getWorld().spawnEntity(e.getLocation().add(0, Height, 0), EntityType.ARMOR_STAND);
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
				String arname = "[||||"+CurrentHealth+"||||]";
				List<Character> arrlist = new ArrayList<>();
				char[] arr = arname.toCharArray();
				for(int i=0; i<arr.length; i++) {
					arrlist.add(arr[i]);
				}

				double rate = (double) CurrentHealth / (double) MaxHealth;
				int index = (int)(arr.length * rate);
				arrlist.add(index, '3');
				arrlist.add(index, '§');

				String customname = "§b";
				for(char ch : arrlist) {
					customname += ch;
				}
				Healthar.setCustomName(customname);

				Healthar.teleport(e.getLocation().add(0, Height, 0));
			}

			if(Namear != null && DamageAfterDelay != 0) {
				Namear.teleport(e.getLocation().add(0, Height+0.25, 0));
			}
			else if(Namear != null) {
				Namear.teleport(e.getLocation().add(0, Height, 0));
			}
		}

		// 패턴 시간 사이클
		if(patterntime == 3600) patterntime = 0;

		patterntime++;

		// disguise 엔티티 텔레포트
		for(Entity entity : disguise.keySet()) {

			// 기준 엔티티의 좌표
			final Location location = entity.getLocation();

			// 엔티티가 변신한 엔티티의 목록과 좌표
			HashMap<Entity, Location> disguises = this.disguise.get(entity);

			// 변신할 엔티티를 순환하며 좌표로 이동
			for(Entity disentity : disguises.keySet()) {

				// set fallingblock's time to 1
				if(disentity instanceof FallingBlock) {
					((FallingBlock) disentity).setTicksLived(1);
					disentity.teleport(location.clone().add(disguises.get(disentity)));
				}
				if(disentity instanceof Player) {
					Location loc = disguises.get(disentity).add(location);
					((CraftPlayer) disentity).getHandle().setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
				}
				else {
					disentity.teleport(location.clone().add(disguises.get(disentity)));
				}
			}
		}
		// TeleportTo 엔티티에게 텔레포트
		if(teleportTo != null) {
			e.teleport(teleportTo.getLocation().clone().add(teleportToLoc));
		}



		// 피해를 받은후 5초가 지나면
		if(DamageAfterDelay > 0) DamageAfterDelay--;

		// 투명화 된 엔티티는 이름과 체력 없애기
		if(DamageAfterDelay == 1) {

			if(!showNameTag) {

				if(Namear != null) Namear.remove();
				Namear = null;

			}
			else if(showNameTag) {

				if(Namear != null) Namear.remove();
				Namear = null;
			}


			HeightTempAdd = 0.25;

			if(Healthar != null) Healthar.remove();
			Healthar = null;

		}


		AmbientParticleCycle();
		AmbientAbilityCycle();
	}

	public void AmbientParticleCycle() {
		if(particleMethod == null) return;
		try {
			particleMethod.invoke(EntityInstance);
		}
		catch(Exception e) {

		}
	}

	public void AmbientAbilityCycle() {
		if(ambientAbilityMethod == null) return;
		try {
			ambientAbilityMethod.invoke(EntityInstance);
		}
		catch(Exception e) {

		}

	}

	public void DeathAbility() {
		if(deathMethod == null) return;
		try {
			deathMethod.invoke(EntityInstance);
		}
		catch(Exception e) {

		}
	}

	public void AttackAbility() {
		if(attackMethod == null) return;
		try {
			attackMethod.invoke(EntityInstance);
		}
		catch(Exception e){

		}
	}

	public void AttackedByAbility() {
		if(attackedByMethod == null) return;
		try {
			attackedByMethod.invoke(EntityInstance);
		}
		catch(Exception e) {

		}
	}

	public void invokeDropTable() {
		for(Player player : getDropItemContribute()) {
			for(ItemStack item : dropTable.keySet()) {
				if(Math.random() <= dropTable.get(item)) {
					Item it = (Item) e.getWorld().spawnEntity(e.getLocation(), EntityType.DROPPED_ITEM);
					it.setItemStack(item);
					it.setOwner(player.getUniqueId());
				}
			}
		}
	}

	public static void CraftNPCRefresh() {

		for(EntityManager entityManager : instance.values()) {

			Location eloc = entityManager.e.getLocation();

			if(entityManager.isDisguiseEntityPlayer()) {

				Player eP = entityManager.getDisguiseEntityPlayer();
				EntityPlayer EP = ((CraftPlayer) eP).getHandle();

				for(Player player : Bukkit.getOnlinePlayers()) {
					if(eloc.getWorld().getName().equals(player.getWorld().getName())) {
						if(eloc.distance(player.getLocation()) > 20) {

							EntityPlayerManager.getInstance().showTo(EP, player);
						}
					}

				}

			}

		}

	}

	public static void showEntityPlayerNPC(Player player) {
		for(Entity lE : instance.keySet()) {
			if(getinstance(lE).isDisguiseEntityPlayer()) {
				Player dP = getinstance(lE).getDisguiseEntityPlayer();
				EntityPlayer eP = ((CraftPlayer) dP).getHandle();
				EntityPlayerManager.getInstance().showTo(eP, player);
			}
		}
	}

	public static void DeleteAllEntity() {
		for(Entity lE : instance.keySet()) {
			instance.get(lE).setCurrentHealth(0);
		}
	}


}

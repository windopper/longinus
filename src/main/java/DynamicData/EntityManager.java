package DynamicData;

import CustomEvents.CustomMobDeathEvent;
import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import Mob.MobListManager;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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
	private String CustomName = "";
	private int CurrentHealth;
	private int PreviousHealth;
	private int MaxHealth;
	private int patterntime = 0;
	private boolean isinChunk = false;
	private int DamageAfterDelay = 0;
	private MobListManager.MobList mobList;
	private double Height = 0;
	
	private EntityManager(@Nonnull LivingEntity e) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = (int)(e.getHealth()*500);
		MaxHealth = (int)(e.getMaxHealth()*500);
		Height = e.getHeight();
	}

	private EntityManager(@Nonnull LivingEntity e, int maxhealth, String CustomName, HashMap<Entity, Location> disguises) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = maxhealth;
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
		this.CustomName = CustomName;
		if(isDisguiseEntityPlayer()) Height = getDisguiseEntityPlayer().getHeight();
		else Height = e.getHeight();

	}



	private EntityManager(@Nonnull LivingEntity e, int maxhealth, MobListManager.MobList mobList) {
		this.e = e;
		if(e==null) Bukkit.broadcastMessage("null!");
		CurrentHealth = maxhealth;
		PreviousHealth = maxhealth;
		MaxHealth = maxhealth;
		this.mobList = mobList;
		CustomName = mobList.getName();
		Height = e.getHeight();
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
		if(isDisguiseEntityPlayer()) Height = getDisguiseEntityPlayer().getHeight();
		else Height = e.getHeight();
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

	public static EntityManager getinstance(LivingEntity e, HashMap<Entity, Location> disguises, String CustomName, int Maxhealth) {
		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, Maxhealth, CustomName, disguises));
		return instance.get(e);
	}

	// 변신하고 등록된 몹
	public static EntityManager getinstance(LivingEntity e, MobListManager.MobList mobList, HashMap<Entity, Location> disguises) {

		if(!instance.containsKey(e)) instance.put(e, new EntityManager(e, mobList.getHealth(), mobList, disguises));
		return instance.get(e);
	}
	
	public static boolean checkinstance(LivingEntity e) {
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

	public static LivingEntity getMasterEntity(EntityPlayer player) {

		for(LivingEntity Master : disguise.keySet()) {
			if(getinstance(Master).getDisguiseEntityPlayer() == null) {
				return null;
			}
			else if(getinstance(Master).getDisguiseEntityPlayer().equals(player)) {
				return Master;
			}
		}
		return null;
	}

	public static List<EntityPlayer> getDisguiseEntitiesPlayer() {

		List<EntityPlayer> eP = new ArrayList<>();

		for(LivingEntity aL : disguise.keySet()) {
			if(EntityManager.getinstance(aL).isDisguiseEntityPlayer()) {
				eP.add(((CraftPlayer) EntityManager.getinstance(aL).getDisguiseEntityPlayer()).getHandle());
			}
		}
		return eP;
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

//			if(CustomName == null && e.getCustomName() != null) {
//				CustomName = e.getCustomName();
//				//EntityNamePacketSender.getInstance().SendPacket(e);
//			}

			// 엔티티 청크 체커
			for(Player player : Bukkit.getOnlinePlayers()) {
				World world = e.getWorld();
				Location location = e.getLocation();

				if(!world.equals(player.getWorld())) continue;

				if(player.getLocation().distance(location) < 100) {
					isinChunk = true;
					break;
				}
				else {
					isinChunk = false;
				}
			}

			if(e.getHealth()<=0) Bukkit.broadcastMessage("is Dead?");

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

						if(disguise instanceof Player) {
							EntityPlayer entityPlayer = ((CraftPlayer) disguise).getHandle();
							EntityPlayerWatcher.Remove(entityPlayer);

							((CraftPlayer) disguise).getHandle().setRemoved(net.minecraft.world.entity.Entity.RemovalReason.a);
						}

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


			if(CurrentHealth<MaxHealth && DamageAfterDelay == 100)
				e.setCustomName(" ");
			// 피해를 받았을때
			if(((Namear == null && CurrentHealth < MaxHealth) && DamageAfterDelay == 100) || (isDisguiseEntityPlayer() && Namear == null)) {

				// 아머스탠드 소환
				if(isDisguiseEntityPlayer())
					Namear = (ArmorStand) e.getWorld().spawnEntity(e.getLocation().add(0, Height, 0), EntityType.ARMOR_STAND);
				else
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
			if((Healthar == null && CurrentHealth < MaxHealth) && DamageAfterDelay == 100) {

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
					disentity.teleport(location.clone().add(disguise.get(disentity)));
				}
				if(disentity instanceof Player) {

					Location loc = disguise.get(disentity).add(location);
					((CraftPlayer) disentity).getHandle().setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

				}
			}
		}


		// 피해를 받은후 5초가 지나면
		if(DamageAfterDelay > 0) DamageAfterDelay--;

		// 투명화 된 엔티티는 이름과 체력 없애기
		if(DamageAfterDelay == 0) {


			// 본체가 투명화라면
			if(isDisguiseEntityPlayer()) {

			}
			else if(e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {

				if(Namear != null) Namear.remove();
				Namear = null;

			}
			else if(!e.hasPotionEffect(PotionEffectType.INVISIBILITY)) {

				e.setCustomName(getCustomName());
				if(Namear != null) Namear.remove();
				Namear = null;
			}

			if(Healthar != null) Healthar.remove();
			Healthar = null;

		}
	}

	public static void showEntityPlayerNPC(Player player) {
		for(LivingEntity lE : instance.keySet()) {
			if(getinstance(lE).isDisguiseEntityPlayer()) {
				Player dP = getinstance(lE).getDisguiseEntityPlayer();
				EntityPlayer eP = ((CraftPlayer) dP).getHandle();

				(new EntityPlayerManager()).showTo(eP, player);
			}
		}
	}


}

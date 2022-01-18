package PlayerManager;

import Exceptions.UndefinedFunctionError;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;
import spellinteracttest.RandomRange;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class PlayerManager {
	
	public static FileConfiguration config;
	
	private final static int DEFAULT_HEALTH = 10000;
	
	private static final HashMap<Player, PlayerManager> instance = new HashMap<>();

	public String AskDeleteClassName;
	public String CurrentClass = "없음";
	public int CurrentClassNumber = 0;
	public String WeaponClass = "없음";
	public int MinDamage = 0;
	public int MaxDamage = 0;
	public int Health = DEFAULT_HEALTH;
	public int MaxShield = (int)(Health / 10);
	public int SpellDamage = 0;
	public int EnergyPerSecond = 1;
	public int Shield = 0;
	public int WalkSpeed = 0;
	public int ManaDecrease = 0;

	public int addiWalkSpeed = 0;
	public int nextManaDecrease = 0;

	public double damageTakenRate = 1;

	public CopyOnWriteArrayList<String> evasion = new CopyOnWriteArrayList<>();
	public List<String> dummyCount = new ArrayList<>();
	public ConcurrentHashMap<String, Integer> dummyValue = new ConcurrentHashMap<>();
	public ConcurrentHashMap<String, Integer> dummyIncrease = new ConcurrentHashMap<>();
	public Set<Runnable> runWhenDamaged = Collections.synchronizedSet(new HashSet<>());
	public Set<Runnable> runWhenAttack = Collections.synchronizedSet(new HashSet<>());
	public Set<Function<Integer, Integer>> takeDamageModifier = Collections.synchronizedSet(new HashSet<>());
	public Set<Function<Integer, Integer>> giveDamageModifier = Collections.synchronizedSet(new HashSet<>());

	public List<String> tabContents = new ArrayList<>();

	public int Str = 0;
	public int Dex = 0;
	public int Def = 0;
	public int Agi = 0;
	public int lvl = 1;
	public int exp = 0;
	public int remainstat = 0;

	private int[] FRTalent = {0, 0, 0, 0};
	private int[] RLTalent = {0, 0, 0, 0};
	private int[] RRTalent = {0, 0, 0, 0};
	private int[] SRTalent = {0, 0, 0, 0};
	private int talentPoint = 0;
	
	public int WeaponLevelreq = 0;
	public int WeaponStrreq = 0;
	public int WeaponDexreq = 0;
	public int WeaponDefreq = 0;
	public int WeaponAgireq = 0;
	
	public int HelmetLevelreq = 0;
	public int HelmetStrreq = 0;
	public int HelmetDexreq = 0;
	public int HelmetDefreq = 0;
	public int HelmetAgireq = 0;
	
	public int ChestplateLevelreq = 0;
	public int ChestplateStrreq = 0;
	public int ChestplateDexreq = 0;
	public int ChestplateDefreq = 0;
	public int ChestplateAgireq = 0;
	
	public int LeggingsLevelreq = 0;
	public int LeggingsStrreq = 0;
	public int LeggingsDexreq = 0;
	public int LeggingsDefreq = 0;
	public int LeggingsAgireq = 0;
	
	public int BootsLevelreq = 0;
	public int BootsStrreq = 0;
	public int BootsDexreq = 0;
	public int BootsDefreq = 0;
	public int BootsAgireq = 0;

	public int ChipAbilityCoolDown = 0;

	private Player p;

	public PlayerManager() {}
	
	public PlayerManager(Player p) {
		this.p = p;
	}

	public static PlayerManager getinstance(@Nonnull Player p) {

		if(!instance.containsKey(p)) {
			instance.put(p, new PlayerManager(p));
			p.sendMessage("UserManager successfully initialized");
		}
		return instance.get(p);
	}
	
	public void removeinstance() {
		instance.remove(p);
	}

	public static void updateloop() {
		
		for(Player p : instance.keySet()) {
			getinstance(p).updatePlayerInfo();
		}
	}

	public int getStr() {
		return Str;
	}
	public int getDex() {
		return Dex;
	}
	public int getDef() {
		return Def;
	}
	public int getAgi() {
		return Agi;
	}
	public int getremainstat() {
		return remainstat = lvl * 2 - Str - Dex - Def - Agi;
	}
	public int getlvl() {
		return lvl;
	}
	public int getexp()	{
		return exp;
	}
	public void setStr(int Str) {
		this.Str = Str;
	}
	public void setDex(int Dex) {
		this.Dex = Dex;
	}
	public void setDef(int Def) {
		this.Def = Def;
	}
	public void setAgi(int Agi) {
		this.Agi = Agi;
	}
	public void setlvl(int lvl) {
		this.lvl = lvl;
	}
	public void setexp(int exp) {
		this.exp = exp;
	}
	public enum RunType {
		whenAttack,
		whenDamaged,
		takeDamageModf,
		giveDamageModf;
	}
	public void addRunWhenAttack(Runnable runnable, int tick) {
		runWhenAttack.add(runnable);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> runWhenAttack.remove(runnable), tick);
	}
	public void addRunWhenDamaged(Runnable runnable, int tick) {
		runWhenDamaged.add(runnable);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> runWhenDamaged.remove(runnable), tick);
	}
	public void addTakeDamageModifier(Function<Integer, Integer> function, int tick) {
		takeDamageModifier.add(function);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> takeDamageModifier.remove(function), tick);
	}
	public void addGiveDamageModifier(Function<Integer, Integer> function, int tick) {
		giveDamageModifier.add(function);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> giveDamageModifier.remove(function), tick);
	}
	public int getTalentPoint() {
		updateTalentPoint();
		return talentPoint;
	}
	public List<Integer> getTalentList(String skill) {
		if(skill.equals("FR")) return Arrays.stream(FRTalent).boxed().toList();
		else if(skill.equals("RL")) return Arrays.stream(RLTalent).boxed().toList();
		else if(skill.equals("RR")) return Arrays.stream(RRTalent).boxed().toList();
		else if(skill.equals("SR")) return Arrays.stream(SRTalent).boxed().toList();
		return Arrays.asList(0, 0, 0, 0);
	}
	public int getTalent(String skill, int tier) {

		updateTalentPoint();

		if(tier>4) return 0;
		if(skill.equals("FR")) return FRTalent[tier-1];
		else if(skill.equals("RL")) return RLTalent[tier-1];
		else if(skill.equals("RR")) return RRTalent[tier-1];
		else if(skill.equals("SR")) return SRTalent[tier-1];
		return 0;
	}
	public void setTalent(String skill, int tier, int talent) {
		boolean canchange = false;
		if(skill.equals("FR") && FRTalent[tier-1] != 0) canchange = true;
		else if(skill.equals("RL") && RLTalent[tier-1] != 0) canchange = true;
		else if(skill.equals("RR") && RRTalent[tier-1] != 0) canchange = true;
		else if(skill.equals("SR") && SRTalent[tier-1] != 0) canchange = true;
		if(canchange) {
			changeTalent(skill, tier, talent);
			return;
		}

		if(talentPoint - tier < 0) {
			p.sendMessage("§c특성 포인트가 부족합니다!");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
			return;
		}
		if(tier>4) return;
		if(skill.equals("FR")) FRTalent[tier-1] = talent;
		else if(skill.equals("RL")) RLTalent[tier-1] = talent;
		else if(skill.equals("RR")) RRTalent[tier-1] = talent;
		else if(skill.equals("SR")) SRTalent[tier-1] = talent;

		updateTalentPoint();
	}
	public void changeTalent(String skill, int tier, int talent) {
		if(skill.equals("FR")) FRTalent[tier-1] = talent;
		else if(skill.equals("RL")) RLTalent[tier-1] = talent;
		else if(skill.equals("RR")) RRTalent[tier-1] = talent;
		else if(skill.equals("SR")) SRTalent[tier-1] = talent;
	}
	public void setTalent(List<Integer> list, String skill) {
		if(list.size() != 4) {
			list = new ArrayList<>();
			list.set(0, 0);
			list.set(1, 0);
			list.set(2, 0);
			list.set(3, 0);
		}
		if(skill.equals("FR")) FRTalent =  list.stream().mapToInt(i -> i).toArray();
		else if(skill.equals("RL")) RLTalent = list.stream().mapToInt(i -> i).toArray();
		else if(skill.equals("RR")) RRTalent = list.stream().mapToInt(i -> i).toArray();
		else if(skill.equals("SR")) SRTalent = list.stream().mapToInt(i -> i).toArray();
	}
	public void resetTalent(String skill) {
		if(skill.equals("FR")) FRTalent = Arrays.asList(0, 0, 0, 0).stream().mapToInt(i->i).toArray();
		else if(skill.equals("RL")) RLTalent = Arrays.asList(0, 0, 0, 0).stream().mapToInt(i->i).toArray();
		else if(skill.equals("RR")) RRTalent = Arrays.asList(0, 0, 0, 0).stream().mapToInt(i->i).toArray();
		else if(skill.equals("SR")) SRTalent = Arrays.asList(0, 0, 0, 0).stream().mapToInt(i->i).toArray();
		updateTalentPoint();
	}
	public void resetAllTalent() {
		resetTalent("RR");
		resetTalent("RL");
		resetTalent("SR");
		resetTalent("FR");

		updateTalentPoint();
	}
	public int getNextTier(String skill) {
		if(skill.equals("FR")) {
			for(int i=0; i<4; i++) {
				if(FRTalent[i] == 0) return i+1;
			}
		}
		else if(skill.equals("RL")) {
			for(int i=0; i<4; i++) {
				if(RLTalent[i] == 0) return i+1;
			}
		}
		else if(skill.equals("RR")) {
			for(int i=0; i<4; i++) {
				if(RRTalent[i] == 0) return i+1;
			}
		}
		else if(skill.equals("SR")) {
			for(int i=0; i<4; i++) {
				if(SRTalent[i] == 0) return i+1;
			}
		}
		return 4;
	}
	public void updateTalentPoint() {
		int point = (int)((double)lvl/5);
		for(int i=0; i<4; i++) {
			if(FRTalent[i] != 0) point -= i+1;
			if(RRTalent[i] != 0) point -= i+1;
			if(SRTalent[i] != 0) point -= i+1;
			if(RLTalent[i] != 0) point -= i+1;
		}
		talentPoint = point;
	}
	
	public void setmap() {
		WeaponClass = "없음";
		MinDamage = 0;
		MaxDamage = 0;
		Health = DEFAULT_HEALTH;
		MaxShield = (int)(Health / 10);
		SpellDamage = 0;
		EnergyPerSecond = 1;
		Shield = 0;
		WalkSpeed = 0;
		ManaDecrease = 0;
	}

	public void statadd(Player p, String stat, int amount) {

		remainstat = lvl * 2 - Str - Dex - Def - Agi;

		if(remainstat - amount < 0) {
			statadd(p, stat, remainstat);
		}

		if(remainstat <= 0) {
			p.sendMessage("§c남은 스탯이 없습니다");
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 2f);
			return;
		}

		if(stat.equals("str")) {

			remainstat-=amount;
			Str+=amount;
			return;
		}
		if(stat.equals("dex")) {

			remainstat-=amount;
			Dex+=amount;
			return;
		}
		if(stat.equals("def")) {

			remainstat-=amount;
			Def+=amount;
			return;
		}
		if(stat.equals("agi")) {

			remainstat -= amount;
			Agi += amount;
			return;
		}
	}

	public void statreset() {
		Str = 0;
		Dex = 0;
		Def = 0;
		Agi = 0;
	}

	public void updatePlayerInfo() {

		dummyIncrease.keySet().forEach((a)->dummyIncrease.replace(a, dummyIncrease.get(a)+1));
		
		WeaponClass = "없음";
		int mindamage = 0;
		int maxdamage = 0;
		int health = DEFAULT_HEALTH;
		int spelldamage = 0;
		int shield = 0;
		int walkspeed = 0;
		int energypersecond = 1;
		int manadecrease = 0;
		int shieldraw = Health / 10;
		int count = 0; // 순서 세기
		
		WeaponLevelreq = 0;
		WeaponStrreq = 0;
		WeaponDexreq = 0;
		WeaponDefreq = 0;
		WeaponAgireq = 0;
		
		HelmetLevelreq = 0;
		HelmetStrreq = 0;
		HelmetDexreq = 0;
		HelmetDefreq = 0;
		HelmetAgireq = 0;
		
		ChestplateLevelreq = 0;
		ChestplateStrreq = 0;
		ChestplateDexreq = 0;
		ChestplateDefreq = 0;
		ChestplateAgireq = 0;
		
		LeggingsLevelreq = 0;
		LeggingsStrreq = 0;
		LeggingsDexreq = 0;
		LeggingsDefreq = 0;
		LeggingsAgireq = 0;
		
		BootsLevelreq = 0;
		BootsStrreq = 0;
		BootsDexreq = 0;
		BootsDefreq = 0;
		BootsAgireq = 0;

		/*
		 * 1 헬멧
		 * 2 갑옷
		 * 3 레깅스
		 * 4 부츠
		 * 5 무기
		 * 
		 * 
		 * 
		 */

		for(ItemStack equipments : getplayerequipments(p)) {
			count++;

			if(equipments.getItemMeta() == null || equipments.getItemMeta().getLore() == null) continue;
			net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(equipments);
			if(!nmsStack.hasTag()) continue;
			NBTTagCompound tag = nmsStack.getTag();
			if(tag.getString("클래스제한").equals("")) continue;

			if(count==5) {
				WeaponClass = tag.getString("클래스제한");
			}

			if(count==1) {
				HelmetLevelreq = tag.getInt("레벨제한");
				HelmetStrreq = tag.getInt("무기강화제한");
				HelmetDexreq = tag.getInt("감각강화제한");
				HelmetDefreq = tag.getInt("외피강화제한");
				HelmetAgireq = tag.getInt("기동강화제한");
				if(getlvl()<HelmetLevelreq) continue;
				if(getStr()<HelmetStrreq) continue;
				if(getDex()<HelmetDexreq) continue;
				if(getDef()<HelmetDefreq) continue;
				if(getAgi()<HelmetAgireq) continue;
			}
			else if(count==2) {
				ChestplateLevelreq = tag.getInt("레벨제한");
				ChestplateStrreq = tag.getInt("무기강화제한");
				ChestplateDexreq = tag.getInt("감각강화제한");
				ChestplateDefreq = tag.getInt("외피강화제한");
				ChestplateAgireq = tag.getInt("기동강화제한");
				if(getlvl()<ChestplateLevelreq) continue;
				if(getStr()<ChestplateStrreq) continue;
				if(getDex()<ChestplateDexreq) continue;
				if(getDef()<ChestplateDefreq) continue;
				if(getAgi()<ChestplateAgireq) continue;
			}
			else if(count==3) {
				LeggingsLevelreq = tag.getInt("레벨제한");
				LeggingsStrreq = tag.getInt("무기강화제한");
				LeggingsDexreq = tag.getInt("감각강화제한");
				LeggingsDefreq = tag.getInt("외피강화제한");
				LeggingsAgireq = tag.getInt("기동강화제한");
				if(getlvl()<LeggingsLevelreq) continue;
				if(getStr()<LeggingsStrreq) continue;
				if(getDex()<LeggingsDexreq) continue;
				if(getDef()<LeggingsDefreq) continue;
				if(getAgi()<LeggingsAgireq) continue;
			}
			else if(count==4) {
				HelmetLevelreq = tag.getInt("레벨제한");
				HelmetStrreq = tag.getInt("무기강화제한");
				HelmetDexreq = tag.getInt("감각강화제한");
				HelmetDefreq = tag.getInt("외피강화제한");
				HelmetAgireq = tag.getInt("기동강화제한");
				if(getlvl()<HelmetLevelreq) continue;
				if(getStr()<HelmetStrreq) continue;
				if(getDex()<HelmetDexreq) continue;
				if(getDef()<HelmetDefreq) continue;
				if(getAgi()<HelmetAgireq) continue;
			}
			else if(count==5) {
				WeaponLevelreq = tag.getInt("레벨제한");
				WeaponStrreq = tag.getInt("무기강화제한");
				WeaponDexreq = tag.getInt("감각강화제한");
				WeaponDefreq = tag.getInt("외피강화제한");
				WeaponAgireq = tag.getInt("기동강화제한");
				if(getlvl()<WeaponLevelreq) continue;
				if(getStr()<WeaponStrreq) continue;
				if(getDex()<WeaponDexreq) continue;
				if(getDef()<WeaponDefreq) continue;
				if(getAgi()<WeaponAgireq) continue;
			}

			if(WeaponClass.equals(CurrentClass)) {

				String damage[] = tag.getString("데미지").split("-");
				mindamage += Integer.parseInt(damage[0]);
				maxdamage += Integer.parseInt(damage[1]);
			}

			health += getTag(tag, "생명력");
			shield += getTag(tag, "보호막");
			spelldamage += getTag(tag, "스킬데미지");
			walkspeed += getTag(tag, "이동속도");
		}

		MinDamage = mindamage;
		MaxDamage = maxdamage;
		Health = health;
		SpellDamage = spelldamage;
		Shield = shield;
		WalkSpeed = walkspeed + addiWalkSpeed;
		EnergyPerSecond = energypersecond;
		ManaDecrease = manadecrease;
		MaxShield =  (int)((double)shieldraw * ((double)(Shield+100)/100));

		SetWalkSpeed();
		
	}

	private void SetWalkSpeed() {

		if(WalkSpeed > 0) {
			p.setWalkSpeed(((float)WalkSpeed) / 100 / 2 + 0.2f);
		}
		else {
			float ws = ((float)WalkSpeed + 100) / 100 * 0.2f < 0 ? 0 : ((float)WalkSpeed + 100) / 100 * 0.2f;
			p.setWalkSpeed(ws);
		}

	}
	
	public Integer getTag(NBTTagCompound nbtTagCompound, String string) {
		int temp[] = nbtTagCompound.getIntArray(string);
		int ratio = temp[1];
		int min = temp[0];
		int max = temp[2];
		int diff = max -  min;
		int addToMin = diff * ratio / 100;
		return (addToMin +  min);
	}
	
	
	public List<ItemStack> getplayerequipments(Player p){
		
		List<ItemStack> list = new ArrayList<>();

		list.add(p.getInventory().getHelmet() == null ? new ItemStack(Material.AIR, 1) : p.getInventory().getHelmet());
		list.add(p.getInventory().getChestplate() == null ? new ItemStack(Material.AIR, 1) : p.getInventory().getChestplate());
		list.add(p.getInventory().getLeggings() == null ? new ItemStack(Material.AIR, 1) : p.getInventory().getLeggings());
		list.add(p.getInventory().getBoots() == null ? new ItemStack(Material.AIR , 1) : p.getInventory().getBoots());
		list.add(p.getInventory().getItemInMainHand() == null ? new ItemStack(Material.AIR, 1) : p.getInventory().getItemInMainHand());
		
		return list;
	}
	
	
	
	public double statstr(Player p) {
		
		if(getStr()==0) return 1;
		
		double sum = 0;
		for(int i = 1; i<= getStr(); i++) {
			
			double multiply = Math.pow(0.99, i);
			sum += multiply;
		}
		
		return (sum+100)/100;
		
	}
	public double statdex(Player p) {
		

		if(getDex()==0) return 1;
		
		double sum = 0;
		
		for(int i = 1; i<= getDex(); i++) {
			
			double multiply = Math.pow(0.99, i);
			sum += multiply;
		}
		
		double r = Math.random();
		
		if(sum/100 > r) {
			return 2;
		}
		else {
			return 1;
		}
		
		
	}
	public double statdef(Player p) {
		
		if(getDef()==0) return 1;
		
		double sum = 0;
		for(int i = 1; i<= getDef(); i++) {
			
			double multiply = Math.pow(0.99, i);
			sum += multiply;
		}
		
		return 1-(sum/100);
		
	}
	public double statagi(Player p) {
		
		if(getAgi()==0) return 1;
		
		double sum = 0;
		for(int i = 1; i<= getAgi(); i++) {

			double multiply = Math.pow(0.99, i);
			sum += multiply;
		}
		
		return sum;
		
	}
	
	public int spelldmgcalculate(Player p, double spellrate) {

		
		if(PlayerManager.getinstance(p).CurrentClass.equals("Accelerator"))
			return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * spellrate * (SpellDamage+100)/100 * PlayerFunction.getinstance(p).ACRate);
		
		
		return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * spellrate * (SpellDamage+100)/100);
		
	}
	
	public int meleedmgcalculate(Player p, double meleerate) {
		
		if(PlayerManager.getinstance(p).CurrentClass.equals("Accelerator"))
			return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * meleerate * PlayerFunction.getinstance(p).ACRate);
		
		return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * meleerate) ;
	} 
	
	public double defcalculate(Player p) {
		return statdef(p);
	}

	public List<String> getProfile() {
		List<String> profileList = new ArrayList<>();

		return profileList;

	}
	
	
}

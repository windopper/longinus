package userdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ClassAbility.Accelerator;
import net.md_5.bungee.api.ChatColor;
import spellinteracttest.RandomRange;

public class UserManager {
	
	public static FileConfiguration config;
	
	private final static int DEFAULT_HEALTH = 10000;
	
	public final static HashMap<Player, Player> dual = new HashMap<>();
	
	private static final HashMap<Player, UserManager> instance = new HashMap<>();
	
	public String AskDeleteClassName;
	public String CurrentClass = "없음";
	public int CurrentClassNumber = 0;
	public String WeaponClass = "없음";
	public int MinDamage = 0;
	public int MaxDamage = 0;
	public int Health = DEFAULT_HEALTH;
	public int ShieldRaw = (int)(Health / 10);
	public int SpellDamage = 0;
	public int EnergyPerSecond = 1;
	public int Shield = 0;
	public int WalkSpeed = 0;
	public int ManaDecrease = 0;
	
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
	
	
	
	
	private Player p;
	
	public UserManager(Player p) {
		this.p = p;
	}

	public static UserManager getinstance(@Nonnull Player p) {
		if(!instance.containsKey(p)) {
			instance.put(p, new UserManager(p));
			p.sendMessage("UserManager successfully initialized");
		}
		return instance.get(p);
	}
	
	public void removeinstance() {
		instance.remove(p);
	}
	
	
	
	public static void updateloop() {
		
		for(Player p : instance.keySet()) {
			getinstance(p).equipmentsetting();
		}
		
				
	}
	
	public void setmap() {

		WeaponClass = "없음";
		MinDamage = 0;
		MaxDamage = 0;
		Health = DEFAULT_HEALTH;
		ShieldRaw = (int)(Health / 10);
		SpellDamage = 0;
		EnergyPerSecond = 1;
		Shield = 0;
		WalkSpeed = 0;
		ManaDecrease = 0;

	}
	
	
	public void equipmentsetting() {
		
		WeaponClass = "없음";
		int mindamage = 0;
		int maxdamage = 0;
		int health = DEFAULT_HEALTH;
		int spelldamage = 0;
		int shield = 0;
		int walkspeed = 0;
		int energypersecond = 1;
		int manadecrease = 0;
		int shieldraw = (int)(Health / 10);
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
			
			List<String> lores = p.getInventory().getItemInMainHand().getItemMeta().getLore();	
			
			if(count==5) {
				
				for(String stri : lores) { // 데미지보다 먼저 체크하는거 방지하기 위해 미리 클래스 체크

					if(stri.equals("")) continue;
					stri = ChatColor.stripColor(stri);
					String[] spliti = stri.split(" ");
					
					if(spliti[0].equals("클래스제한")) { // 들고 있는 무기의 직업 체크
						WeaponClass = spliti[2];
						break;
					}
					
				}
			}
		
			for(String data : lores) { // 문자열 나누기
				
				if(data.equals("")) continue;
				
				data = ChatColor.stripColor(data);
				String[] split = data.split(" ");

				if(WeaponClass.equals(CurrentClass)) { // 직업이 맞아야지 무기 특성을 유저에 적용가능
					
					if(split[0].equals("데미지:")) { // 무기에서 데미지만 추출하기
						String[] split_2 = split[1].split("-");
						int mindmg = Integer.parseInt(split_2[0]);
						int maxdmg = Integer.parseInt(split_2[1]);
						
						mindamage += mindmg;
						maxdamage += maxdmg;

					}
					
					if(split[0].equals("레벨제한")) {
						if(count==1) {
							HelmetLevelreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getlvl()<HelmetLevelreq) break;
						}
						if(count==2) {
							ChestplateLevelreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getlvl()<ChestplateLevelreq) break;
						}
						if(count==3) {
							LeggingsLevelreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getlvl()<LeggingsLevelreq) break;
						}
						if(count==4) {
							BootsLevelreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getlvl()<BootsLevelreq) break;
						}
						if(count==5) { 
							WeaponLevelreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getlvl()<WeaponLevelreq) break;
						}
						
						
							
					}
					if(split[0].equals("무기강화최소")) {
						if(count==1) {
							HelmetStrreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getStr()<HelmetStrreq) break;
						}
						if(count==2) {
							ChestplateStrreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getStr()<ChestplateStrreq) break;
						}
						if(count==3) {
							LeggingsStrreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getStr()<LeggingsStrreq) break;
						}
						if(count==4) {
							BootsStrreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getStr()<BootsStrreq) break;
						}
						if(count==5) { 
							WeaponStrreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getStr()<WeaponStrreq) break;
						}
					}
					if(split[0].equals("감각강화최소")) {
						if(count==1) {
							HelmetDexreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDex()<HelmetDexreq) break;
						}
						if(count==2) {
							ChestplateDexreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDex()<ChestplateDexreq) break;
						}
						if(count==3) {
							LeggingsDexreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDex()<LeggingsDexreq) break;
						}
						if(count==4) {
							BootsDexreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDex()<BootsDexreq) break;
						}
						if(count==5) { 
							WeaponDexreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDex()<WeaponDexreq) break;
							
						}
					}
					if(split[0].equals("외피강화최소")) {
						if(count==1) {
							HelmetDefreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDef()<HelmetDefreq) break;
						}
						if(count==2) {
							ChestplateDefreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDef()<ChestplateDefreq) break;
						}
						if(count==3) {
							LeggingsDefreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDef()<LeggingsDefreq) break;
						}
						if(count==4) {
							BootsDefreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDef()<BootsDefreq) break;
						}
						if(count==5) { 
							WeaponDefreq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getDef()<WeaponDefreq) break;
						}
					}
					if(split[0].equals("기동강화최소")) {
						if(count==1) {
							HelmetAgireq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getAgi()<HelmetAgireq) break;
						}
						if(count==2) {
							ChestplateAgireq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getAgi()<ChestplateAgireq) break;
						}
						if(count==3) {
							LeggingsAgireq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getAgi()<LeggingsAgireq) break;
						}
						if(count==4) {
							BootsAgireq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getAgi()<BootsAgireq) break;
						}
						if(count==5) { 
							WeaponAgireq = Integer.parseInt(split[2]);
							if(UserStatManager.getinstance(p).getAgi()<WeaponAgireq) break;
						}
					}
					
					
					if(split[0].equals("생명력")) { // 생명력 추출
						
						health += Integer.parseInt(split[1]);
						
					}
					if(split[0].equals("보호막")) { // 보호막 추출
						int a = Integer.parseInt(split[1].replace("%", ""));
						
						shield += a;
					}
					
					if(split[0].equals("스킬데미지")) { // 스킬데미지 추출
						int a = Integer.parseInt(split[1].replace("%", ""));
						
						spelldamage += a;
					}

				}
			}
			
			
		}
		
		
			


		MinDamage = mindamage;
		MaxDamage = maxdamage;
		Health = health;
		SpellDamage = spelldamage;
		Shield = shield;
		WalkSpeed = walkspeed;
		EnergyPerSecond = energypersecond;
		ManaDecrease = manadecrease;
		ShieldRaw =  (int)((double)shieldraw * ((double)(Shield+100)/100));
		
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
		
		if(UserStatManager.getinstance(p).getStr()==0) return 1;
		
		double sum = 0;
		for(int i=1; i<=UserStatManager.getinstance(p).getStr(); i++) {
			
			double multiply = Math.pow(0.99, i);
			sum += multiply;
		}
		
		return (sum+100)/100;
		
	}
	public double statdex(Player p) {
		

		if(UserStatManager.getinstance(p).getDex()==0) return 1;
		
		double sum = 0;
		
		for(int i=1; i<=UserStatManager.getinstance(p).getDex(); i++) {
			
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
		
		if(UserStatManager.getinstance(p).getDef()==0) return 1;
		
		double sum = 0;
		for(int i=1; i<=UserStatManager.getinstance(p).getDef(); i++) {
			
			double multiply = Math.pow(0.99, i);
			sum += multiply;
		}
		
		return 1-(sum/100);
		
	}
	public double statagi(Player p) {
		
		if(UserStatManager.getinstance(p).getAgi()==0) return 1;
		
		double sum = 0;
		for(int i=1; i<=UserStatManager.getinstance(p).getAgi(); i++) {

			double multiply = Math.pow(0.99, i);
			sum += multiply;
		}
		
		return sum;
		
	}
	
	public int spelldmgcalculate(Player p, double spellrate) {

		
		if(Accelerator.rate.containsKey(p))
			return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * spellrate * (SpellDamage+100)/100 * Accelerator.rate.get(p));
		
		
		return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * spellrate * (SpellDamage+100)/100);
		
	}
	
	public int meleedmgcalculate(Player p, double meleerate) {
		
		if(Accelerator.rate.containsKey(p))
			return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * meleerate * Accelerator.rate.get(p));
		
		return (int)(RandomRange.range(MinDamage, MaxDamage) * statdex(p) * statstr(p) * meleerate) ;
	} 
	
	public double defcalculate(Player p) {
		return statdef(p);
	}
	
	
}

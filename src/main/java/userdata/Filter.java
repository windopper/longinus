package userdata;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Filter {
	
	public static int Require(Player p) { // 무기 안
		
		/*
		 0 무기클래스가 할당되지 않은 일반적인 아이템
		 1 무기클래스와 현재 클래스가 같음
		 2 무기클래스가 있으나 현재클래스가 다름
		 3 userrequipmentchecker에 걸림
		 
		 
		 
		 */
		
		String CurrentClass = UserManager.getinstance(p).CurrentClass;
		String WeaponClass = UserManager.getinstance(p).WeaponClass;

		
		if(WeaponClass.equals("없음")) {
			if(p.getInventory().getItemInMainHand() != null) {
				if(p.getInventory().getItemInMainHand().getItemMeta() != null) {
					if(ChatColor.stripColor(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).equals("훈련용 검")) {
						return 1;
					}
				}
			}
		}
		
		if(CurrentClass.equals(WeaponClass)) { // 이름이 같으면
			if(WeaponClass.equals("없음")) { // 근데 무기클래스가 nothing1 이라면
				
				return 0; // 0이면 무기클래스가 할당되지 않은 일반적인 아이템을 듬
			}
			else { // 이름이 같고, 무기 클래스가 nothing1가 아니면
				
				
				if(!UserRequirementChecker(p)) {
					
					//p.sendMessage("§c안됨");
					
					return 3;
				}
				
				return 1; // 1이면 무기클래스가 할당된 아이템을 듬
			}

		}
		else { // 이름이 다르면
			
			if(WeaponClass.equals("없음")) { // 이름이 다르고 무기클래스가 할당 되지 않은 아이템을 들었을때
				return 0;
			}
			else { 
				
			}
			
			return 2; // 2이면 무기클래스가 할당된 아이템을 들었으나 클래스가 다름
		}
		
		

	}
	
	public static void UserRequirementChecker(int req, Player p) {
		int level = UserStatManager.getinstance(p).getlvl();
		if(level<req) {
			p.sendMessage("§c아직 필요레벨을 충족하지 못하였습니다");
		}
	}
	
	
	private static boolean UserRequirementChecker(Player p) {

		int lvl = UserStatManager.getinstance(p).getlvl();
		int str = UserStatManager.getinstance(p).getStr();
		int dex = UserStatManager.getinstance(p).getDex();
		int def = UserStatManager.getinstance(p).getDef();
		int agi = UserStatManager.getinstance(p).getAgi();
		
		if(lvl < UserManager.getinstance(p).WeaponLevelreq || 
				lvl < UserManager.getinstance(p).HelmetLevelreq ||
				lvl < UserManager.getinstance(p).ChestplateLevelreq ||
				lvl < UserManager.getinstance(p).LeggingsLevelreq ||
				lvl < UserManager.getinstance(p).BootsLevelreq) {
			
			p.sendMessage("§c아직 필요레벨을 충족하지 못하였습니다");
			
			return false;
		}
		
		if(str < UserManager.getinstance(p).WeaponStrreq || 
				str < UserManager.getinstance(p).HelmetStrreq ||
				str < UserManager.getinstance(p).ChestplateStrreq ||
				str < UserManager.getinstance(p).LeggingsStrreq ||
				str < UserManager.getinstance(p).BootsStrreq) {
			
			p.sendMessage("§c무기강화스탯이 부족합니다");
			
			return false;
		}
		
		if(dex < UserManager.getinstance(p).WeaponDexreq || 
				dex < UserManager.getinstance(p).HelmetDexreq ||
				dex < UserManager.getinstance(p).ChestplateDexreq ||
				dex < UserManager.getinstance(p).LeggingsDexreq ||
				dex < UserManager.getinstance(p).BootsDexreq) {
			
			p.sendMessage("§c감각강화스탯이 부족합니다");	
			
			return false;
		}
		
		if(def < UserManager.getinstance(p).WeaponDefreq || 
				def < UserManager.getinstance(p).HelmetDefreq ||
				def < UserManager.getinstance(p).ChestplateDefreq ||
				def < UserManager.getinstance(p).LeggingsDefreq ||
				def < UserManager.getinstance(p).BootsDefreq) {
			
			p.sendMessage("§c외피강화스탯이 부족합니다");
			
			return false;
		}
		
		if(agi < UserManager.getinstance(p).WeaponAgireq || 
				agi < UserManager.getinstance(p).HelmetAgireq ||
				agi < UserManager.getinstance(p).ChestplateAgireq ||
				agi < UserManager.getinstance(p).LeggingsAgireq ||
				agi < UserManager.getinstance(p).BootsAgireq) {
			
			p.sendMessage("§c기동강화스탯이 부족합니다");
			
			return false;
		}
		
		
		return true;
	}
	
	

}

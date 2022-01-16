package PlayerManager;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Filter {

	public enum FilterType {
		generalItem,
		sameWpClassAndCurrentClass,
		hasWpClassButDiffCurrentClass,
		filteredWithUserEquipmentChecker,
	}
	
	public static Filter.FilterType Require(Player p) { // 무기 안
		
		/*
		 0 무기클래스가 할당되지 않은 일반적인 아이템
		 1 무기클래스와 현재 클래스가 같음
		 2 무기클래스가 있으나 현재클래스가 다름
		 3 userrequipmentchecker에 걸림
		 */
		
		String CurrentClass = PlayerManager.getinstance(p).CurrentClass;
		String WeaponClass = PlayerManager.getinstance(p).WeaponClass;

		if(WeaponClass.equals("없음")) {
				if(p.getInventory().getItemInMainHand().getItemMeta() != null) {
					if(ChatColor.stripColor(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).equals("훈련용 검")) {
						return Filter.FilterType.sameWpClassAndCurrentClass;
					}
				}
		}
		if(CurrentClass.equals(WeaponClass)) { // 이름이 같으면
			if(WeaponClass.equals("없음")) { // 근데 무기클래스가 nothing1 이라면
				return Filter.FilterType.generalItem; // 0이면 무기클래스가 할당되지 않은 일반적인 아이템을 듬
			}
			else { // 이름이 같고, 무기 클래스가 nothing1가 아니면

				if(!UserRequirementChecker(p)) {
					return Filter.FilterType.filteredWithUserEquipmentChecker;
				}
				return Filter.FilterType.sameWpClassAndCurrentClass; // 1이면 무기클래스가 할당된 아이템을 듬
			}
		}
		else { // 이름이 다르면
			if (WeaponClass.equals("없음")) { // 이름이 다르고 무기클래스가 할당 되지 않은 아이템을 들었을때
				return Filter.FilterType.generalItem;
			}
			return Filter.FilterType.hasWpClassButDiffCurrentClass; // 2이면 무기클래스가 할당된 아이템을 들었으나 클래스가 다름
		}
	}
	
	public static void UserRequirementChecker(int req, Player p) {
		int level = PlayerManager.getinstance(p).getlvl();
		if(level<req) {
			p.sendMessage("§c아직 필요레벨을 충족하지 못하였습니다");
		}
	}
	private static boolean UserRequirementChecker(Player p) {

		int lvl = PlayerManager.getinstance(p).getlvl();
		int str = PlayerManager.getinstance(p).getStr();
		int dex = PlayerManager.getinstance(p).getDex();
		int def = PlayerManager.getinstance(p).getDef();
		int agi = PlayerManager.getinstance(p).getAgi();
		
		if(lvl < PlayerManager.getinstance(p).WeaponLevelreq ||
				lvl < PlayerManager.getinstance(p).HelmetLevelreq ||
				lvl < PlayerManager.getinstance(p).ChestplateLevelreq ||
				lvl < PlayerManager.getinstance(p).LeggingsLevelreq ||
				lvl < PlayerManager.getinstance(p).BootsLevelreq) {
			
			p.sendMessage("§c아직 필요레벨을 충족하지 못하였습니다");
			
			return false;
		}
		
		if(str < PlayerManager.getinstance(p).WeaponStrreq ||
				str < PlayerManager.getinstance(p).HelmetStrreq ||
				str < PlayerManager.getinstance(p).ChestplateStrreq ||
				str < PlayerManager.getinstance(p).LeggingsStrreq ||
				str < PlayerManager.getinstance(p).BootsStrreq) {
			
			p.sendMessage("§c무기강화스탯이 부족합니다");
			
			return false;
		}
		
		if(dex < PlayerManager.getinstance(p).WeaponDexreq ||
				dex < PlayerManager.getinstance(p).HelmetDexreq ||
				dex < PlayerManager.getinstance(p).ChestplateDexreq ||
				dex < PlayerManager.getinstance(p).LeggingsDexreq ||
				dex < PlayerManager.getinstance(p).BootsDexreq) {
			
			p.sendMessage("§c감각강화스탯이 부족합니다");	
			
			return false;
		}
		
		if(def < PlayerManager.getinstance(p).WeaponDefreq ||
				def < PlayerManager.getinstance(p).HelmetDefreq ||
				def < PlayerManager.getinstance(p).ChestplateDefreq ||
				def < PlayerManager.getinstance(p).LeggingsDefreq ||
				def < PlayerManager.getinstance(p).BootsDefreq) {
			
			p.sendMessage("§c외피강화스탯이 부족합니다");
			
			return false;
		}
		
		if(agi < PlayerManager.getinstance(p).WeaponAgireq ||
				agi < PlayerManager.getinstance(p).HelmetAgireq ||
				agi < PlayerManager.getinstance(p).ChestplateAgireq ||
				agi < PlayerManager.getinstance(p).LeggingsAgireq ||
				agi < PlayerManager.getinstance(p).BootsAgireq) {
			
			p.sendMessage("§c기동강화스탯이 부족합니다");
			
			return false;
		}
		return true;
	}
	
	

}

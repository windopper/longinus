package DynamicData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import PlayerData.UserManager;

public class PlayerCombination {
	
	private static PlayerCombination Combination;
	
	private static final HashMap<Player, PlayerCombination> instance = new HashMap<>();
	
	
	private Player p;
	private String keybind = "none";
	private String key1 = "none";
	private String key2 = "none";
	private String keymessage = "none";
	private int keycooldown = 0;
	private ItemStack previousweapon;
	
	public final static List<Player> sneakingleft = new ArrayList<>();
	
	public final static String blank = "                     ";
	
	private PlayerCombination(Player p) {
		this.p=p;
	}
	public static PlayerCombination getinstance(Player p) {
		if(!instance.containsKey(p)) instance.put(p, new PlayerCombination(p));
		return instance.get(p);
	}
	
	public void removeinstance() {
		instance.remove(p);
	}
	
	public String getKeybind() {
		return keybind;
	}
	public String getKey1() {
		return key1;
	}
	public String getKey2() {
		return key2;
	}
	public int getKeycooldown() {
		return keycooldown;
	}
	public String getKeymessage() {
		return keymessage;
	}
	
	public void setKey1(String key1) {
		this.key1 = key1;
	}
	public void setKeybind(String keybind) {
		this.keybind = keybind;
	}
	public void setKey2(String key2) {
		this.key2 = key2;
	}
	public void setKeycooldown(int keycooldown) {
		this.keycooldown = keycooldown;
	}
	public void setKeymessage(String keymessage) {
		this.keymessage = keymessage;
	}
	
	
	public void KeyBind() {
			
		if(sneakingleft.contains(p)) { // 기관총
			ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "L");
		}
		if(sneakingleft.contains(p) && !p.isSneaking()) {
			
			sneakingleft.remove(p);
		}
		
		
		if(p.isSneaking() && UserManager.getinstance(p).CurrentClass.equals("블래스터") && keybind.equals("L")) {
			
			if(!sneakingleft.contains(p)) {
				sneakingleft.add(p);
			}							
		}
		
		if(keybind.equals("L") && key1.equals("none")) { // 첫번째가 없고 좌클릭이 시작일때
			ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "L");
			
			keybind = "none";
			return;
		}						
			
		if(p.isSneaking() && !key1.equals("SHIFT") && key1.equals("F")) {
			
		}

		
		if(!keybind.equals("none") && key1.equals("none") && key2.equals("none")) { // 첫번재 keybind 활성화
			key1 = keybind; // 저장
			keycooldown = 1; // 활성화되면 1부터 증가
			p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, 2);
			
			keybind = "none";
			
		}
		if(!keybind.equals("none") && !key1.equals("none") && key2.equals("none")) { // 두번째 keybind 활성화
			key2 = keybind;
			
			
			
			if(key1.equals("R") && key2.equals("L")) {
				ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "RL");
				// p.sendTitle("",blank+"RL", 5, 20, 10);
			}
			if(key1.equals("R") && key2.equals("R")) {
				ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "RR");
				//p.sendTitle("",blank+"RR", 5, 20, 10);
			}
			if(key1.equals("R") && key2.equals("F")) {
				ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "RF");
				//p.sendTitle("",blank+"RF", 5, 20, 10);
			}
			if(key1.equals("F") && key2.equals("R")) {
				ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "FR");
				//p.sendTitle("",blank+"F L", 5, 20, 10);
			}
			if(key1.equals("F") && key2.equals("F")) {
				ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "FF");
				//p.sendTitle("",blank+"FF", 5, 20, 10);
			}
			if(key1.equals("SHIFT") && key2.equals("F")) {
				ClassAbility.Combination.getinstance().Checkclass(UserManager.getinstance(p).CurrentClass, p, "SHIFTF");
				//p.sendTitle("",blank+"SHIFTF", 5, 20, 10);
			}
			
			
			keybind = "none";
			key1 = "none";
			key2 = "none";
			keymessage = "                     ";
			keycooldown = 0;
			
		}
		
		if(keycooldown>0) { //0이상이면 증가
			keycooldown++;						
		}
		
		if(keycooldown>30 || keycooldown==0) { // 30이상이면 키 초기화
			key1 = "none";
			key2 = "none";
			keymessage = "                     ";
			keycooldown = 0;
			

		
	}	
		
		


	}

}

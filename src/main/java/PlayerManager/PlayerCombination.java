package PlayerManager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

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
	private Boolean sneakingleft = false;
	private String DelaySave = "none";
	
	//public final static List<Player> sneakingleft = new ArrayList<>();
	
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
			
		if(sneakingleft) { // 기관총
			//ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "L");
		}
		if(sneakingleft && !p.isSneaking()) {
			sneakingleft = false;
		}
		
		
		if(p.isSneaking() && keybind.equals("L")) {

			DelaySave = "SHIFTL";

		}
		else if(!p.isSneaking() && keybind.equals("L")) {
			DelaySave = "L";
			//ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "L");
		}
		else if(!p.isSneaking() && keybind.equals("R")) {
			DelaySave = "R";
			//ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "FL");
		}
		else if(p.isSneaking() && keybind.equals("R")) {
			ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "SHIFTR");
		}
		else if(p.isSneaking() && !key1.equals("SHIFT") && key1.equals("F")) {
			
		}
		else if(PlayerFunction.getinstance(p).getMeleeDelay() == 0) {
			if(!DelaySave.equals("none")) {
				PlayerFunction.getinstance(p).addMeleeCommand(DelaySave);
				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, DelaySave);
				DelaySave = "none";
			}
		}

//		if(keybind.equals("L") && key1.equals("none") && !p.isSneaking()) { // 첫번째가 없고 좌클릭이 시작일때
//			ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "L");
//			keybind = "none";
//			return;
//		}
//		if(!keybind.equals("none") && key1.equals("none") && key2.equals("none")) { // 첫번재 keybind 활성화
//			if(keybind.equals("L")) {
//				Bukkit.broadcastMessage(key1+" "+key2);
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "L");
//				keybind = "none";
//			}
//			else {
//				key1 = keybind; // 저장
//				keycooldown = 1; // 활성화되면 1부터 증가
//				p.playSound(p.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 1, 2);
//				keybind = "none";
//			}
//		}
//		if(!keybind.equals("none") && !key1.equals("none") && key2.equals("none")) { // 두번째 keybind 활성화
//			key2 = keybind;
//			keybind = "none";
//		}
//
//		if(PlayerFunction.getinstance(p).getMeleeCombo() >=2 &&
//				((key1.equals("SHIFT") && key2.equals("L")) || (key1.equals("F") && key2.equals("L")))) {
//
//			DelaySave = key1+key2;
//		}
//		else if(PlayerFunction.getinstance(p).getMeleeCombo() ==1) DelaySave = "none";
//
//		if(PlayerFunction.getinstance(p).getMeleeDelay() == 0) {
//			if(!DelaySave.equals("none")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, DelaySave);
//				DelaySave = "none";
//			}
//		}
//
//
//		if(keybind.equals("none") && !key1.equals("none") && !key2.equals("none")) {
//
//			Bukkit.broadcastMessage(key1+" "+key2);
//
//			if(key1.equals("R") && key2.equals("L")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "RL");
//				// p.sendTitle("",blank+"RL", 5, 20, 10);
//			}
//			else if(key1.equals("R") && key2.equals("R")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "RR");
//				//p.sendTitle("",blank+"RR", 5, 20, 10);
//			}
//			else if(key1.equals("R") && key2.equals("F")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "RF");
//				//p.sendTitle("",blank+"RF", 5, 20, 10);
//			}
//			if(key1.equals("F") && key2.equals("R")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "FR");
//				//p.sendTitle("",blank+"F L", 5, 20, 10);
//			}
//			else if(key1.equals("F") && key2.equals("F")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "FF");
//				//p.sendTitle("",blank+"FF", 5, 20, 10);
//			}
//			else if(key1.equals("SHIFT") && key2.equals("F")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "SHIFTF");
//				//p.sendTitle("",blank+"SHIFTF", 5, 20, 10);
//			}
//			else if(key1.equals("SHIFT") && key2.equals("L")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "SHIFTL");
//			}
//			else if(key1.equals("F") && key2.equals("L")) {
//				ClassAbility.Combination.getinstance().Checkclass(PlayerManager.getinstance(p).CurrentClass, p, "FL");
//			}
//
//			key1 = "none";
//			key2 = "none";
//			keymessage = "                     ";
//			keycooldown = 0;
//
//		}

		if(keycooldown>0) { //0이상이면 증가
			keycooldown++;						
		}
		
		if(keycooldown>30 || keycooldown==0) { // 30이상이면 키 초기화
			key1 = "none";
			key2 = "none";
			keymessage = "                     ";
			keycooldown = 0;
		}

		keybind = "none";
		
		


	}

}

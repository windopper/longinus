package Items;


import net.md_5.bungee.api.ChatColor;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spellinteracttest.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WeaponManager {
	
	public final static String statlist[] = {"생명력","스킬데미지","보호막","이동속도","에너지충전"};

	private final String IName;

	public WeaponManager(String name) {
		IName = name;
	}
	
	public Boolean checkname(String name) {

		FileConfiguration config = Main.getInstance().getConfig();
		
		return config.contains(name+".Name");
	}
	
	
	
	String getfiledata(String name, String name_2) {
		
		FileConfiguration config = Main.getInstance().getConfig();
		
		return config.getString(name+"."+name_2)+" ";
	}
	
	
	public String getgrade(String name) {
		
		return getfiledata(name, "Grade");		
	}
	
	public String gettype(String name) {
		return getfiledata(name, "Type");
	}

	public Integer getRandomPercent() {
		return (int)(Math.random() * 100) + 1;
	}

	public String getIdedValue(int ratio, int min, int max) {
		int diff = max -  min;
		int addToMin = diff * ratio / 100;
		return Integer.toString(addToMin +  min);
	}

	public Integer getMinValue(String value) {
		String split[] = value.split("~");
		return Integer.parseInt(split[0].replaceAll("%", ""));
	}

	public Integer getMaxValue(String value) {
		String split[] = value.split("~");
		return Integer.parseInt(split[1].replaceAll("%", ""));
	}

	public ItemStack getitem() {
		
		FileConfiguration config = Main.getInstance().getConfig();
		
		ItemStack item = new ItemStack(Material.STONE, 1);

		if(gettype(IName).equals("Material.DIAMOND_SWORD ")) {
			item = new ItemStack(Material.DIAMOND_SWORD, 1);
		}
		else if(gettype(IName).equals("Material.DIAMOND_HOE ")) {
			item = new ItemStack(Material.DIAMOND_HOE, 1);
		}
		else if(gettype(IName).equals("Material.DIAMOND_PICKAXE ")) {
			item = new ItemStack(Material.DIAMOND_PICKAXE, 1);
		}
		else if(gettype(IName).equals("Material.DIAMOND_SHOVEL ")) {
			item = new ItemStack(Material.DIAMOND_SHOVEL, 1);
		}
		else if(gettype(IName).equals("Material.DIAMOND_AXE ")) {
			item = new ItemStack(Material.DIAMOND_AXE, 1);
		}
		else if(gettype(IName).equals("Material.BOW ")) {
			item = new ItemStack(Material.BOW, 1);
		}


		HashMap<String, int[]> Registered = new HashMap<>();
		HashMap<String, String> statColoredContainer = new HashMap<>();

		// 비율 저장 0 ~ 100
		// 0 이면 최소수치 100이면 최대수치
		// 데이터베이스에 저장된 값과 비율로 계산
		// 아이템 수치 조정용
		for(String stats : statlist) {

			String configvalue;
			if(config.getString(IName+"."+stats) == null) configvalue = "0~0";
			else configvalue = config.getString(IName+"."+stats);

			// arr[0]=최소, arr[1]=비율, arr[2]=최대
			// ex) arr[] = { 0, 50, 10 } -> 실제 값 5
			int arr[] = { getMinValue(configvalue), getRandomPercent(), getMaxValue(configvalue) };
			// 중복 방지
			Registered.put(stats, arr);
		}

		ItemMeta itemmeta = item.getItemMeta();

		// 로어 설정
		for(String stats : statlist) {

			if(!config.contains(IName+"."+stats)) continue;

			String rawString = config.getString(IName+"."+stats);

			int percent = 0;

			if(rawString.contains("%")) {
				percent = 1;
			}

			String str = getIdedValue(Registered.get(stats)[1], Registered.get(stats)[0], Registered.get(stats)[2]);

			if(Integer.parseInt(str) > 0) {
				if(percent == 1) str+="%";
				statColoredContainer.put(stats, "§f"+stats+"§f §a+"+str+"§a");
			}
			else {
				if(percent == 1) str+="%";
				statColoredContainer.put(stats, "§f"+stats+"§f §c"+str+"§c");
			}
		}

		if(config.contains(IName+".custommodeldata")) {
			itemmeta.setCustomModelData(config.getInt(IName+".custommodeldata"));
		}

		if(getgrade(IName).equals("Legendary ")){
			itemmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b"+config.getString(IName+".Name")+"&b"));
		}

		ArrayList<String> list = new ArrayList<>();
		list.add("");
		list.add("§6데미지: "+config.getString(IName+".데미지")+"§6");
		list.add("");
		list.add("§7클래스제한 : "+config.getString(IName+".클래스제한")+"§f");
		list.add("§7레벨제한 : "+config.getString(IName+".레벨제한")+"§f");
		if(config.contains(IName+".무기강화최소")) list.add("§7무기강화최소 : "+config.getString(IName+".무기강화최소")+"§f");
		if(config.contains(IName+".감각강화최소")) list.add("§7감각강화최소 : "+config.getString(IName+".감각강화최소")+"§f");
		if(config.contains(IName+".외피강화최소")) list.add("§7외피강화최소 : "+config.getString(IName+".외피강화최소")+"§f");
		if(config.contains(IName+".기동강화최소")) list.add("§7기동강화최소 : "+config.getString(IName+".기동강화최소")+"§f");
		list.add("");
		for(String stats : statColoredContainer.keySet()) {
			if(config.contains(IName+"."+stats)) {

				list.add(statColoredContainer.get(stats));
				list.add("");
			}
		}

		itemmeta.setLore(list);
		itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(itemmeta);

		// 태그 메모리 설정
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbtTagCompound = nmsStack.hasTag() ? nmsStack.getTag() : nmsStack.getOrCreateTag();

		for(String stats : Registered.keySet()) {
			nbtTagCompound.setIntArray(stats, Registered.get(stats));
		}

		nbtTagCompound.setString("이름", itemmeta.getDisplayName());
		nbtTagCompound.setString("데미지", config.getString(IName+"."+"데미지"));
		nbtTagCompound.setString("클래스제한", config.getString(IName+"."+"클래스제한"));
		nbtTagCompound.setInt("레벨제한", config.getInt(IName+"."+"레벨제한"));
		nbtTagCompound.setInt("무기강화제한", config.getInt(IName+"."+"무기강화최소"));
		nbtTagCompound.setInt("감각강화제한", config.getInt(IName+"."+"감각강화최소"));
		nbtTagCompound.setInt("외피강화제한", config.getInt(IName+"."+"외피강화최소"));
		nbtTagCompound.setInt("기동강화제한", config.getInt(IName+"."+"기동강화최소"));
		nbtTagCompound.setBoolean("교환", true);
		nbtTagCompound.setString("UUID", UUID.randomUUID().toString());

		// 태그 저장
		nmsStack.setTag(nbtTagCompound);
		item = CraftItemStack.asBukkitCopy(nmsStack);

		return item;
	}

}

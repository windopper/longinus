package weapons;


import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.md_5.bungee.api.ChatColor;
import spellinteracttest.Main;
public class WeaponManager {
	
	private final static String statlist[] = {"생명력","스킬데미지","보호막","이동속도","에너지충전"};
	
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
	
	public String getlevel(String name) {
		return getfiledata(name, "LvRequire");
	}
	
	public String gethealth(String name) {
		return getfiledata(name, "Health");
	}
	public String getspelldamage(String name) {
		return getfiledata(name, "SpellDamage");
	}
	public String getshield(String name) {
		return getfiledata(name, "Shield");
	}
	public String getwalkspeed(String name) {
		return getfiledata(name, "WalkSpeed");
	}
	
	public ItemStack getitem(String name) {
		
		FileConfiguration config = Main.getInstance().getConfig();
		
		ItemStack item = new ItemStack(Material.STONE, 1);
		
		if(gettype(name).equals("Material.DIAMOND_SWORD ")) {
			item = new ItemStack(Material.DIAMOND_SWORD, 1);
			
		}
		else if(gettype(name).equals("Material.DIAMOND_HOE ")) {
			item = new ItemStack(Material.DIAMOND_HOE, 1);
			
		}
		else if(gettype(name).equals("Material.DIAMOND_PICKAXE ")) {
			item = new ItemStack(Material.DIAMOND_PICKAXE, 1);
			
		}
		else if(gettype(name).equals("Material.DIAMOND_SHOVEL ")) {
			item = new ItemStack(Material.DIAMOND_SHOVEL, 1);	
		}
		else if(gettype(name).equals("Material.DIAMOND_AXE ")) {
			item = new ItemStack(Material.DIAMOND_AXE, 1);
			
		}
		ItemMeta itemmeta = item.getItemMeta();
		if(getgrade(name).equals("Legendary ")){
			itemmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b"+config.getString(name+".Name")+"&b"));
		}
		
		
		
		ArrayList<String> list = new ArrayList<>();
		list.add("");
		list.add("§6데미지: "+config.getString(name+".데미지")+"§6");
		list.add("");
		list.add("§7클래스제한 : "+config.getString(name+".클래스제한")+"§f");
		list.add("§7레벨제한 : "+config.getString(name+".레벨제한")+"§f");
		if(config.contains(name+".무기강화최소")) list.add("§7무기강화최소 : "+config.getString(name+".무기강화최소")+"§f");
		if(config.contains(name+".감각강화최소")) list.add("§7감각강화최소 : "+config.getString(name+".감각강화최소")+"§f");
		if(config.contains(name+".외피강화최소")) list.add("§7외피강화최소 : "+config.getString(name+".외피강화최소")+"§f");
		if(config.contains(name+".기동강화최소")) list.add("§7기동강화최소 : "+config.getString(name+".기동강화최소")+"§f");
		list.add("");
		for(String stats : statlist) {
			if(config.contains(name+"."+stats)) {
				
				int percent = 0;
				
				if(config.getString(name+"."+stats).contains("%")) {
					percent = 1;
				}
				
				String str = randomvalue.getvalue(config.getString(name+"."+stats));
				
				
				if(Integer.parseInt(str) > 0) {
					if(percent == 1) str+="%";
					list.add("§f"+stats+"§f §a+"+str+"§a");
				}
				else {
					if(percent == 1) str+="%";
					list.add("§f"+stats+"§f §c"+str+"§c");
				}
				
				list.add("");
								
			}
			
		}
		
		if(config.getString(name+".클래스제한").equals("아이테르")) {
			Multimap<Attribute,AttributeModifier> d = ArrayListMultimap.create();
			AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "AttackSpeed", 1, Operation.ADD_NUMBER, EquipmentSlot.HAND);
			d.put(Attribute.GENERIC_ATTACK_SPEED, modifier);

			itemmeta.setAttributeModifiers(d);
		}
		//itemmeta.setLore(Arrays.asList("§7Lv.Min"+getlevel(name),"",getgrade(name)));
		itemmeta.setLore(list);
		
		
		if(config.contains(name+".custommodeldata")) {
			
			if(config.getString(name+".custommodeldata").equals("1")) {
				itemmeta.setCustomModelData(1);
			}
			if(config.getString(name+".custommodeldata").equals("2")) {
				itemmeta.setCustomModelData(2);
			}
			if(config.getString(name+".custommodeldata").equals("3")) {
				itemmeta.setCustomModelData(3);
			}
			if(config.getString(name+".custommodeldata").equals("4")) {
				itemmeta.setCustomModelData(4);
			}
			if(config.getString(name+".custommodeldata").equals("5")) {
				itemmeta.setCustomModelData(5);
			}
			if(config.getString(name+".custommodeldata").equals("6")) {
				itemmeta.setCustomModelData(6);
			}
			if(config.getString(name+".custommodeldata").equals("7")) {
				itemmeta.setCustomModelData(7);
			}
		}
		

		
		
		 
		item.setItemMeta(itemmeta);
		
		
		return item;
	}

}

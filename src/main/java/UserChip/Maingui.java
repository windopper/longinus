package UserChip;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import userdata.UserStatManager;
import userdata.UserFileManager;
import userdata.UserManager;

public class Maingui {
	
	private static Maingui Maingui;
	
	private Maingui() {
		
	}
	
	public static Maingui getinstance() {
		if(Maingui == null) Maingui = new Maingui();
		return Maingui;
	}
	
	
	public ItemStack chipitemget(Player p) {
		
		ItemStack item = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d§l§o"+p.getName()+"§o§l&d§r&5의 메모리 카드"));
		meta.setLore(Arrays.asList(
				"§8유틸리티 아이템§8",
				"",
				""));
		item.setItemMeta(meta);
		return item;
	}
	
	public void chipitemguiopen(Player p) {
		Inventory gui = Bukkit.createInventory(null, 36, "메모리카드");
		
		gui.setItem(4, alteraitem(p));
		gui.setItem(11, alarm(p));
		gui.setItem(12, questbook());
		gui.setItem(13, classitem());
		gui.setItem(14, statsetting(p));
		gui.setItem(15, collectingitem());
		gui.setItem(31, returnitem());
		
		
		p.openInventory(gui);

		
		
	}
	
	public ItemStack questbook() { // 퀘스트 아이템
		
		ItemStack questbook = new ItemStack(Material.BOOK, 1);
		ItemMeta questmeta = questbook.getItemMeta();
		questmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6퀘스트 북&6"));
		questmeta.setLore(Arrays.asList(
				"",
				"",
				""));
		
		questbook.setItemMeta(questmeta);
		
		return questbook;
	}
	
	public ItemStack statsetting(Player p) { // 스탯 관리 창
		ItemStack statsetting = new ItemStack(Material.COMPARATOR, 1);
		ItemMeta statmeta = statsetting.getItemMeta();
		statmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6스탯 관리&6"));
		statmeta.setLore(Arrays.asList(
				"§c무기강화 §6 Lv."+UserStatManager.getinstance(p).getStr(),
				"§e감각강화 §6 Lv."+UserStatManager.getinstance(p).getDex(),
				"§5외피강화 §6 Lv."+UserStatManager.getinstance(p).getDef(),
				"§b기동강화 §6 Lv."+UserStatManager.getinstance(p).getAgi(),
				""
				));
		statsetting.setItemMeta(statmeta);
		
		return statsetting;
	}
	
	public ItemStack alarm(Player p) { // 유저 알림 창
		
		
		ItemStack alarm = new ItemStack(Material.CLOCK, 1);
		ItemMeta alarmmeta = alarm.getItemMeta();
		alarmmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6유저 알림&6"));
		
		int amount = UserAlarmManager.instance().getalarmamount(p);
		
		if(amount ==0)
			alarmmeta.setLore(Arrays.asList("§7알람이 없습니다§7"));
		else if(amount ==1) {

			ArrayList<String> list = UserAlarmManager.instance().getalarmlist(p, 0);
			
			int i = 0;
			
			for(String str : list) { // 색깔코드 입히기
				str = "§7"+str;
				list.set(i, str);
				i++;
			}

			alarmmeta.setLore(list);
		}
			
		else {
			ArrayList<String> list = UserAlarmManager.instance().getalarmlist(p, 0);
			
			int i = 0;
			
			for(String str : list) { // 색깔코드 입히기
				str = "§7"+str;
				list.set(i, str);
				i++;
			}
			
			list.add("§7외 "+Integer.toString(amount-1)+"개");
			alarmmeta.setLore(list);
		}
			
		
		alarm.setItemMeta(alarmmeta);
		
		return alarm;
	}
	
	public ItemStack collectingitem() { // 수집품
		
		ItemStack item = new ItemStack(Material.LEATHER, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6수집품들"));
		meta.setLore(Arrays.asList(
				"§7최근 수집된 표본",
				""));
		
		item.setItemMeta(meta);
		
		
		return item;
		
	}
	
	public ItemStack classitem() {
		
		ItemStack item = new ItemStack(Material.BEACON, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d클래스 선택"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack alteraitem(Player p) {
		
		int gold = UserFileManager.getinstance().getGold(p);
		
		ItemStack item = new ItemStack(Material.DIAMOND_BLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7보유 알테라 : &6"+gold));
		meta.setLore(Arrays.asList(
				"§7좌클릭으로 송금하기"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack returnitem() {
		
		ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b롱기누스로 귀환"));
		meta.setLore(Arrays.asList(
				"§7클릭하여 귀환"));
		
		item.setItemMeta(meta);
		return item;
	}

}

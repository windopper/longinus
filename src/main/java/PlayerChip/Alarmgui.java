package PlayerChip;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Alarmgui {
	
	/* 알람 종류
	 * notification - 공지
	 * trademarket - 트레이드마켓 매매 목록
	 * usertrade - 유저간의 거래 목록
	 * alterasend - 알테라 출급 또는 입금
	 * alterareceive
	 * 
	 * 
	 * 
	 * 
	 */
	

	private static Alarmgui Alarmgui;
	
	private Alarmgui() {
		
	}
	
	public static Alarmgui getinstance() {
		if(Alarmgui == null) Alarmgui = new Alarmgui();
		return Alarmgui;
	}
	
	public void AlarmGuiOpen(Player p) {
		
		Inventory gui = Bukkit.createInventory(null, 54, "Alarm");
		
		int limit = UserAlarmManager.instance().getalarmamount(p);
		
		for(int i=0; i<limit; i++) {
			gui.setItem(i, AlarmItems(p, i));
		}
		
		if(UserAlarmManager.instance().getalarmamount(p)==0) {
			gui.setItem(22, noalarmindicateitem());
		}
		
		
		
		
		gui.setItem(53, alarmdeleteitem());
		gui.setItem(45, backtomenuitem());
		
		p.openInventory(gui);
		 
		
	}
	
	public ItemStack AlarmItems(Player p, int location) {
		
		ItemStack item = null;
		
		String alarmtype = UserAlarmManager.instance().getalarmtype(p, location);
		
		if(alarmtype.equals("notification")) item = new ItemStack(Material.PAPER, 1);
		if(alarmtype.equals("alterasend")) item = new ItemStack(Material.REDSTONE_BLOCK, 1);
		if(alarmtype.equals("alterareceive")) item = new ItemStack(Material.EMERALD_BLOCK, 1);
		
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6알림&6"));
		
		ArrayList<String> list = UserAlarmManager.instance().getalarmlist(p, location);
		
		int i = 0;
		
		for(String str : list) { // 색깔코드 입히기
			list.set(i, str);
			i++;
		}
		
		meta.setLore(list);
		item.setItemMeta(meta);
		
		
		
		return item;
	}
	
	public ItemStack alarmdeleteitem() {
		
		ItemStack item = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c알람 삭제"));
		meta.setLore(Arrays.asList(
				"§7우클릭으로 모두 삭제",
				"§7좌클릭으로 가장 오래된 알람 삭제"));
		
		item.setItemMeta(meta);
		
		
		
		
		return item;
	}
	
	public ItemStack backtomenuitem() {
		ItemStack item = new ItemStack(Material.ARROW, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c뒤로 가기"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack noalarmindicateitem() {
		ItemStack item = new ItemStack(Material.REDSTONE_BLOCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c이런! 어떠한 소식도 없네요"));
		meta.setLore(Arrays.asList("§7정말 슬픈 일이네요"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	
}

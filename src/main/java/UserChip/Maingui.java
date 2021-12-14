package UserChip;

import Party.PartyManager;
import UserData.UserFileManager;
import UserData.UserManager;
import UserData.UserStatManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Maingui {
	
	private static Maingui Maingui;
	public final int alteraslot = 4;
	public final int playerprofileslot = 8;
	public final int alarmslot = 11;
	public final int questbookslot = 12;
	public final int classitemslot = 13;
	public final int statsettingslot = 14;
	public final int collectingitemslot = 15;
	public final int skilltraits = 20;
	public final int biochips = 21;
	public final int partymanageitemslot = 36;
	public final int returnitemslot = 40;

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
		Inventory gui = Bukkit.createInventory(null, 45, "메모리카드");
		
		gui.setItem(alteraslot, alteraitem(p));
		gui.setItem(playerprofileslot, PlayerProfile(p));
		gui.setItem(alarmslot, alarm(p));
		gui.setItem(questbookslot, questbook());
		gui.setItem(classitemslot, classitem());
		gui.setItem(statsettingslot, statsetting(p));
		gui.setItem(collectingitemslot, collectingitem());
		gui.setItem(skilltraits, skilltraitsitem(p));
		gui.setItem(biochips, biochipsitem(p));
		gui.setItem(partymanageitemslot, partyManageitem(p));
		gui.setItem(returnitemslot, returnitem());
		
		
		p.openInventory(gui);

		
		
	}
	private ItemStack skilltraitsitem(Player p)	{
		ItemStack itemStack = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§b스킬 특성");
		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	private ItemStack biochipsitem(Player p) {
		ItemStack itemStack = new ItemStack(Material.END_CRYSTAL, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§5생체칩");
		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}


	private ItemStack PlayerProfile(Player p) {

		String CurrentClass = UserManager.getinstance(p).CurrentClass;
		String Level = Integer.toString(UserStatManager.getinstance(p).getlvl());


		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(p);
		meta.setDisplayName("§e§o§l"+p.getName()+"의 프로필");
		meta.setLore(Arrays.asList("",
				"§9- §d클래스 : "+CurrentClass,
				"§9- §d레벨 : "+Level));

		item.setItemMeta(meta);

		return item;
	}

	private ItemStack partyManageitem(Player p) {

		ItemStack itemStack = new ItemStack(Material.CARTOGRAPHY_TABLE, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§b파티 관리");
		PartyManager partyManager = PartyManager.getParty(p);
		if(partyManager == null) {
			itemMeta.setLore(Arrays.asList("§7현재 파티가 없습니다"));
			itemStack.setItemMeta(itemMeta);
			return itemStack;
		}
		else {
			List<Player> partyList = partyManager.getMembers();
			List<String> loreList = new ArrayList<String>();
			loreList.add("");

			for(Player playerList : partyList) {
				loreList.add("§7- §e"+playerList.getName());
			}
			itemMeta.setLore(loreList);
			itemStack.setItemMeta(itemMeta);
			return itemStack;
		}
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

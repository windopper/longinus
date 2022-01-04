package PlayerChip;

import PlayerManager.PlayerManager;
import QuestFunctions.QuestList;
import SQL.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Questgui {
	
	private static Questgui Questgui;
	
	private Questgui() {
		
	}
	
	public static Questgui getinstance() {
		if(Questgui == null) Questgui = new Questgui();
		return Questgui;
	}
	
	public void QuestGuiOpen(Player p) {
		
		Inventory gui = Bukkit.createInventory(null, 54, "퀘스트");
		gui.setItem(45, backtomenuitem());
		p.openInventory(QuestBooks(gui, p));
	}
	
	public ItemStack backtomenuitem() {

		ItemStack item = new ItemStack(Material.ARROW, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c뒤로 가기"));
		item.setItemMeta(meta);
		
		return item;
	}
	
	public Inventory QuestBooks(Inventory gui, Player p) {
		
		String uuid = p.getUniqueId().toString();
		String CurrentClass = PlayerManager.getinstance(p).CurrentClass;
		int CurrentClassNumber = PlayerManager.getinstance(p).CurrentClassNumber;
		String path = CurrentClass+"/"+CurrentClassNumber+".quests";

		PlayerClass pC = new PlayerClass(p);
		YamlConfiguration yaml = pC.getClassFile();
		int slot = 0;
		
		for(String Questname : yaml.getConfigurationSection(path).getKeys(false)) {
			gui.setItem(slot, callQuestBook(Questname, QuestList.valueOf(Questname).getLevelReq(), yaml.getInt(path+"."+Questname+".progress")));
			slot++;
		}
		
		return gui;
	}
	
	public ItemStack callQuestBook(String str, int level, int progress) {
		ItemStack item = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = item.getItemMeta();
		String replacestr = str.replaceAll("_", " ");
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l"+replacestr));
		
		if(progress == 0) {
			meta.setLore(Arrays.asList("",
					"§f레벨제한 : "+Integer.toString(level),
					"",
					"§c아직 시작하지 않았습니다",
					""));
		}
		else if(progress == 1 ) {
			meta.setLore(Arrays.asList("",
					"§f레벨제한 : "+Integer.toString(level),
					"",
					"§a완료",
					""));
		}
		else if(progress >= 2) {
			meta.setLore(Arrays.asList("",
					"§f레벨제한 : "+Integer.toString(level),
					"",
					"§e진행중..",
					""));
		}
		

		item.setItemMeta(meta);

		return item;
	}
}

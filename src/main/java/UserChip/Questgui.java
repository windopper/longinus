package UserChip;

import java.io.File;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Quest.QuestList;
import userdata.UserManager;

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
		String username = p.getName();
		String CurrentClass = UserManager.getinstance(p).CurrentClass;
		int CurrentClassNumber = UserManager.getinstance(p).CurrentClassNumber;
		String path = "Class."+CurrentClass+"/"+Integer.toString(CurrentClassNumber)+".quests";
		
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		int slot = 0;
		
		for(String Questname : config.getConfigurationSection(path).getKeys(false)) {
			gui.setItem(slot, callQuestBook(Questname, QuestList.valueOf(Questname).getLevelReq(), config.getInt(file+".progress")));
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
					"§c아직 시작하지 않았습니다",
					""));
		}
		else if(progress == 1 ) {
			meta.setLore(Arrays.asList("",
					"§f레벨제한 : "+Integer.toString(level),
					"§a완료",
					""));
		}
		else if(progress >= 2) {
			meta.setLore(Arrays.asList("",
					"§f레벨제한 : "+Integer.toString(level),
					"§e진행중..",
					""));
		}
		

		item.setItemMeta(meta);

		return item;
	}
}

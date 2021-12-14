package UserStorage;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import PlayerData.UserFileManager;

public class UserStorageManager {
	
	private static UserStorageManager BankSave;
	
	private UserStorageManager() {
		
	}
	
	public static UserStorageManager getinstance() {
		if(BankSave == null) BankSave = new UserStorageManager();
		return BankSave;
	}
	
	public void Save(Player p, Inventory inv, String page) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(String slot : UserFileManager.getinstance().getStorageSlot()) {
			
			config.set("storage.storage"+page+"."+slot, inv.getItem(Integer.parseInt(slot)));
			
		}
		
		try {
			config.save(file);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Inventory Call(Player p, Inventory inv, String page) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(String slot : UserFileManager.getinstance().getStorageSlot()) {

			inv.setItem(Integer.parseInt(slot), config.getItemStack("storage.storage"+page+"."+slot));
			
		}
		
		
		return inv;
		
	}
	
	public boolean CheckBankPage(Player p, String page) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		boolean value = config.contains("storage.storage"+page);
		
		return value;
		
		
	}
	
	public int MaxBankPage(Player p) {
		
		int i = 1;
		while(CheckBankPage(p, Integer.toString(i))) {
			i++;
		}
		i--;
		
		return i;
	}
	

}

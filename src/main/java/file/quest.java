package file;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class quest {
	//TODO 지우기
	private static quest q;
	
	
	private quest() {
		
	}
	
	public static quest getinstance() {
		if(q == null) q = new quest();
		return q;
	}
	
	
	
	
	
	public void set(Player p, String category, String write) {
		
		File questfile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "userquest.yml");
		FileConfiguration quest = YamlConfiguration.loadConfiguration(questfile);
	
		
		if(quest.contains(p.getUniqueId().toString()+"."+category)) quest.set(p.getUniqueId().toString()+"."+category, write);
		
		try {
			quest.save(questfile);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//save();
	}
	
	public void set(Player p, String category, int write) {
		
		File questfile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "userquest.yml");
		FileConfiguration quest = YamlConfiguration.loadConfiguration(questfile);

		if(quest.contains(p.getUniqueId().toString()+"."+category)) quest.set(p.getUniqueId().toString()+"."+category, write);
		
		try {
			quest.save(questfile);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//save();
	}
	
	
	public String getstring(Player p, String category) {
		
		File questfile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "userquest.yml");
		FileConfiguration quest = YamlConfiguration.loadConfiguration(questfile);
		
		if(quest.contains(p.getUniqueId().toString()+"."+category)) {
			return quest.getString(p.getUniqueId().toString()+"."+category);
		}
		return null;

	}
	
	public int getint(Player p, String category) {
		
		File questfile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "userquest.yml");
		FileConfiguration quest = YamlConfiguration.loadConfiguration(questfile);
			
		if(quest.contains(p.getUniqueId().toString()+"."+category)) {
			return quest.getInt(p.getUniqueId().toString()+"."+category);
		}
		return 0;

	}
	
//	public void save() {
//		
//		File questfile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "userquest.yml");
//		FileConfiguration quest = YamlConfiguration.loadConfiguration(questfile);
//		
//		
//		try {
//			quest.save(questfile);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//	}
}

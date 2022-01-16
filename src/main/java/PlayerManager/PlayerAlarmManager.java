package PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PlayerAlarmManager {
	
	private static PlayerAlarmManager Alarmfile;
	
	private PlayerAlarmManager() {
		
	}
	
	public static PlayerAlarmManager instance() {
		if(Alarmfile == null) Alarmfile = new PlayerAlarmManager();
		return Alarmfile;
	}
	public void register(Player p) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
		if(config.contains("alarm")) return;
		
		for(int i=0; i<50; i++) {
			config.set("alarm."+i+".content", "");
			config.set("alarm."+i+".type", "");
			config.set("alarm."+i+".date", "");
			
		}
		try {
			config.save(file);
		}
		catch(Exception e) {
			p.sendMessage("오류 발생");
		}
	}
	
	public void addalarm(Player p, String contents, String type) {
		
		String uuid = p.getUniqueId().toString();
		
		SimpleDateFormat date = new SimpleDateFormat("발신 날짜 yy년 MM월 dd일 HH시 mm분", Locale.KOREA);
		String datestr = date.format(new Date());
		datestr = "§7"+datestr;
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		if(!config.contains("alarm")) {
			register(p);
		}
		for(int i=49; i>=0; i-- ) {

			int j = i+1;
			
			config.set("alarm."+j+".content", config.getString("alarm."+i+".content"));
			config.set("alarm."+j+".type", config.getString("alarm."+i+".type"));
			config.set("alarm."+j+".date", config.getString("alarm."+i+".date"));
			
			if(i==0) {
				config.set("alarm."+i+".content", contents);
				config.set("alarm."+i+".type", type);
				config.set("alarm."+i+".date", datestr);
			}
		}
		
		try {
			config.save(file);
		}
		catch(Exception e) {
			p.sendMessage("오류 발생");
		}
	}
	public void removeoldonealarm(Player p) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
		for(int i=49; i>=0; i-- ) {
			
			if(config.getString("alarm."+i+".content").equals("")) continue;
			
			config.set("alarm."+i+".content", "");
			config.set("alarm."+i+".type", "");
			config.set("alarm."+i+".date", "");
			break;
		}
		
		try {
			config.save(file);
		}
		catch(Exception e) {
			p.sendMessage("오류 발생");
		}
	}
	public void removeallalarms(Player p) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		for(int i=49; i>=0; i-- ) {
			config.set("alarm."+i+".content", "");
			config.set("alarm."+i+".type", "");
			config.set("alarm."+i+".date", "");
		}
		
		try {
			config.save(file);
		}
		catch(Exception e) {
			p.sendMessage("오류 발생");
		}
	}

	public void addalarmtoallplayers(String contents, String type) {
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(),"joinedplayer.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		
		ConfigurationSection players = config.getConfigurationSection("Player");
		
		for(String uuid : players.getKeys(false)) {
			
			File file2 = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
			FileConfiguration config2 = YamlConfiguration.loadConfiguration(file2);
			
			for(int i=49; i>=0; i--) {
				
				int j = i+1;
				
				if(i!=49) {
					config2.set("alarm."+j+".content", config2.getString("alarm."+i+".content"));
					config2.set("alarm."+j+".type", config2.getString("alarm."+i+".type"));
				}
				
				if(i==0) {
					config2.set("alarm."+i+".content", contents);
					config2.set("alarm."+i+".type", type);
				}
			}
			
			try {
				config2.save(file2);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	public int getalarmamount(Player p) {
		
		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		int amount = 0;
		
		for(int i=0; i<50; i++) {
			if(!config.getString("alarm."+i+".content").equals("")) amount++;
		}
		
		return amount;
	}
	public ArrayList<String> getalarmlist(Player p, int location){

		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		
		String contents = config.getString("alarm."+location+".content");
		String dates = config.getString("alarm."+location+".date");
		
		SimpleDateFormat currentdate = new SimpleDateFormat("yy/MM/dd HH:mm:ss", Locale.KOREA);
		String currentdates = currentdate.format(new Date());
		
		
		String splits[] = contents.split("\\*");
		
		ArrayList<String> list = new ArrayList<>();

		for(int i=0; i<splits.length; i++) {
			list.add(splits[i]);
		}
		list.add("");
		list.add(dates);
		return list;
	}
	
	public String getalarmtype(Player p, int location) {

		String uuid = p.getUniqueId().toString();
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		
		String type = config.getString("alarm."+location+".type");
		
		return type;
	}
	
	
	
	

}

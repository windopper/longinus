package PlayerManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class LastClassLocation {
	
	private static LastClassLocation LastClassLocation;
	
	private LastClassLocation() {
		
	}
	
	public static LastClassLocation getinstance() {
		if(LastClassLocation == null) LastClassLocation = new LastClassLocation();
		return LastClassLocation;
	}

	public String coordtostring(Player p) {
		
		String world = p.getWorld().getName();
		String x = Double.toString(p.getLocation().getX());
		String y = Double.toString(p.getLocation().getY());
		String z = Double.toString(p.getLocation().getZ());
		
		String result = world+","+x+","+y+","+z;
		return result;
	}
	
	public Location stringtocoord(Player p, String classname) {
		
		String uuid = p.getUniqueId().toString();
		String classaddress = "Class."+classname;
		
		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		
		String coord = config.getString(classaddress+".coord");
		
		if(coord.equals("0") || coord == null) { // 저장된 로케이션이 없을때
			Location zero = new Location(Bukkit.getWorld("world"), 7.5, 145, 4.5);
			return zero;
		}
		
		String split[] = coord.split(",");
		String world = split[0];
		double x = Double.parseDouble(split[1]);
		double y = Double.parseDouble(split[2]);
		double z = Double.parseDouble(split[3]);
		Location location = new Location(Bukkit.getWorld(world), x, y, z);
		
		return location;
	}
	
	public void classchangeteleport(Player p, String classname) {
		
		Location location = stringtocoord(p, classname);
		p.teleport(location);
		SeveralMessages.ClassChangeMessage(p);
	}
	
}

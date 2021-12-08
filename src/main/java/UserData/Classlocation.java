package UserData;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Classlocation {
	
	private static Classlocation Classlocation;
	
	private Classlocation() {
		
	}
	
	public static Classlocation getinstance() {
		if(Classlocation == null) Classlocation = new Classlocation();
		return Classlocation;
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
		Double x = Double.parseDouble(split[1]);
		Double y = Double.parseDouble(split[2]);
		Double z = Double.parseDouble(split[3]);
		Location location = new Location(Bukkit.getWorld(world), x, y, z);
		
		return location;
	}
	
	public void classchangeteleport(Player p, String classname) {
		
		Location location = stringtocoord(p, classname);
		p.teleport(location);
		SeveralMessages.ClassChangeMessage(p);
	}
	
}

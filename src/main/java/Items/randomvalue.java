package Items;


import org.bukkit.Bukkit;

import spellinteracttest.*;

public class randomvalue {
	
	public static String getvalue(String stats) {
		
		String splitstr[] = stats.split("~");
		splitstr[0] = splitstr[0].replace("%", "");
		splitstr[1] = splitstr[1].replace("%", "");
		
		Bukkit.broadcastMessage(splitstr[0]);
		Bukkit.broadcastMessage(splitstr[1]);
		
		return Integer.toString(RandomRange.range(Integer.parseInt(splitstr[0]), Integer.parseInt(splitstr[1])));
		
	}
}

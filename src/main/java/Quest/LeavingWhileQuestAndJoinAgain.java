package Quest;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LeavingWhileQuestAndJoinAgain {
	

	
	public void restore(Player p) {
		
		File questfile = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), "userquest.yml");
		FileConfiguration quest = YamlConfiguration.loadConfiguration(questfile);
		
		String uuid = p.getUniqueId().toString();
		
		if(quest.contains(uuid+".tutorial")) {  // 튜토리얼 퀘스트
			if(quest.get(uuid+".tutorial").equals(0)){			
				p.teleport(new Location(Bukkit.getWorld("world"), -80.5, 12, 152.5, -90, 0));
				p.sendMessage("§c튜토리얼을 완료하지 않고 게임에서 이탈했습니다!§c");
			}
		}
		
		
	}

}

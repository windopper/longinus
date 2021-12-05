package QuestFunctions;

import Mob.RightClickNPC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import userdata.UserManager;

import java.io.File;

public class UserQuestManager implements EventsInterface, Listener {

	/* 0 시작안함
	 * 1 완료함
	 *
	 * 2~ 퀘스트 단계
	 *
	 *
	 *
	 */

	private static UserQuestManager UserQuestManager;

	private UserQuestManager() {

	}

	public static UserQuestManager getinstance() {
		if(UserQuestManager == null) UserQuestManager = new UserQuestManager();
		return UserQuestManager;
	}

	@EventHandler
	public void NpcRightClicked(RightClickNPC event) {

		String NPCName = event.getNPC().getName();
		Player player = event.getPlayer();

		QuestNPCFunctions QNF = new QuestNPCFunctions();
		QNF.NPCForQuest(NPCName, player);

	}

	@Override
	public void AcceptQuest(String questname, Player p) {

		String uuid = p.getUniqueId().toString();
		String Class = UserManager.getinstance(p).CurrentClass+"/"+UserManager.getinstance(p).CurrentClassNumber;

		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		config.set("Class."+Class+".quests."+questname+".progress", 2);

		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void ShowingNextQuestContext(String questname, Player p) {

		StackTraceElement[] Ele = new Throwable().getStackTrace();


	}

	@Override
	public void SetQuestNextStep(String questname, Player p) {

		String uuid = p.getUniqueId().toString();
		String Class = UserManager.getinstance(p).CurrentClass+"/"+UserManager.getinstance(p).CurrentClassNumber;

		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		config.set("Class."+Class+".quests."+questname+".progress",
				config.getInt("Class."+Class+".quests."+questname+".progress")+1);

		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void CompleteQuest(String questname, Player p) {

		String uuid = p.getUniqueId().toString();
		String Class = UserManager.getinstance(p).CurrentClass+"/"+UserManager.getinstance(p).CurrentClassNumber;

		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		config.set("Class."+Class+".quests."+questname+".progress", 1);

		try {
			config.save(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	public int getQuestStep(String questname, Player p) {
		String uuid = p.getUniqueId().toString();
		String Class = UserManager.getinstance(p).CurrentClass+"/"+UserManager.getinstance(p).CurrentClassNumber;

		File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		return config.getInt("Class."+Class+".quests."+questname+".progress");

	}
}

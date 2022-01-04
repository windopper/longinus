package QuestFunctions;

import PlayerManager.PlayerManager;
import Quests.FirstMission;
import SQL.PlayerQuest;
import Shop.RightClickNPC;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class UserQuestManager implements EventsInterface, Listener {

	private final static List<Player> ClickDelay = new ArrayList<>();

	/* 0 시작안함
	 * 1 완료함
	 *
	 * 2~ 퀘스트 단계
	 *
	 *
	 *
	 */

	private static UserQuestManager UserQuestManager = new UserQuestManager();

	public UserQuestManager() {

	}

	public static UserQuestManager Singleton() {
		return UserQuestManager;
	}

	public void RemoveQuestsInstances(Player player) {
		FirstMission.getinstance(player).removeinstance();
	}


	@EventHandler
	public void NpcRightClicked(RightClickNPC event) {

		EntityPlayer NPC = event.getNPC();
		Player player = event.getPlayer();

		if(ClickDelay.contains(player)) return;
		ClickDelay.add(player);

		Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> ClickDelay.remove(player), 15);

		QuestFunctions QNF = new QuestFunctions(player);
		QuestNPCManager QNM = QuestNPCManager.getinstance();
		HashMap<EntityPlayer, EntityArmorStand> NPCSets = QNM.getNPCSets();

		if(!NPCSets.containsKey(NPC)) return;

		QNF.NPCForQuest(NPC, ChatColor.stripColor(NPCSets.get(NPC).getCustomName().getString()));
	}

	@Override
	public void AcceptQuest(String questname, Player p) {

		String InsertSpace = questname.replaceAll("_", " ");
		p.sendTitle("§a임무 시작", "§e"+InsertSpace, 20, 80, 20);
		p.sendMessage("§5>> §a임무 시작: §6"+InsertSpace);
		p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2 ,1);

		String uuid = p.getUniqueId().toString();
		String Class = PlayerManager.getinstance(p).CurrentClass+"/"+ PlayerManager.getinstance(p).CurrentClassNumber;
		PlayerQuest pQ = new PlayerQuest(p);
		pQ.setQuestProgress(Class, questname, 2);

	}

	@Override
	public void ShowingNextQuestContext(String questname, Player p) {

		StackTraceElement[] Ele = new Throwable().getStackTrace();

	}

	@Override
	public void SetQuestNextStep(String questname, Player p) {

		String uuid = p.getUniqueId().toString();
		String Class = PlayerManager.getinstance(p).CurrentClass+"/"+ PlayerManager.getinstance(p).CurrentClassNumber;

		PlayerQuest pQ = new PlayerQuest(p);
		pQ.addQuestProgress(Class, questname);

	}

	@Override
	public void CompleteQuest(String questname, Player p) {

		String InsertSpace = questname.replaceAll("_", " ");
		p.sendTitle("§a임무 완료", "§e"+InsertSpace, 20, 80, 20);
		p.sendMessage("§5>> §a임무 완료: §6"+InsertSpace);
		p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2 ,1);

		String uuid = p.getUniqueId().toString();
		String Class = PlayerManager.getinstance(p).CurrentClass+"/"+ PlayerManager.getinstance(p).CurrentClassNumber;

		(new PlayerQuest(p)).setQuestProgress(Class, questname, 1);
	}

	public int getQuestStep(String questname, Player p) {
		String uuid = p.getUniqueId().toString();
		String Class = PlayerManager.getinstance(p).CurrentClass+"/"+ PlayerManager.getinstance(p).CurrentClassNumber;

		return (new PlayerQuest(p)).getQuestProgress(Class, questname);
	}

	public void QuestReset(Player p) {

		String uuid = p.getUniqueId().toString();
		String Class = PlayerManager.getinstance(p).CurrentClass+"/"+ PlayerManager.getinstance(p).CurrentClassNumber;

		PlayerQuest pQ = new PlayerQuest(p);

		Arrays.stream(QuestList.values()).forEach(value-> {
			pQ.setQuestProgress(Class, value.name(), 0);
		});

	}
}

package QuestFunctions;

import org.bukkit.entity.Player;

public interface EventsInterface {
	
	public void AcceptQuest(String questname, Player p);
	
	public void ShowingNextQuestContext(String questname, Player p);
	
	public void SetQuestNextStep(String questname, Player p);
	
	public void CompleteQuest(String questname, Player p);
	

}

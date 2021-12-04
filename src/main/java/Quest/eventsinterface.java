package Quest;

import org.bukkit.entity.Player;

public interface eventsinterface {
	
	public void AcceptQuest(String questname, Player p);
	
	public void ShowingNextQuestContext(String questname, Player p);
	
	public void SetQuestNextStep(String questname, Player p);
	
	public void CompleteQuest(String questname, Player p);
	

}

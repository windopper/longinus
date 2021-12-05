package QuestFunctions;

import QuestClasses.TestQuest;
import org.bukkit.entity.Player;

public class QuestNPCFunctions {

    public void addQuestNPCs() {

        TestQuest.getSingleton().addQuestNPCs();

    }

    public void NPCForQuest(String name, Player p) {

        if(name.equals("TestQuest")) TestQuest.getinstance(p).QuestProgress();



    }

}

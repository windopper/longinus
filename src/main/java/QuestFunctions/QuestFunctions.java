package QuestFunctions;

import Quests.FirstMission;
import Quests.TestQuest;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class QuestFunctions {

    private Player p;

    public QuestFunctions() {

    }
    public QuestFunctions(Player p) {
        this.p = p;
    }

    public void initQuestNPCs() {

        TestQuest.getSingleton().addQuestNPCs();
        FirstMission.initQuestNPCs();

    }
    public void NPCForQuest(EntityPlayer NPC, String name) {

        if(name.equals("TestQuest")) TestQuest.getinstance(p).QuestProgress();
        if(name.equals("아르안"))
        {
            FirstMission.getinstance(p).QuestProgress(NPC, name);
        }
        if(name.equals("데이즈")) {
            FirstMission.getinstance(p).QuestProgress(NPC, name);
        }
    }

    public void StandardScript(String scripts) {
        String splits[] = scripts.split(":");
        p.sendMessage("§5- §b"+splits[0]+":§f"+splits[1]);
    }

    public boolean ShowScripts(List<String> Scripts, int detailStep) {
        p.playSound(p.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1, 1);
        if(Scripts.size()==detailStep+1) {
            StandardScript(Scripts.get(detailStep));
            return false;
        }
        if(Scripts.size() == detailStep) {
            return true;
        }
        StandardScript(Scripts.get(detailStep));
        return false;
    }

    public void ShowScript(String Script) {
        StandardScript(Script);
    }
}

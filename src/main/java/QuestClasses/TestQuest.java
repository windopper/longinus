package QuestClasses;

import Mob.ShopNPCManager;
import QuestFunctions.QuestList;
import QuestFunctions.UserQuestManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import userdata.UserStatManager;

import java.util.HashMap;

public class TestQuest {

    private static final HashMap<Player, TestQuest> instance = new HashMap<>();
    private static final TestQuest Singleton = new TestQuest();

    private final String Questname = "TestQuest";
    private int detailStep = 0;
    private Player p;



    private TestQuest() {

    }

    private TestQuest(Player p) {
        this.p = p;
    }

    public static TestQuest getinstance(Player p) {
        if(!instance.containsKey(p)) instance.put(p, new TestQuest(p));
        return instance.get(p);
    }

    public static TestQuest getSingleton() {
        return Singleton;
    }

    public void addQuestNPCs() {
       // Location StartLoc = new Location(Bukkit.getWorld("gliese581c"), -155.5, 124, 245.5, 146, 11);
       // ShopNPCManager.getinstance().createNPC(StartLoc, "TestQuest", "", "");
    }

    public final void QuestProgress() {

        int QuestStep = UserQuestManager.Singleton().getQuestStep(Questname, p);

        // When Progress is 0
        if(QuestStep == 0) {
            if(QuestList.valueOf(Questname).getLevelReq() > UserStatManager.getinstance(p).getlvl()) {
                p.sendMessage("레벨부족");
            }
            else {
                UserQuestManager.Singleton().AcceptQuest(Questname, p);
                p.sendMessage("체크됨");
            }

        }

        // When Progress is 2
        if(QuestStep == 2) {

        }

        // etc..
        if(QuestStep == 3) {

        }

        if(QuestStep == 4) {

        }


    }
}

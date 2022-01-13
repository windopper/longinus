package PlayerChip.SkillTalent;

import PlayerManager.PlayerManager;
import org.bukkit.entity.Player;

public class ClickEvent {
    public static void run(Player player) {
        PlayerManager pm = PlayerManager.getinstance(player);
        String currentClass = pm.CurrentClass;
        if(currentClass.equals("플록스")) (new TalentUI(player)).openGUI();
        else if(currentClass.equals("아이테르")) (new TalentUI(player)).openGUI();
        else if(currentClass.equals("카오스")) (new TalentUI(player)).openGUI();
    }
}

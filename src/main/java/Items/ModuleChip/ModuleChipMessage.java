package Items.ModuleChip;

import org.bukkit.entity.Player;

public class ModuleChipMessage {
    private static ModuleChipMessage moduleChipMessage;
    private String blank = "                          ";
    private ModuleChipMessage() {

    }
    public static ModuleChipMessage getInstance() {
        if(moduleChipMessage == null) moduleChipMessage = new ModuleChipMessage();
        return moduleChipMessage;
    }
    public void moduleUseMessage(Player player, String s) {
        player.sendMessage("§5>> §r"+s);
    }
}

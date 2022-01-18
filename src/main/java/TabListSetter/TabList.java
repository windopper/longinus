package TabListSetter;

import PlayerManager.PlayerManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.Listener;

public class TabList implements Listener {

    private Player player;
    private final String blank = "                                             ";
    private PlayerManager pm;

    public TabList(Player player) {
        this.player = player;
        this.pm = PlayerManager.getinstance(player);
    }
    public void init(String string) {
        player.setPlayerListFooter(blank+"\n\n\n"+string+"\n");
    }
    public static void updateTabContent(Player player) {

    }
}

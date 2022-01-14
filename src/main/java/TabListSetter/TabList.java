package TabListSetter;

import org.bukkit.entity.Player;

public class TabList {

    private Player player;
    private final String blank = "                                             ";

    public TabList(Player player) {
        this.player = player;
    }
    public void init(String string) {
        player.setPlayerListFooter(blank+"\n\n\n"+string+"\n");
    }

}

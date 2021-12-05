package CustomEvents;

import dynamicdata.PlayerFunction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
public class PlayerClassChange implements Listener {

    @EventHandler
    public void PlayerClassChange(PlayerClassChangeEvent e) {

        Player player = e.getPlayer();
        PlayerFunction.getinstance(player).resetFunctions();

    }

}

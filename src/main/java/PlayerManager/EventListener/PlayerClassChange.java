package PlayerManager.EventListener;

import CustomEvents.PlayerClassChangeEvent;
import Duel.DuelManager;
import PlayerManager.PlayerFunction;
import PlayerManager.SeveralMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerClassChange implements Listener {

    @EventHandler
    public void PlayerClassChange(PlayerClassChangeEvent e) {

        Player player = e.getPlayer();
        PlayerFunction.getinstance(player).resetFunctions();
        DuelManager.getDuelManager(player).setLoser(player);

        SeveralMessages.ClassChangeMessage(player);

    }
}

package Party;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventProcess implements Listener {

    @EventHandler
    public void serverQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PartyManager.getinstance().QuitParty(player);
    }
}

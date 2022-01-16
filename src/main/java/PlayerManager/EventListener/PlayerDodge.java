package PlayerManager.EventListener;

import PlayerManager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDodge implements Listener {
    @EventHandler
    public void PlayerDodge(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player player) {
            PlayerManager PM = PlayerManager.getinstance(player);
            if(PM.evasion.size()>=1) {
                event.setCancelled(true);
            }
        }
    }
}

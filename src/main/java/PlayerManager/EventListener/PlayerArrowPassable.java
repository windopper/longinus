package PlayerManager.EventListener;

import ClassAbility.entitycheck;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerArrowPassable implements Listener {
    // 듀얼시 화살 통과 못하게 일반 상태에서는 화살 통과 가능
    @EventHandler
    public void PlayerHitByArrow(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if(damager instanceof Arrow && entity instanceof Player) {
            Arrow arrow = (Arrow) damager;
            Entity shooter = (Entity) arrow.getShooter();
            if(shooter instanceof Player) {
                if(entitycheck.duelcheck((Player) entity, (Player) shooter)) {
                    ((Player) entity).setCollidable(true);
                }
                else {
                    ((Player) entity).setCollidable(true);
                    //((Player) entity).setCollidable(false);
                    event.setCancelled(true);
                }
            }
        }
    }
}

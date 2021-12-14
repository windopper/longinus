package Mob;

import DynamicData.Damage;
import DynamicData.EntityManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import spellinteracttest.RandomRange;

public class MobAttackManager implements Listener {

    @EventHandler
    public void MobAttackEvent(EntityDamageByEntityEvent event) {
        Entity Damager = event.getDamager();
        Entity Taker = event.getEntity();
        if(Taker instanceof Player && Damager instanceof LivingEntity) {
            Player player = (Player) Taker;

            MobListManager.MobList mobList
                    = EntityManager.getinstance((LivingEntity)Damager).getMobList();
            if(mobList == null) return;

            int damage = RandomRange.range(mobList.getMindamage(), mobList.getMaxdamage());
            Damage.getinstance().taken(damage, player, (LivingEntity) Damager);


        }
    }
}

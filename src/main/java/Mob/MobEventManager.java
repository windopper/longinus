package Mob;

import CustomEvents.CustomMobDeathEvent;
import DynamicData.Damage;
import EntityPlayerManager.EntityPlayerWatcher;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerLevelManager;
import PlayerManager.PlayerManager;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import spellinteracttest.RandomRange;

public class MobEventManager implements Listener {

    @EventHandler
    public void MobAttackEvent(EntityDamageByEntityEvent event) {

        Entity Damager = event.getDamager();
        Entity Taker = event.getEntity();
        event.setDamage(0.1);

        if(!EntityManager.checkinstance(Damager)) return;

        MobListManager.MobList mobList = EntityManager.getinstance(Damager).getMobList();

        if(mobList == null) return;

        int damage = RandomRange.range(mobList.getMindamage(), mobList.getMaxdamage());

        if(Damager instanceof LivingEntity && Taker instanceof LivingEntity) {

            if(Taker.isInvulnerable()) return;

            // 맞은이가 플레이어 형식이라면
            if(Taker instanceof Player) {

                Player PTaker = (Player) Taker;

                for(Player oP : Bukkit.getOnlinePlayers()) {
                    if(oP.getName().equals(PTaker.getName())) {

                        Damage.getinstance().taken(damage, PTaker, (LivingEntity)Damager);
                    }
                }
                return;
            }
            // 플레이어 형태의 엔티티가 때리면
            if(EntityManager.getinstance((LivingEntity) Damager).isDisguiseEntityPlayer()) {

                Player EntityPlayer = EntityManager.getinstance((LivingEntity) Damager).getDisguiseEntityPlayer();
                net.minecraft.server.level.EntityPlayer eP = ((CraftPlayer) EntityPlayer).getHandle();

                PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(eP, 0);

                EntityPlayerWatcher.sendPacket(eP, packetPlayOutAnimation);


            }

            Damage.getinstance().taken(damage, (LivingEntity)Taker, (LivingEntity)Damager);
            return;
        }


    }

    @EventHandler
    public void MobDeathEvent(CustomMobDeathEvent event) {

        MobListManager.MobList mobList = event.getMobList();
        Entity entity = event.getEntity();
        Location eloc = entity.getLocation();

        PlayerLevelManager.getInstance().XPContribute(entity, mobList);

        for(Player p : Bukkit.getOnlinePlayers()) {

            // 바이V 정수 수집
            if(p.getWorld().getName().equals(entity.getWorld().getName()) && PlayerManager.getinstance(p).CurrentClass.equals("바이V")) {
                Location ploc = p.getLocation();
                double dist = eloc.distance(ploc);
                if(dist<10) PlayerFunction.getinstance(p).essence++;
            }
        }
    }

    // 엔티티 화살 넉백 제한
    @EventHandler
    public void ArrotHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if(damager instanceof Arrow && entity instanceof LivingEntity) {
            if(!EntityStatusManager.getinstance((LivingEntity) entity).canKnockback())
                event.setCancelled(true);
        }
    }
}

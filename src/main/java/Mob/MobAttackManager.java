package Mob;

import DynamicData.Damage;
import DynamicData.EntityManager;
import EntityPlayerManager.EntityPlayerWatcher;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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
        event.setDamage(0.1);

        MobListManager.MobList mobList = EntityManager.getinstance((LivingEntity) Damager).getMobList();

        if(mobList == null) return;

        int damage = RandomRange.range(mobList.getMindamage(), mobList.getMaxdamage());

        if(Damager instanceof LivingEntity && Taker instanceof LivingEntity) {

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
}

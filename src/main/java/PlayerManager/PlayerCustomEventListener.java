package PlayerManager;

import ClassAbility.Aether.Aether;
import ClassAbility.entitycheck;
import CustomEvents.PlayerClassChangeEvent;
import CustomEvents.PlayerDeathEvent;
import CustomEvents.PlayerTakeDamageEvent;
import Duel.DuelManager;
import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import Mob.EntityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerCustomEventListener implements Listener {

    @EventHandler
    public void PlayerClassChange(PlayerClassChangeEvent e) {

        Player player = e.getPlayer();
        PlayerFunction.getinstance(player).resetFunctions();
        DuelManager.getDuelManager(player).setLoser(player);

        SeveralMessages.ClassChangeMessage(player);

    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {

        Player player = e.getPlayer();

        if (DuelManager.checkInDuel(player)) {
            DuelManager.getDuelManager(player).setLoser(player);
            PlayerHealthShield.getinstance(player).setCurrentHealth(PlayerManager.getinstance(player).Health);
            return;
        }

        if(!player.isDead()) {
            player.setHealth(0); // 안 죽었을때 체력 0
            PlayerHealthShield.getinstance(player).setCurrentHealth(0);
        }
        else
        {
            //player.spigot().respawn();
        }

        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {

            EntityManager.getDisguiseEntitiesPlayer().stream().forEach(value -> EntityPlayerWatcher.Remove(value, player));

        }, 5);

        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {

            EntityManager.getDisguiseEntitiesPlayer().stream().forEach(value -> EntityPlayerManager.getInstance().showTo(value, player));

        },20);

    }

    @EventHandler
    public void PlayerTakeDamageEvent(PlayerTakeDamageEvent e) {

        Entity damager = e.getDamager();
        Player target = e.getTarget();
        int damage = e.getDamage();

        if(PlayerManager.getinstance(target).CurrentClass.equals("아이테르")) {
            Aether.getinstance().DmgtoImpulse(damage, target, target); // 아이테르 패시브 활성화를 위해 받은 피해를 저장
        }

        for(Entity player : target.getNearbyEntities(10, 10, 10)) { // 데미지를 받은 플레이어 근처에 "아이테르"가 있으면 impulse 에너지를 부여
            if(player instanceof Player) {
                Player pl = (Player) player;
                //자신이 아이테르가 아니고 자신주변 10칸 이내에 아이테르가 있으면 그 사람에게 에너지를 줌
                if(PlayerManager.getinstance(pl).CurrentClass.equals("아이테르") && !entitycheck.duelcheck(pl, target)) {
                    Aether.getinstance().DmgtoImpulse((int)(damage/2), pl, target);
                }
            }
        }

        if(PlayerFunction.getinstance(target).ACPassiveCoolDown > 0 && PlayerManager.getinstance(target).CurrentClass.equals("엑셀러레이터")) {
            // 엑셀러레이터가 맞으면 패시브 쿨다운 초기화
            PlayerFunction.getinstance(target).ACPassiveCoolDown = 0;
        }
        ReturnToBase.ReturnMech.getinstance().ReturnCancel(target); // 귀환을 하고 있다면 귀환을 취소해 버리기
    }

    @EventHandler
    public void PlayerEnvironmentsDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            EntityDamageEvent.DamageCause cause = event.getCause();

            if(cause == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                double dist = player.getFallDistance();
                if(dist < 6) {
                    return;
                }
                double ratio = dist > 50.0d ? 1 : 1 - (50.0 - dist) / 50.0;
                PlayerHealthShield.getinstance(player).setDamage((int)(ratio * PlayerManager.getinstance(player).Health));
            }
            else if(cause == EntityDamageEvent.DamageCause.LAVA) {
                double r = Math.random() * 0.05 + 0.05;
                PlayerHealthShield.getinstance(player).setDamage((int)(r * PlayerManager.getinstance(player).Health));
            }
//            else if(cause == EntityDamageEvent.DamageCause.FIRE) {
//                double r = Math.random() * 0.05 + 0.02;
//                PlayerHealthShield.getinstance(player).setDamage((int)(r * PlayerManager.getinstance(player).Health));
//            }
            else if(cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                double r = Math.random() * 0.03 + 0.02;
                PlayerHealthShield.getinstance(player).setDamage((int)(r * PlayerManager.getinstance(player).Health));
            }
        }
    }

//    @EventHandler
//    public void PlayerEnvironmentsBlockDamageEvent(EntityDamageByBlockEvent event) {
//        if(event.getEntity() instanceof Player) {
//            Player player = (Player) event.getEntity();
//            EntityDamageEvent.DamageCause cause = event.getCause();
//
//            double r = Math.random() * 0.03 + 0.02;
//            PlayerHealthShield.getinstance(player).setDamage((int) (r * PlayerManager.getinstance(player).Health));
//        }
//    }


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

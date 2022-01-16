package PlayerManager.EventListener;

import ClassAbility.Aether.Aether;
import ClassAbility.entitycheck;
import CustomEvents.PlayerTakeDamageEvent;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerTakeDamage implements Listener {

    @EventHandler
    public void PlayerTakeDamageEvent(PlayerTakeDamageEvent e) {

        Entity damager = e.getDamager();
        Player target = e.getTarget();
        int damage = e.getDamage();

        PlayerFunction PF = PlayerFunction.getinstance(target);
        PlayerEnergy PE = PlayerEnergy.getinstance(target);
        PlayerManager PM = PlayerManager.getinstance(target);

        if(PlayerManager.getinstance(target).CurrentClass.equals("아이테르")) {
            Aether.getinstance().DmgtoImpulse(damage, target, target); // 아이테르 패시브 활성화를 위해 받은 피해를 저장
        }

        for(Entity player : target.getNearbyEntities(10, 10, 10)) { // 데미지를 받은 플레이어 근처에 "아이테르"가 있으면 impulse 에너지를 부여
            if(player instanceof Player pl) {
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
            else if(cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                double r = Math.random() * 0.03 + 0.02;
                PlayerHealthShield.getinstance(player).setDamage((int)(r * PlayerManager.getinstance(player).Health));
            }
        }
    }

}

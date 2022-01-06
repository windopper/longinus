package DynamicData;

import CustomEvents.PlayerTakeDamageEvent;
import Mob.EntityManager;
import Mob.MobListManager;
import PlayerManager.EntityHealthBossBar;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import spellinteracttest.Main;

public class Damage {
	
	private static Damage Damage;
	
	private Damage() {
		
	}
	
	public static Damage getinstance() {
		if(Damage == null) Damage = new Damage();
		return Damage;
	}
	
	public void taken(int damage, LivingEntity taker, LivingEntity damager) {
		
		if(taker instanceof Player user) {
			taker.setMaximumNoDamageTicks(10);
			taker.setNoDamageTicks(0);
			user.damage(0.001);
			//플레이어 체력 & 쉴드 객체 가져오기
			PlayerHealthShield PH = PlayerHealthShield.getinstance(user);
			// 방어도 계산
			final int dmg = (int)(damage * PlayerManager.getinstance(user).defcalculate(user));

			HologramIndicator.getinstance().DamageIndicator(dmg, taker);
			PlayerHealthShield.getinstance(user).setDamage(damage);

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerTakeDamageEvent(damager, user, dmg));
				}
			}, 0);

			return;
		}
		else if(!(taker instanceof ArmorStand) && (taker instanceof LivingEntity)) {

			if(taker.isInvulnerable()) return;

			MobListManager.MobList mobList = EntityManager.getinstance(taker).getMobList();

			if(taker.getCustomName() != null) { // 튜토리얼
				String split[] = taker.getCustomName().split("m");
				if(split[0].equals("exa")) {
					return;
				}
			}

			taker.setLastDamageCause(new EntityDamageEvent(damager, EntityDamageEvent.DamageCause.ENTITY_ATTACK ,0.001));
			taker.setMaximumNoDamageTicks(1);
			taker.setNoDamageTicks(0);
			taker.damage(0.0001);
			
			EntityManager EH = EntityManager.getinstance(taker);

			if(damager instanceof Player) { // 보스바
				EntityHealthBossBar.getinstance((Player)damager).EntityShowHealthBossbar((Player)damager, taker);
			}

			if(damager instanceof Player) EH.setDamageValue(damage, (Player) damager);
			else EH.setDamageValue(damage);
			// 엔티티의 변화 감지
			//EH.EntityWatcher();

			HologramIndicator.getinstance().DamageIndicator(damage, taker);
		}
	}
}

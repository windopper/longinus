package DynamicData;

import CustomEvents.PlayerTakeDamageEvent;
import Mob.EntityManager;
import Mob.MobListManager;
import PlayerManager.EntityHealthBossBar;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import PlayerManager.PlayerEnergy;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import spellinteracttest.Main;

import java.util.function.Function;

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

			//플레이어 체력 & 쉴드 객체 가져오기
			PlayerHealthShield PH = PlayerHealthShield.getinstance(user);
			PlayerFunction PF = PlayerFunction.getinstance(user);
			PlayerEnergy PE = PlayerEnergy.getinstance(user);
			PlayerManager PM = PlayerManager.getinstance(user);

			if(PM.evasion.size()>=1) {
				HologramIndicator.getinstance().Indicator(ChatColor.of("#87CEFA")+"회피!", user.getLocation(), 30);
				PM.evasion.remove(0);
				return;
			}

			taker.setMaximumNoDamageTicks(1);
			taker.setNoDamageTicks(0);
			user.damage(0.001);

			// 방어도 계산
			int dmg = (int)(damage * PlayerManager.getinstance(user).defcalculate(user));
			dmg *= PM.damageTakenRate;

			// 피해를 받았을 때 작동
			PM.runWhenDamaged.forEach((a)->a.run());
			// 피해를 주는 이가 플레이어일 경우
			if(damager instanceof Player player) {
				PlayerManager.getinstance(player).runWhenAttack.forEach(Runnable::run);
				for(Function<Integer, Integer> modf : PlayerManager.getinstance(player).giveDamageModifier) {
					damage = modf.apply(damage);
				}
			}

			// 데미지 modify
			for(Function<Integer, Integer> modf : PM.takeDamageModifier) {
				dmg = modf.apply(dmg);
			}

			HologramIndicator.getinstance().DamageIndicator(dmg, taker);
			PlayerHealthShield.getinstance(user).setDamage(dmg);

			final int fidmg = dmg;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerTakeDamageEvent(damager, user, fidmg));
				}
			}, 0);
		}
		else if(!(taker instanceof ArmorStand) && (taker != null)) {

			EntityManager EH = EntityManager.getinstance(taker);
			damage *= EH.damageTakenRate;

			if(EH.canDamagedByPlayer()) {
				if(damager instanceof Player) return;
			}
			if(taker.isInvulnerable()) return;
			taker.getWorld().playSound(taker.getLocation(), Sound.ENTITY_GENERIC_HURT, 1, 1);

			MobListManager.MobList mobList = EntityManager.getinstance(taker).getMobList();

//			if(taker.getCustomName() != null) { // 튜토리얼
//				String split[] = taker.getCustomName().split("m");
//				if(split[0].equals("exa")) {
//					return;
//				}
//			}

			taker.setLastDamageCause(new EntityDamageEvent(damager, EntityDamageEvent.DamageCause.ENTITY_ATTACK ,0.001));
			taker.setMaximumNoDamageTicks(1);
			taker.setNoDamageTicks(0);
			taker.damage(0.0001);

			if(damager instanceof Player player) { // 보스바
				EntityHealthBossBar.getinstance((Player)damager).EntityShowHealthBossbar(player, taker);

				PlayerFunction PF = PlayerFunction.getinstance(player);
				PlayerEnergy PE = PlayerEnergy.getinstance(player);
				PlayerManager PM = PlayerManager.getinstance(player);

				PM.runWhenAttack.forEach(Runnable::run);

				for(Function<Integer, Integer> modf : PM.giveDamageModifier) {
					damage = modf.apply(damage);
				}

				// 카오스 FR 스킬
//				if(PF.KhaosFR > 0 && PE.getEnergy()>1 && PE.getEnergy() < 20) {
//					DynamicData.HologramIndicator.getinstance().ManaIndicator(1, player.getLocation());
//					PE.setEnergy(PE.getEnergy() + 1);
//				}
			}

			EH.setDamageValue(damage, damager);
			HologramIndicator.getinstance().DamageIndicator(damage, taker);
		}
	}
}

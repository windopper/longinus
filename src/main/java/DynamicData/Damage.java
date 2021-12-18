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
		
		if(taker instanceof Player) {
			Player user = (Player) taker;
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

			taker.setLastDamageCause(new EntityDamageEvent(damager, EntityDamageEvent.DamageCause.ENTITY_ATTACK ,0.1));
			taker.setMaximumNoDamageTicks(1);
			taker.setNoDamageTicks(0);
			taker.damage(0.1);
			
			EntityManager EH = EntityManager.getinstance(taker);

			if(damager instanceof Player) { // 보스바
				EntityHealthBossBar.getinstance((Player)damager).EntityShowHealthBossbar((Player)damager, taker);
			}

			if(damager instanceof Player) EH.setDamageValue(damage, (Player) damager);
			else EH.setDamageValue(damage);

			HologramIndicator.getinstance().DamageIndicator(damage, taker);
		}
		
		
	}
//	public void take(int dmg, LivingEntity takenP) {
//
//		if(takenP instanceof Player) {
//			Player user = (Player) takenP;
//
//			//플레이어 체력 & 쉴드 객체 가져오기
//			PlayerHealthShield PH = PlayerHealthShield.getinstance(user);
//
//
//			user.damage(0.1);
//
//
//			if(PH.getShieldRegenerateStop()==0) //피해 받으면 보호막 재생이 멈춤
//				PH.setShieldRegenerateStop();
//
//			// 쉴드가 있을때
//			if(PH.getCurrentShield() > 0) {
//				if(PH.getCurrentShield()-dmg <= 0) { //쉴드가 깨짐
//					PH.setCurrentShield(0);
//					PH.setShieldRegenerateCooldown(0);
//
//					user.getWorld().spawnParticle(Particle.BLOCK_CRACK, user.getLocation(), 50, 0.5, 0.5, 0.5, Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
//					HologramIndicator.getinstance().ShieldBroken(user);
//				}
//				else {
//					PH.setCurrentShield(PH.getCurrentShield()-dmg);
//				}
//
//			}
//
//
//			// 쉴드가 없을때
//			else {
//				if(PH.getCurrentHealth()-dmg>0) {
//					PH.setCurrentHealth(PH.getCurrentHealth()-dmg);
//				}
//				else {
//					PH.setCurrentHealth(0);
//				}
//
//			}
//
//			if(UserManager.getinstance(user).CurrentClass.equals("아이테르")) {
//				Aether.getinstance().DmgtoImpulse(dmg, user, user); // 아이테르 패시브 활성화를 위해 받은 피해를 저장
//			}
//
//
//			for(Entity player : takenP.getNearbyEntities(10, 10, 10)) { // 데미지를 받은 플레이어 근처에 "아이테르"가 있으면 impulse 에너지를 부여
//				if(player instanceof Player) {
//					Player pl = (Player) player;
//					//자신이 아이테르가 아니고 자신주변 10칸 이내에 아이테르가 있으면 그 사람에게 에너지를 줌
//					if(UserManager.getinstance(pl).CurrentClass.equals("아이테르")) {
//						Aether.getinstance().DmgtoImpulse(dmg, pl, user);
//					}
//				}
//			}
//
//			if(PlayerFunction.getinstance(user).ACPassiveCoolDown > 80 && UserManager.getinstance(user).CurrentClass.equals("엑셀러레이터")) {
//				// 엑셀러레이터가 맞으면 패시브 쿨다운 초기화
//				PlayerFunction.getinstance(user).ACPassiveCoolDown = 80;
//			}
//
//			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
//
//
//			return;
//		}
//
//		else if(!(takenP instanceof ArmorStand) && (takenP instanceof LivingEntity)) {
//
//			if(takenP.isInvulnerable()) return;
//
//			takenP.setMaximumNoDamageTicks(1);
//			takenP.setNoDamageTicks(0);
//
//			takenP.damage(0.1);
//
//			EntityManager EH = EntityManager.getinstance(takenP);
//			EH.setDamageValue(dmg);
//			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
//		}
//	}
}

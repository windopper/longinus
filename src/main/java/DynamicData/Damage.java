package DynamicData;

import CustomEvents.PlayerDeathEvent;
import CustomEvents.PlayerTakeDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import ClassAbility.Aether;
import ClassAbility.entitycheck;
import Mob.mobhitsound;
import UserData.UserManager;
import spellinteracttest.Main;

public class Damage {
	
	private static Damage Damage;
	
	private Damage() {
		
	}
	
	public static Damage getinstance() {
		if(Damage == null) Damage = new Damage();
		return Damage;
	}
	
	public void taken(int damage, final LivingEntity takenP, final LivingEntity damager) {
		
		if(takenP instanceof Player) {
			Player user = (Player) takenP;
			
			//플레이어 체력 & 쉴드 객체 가져오기
			PlayerHealth PH = PlayerHealth.getinstance(user);

			// 방어도 계산
			final int dmg = (int)(damage * UserManager.getinstance(user).defcalculate(user));

			user.damage(0.1);
			
			if(PH.getShieldRegenerateStop()==0) //피해 받으면 보호막 재생이 멈춤
				PH.setShieldRegenerateStop();
			
			// 쉴드가 있을때
			if(PH.getCurrentShield() > 0) {
				if(PH.getCurrentShield()-dmg <= 0) { //쉴드가 깨짐
					PH.setCurrentShield(0);
					PH.setShieldRegenerateCooldown(0);
					
					user.getWorld().spawnParticle(Particle.BLOCK_CRACK, user.getLocation(), 50, 0.5, 0.5, 0.5, Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
					HologramIndicator.getinstance().ShieldBroken(user);
				}
				else {
					PH.setCurrentShield(PH.getCurrentShield()-dmg);
				}
				
			}

			// 쉴드가 없을때
			else {
				if(PH.getCurrentHealth() - dmg>0) {
					PH.setCurrentHealth(PH.getCurrentHealth() - dmg);
				}
				else {
					PH.setCurrentHealth(0);
				}

			}


			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerTakeDamageEvent(damager, user, dmg));
				}
			}, 0);

			return;
		}
		
		else if(!(takenP instanceof ArmorStand) && (takenP instanceof LivingEntity)) {
			
			if(takenP.getCustomName() != null) { // 튜토리얼 
				String split[] = takenP.getCustomName().split("m");
				if(split[0].equals("exa")) {
					return;
				}
			}
			
			takenP.setMaximumNoDamageTicks(1);
			takenP.setNoDamageTicks(0);	
			takenP.damage(0.1);
			
			EntityHealthManager EH = EntityHealthManager.getinstance(takenP);
			
			
			if(EH.getCurrentHealth()-damage < 0) { // 바이V 정수 수집
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getWorld().getName().equals(takenP.getWorld().getName()) && UserManager.getinstance(p).CurrentClass.equals("바이V")) {
						Location eloc = takenP.getLocation();
						Location ploc = p.getLocation();
						double dist = eloc.distance(ploc);
						if(dist<10) PlayerFunction.getinstance(p).essence++;
					}
				}
			}
			
			EH.setCurrentHealth(EH.getCurrentHealth()-damage);
			
			if(damager instanceof Player) {
				EH.EntityHealthWatcher();
				EntityHealthBossBar.getinstance((Player)damager).EntityShowHealthBossbar((Player)damager, takenP);
			}
			
			HologramIndicator.getinstance().DamageIndicator(damage, takenP);
			mobhitsound mobhitsound = new mobhitsound();
			mobhitsound.sound(takenP);

		}
		
		
	}
	
	
	public void taken(int dmg, LivingEntity takenP) {
		
		if(takenP instanceof Player) {
			Player user = (Player) takenP;
			
			//플레이어 체력 & 쉴드 객체 가져오기
			PlayerHealth PH = PlayerHealth.getinstance(user);
			
			
			user.damage(0.1);
			
			
			if(PH.getShieldRegenerateStop()==0) //피해 받으면 보호막 재생이 멈춤
				PH.setShieldRegenerateStop();
			
			// 쉴드가 있을때
			if(PH.getCurrentShield() > 0) {
				if(PH.getCurrentShield()-dmg <= 0) { //쉴드가 깨짐
					PH.setCurrentShield(0);
					PH.setShieldRegenerateCooldown(0);
					
					user.getWorld().spawnParticle(Particle.BLOCK_CRACK, user.getLocation(), 50, 0.5, 0.5, 0.5, Material.PURPLE_GLAZED_TERRACOTTA.createBlockData());
					HologramIndicator.getinstance().ShieldBroken(user);
				}
				else {
					PH.setCurrentShield(PH.getCurrentShield()-dmg);
				}
				
			}
			
			
			// 쉴드가 없을때
			else {
				if(PH.getCurrentHealth()-dmg>0) {
					PH.setCurrentHealth(PH.getCurrentHealth()-dmg);
				}
				else {
					PH.setCurrentHealth(0);
				}

			}
			
			if(UserManager.getinstance(user).CurrentClass.equals("아이테르")) {
				Aether.getinstance().DmgtoImpulse(dmg, user, user); // 아이테르 패시브 활성화를 위해 받은 피해를 저장
			}
				
			
			for(Entity player : takenP.getNearbyEntities(10, 10, 10)) { // 데미지를 받은 플레이어 근처에 "아이테르"가 있으면 impulse 에너지를 부여
				if(player instanceof Player) {
					Player pl = (Player) player;
					//자신이 아이테르가 아니고 자신주변 10칸 이내에 아이테르가 있으면 그 사람에게 에너지를 줌
					if(UserManager.getinstance(pl).CurrentClass.equals("아이테르")) {
						Aether.getinstance().DmgtoImpulse(dmg, pl, user);
					}
				}
			}
			
			if(PlayerFunction.getinstance(user).ACPassiveCoolDown > 80 && UserManager.getinstance(user).CurrentClass.equals("엑셀러레이터")) {
				// 엑셀러레이터가 맞으면 패시브 쿨다운 초기화
				PlayerFunction.getinstance(user).ACPassiveCoolDown = 80;
			}
			
			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
			
			
			return;
		}
		
		else if(!(takenP instanceof ArmorStand) && (takenP instanceof LivingEntity)) {
			
			takenP.setMaximumNoDamageTicks(1);
			takenP.setNoDamageTicks(0);
			
			takenP.damage(0.1);
			
			EntityHealthManager EH = EntityHealthManager.getinstance(takenP);
			
			EH.setCurrentHealth(EH.getCurrentHealth()-dmg);
			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
		}
		
		

		

		
		

		
	}
	

}

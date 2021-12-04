package Interact;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import ClassAbility.Accelerator;
import ClassAbility.Aether;
import ClassAbility.ByV;
import ClassAbility.entitycheck;
import Mob.mobhitsound;
import dynamicdata.EntityHealth;
import dynamicdata.EntityHealthBossBar;
import dynamicdata.HologramIndicator;
import dynamicdata.PlayerHealth;
import userdata.UserManager;
import userdata.UserStatManager;

public class Damage {
	
	private static Damage Damage;
	
	private Damage() {
		
	}
	
	public static Damage getinstance() {
		if(Damage == null) Damage = new Damage();
		return Damage;
	}
	
	public void taken(int dmg, LivingEntity takenP, LivingEntity damager) {
		
		if(takenP instanceof Player) {
			Player user = (Player) takenP;
			
			//플레이어 체력 & 쉴드 객체 가져오기
			PlayerHealth PH = PlayerHealth.getinstance(user);
			
			
			// 방어도 계산
			dmg = (int)(dmg * UserManager.getinstance(user).defcalculate(user));
			
			
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
					if(UserManager.getinstance(pl).CurrentClass.equals("아이테르") && !entitycheck.duelcheck(pl, takenP)) {
						Aether.getinstance().DmgtoImpulse((int)(dmg/2), pl, user);
					}
				}
			}
			
			if(!Accelerator.passivecooldown.containsKey(user) && UserManager.getinstance(user).CurrentClass.equals("엑셀러레이터")) { 
				// 엑셀러레이터가 맞으면 패시브 쿨다운 초기화
				Accelerator.passivecooldown.put(user, 0);
			}
			
			
			returns.ReturnMech.getinstance().ReturnCancel(user); // 귀환을 하고 있다면 귀환을 취소해 버리기
			
			
			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
			
			
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
			
			EntityHealth EH = EntityHealth.getinstance(takenP);
			
			
			if(EH.getCurrentHealth()-dmg < 0) { // 바이V 정수 수집
				for(Player p : Bukkit.getOnlinePlayers()) {
					if(p.getWorld().getName().equals(takenP.getWorld().getName()) && UserManager.getinstance(p).CurrentClass.equals("바이V")) {
						Location eloc = takenP.getLocation();
						Location ploc = p.getLocation();
						double dist = eloc.distance(ploc);
						if(dist<10 && ByV.essence.containsKey(p)) ByV.essence.replace(p, ByV.essence.get(p)+1);
					}
				}
			}
			
			EH.setCurrentHealth(EH.getCurrentHealth()-dmg);
			
			if(damager instanceof Player) {
				EntityHealthBossBar.getinstance((Player)damager).EntityShowHealthBossbar((Player)damager, takenP);
			}
			
			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
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
			
			if(!Accelerator.passivecooldown.containsKey(user) && UserManager.getinstance(user).CurrentClass.equals("엑셀러레이터")) { 
				// 엑셀러레이터가 맞으면 패시브 쿨다운 초기화
				Accelerator.passivecooldown.put(user, 0);
			}
			
			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
			
			
			return;
		}
		
		else if(!(takenP instanceof ArmorStand) && (takenP instanceof LivingEntity)) {
			
			takenP.setMaximumNoDamageTicks(1);
			takenP.setNoDamageTicks(0);
			
			takenP.damage(0.1);
			
			EntityHealth EH = EntityHealth.getinstance(takenP);
			
			EH.setCurrentHealth(EH.getCurrentHealth()-dmg);
			HologramIndicator.getinstance().DamageIndicator(dmg, takenP);
		}
		
		

		

		
		

		
	}
	

}

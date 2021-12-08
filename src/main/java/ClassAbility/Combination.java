package ClassAbility;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import DynamicData.Damage;
import QuestClasses.Tutorial;
import DynamicData.EntityStatus;
import DynamicData.PlayerEnergy;
import DynamicData.PlayerFunction;
import DynamicData.PlayerHealth;
import UserData.UserManager;
import UserData.UserStatManager;

public class Combination {
	
	private static Combination Combination;
	 
	public final static HashMap<Player, Integer> trainermelee = new HashMap<>();
	
	private final static String manaexhaustion = "§c에너지가 부족합니다§c";
	private final static String robotexhaustion = "§c나노로봇이 부족합니다§c";
	private final static String impulseexhaustion = "§c충격량이 부족합니다§c";
	private final static String essenceexhaustion = "§c정수가 부족합니다§c";
	private final static String levelrequire = "§c아직 사용할 수 없는 스킬입니다";
	
	public final static String blank = "                          ";

	private Combination() {
		
	}
	
	public static Combination getinstance() {
		if(Combination == null) Combination = new Combination();
		return Combination;
	}
	
	
	public void removemaps(Player p) {
		trainermelee.remove(p);
	}
	
	public void Checkclass(String name, Player p, String combo) {
		if(name.equals("아이테르")) Aether(p, combo);
		if(name.equals("엑셀러레이터")) Accelerator(p, combo);
		if(name.equals("블래스터")) Blaster(p, combo);
		if(name.equals("바이V")) ByV(p, combo);
		if(name.equals("플록스")) Phlox(p, combo);
		if(name.equals("없음")) Trainer(p, combo);
	}
	public void Sound(Player p) {
		p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
	}
	public void Warning(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
	}
	
	
	public void energyoverload(Player p, String combo) {
		
		PlayerEnergy PE = PlayerEnergy.getinstance(p);
		
		if(PE.getPreviousSkill().equals("none")) { // 이전에 쓴 스킬이 없으면
			PE.setPreviousSkill(combo);
			PE.setEnergyOverload(0);
			PE.setEnergyOverloadCooldown(1);
		}
		
		else {
			if(PE.getPreviousSkill().equals(combo)) { // 이전에 쓴 스킬이 지금 스킬이랑 같으면
				if(PE.getEnergyOverload()==0) { // 에너지 과부하가 없으면
					
					PE.setEnergyOverload(PE.getEnergyOverload()+1);
					PE.setEnergyOverloadCooldown(1);
				}
				else {
					 // 에너지 과부하가 있으면 과부하 1더 추가
					PE.setEnergyOverload(PE.getEnergyOverload()+1);
					// 에너지 과부하 쿨다운 시작
					PE.setEnergyOverloadCooldown(1);
				}
			}
			else { // 이전에 쓴 스킬이 지금 스킬이랑 다르면
				PE.setPreviousSkill(combo);
				PE.setEnergyOverload(0);
				PE.setEnergyOverloadCooldown(1);
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void Trainer(final Player p, String combo) {
		
		int ManaDecrease = UserManager.getinstance(p).ManaDecrease;
		
		
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int charge = 1 - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int arrow = 1 - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		
		String RR = "§o§l대쉬§l§o §3§l-⚡§l"+(charge);
		String FF = "§o§l다트 던지기§l§o §3§l-⚡§l"+(arrow);
		
		if(combo.equals("RR")) {
			
			if(CurrentMana >= charge ) {
				Sound(p);
				p.sendTitle("",blank+RR, 5, 20, 10);
				
				energyoverload(p, combo);
				
				PlayerEnergy.getinstance(p).removeEnergy(charge);
				
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(!Tutorial.exambothitcount.containsKey(p))
						pl.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
					else
						p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
					
				}
				Vector dir = p.getEyeLocation().getDirection();
				dir.normalize();
				p.setVelocity(dir.multiply(1));
				
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}
			

		}
		else if(combo.equals("FF")) {
			
			if(CurrentMana >= arrow) {
				Sound(p);
				p.sendTitle("",blank+FF, 5, 20, 10);
				
				energyoverload(p, combo);
				
				PlayerEnergy.getinstance(p).removeEnergy(arrow);
				
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(!Tutorial.exambothitcount.containsKey(p))
						pl.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ARROW_SHOOT, 1, 1);
					else 
						p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ARROW_SHOOT, 1, 1);
					
				}
				Vector dir = p.getEyeLocation().getDirection();
				dir.normalize();
				dir.multiply(1.5);
				Arrow ar = (Arrow) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.ARROW);
				int dmg = UserManager.getinstance(p).meleedmgcalculate(p, 1);
				ar.setVelocity(dir);
				ar.setCustomName("dart"+":"+p.getName());
				
				ar.addScoreboardTag(p.getName());
				
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("L")) {
			
			if(!trainermelee.containsKey(p)) {
							
				HashMap<Entity, Integer> meleehit = new HashMap<>();
				trainermelee.put(p, 0);
				Vector dir1 = p.getLocation().getDirection();
				Location loc1 = p.getEyeLocation();
				dir1.normalize();
				dir1.multiply(0.2);
				for(Player pl : Bukkit.getOnlinePlayers()) { // 튜토리얼 소리 통제
					if(!Tutorial.exambothitcount.containsKey(p))
						pl.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
				}
				if(Tutorial.exambothitcount.containsKey(p))
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
				
				
				for(int i=0; i<15; i++) {
					
					for(Player pl : Bukkit.getOnlinePlayers()) {
						
//						if(i==14) {
//							pl.spawnParticle(Particle.SMOKE_NORMAL, loc1, 20, 0.5, 0.5, 0.5, 0.1, null);
//						}
//						else if(i==12) {
//							pl.spawnParticle(Particle.BLOCK_CRACK, loc1, 20, 0.5, 0.5, 0.5, 0, Material.SEA_LANTERN.createBlockData());
//						}
						if(i==13) {
							if(!Tutorial.exambothitcount.containsKey(p))
								pl.spawnParticle(Particle.SWEEP_ATTACK, loc1, 10, 0.5, 0.5, 0.5, 0, null);
							else 
								p.spawnParticle(Particle.SWEEP_ATTACK, loc1, 10, 0.5, 0.5, 0.5, 0, null);
							
							
						}
					}
					
					for(Entity e : p.getWorld().getLivingEntities()) {
						
						Location eloc = e.getLocation();
						double dist = eloc.distance(loc1);
						
						if(dist<2 || e.getBoundingBox().contains(loc1.getX(), loc1.getY(), loc1.getZ())) {
							
							if(entitycheck.duelcheck(e, p) && entitycheck.entitycheck(e) && !meleehit.containsKey(e)) {
								p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2);
								meleehit.put(e, 0);
								LivingEntity le = (LivingEntity) e;
								int dmg = UserManager.getinstance(p).meleedmgcalculate(p, 1);
								Damage.getinstance().taken(dmg, le, p);
								EntityStatus.getinstance(le).KnockBack(p, 0.3);
								
								if(le.getCustomName() != null) {
									if(le.getCustomName().equals("샌드백")) {
										if(PlayerHealth.getinstance(p).getCurrentShield()>0) {
											Damage.getinstance().taken(2000, (LivingEntity) p, p);
											p.sendMessage("§e시험 진행 A.I:§e §f시간이 지나면 보호막은 자동으로 채워지니 염려하지 않으셔도 됩니다.");
											p.playSound(p.getLocation(), "meme.tut6", 5, 1);
											Tutorial.trainerbothit.put(p, 1);
										}
									}
								}
								
								if(!Tutorial.examset.containsKey(p)) {
									
									if(le.getCustomName() != null) {  // 슬라임 봇 때릴 때
										String split[] = le.getCustomName().split("m");
										if(split.length == 2) {
											if(split[1] != null) {
												
												if(Tutorial.exambothit.containsKey(p)) {// 튜토리얼 활성화?
													
													int number = Integer.parseInt(split[1]);
													
													if(Tutorial.exambothit.get(p)[number-1] == 0) { // 때린 봇의 번혿가 0번이면
														
														Tutorial.exambothit.get(p)[number-1] = number; // 때린 봇 번호 추가
														Tutorial.exambothitcount.replace(p, Tutorial.exambothitcount.get(p)+1); // 횟수 추가
														break; // 번호 넣으면 탈출
													}	
													
													
													
													
												}
												
											}
										}

									}
								}
								

								
							}
						}
					}			
					loc1.add(dir1);
				}
				PotionEffect potion = new PotionEffect(PotionEffectType.SLOW_DIGGING, 10, 10);
				p.addPotionEffect(potion, true);
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						
						trainermelee.replace(p, trainermelee.get(p)+1);
						if(trainermelee.get(p) >=2) {
							trainermelee.remove(p);
							cancel();
						}
						
					}
				}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 10);
			}
			

			
			
			

		}
	}
	
	public void Aether(Player p, String combo) {

		PlayerFunction PF = PlayerFunction.getinstance(p);
		
		int lvl = UserStatManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = UserManager.getinstance(p).ManaDecrease;
		
		int impulseswitchshieldmana = ClassAbility.Aether.ImpulseSwitchShieldmana - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int shieldswitchchargemana = ClassAbility.Aether.ShieldSwitchChargemana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int WeaponModeChangemana = ClassAbility.Aether.WeaponModeChangemana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int impulseswitchweaponmana = ClassAbility.Aether.ImpulseSwitchWeaponmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int impulseswitchenergymana = ClassAbility.Aether.ImpulseSwitchEnergymana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		
		String RL = "§o§l충격량전환: 보호막§l§o §3§l-⚡§l"+(impulseswitchshieldmana);
		String RR = "§o§l충격량전환: 돌진§l§o §3§l-⚡§l"+(shieldswitchchargemana);
		String FR = "§o§l무기 모드 변경§l§o §3§l-⚡§l"+(WeaponModeChangemana);
		String RF = "§o§l충격량전환: 레이저§l§o §3§l-⚡§l"+(impulseswitchweaponmana);
		String FF = "§o§l충격량전환: 에너지§l§o §c§l-☈§l100";
		
		if(combo.equals("L")) {
			
			if(PlayerFunction.getinstance(p).getMelee()==0 && PF.getMeleemode() == 0) Aether.getinstance().melee(p);
			if(PlayerFunction.getinstance(p).getMelee()==0 && PF.getMeleemode() == 1) Aether.getinstance().melee2(p);

		}
		if(combo.equals("RL")) {
			
			if(CurrentMana >= impulseswitchshieldmana ) {
				Sound(p);
				p.sendTitle("",blank+RL, 5, 20, 10);
				energyoverload(p, combo);
				Aether.getinstance().ImpulseSwitchShield(p, impulseswitchshieldmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= shieldswitchchargemana ) {
				Sound(p);
				p.sendTitle("",blank+RR, 5, 20, 10);
				energyoverload(p, combo);
				Aether.getinstance().ShieldSwitchCharge(p, shieldswitchchargemana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}
			

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= WeaponModeChangemana) {
				Sound(p);
				p.sendTitle("",blank+FR, 5, 20, 10);
				energyoverload(p, combo);
				Aether.getinstance().SwitchWeapon(p, WeaponModeChangemana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= impulseswitchweaponmana) {
				Sound(p);
				p.sendTitle("",blank+RF, 5, 20, 10);
				energyoverload(p, combo);
				Aether.getinstance().ImpulseSwitchWeapon(p, impulseswitchweaponmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(PF.AEImpulse >= 100d) {
				Sound(p);
				p.sendTitle("",blank+FF, 5, 20, 10);
				Aether.getinstance().ImpulseSwitchEnergy(p);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+impulseexhaustion, 5, 20, 10);
			}

		}
		
		
		
	}

	public void Accelerator(Player p, String combo) {
		
		int lvl = UserStatManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = UserManager.getinstance(p).ManaDecrease;
		
		int RLmana = ClassAbility.Accelerator.movehitmana - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RRmana = ClassAbility.Accelerator.adrenalinemana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RFmana = ClassAbility.Accelerator.bombthrowmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FRmana = ClassAbility.Accelerator.randomfiremana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FFmana = ClassAbility.Accelerator.particleaccelerationmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		
		String RL = "§o§l기동 타격§l§o §3§l-⚡§l"+(RLmana);
		String RR = "§o§l아드레날린§l§o §3§l-⚡§l"+(RRmana);
		String FR = "§o§l난사§l§o §3§l-⚡§l"+(FRmana);
		String RF = "§o§l플라즈마 고폭탄§l§o §3§l-⚡§l"+(RFmana);
		String FF = "§o§l입자 가속§l§o §3§l-⚡§l"+(FFmana);
		
		if(combo.equals("L")) {
			
			if(PlayerFunction.getinstance(p).getMelee() == 0) Accelerator.getinstance().melee(p);

		}
		if(combo.equals("RL")) {
			
			
			if(CurrentMana >= RLmana ) {
				Sound(p);
				p.sendTitle("",blank+RL, 5, 20, 10);
				energyoverload(p, combo);
				Accelerator.getinstance().movehit(p, RLmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RRmana ) {
				Sound(p);
				p.sendTitle("",blank+RR, 5, 20, 10);
				energyoverload(p, combo);
				Accelerator.getinstance().adrenaline(p, RRmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}
			

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FRmana) {
				Sound(p);
				p.sendTitle("",blank+FR, 5, 20, 10);
				energyoverload(p, combo);
				Accelerator.getinstance().randomfire(p, FRmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RFmana) {
				Sound(p);
				p.sendTitle("",blank+RF, 5, 20, 10);
				energyoverload(p, combo);
				Accelerator.getinstance().bombthrow(p, RFmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FFmana) {
				Sound(p);
				p.sendTitle("",blank+FF, 5, 20, 10);
				energyoverload(p, combo);
				Accelerator.getinstance().particleacceleration(p, FFmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		
		
		
	}
	
	
	public void ByV(Player p, String combo) {
		
		int lvl = UserStatManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = UserManager.getinstance(p).ManaDecrease;

		
		int RLmana = ClassAbility.ByV.recovermana - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RRmana = ClassAbility.ByV.takendownmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RFmana = ClassAbility.ByV.chainmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FRmana = ClassAbility.ByV.punchmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FFmana = ClassAbility.ByV.shockwavemana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		
		String RL = "§o§l회복§l§o §3§l-⚡§l"+(RLmana);
		String RR = "§o§l내려찍기§l§o §3§l-⚡§l"+(RRmana);
		String FR = "§o§l사슬 발사§l§o §3§l-⚡§l"+(FRmana);
		String RF = "§o§l아광속 펀치§l§o §3§l-⚡§l"+(RFmana);
		String FF = "§o§l충격파§l§o §3§l-⚡§l"+(FFmana);
		
		if(combo.equals("L")) {
			
			if(PlayerFunction.getinstance(p).getMelee()==0) ByV.getinstance().melee(p);

		}
		if(combo.equals("RL")) {
			
			if(CurrentMana >= RLmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle("",blank+RL, 5, 20, 10);
				energyoverload(p, combo);
				ByV.getinstance().recover(p, RLmana);
			}
			else {
				if(CurrentMana < RLmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle("",blank+essenceexhaustion, 5, 20, 10);
				}		

			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RRmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle("",blank+RR, 5, 20, 10);
				energyoverload(p, combo);
				ByV.getinstance().takedown(p, RRmana);
			}
			else {
				if(CurrentMana < RRmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle("",blank+essenceexhaustion, 5, 20, 10);
				}
			}
			

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FRmana) {
				Sound(p);
				p.sendTitle("",blank+FR, 5, 20, 10);
				energyoverload(p, combo);
				ByV.getinstance().chain(p, FRmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RFmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle("",blank+RF, 5, 20, 10);
				energyoverload(p, combo);
				ByV.getinstance().punch(p, RFmana);
			}
			else {
				if(CurrentMana < RFmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle("",blank+essenceexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FFmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle("",blank+FF, 5, 20, 10);
				energyoverload(p, combo);
				ByV.getinstance().shockwave(p, FFmana);
			}
			else {
				if(CurrentMana < FFmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle("",blank+essenceexhaustion, 5, 20, 10);
				}
			}

		}
		
		
		
	}

	public void Phlox(Player p, String combo) {
		
		int lvl = UserStatManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int CurrentRobot = PlayerFunction.getinstance(p).PHNanoRobot;
		int ManaDecrease = UserManager.getinstance(p).ManaDecrease;
		
		int RLrobot = ClassAbility.Phlox.healrobot;
		int RRrobot = ClassAbility.Phlox.escaperobot;
		int RFrobot = ClassAbility.Phlox.annihilationrobot;
		int FRrobot = ClassAbility.Phlox.interruptrobot;
		
		int RLmana = ClassAbility.Phlox.healmana - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RRmana = ClassAbility.Phlox.escapemana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RFmana = ClassAbility.Phlox.annihilationmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FRmana = ClassAbility.Phlox.interruptmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FFmana = ClassAbility.Phlox.robotmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		
		String RL = "§o§l정밀치료§l§o §3§l-⚡§l"+(RLmana);
		String RR = "§o§l긴급탈출§l§o §3§l-⚡§l"+(RRmana);
		String FR = "§o§l방해장 가동§l§o §3§l-⚡§l"+(FRmana);
		String RF = "§o§l섬멸개시§l§o §3§l-⚡§l"+(RFmana);
		String FF = "§o§l로봇급조§l§o §3§l-⚡§l"+(FFmana);
		
		if(combo.equals("L")) {
			
			if(PlayerFunction.getinstance(p).getMelee()==0) Phlox.getinstance().melee(p);

		}
		if(combo.equals("RL")) {
			
			if(CurrentMana >= RLmana && CurrentRobot >= RLrobot) {
				Sound(p);
				p.sendTitle("",blank+RL, 5, 20, 10);
				energyoverload(p, combo);
				Phlox.getinstance().heal(p, RLmana);
			}
			else {
				Warning(p);
				if(CurrentMana < RLmana) {
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					p.sendTitle("",blank+robotexhaustion, 5, 20, 10);
				}

			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RRmana && CurrentRobot >= RRrobot) {
				Sound(p);
				p.sendTitle("",blank+RR, 5, 20, 10);
				energyoverload(p, combo);
				Phlox.getinstance().escape(p, RRmana);
			}
			else {
				Warning(p);
				if(CurrentMana < RRmana) {
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					p.sendTitle("",blank+robotexhaustion, 5, 20, 10);
				}
			}
			

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FRmana && CurrentRobot >= FRrobot) {
				Sound(p);
				p.sendTitle("",blank+FR, 5, 20, 10);
				energyoverload(p, combo);
				Phlox.getinstance().interrupt(p, FRmana);
			}
			else {
				Warning(p);
				if(CurrentMana < FRmana) {
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					p.sendTitle("",blank+robotexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RFmana && CurrentRobot >= RFrobot) {
				Sound(p);
				p.sendTitle("",blank+RF, 5, 20, 10);
				energyoverload(p, combo);
				Phlox.getinstance().annihilation(p, RFmana);
			}
			else {
				Warning(p);
				if(CurrentMana < RFmana) {
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					p.sendTitle("",blank+robotexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FFmana) {
				Sound(p);
				p.sendTitle("",blank+FF, 5, 20, 10);
				energyoverload(p, combo);
				Phlox.getinstance().robot(p, FFmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		
		
		
	}
	
	
	public void Blaster(Player p, String combo) {
		
		int lvl = UserStatManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = UserManager.getinstance(p).ManaDecrease;
		
		int RLmana = ClassAbility.Blaster.railgunmana - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RRmana = ClassAbility.Blaster.grenadelaunchermana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RFmana = ClassAbility.Blaster.riflemana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FRmana = ClassAbility.Blaster.energytransmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FFmana = ClassAbility.Blaster.magneticfieldmana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		
		String RL = "§o§l모드전환: 레일건§l§o §3§l-⚡§l"+(RLmana);
		String RR = "§o§l모드전환: 유탄발사기§l§o §3§l-⚡§l"+(RRmana);
		String RF = "§o§l모드전환: 라이플§l§o §3§l-⚡§l"+(RFmana);
		String FR = "§o§l에너지전환: 생명력§l§o §3§l-⚡§l"+(FRmana);
		String FF = "§o§l자기장§l§o §3§l-⚡§l"+(FFmana);
		
		if(combo.equals("L")) {
			
			if(PlayerFunction.getinstance(p).getMelee()==0) Blaster.getinstance().melee(p);

		}
		if(combo.equals("RL")) {
			
			if(CurrentMana >= RLmana) {
				if(PlayerFunction.getinstance(p).getMeleemode()==0) {
					Warning(p);
					p.sendTitle("",blank+"§c이미 해당 모드입니다§c", 5, 20, 10);
					return;
				}
				Sound(p);
				p.sendTitle("",blank+RL, 5, 20, 10);
				energyoverload(p, combo);
				Blaster.getinstance().railgun(p, RLmana);
			}
			else {
				if(CurrentMana < RLmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}

			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}

			if(CurrentMana >= RRmana) {
				if(PlayerFunction.getinstance(p).getMeleemode()==1) {
					Warning(p);
					p.sendTitle("",blank+"§c이미 해당 모드입니다§c", 5, 20, 10);
					return;
				}	
				Sound(p);
				p.sendTitle("",blank+RR, 5, 20, 10);
				energyoverload(p, combo);
				Blaster.getinstance().grenadelauncher(p, RRmana);
			}
			else {
				if(CurrentMana < RRmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
			}
			

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RFmana) {
				if(PlayerFunction.getinstance(p).getMeleemode()==2) {
					Warning(p);
					p.sendTitle("",blank+"§c이미 해당 모드입니다§c", 5, 20, 10);
					return;
				}	
				Sound(p);
				p.sendTitle("",blank+RF, 5, 20, 10);
				energyoverload(p, combo);
				Blaster.getinstance().rifle(p, RFmana);
			}
			else {
				if(CurrentMana < RFmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FRmana) {
				Sound(p);
				p.sendTitle("",blank+FR, 5, 20, 10);
				energyoverload(p, combo);
				Blaster.getinstance().energytrans(p, CurrentMana);
			}
			else {
				if(CurrentMana < FRmana) {
					Warning(p);
					p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle("",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FFmana) {
				Sound(p);
				p.sendTitle("",blank+FF, 5, 20, 10);
				energyoverload(p, combo);
				Blaster.getinstance().magneticfield(p, FFmana);
			}
			else {
				Warning(p);
				p.sendTitle("",blank+manaexhaustion, 5, 20, 10);
			}

		}
		
		
		
	}
	
	
	

}

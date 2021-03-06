package ClassAbility;

import ClassAbility.Aether.Aether;
import ClassAbility.Aether.AetherMelee;
import ClassAbility.Cheiron.Cheiron;
import ClassAbility.Cheiron.CheironMelee;
import ClassAbility.Khaos.Khaos;
import ClassAbility.Khaos.KhaosMelee;
import ClassAbility.Phlox.Phlox;
import ClassAbility.Shield.Shield;
import ClassAbility.Shield.ShieldMelee;
import PlayerManager.PlayerCombination;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Combination {

	private static Combination Combination;
	 
	//public final static HashMap<Player, Integer> trainermelee = new HashMap<>();
	
	public final static String manaexhaustion = "§c에너지가 부족합니다§c";
	public final static String robotexhaustion = "§c나노로봇이 부족합니다§c";
	public final static String impulseexhaustion = "§c충격량이 부족합니다§c";
	public final static String essenceexhaustion = "§c정수가 부족합니다§c";
	public final static String levelrequire = "§c아직 사용할 수 없는 스킬입니다";
	
	public final static String blank = "                          ";
	public final static String blank2 = "            ";

	private Combination() {
		
	}
	
	public static Combination getinstance() {
		if(Combination == null) Combination = new Combination();
		return Combination;
	}

	public enum Classes {
		아이테르,
		엑셀러레이터,
		블래스터,
		바이V,
		플록스,
		카오스,
		없음;
	}

	public void removemaps(Player p) {
		//trainermelee.remove(p);
	}
	
	public void Checkclass(String name, Player p, String combo) {

		if(PacketRecord.Record.getInstance().isRecording()) {
			PacketRecord.Record.getInstance().recordSkill(p, combo);
			PacketRecord.Record.getInstance().recordCombo(p, PlayerFunction.getinstance(p).getMeleeCombo());
		}

		PlayerCombination.getinstance(p).setPreviousSkill(combo);

		if(name.equals("아이테르")) Aether(p, combo);
		else if(name.equals("엑셀러레이터")) Accelerator(p, combo);
		else if(name.equals("블래스터")) Blaster(p, combo);
		else if(name.equals("바이V")) ByV(p, combo);
		else if(name.equals("플록스")) Phlox(p, combo);
		else if(name.equals("카오스")) Khaos(p, combo);
		else if(name.equals("없음")) Trainer(p, combo);
		else if(name.equals("케이론")) Cheiron(p, combo);
		else if(name.equals("쉴드")) Shield(p, combo);
	}

	public void Sound(Player p) {
		p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
	}
	public void Warning(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
	}


	
	@SuppressWarnings("deprecation")
	public void Trainer(final Player p, String combo) {
//
//		int ManaDecrease = PlayerManager.getinstance(p).ManaDecrease;
//
//
//
//		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
//		int charge = 1 - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
//		int arrow = 1 - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
//
//		String RR = "§o§l대쉬§l§o §3§l-⚡§l"+(charge);
//		String FF = "§o§l다트 던지기§l§o §3§l-⚡§l"+(arrow);
//
//		if(combo.equals("RR")) {
//
//			if(CurrentMana >= charge ) {
//				Sound(p);
//				p.sendTitle(" ",blank+RR, 5, 20, 10);
//
//				energyoverload(p, combo);
//
//				PlayerEnergy.getinstance(p).removeEnergy(charge);
//
//				for(Player pl : Bukkit.getOnlinePlayers()) {
//					if(!Tutorial.exambothitcount.containsKey(p))
//						pl.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
//					else
//						p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
//
//				}
//				Vector dir = p.getEyeLocation().getDirection();
//				dir.normalize();
//				p.setVelocity(dir.multiply(1));
//
//			}
//			else {
//				Warning(p);
//				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
//			}
//
//
//		}
//		else if(combo.equals("FF")) {
//
//			if(CurrentMana >= arrow) {
//				Sound(p);
//				p.sendTitle(" ",blank+FF, 5, 20, 10);
//
//				energyoverload(p, combo);
//
//				PlayerEnergy.getinstance(p).removeEnergy(arrow);
//
//				for(Player pl : Bukkit.getOnlinePlayers()) {
//					if(!Tutorial.exambothitcount.containsKey(p))
//						pl.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ARROW_SHOOT, 1, 1);
//					else
//						p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ARROW_SHOOT, 1, 1);
//
//				}
//				Vector dir = p.getEyeLocation().getDirection();
//				dir.normalize();
//				dir.multiply(1.5);
//				Arrow ar = (Arrow) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.ARROW);
//				int dmg = PlayerManager.getinstance(p).meleedmgcalculate(p, 1);
//				ar.setVelocity(dir);
//				ar.setCustomName("dart"+":"+p.getName());
//
//				ar.addScoreboardTag(p.getName());
//
//			}
//			else {
//				Warning(p);
//				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
//			}
//
//		}
//		else if(combo.equals("L")) {
//
//			if(!trainermelee.containsKey(p)) {
//
//				HashMap<Entity, Integer> meleehit = new HashMap<>();
//				trainermelee.put(p, 0);
//				Vector dir1 = p.getLocation().getDirection();
//				Location loc1 = p.getEyeLocation();
//				dir1.normalize();
//				dir1.multiply(0.2);
//				for(Player pl : Bukkit.getOnlinePlayers()) { // 튜토리얼 소리 통제
//					if(!Tutorial.exambothitcount.containsKey(p))
//						pl.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
//				}
//				if(Tutorial.exambothitcount.containsKey(p))
//					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
//
//
//				for(int i=0; i<15; i++) {
//
//					for(Player pl : Bukkit.getOnlinePlayers()) {
//
////						if(i==14) {
////							pl.spawnParticle(Particle.SMOKE_NORMAL, loc1, 20, 0.5, 0.5, 0.5, 0.1, null);
////						}
////						else if(i==12) {
////							pl.spawnParticle(Particle.BLOCK_CRACK, loc1, 20, 0.5, 0.5, 0.5, 0, Material.SEA_LANTERN.createBlockData());
////						}
//						if(i==13) {
//							if(!Tutorial.exambothitcount.containsKey(p))
//								pl.spawnParticle(Particle.SWEEP_ATTACK, loc1, 10, 0.5, 0.5, 0.5, 0, null);
//							else
//								p.spawnParticle(Particle.SWEEP_ATTACK, loc1, 10, 0.5, 0.5, 0.5, 0, null);
//
//
//						}
//					}
//
//					for(Entity e : p.getWorld().getLivingEntities()) {
//
//						Location eloc = e.getLocation();
//						double dist = eloc.distance(loc1);
//
//						if(dist<2 || e.getBoundingBox().contains(loc1.getX(), loc1.getY(), loc1.getZ())) {
//
//							if(entitycheck.duelcheck(e, p) && entitycheck.entitycheck(e) && !meleehit.containsKey(e)) {
//								p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2);
//								meleehit.put(e, 0);
//								LivingEntity le = (LivingEntity) e;
//								int dmg = PlayerManager.getinstance(p).meleedmgcalculate(p, 1);
//								Damage.getinstance().taken(dmg, le, p);
//								EntityStatusManager.getinstance(le).KnockBack(p, 0.3);
//
//								if(le.getCustomName() != null) {
//									if(le.getCustomName().equals("샌드백")) {
//										if(PlayerHealthShield.getinstance(p).getCurrentShield()>0) {
//											Damage.getinstance().taken(2000, (LivingEntity) p, p);
//											p.sendMessage("§e시험 진행 A.I:§e §f시간이 지나면 보호막은 자동으로 채워지니 염려하지 않으셔도 됩니다.");
//											p.playSound(p.getLocation(), "meme.tut6", 5, 1);
//											Tutorial.trainerbothit.put(p, 1);
//										}
//									}
//								}
//
//								if(!Tutorial.examset.containsKey(p)) {
//
//									if(le.getCustomName() != null) {  // 슬라임 봇 때릴 때
//										String split[] = le.getCustomName().split("m");
//										if(split.length == 2) {
//											if(split[1] != null) {
//
//												if(Tutorial.exambothit.containsKey(p)) {// 튜토리얼 활성화?
//
//													int number = Integer.parseInt(split[1]);
//
//													if(Tutorial.exambothit.get(p)[number-1] == 0) { // 때린 봇의 번혿가 0번이면
//
//														Tutorial.exambothit.get(p)[number-1] = number; // 때린 봇 번호 추가
//														Tutorial.exambothitcount.replace(p, Tutorial.exambothitcount.get(p)+1); // 횟수 추가
//														break; // 번호 넣으면 탈출
//													}
//
//
//
//
//												}
//
//											}
//										}
//
//									}
//								}
//
//
//
//							}
//						}
//					}
//					loc1.add(dir1);
//				}
//				PotionEffect potion = new PotionEffect(PotionEffectType.SLOW_DIGGING, 10, 10);
//				p.addPotionEffect(potion, true);
//
//				new BukkitRunnable() {
//
//					@Override
//					public void run() {
//
//						trainermelee.replace(p, trainermelee.get(p)+1);
//						if(trainermelee.get(p) >=2) {
//							trainermelee.remove(p);
//							cancel();
//						}
//
//					}
//				}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 10);
//			}
//
//
//
//
//
//
//		}
	}

	public void Khaos(Player p, String combo) {
		PlayerFunction PF = PlayerFunction.getinstance(p);

		if(combo.equals("L") || combo.equals("SHIFTL") || combo.equals("R")) {
			if(PF.getMeleeDelay() == 0) {
				(new KhaosMelee(p)).Melee(combo);
			}
		}
		if(combo.equals("SHIFTR") || combo.equals("RL") || combo.equals("RR") || combo.equals("FR")) {
			(new Khaos(p)).Skill(combo);
		}
	}

	public void Shield(Player p, String combo) {
		PlayerFunction PF = PlayerFunction.getinstance(p);

		if(combo.equals("L") || combo.equals("SHIFTL") || combo.equals("R")) {
			if(PF.getMeleeDelay() == 0) {
				(new ShieldMelee(p)).Melee(combo);
			}
		}
		if(combo.equals("SHIFTR") || combo.equals("RL") || combo.equals("RR") || combo.equals("FR")) {
			(new Shield(p)).Skill(combo);
		}
	}

	public void Cheiron(Player p, String combo) {
		PlayerFunction PF = PlayerFunction.getinstance(p);

		if(combo.equals("SHIFTR") || combo.equals("R")) {
			if(PF.getMeleeDelay() == 0) {
				(new CheironMelee(p)).Melee(combo);
			}
		}
		else if(combo.equals("LL") || combo.equals("LR") || combo.equals("SHIFTL") || combo.equals("FL")) {
			(new Cheiron(p)).Skill(combo);
		}

	}

	public void Aether(Player p, String combo) {

		PlayerFunction PF = PlayerFunction.getinstance(p);
		
		int lvl = PlayerManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = PlayerManager.getinstance(p).ManaDecrease;
		
		if(combo.equals("L") || combo.equals("SHIFTL") || combo.equals("R")) {
			
			if(PlayerFunction.getinstance(p).getMeleeDelay()==0) {
				(new AetherMelee(p)).Melee(combo);
				//Aether.getinstance().melee(p);
			}

		}
		else if(combo.equals("RR") || combo.equals("RL") || combo.equals("FR") || combo.equals("SHIFTR")) {
			(new Aether(p)).Skill(combo);
		}
	}

	public void Accelerator(Player p, String combo) {
		
		int lvl = PlayerManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = PlayerManager.getinstance(p).ManaDecrease;
		
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
			
			if(PlayerFunction.getinstance(p).getMeleeDelay() == 0) Accelerator.getinstance().melee(p);

		}
		if(combo.equals("RL")) {

			if(CurrentMana >= RLmana ) {
				Sound(p);
				p.sendTitle(" ",blank+RL, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Accelerator.getinstance().movehit(p, RLmana);
			}
			else {
				Warning(p);
				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RRmana ) {
				Sound(p);
				p.sendTitle(" ",blank+RR, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Accelerator.getinstance().adrenaline(p, RRmana);
			}
			else {
				Warning(p);
				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
			}
			

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FRmana) {
				Sound(p);
				p.sendTitle(" ",blank+FR, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Accelerator.getinstance().randomfire(p, FRmana);
			}
			else {
				Warning(p);
				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RFmana) {
				Sound(p);
				p.sendTitle(" ",blank+RF, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Accelerator.getinstance().bombthrow(p, RFmana);
			}
			else {
				Warning(p);
				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FFmana) {
				Sound(p);
				p.sendTitle(" ",blank+FF, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Accelerator.getinstance().particleacceleration(p, FFmana);
			}
			else {
				Warning(p);
				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
			}

		}
		
		
		
	}
	


	public void ByV(Player p, String combo) {
		
		int lvl = PlayerManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = PlayerManager.getinstance(p).ManaDecrease;

		
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
			
			if(PlayerFunction.getinstance(p).getMeleeDelay()==0) ByV.getinstance().melee(p);

		}
		if(combo.equals("RL")) {
			
			if(CurrentMana >= RLmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle(" ",blank+RL, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				ByV.getinstance().recover(p, RLmana);
			}
			else {
				if(CurrentMana < RLmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle(" ",blank+essenceexhaustion, 5, 20, 10);
				}		

			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RRmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle(" ",blank+RR, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				ByV.getinstance().takedown(p, RRmana);
			}
			else {
				if(CurrentMana < RRmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle(" ",blank+essenceexhaustion, 5, 20, 10);
				}
			}
			

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FRmana) {
				Sound(p);
				p.sendTitle(" ",blank+FR, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				ByV.getinstance().chain(p, FRmana);
			}
			else {
				Warning(p);
				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
			}

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RFmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle(" ",blank+RF, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				ByV.getinstance().punch(p, RFmana);
			}
			else {
				if(CurrentMana < RFmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle(" ",blank+essenceexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FFmana && PlayerFunction.getinstance(p).essence >= 1) {
				Sound(p);
				p.sendTitle(" ",blank+FF, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				ByV.getinstance().shockwave(p, FFmana);
			}
			else {
				if(CurrentMana < FFmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}
				else {
					Warning(p);
					p.sendTitle(" ",blank+essenceexhaustion, 5, 20, 10);
				}
			}

		}
		
		
		
	}

	public void Phlox(Player p, String combo) {

		if(combo.equals("L") || combo.equals("SHIFTL") || combo.equals("R")) {

			if(PlayerFunction.getinstance(p).getMeleeDelay()==0) {
				(new AetherMelee(p)).Melee(combo);
			}

		}
		else if(combo.equals("RR") || combo.equals("RL") || combo.equals("FR") || combo.equals("SHIFTR")) {
			(new Phlox(p)).Skill(combo);
		}
	}
	
	
	public void Blaster(Player p, String combo) {
		
		int lvl = PlayerManager.getinstance(p).getlvl();
		
		int CurrentMana = PlayerEnergy.getinstance(p).getEnergy();
		int ManaDecrease = PlayerManager.getinstance(p).ManaDecrease;
		
		int RLmana = ClassAbility.Blaster.TurretDropMana - ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RRmana = ClassAbility.Blaster.TurretUpgradeMana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int RFmana = ClassAbility.Blaster.SelfExplosion -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FRmana = ClassAbility.Blaster.EnergyTransMana -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		int FFmana = ClassAbility.Blaster.Acceleration -ManaDecrease + PlayerEnergy.getinstance(p).getEnergyOverload();
		
		String RL = "§o§l모드전환: 레일건§l§o §3§l-⚡§l"+(RLmana);
		String RR = "§o§l모드전환: 유탄발사기§l§o §3§l-⚡§l"+(RRmana);
		String RF = "§o§l모드전환: 라이플§l§o §3§l-⚡§l"+(RFmana);
		String FR = "§o§l에너지전환: 생명력§l§o §3§l-⚡§l"+(FRmana);
		String FF = "§o§l자기장§l§o §3§l-⚡§l"+(FFmana);
		
		if(combo.equals("L")) {
			
			if(PlayerFunction.getinstance(p).getMeleeDelay()==0) Blaster.getinstance().melee(p);

		}
		if(combo.equals("RL")) {
			
			if(CurrentMana >= RLmana) {
//				if(PlayerFunction.getinstance(p).getMeleemode()==0) {
//					Warning(p);
//
//					p.sendTitle(" ",blank+"§c이미 해당 모드입니다§c", 5, 20, 10);
//					return;
//				}
				Sound(p);
				p.sendTitle(" ",blank+RL, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Blaster.getinstance().TurretDrop(p, RLmana);
			}
			else {
				if(CurrentMana < RLmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}

			}

		}
		else if(combo.equals("RR")) {
			
			if(lvl<10) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}

			if(CurrentMana >= RRmana) {
				if(PlayerFunction.getinstance(p).getMeleemode()==1) {
					Warning(p);
					p.sendTitle(" ",blank+"§c이미 해당 모드입니다§c", 5, 20, 10);
					return;
				}	
				Sound(p);
				p.sendTitle(" ",blank+RR, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Blaster.getinstance().grenadelauncher(p, RRmana);
			}
			else {
				if(CurrentMana < RRmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}
			}
			

		}
		else if(combo.equals("RF")) {
			
			if(lvl<5) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= RFmana) {
				if(PlayerFunction.getinstance(p).getMeleemode()==2) {
					Warning(p);
					p.sendTitle(" ",blank+"§c이미 해당 모드입니다§c", 5, 20, 10);
					return;
				}	
				Sound(p);
				p.sendTitle(" ",blank+RF, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Blaster.getinstance().rifle(p, RFmana);
			}
			else {
				if(CurrentMana < RFmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("FR")) {
			
			if(lvl<15) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FRmana) {
				Sound(p);
				p.sendTitle(" ",blank+FR, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Blaster.getinstance().energytrans(p, CurrentMana);
			}
			else {
				if(CurrentMana < FRmana) {
					Warning(p);
					p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
				}
			}

		}
		else if(combo.equals("FF")) {
			
			if(lvl<20) {
				Warning(p);
				p.sendTitle(" ",blank+levelrequire, 5, 20, 10);
				return;
			}
			
			if(CurrentMana >= FFmana) {
				Sound(p);
				p.sendTitle(" ",blank+FF, 5, 20, 10);
				PlayerEnergy.getinstance(p).energyOverload(combo);
				Blaster.getinstance().magneticfield(p, FFmana);
			}
			else {
				Warning(p);
				p.sendTitle(" ",blank+manaexhaustion, 5, 20, 10);
			}

		}
		
		
		
	}
	
	
	

}

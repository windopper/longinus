package ClassAbility.Aether;

import ClassAbility.Combination;
import ClassAbility.SpellManager;
import ClassAbility.entitycheck;
import DynamicData.Damage;
import utils.DuraAbilityHandler;
import utils.targetBuilder;
import Mob.EntityStatusManager;
import PlayParticle.PlayParticle;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import PlayParticle.Rotate;
import com.google.common.base.Enums;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import spellinteracttest.Main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static PlayParticle.Rotate.*;

public class Aether {
	
	private static Aether Aether;
	/*아이테르 스킬 */

	public static Aether getinstance()	{
		if(Aether == null) Aether = new Aether();
		return Aether;
	}

	private Player player;
	private PlayerFunction playerFunction;
	private PlayerManager pm;
	private int CurrentMana;
	private int ManaDecrease;


	public Aether(Player player) {
		this.player = player;
		this.playerFunction = PlayerFunction.getinstance(player);
		this.CurrentMana = PlayerEnergy.getinstance(player).getEnergy();
		this.ManaDecrease = PlayerManager.getinstance(player).ManaDecrease;
		this.pm = PlayerManager.getinstance(player);
	}

	private Aether() {

	}

	private enum ENUM {

		RR(6, "§o§l충격량전환: 돌진§l§o §3§l-⚡§l"),
		RL(6, "§o§l충격량전환: 보호막§l§o §3§l-⚡§l"),
		FR(8, "§o§l충격량전환: 블레이드오러§l§o §3§l-⚡§l"),
		SHIFTR(8, "§o§l칼날 와류§l§o §3§l-⚡§l");

		private int mana;
		private String title;

		ENUM(int mana, String title) {
			this.mana = mana;
			this.title = title;
		}

		int getMana() {
			return mana;
		}

		String getTitle() {
			return title;
		}
	}

	public int Skill(String combo) {

		if(!Enums.getIfPresent(ENUM.class, combo).isPresent()) return 0;

		int RLtII = pm.getTalent("RL", 2);
		int RLtIII = pm.getTalent("RL", 3);
		int FRtII = pm.getTalent("FR", 2);
		int FRtIII = pm.getTalent("FR", 3);
		int originMana = ENUM.valueOf(combo).getMana();

		if(RLtII == 2 && combo.equals("RL")) originMana += 2;
		if(RLtIII == 3 && combo.equals("RL")) originMana -= 2;
		if(FRtII == 3 && combo.equals("FR")) originMana -= 1;
		if(FRtIII == 3 && combo.equals("FR")) originMana -= 2;

		int mana = originMana - ManaDecrease <= 0 ? 1 : originMana - ManaDecrease
				+ PlayerEnergy.getinstance(player).getEnergyOverload();
		String title = ENUM.valueOf(combo).getTitle()+mana;

		if(mana <= CurrentMana) {
			PlayerEnergy.getinstance(player).useEnergy(mana);
			if(combo.equals("RR")) ShieldSwitchCharge();
			else if(combo.equals("RL")) ImpulseSwitchShield(mana);
			else if(combo.equals("FR")) ImpulseSwitchWeapon();
			else if(combo.equals("SHIFTR")) SR();


			Combination.getinstance().Sound(player);
			player.sendTitle(" ", Combination.blank+title, 5, 20, 10);
			PlayerEnergy.getinstance(player).energyOverload(combo);
			return mana;
		}
		else {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
			player.sendTitle(" ", Combination.blank+Combination.manaexhaustion, 0, 20, 10);
		}

		return 0;
	}
	
	public void ImpulseSwitchShield(int usedMana) {

		int tI = pm.getTalent("RL", 1);
		int tII = pm.getTalent("RL", 2);
		int tIII = pm.getTalent("RL", 3);
		int tIV = pm.getTalent("RL", 4);

		PlayerFunction PF = PlayerFunction.getinstance(player);
		double multiplyshield = 0;
		double maxShieldPercent = 0.5;
		int radius = 7;
		double usingImpulse = Double.parseDouble(String.format("%.2f", PF.AEImpulse/2));

		if(tI == 1) multiplyshield += 0.2;
		else if(tI == 2) radius = 9;
		else if(tI == 3) {
			pm.addiWalkSpeed += 10;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> pm.addiWalkSpeed -= 10, 40);
		}

		if(tII == 1) {
			double tIIMultiply = 0;
			for(LivingEntity lE : player.getWorld().getLivingEntities()) {
				Location lloc = lE.getLocation();
				if(lloc.distance(player.getLocation()) < radius && lE instanceof Player pl && !entitycheck.duelcheck(lE, player)) {
					tIIMultiply += 0.1;
				}
			}
			multiplyshield += tIIMultiply;
		}
		else if(tII == 2) {
			usingImpulse = Double.parseDouble(String.format("%.2f", usingImpulse/2));
		}
		else if(tIII == 2) {
			maxShieldPercent = 0.8;
		}
		else if(tIII == 3) {

		}
		if(tIV == 1) {
			PlayerHealthShield.getinstance(player).setImmortality(0.1d, 20);
		}
		else if(tIV == 2) {
			PlayerFunction.getinstance(player).AERLtIV2 = true;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class),
					() -> PlayerFunction.getinstance(player).AERLtIV2 = false, 60);
		}
		else if(tIV == 3) {
			final PlayerHealthShield phs = PlayerHealthShield.getinstance(player);
			final double finalusingImpulse = usingImpulse;
			new BukkitRunnable() {

				int time = 0;

				@Override
				public void run() {

					if(phs.getCurrentShield() <= 0) {
						PlayerFunction.getinstance(player).addAEImpulse(finalusingImpulse);
						PlayerEnergy.getinstance(player).addEnergy(usedMana);
						cancel();
						return;
					}

					if(time >= 12) cancel();
					time ++;
				}
			}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
		}


		final int finalshield = (int)((double)PlayerManager.getinstance(player).Health *
				( (multiplyshield+1) * (maxShieldPercent * PF.AEImpulse / 1000 + 0.05)));
		final int finalradius = radius;

		if(tIII == 1) {
			tIII1(radius);
		}
		
		summonCircle4(player.getLocation(), 1);
		
		PlayerHealthShield.getinstance(player).ShieldAdd(finalshield, player);

		PF.AEImpulse = Double.parseDouble(String.format("%.2f", PF.AEImpulse-usingImpulse));

		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);

		for(LivingEntity lE : player.getWorld().getLivingEntities()) {
			Location lloc = lE.getLocation();
			if(lloc.distance(player.getLocation()) < radius && lE instanceof Player pl && !entitycheck.duelcheck(lE, player) && player != lE) {
				if(!pl.isOnline()) continue;
				PlayerHealthShield.getinstance(pl).ShieldAdd(finalshield, player);
				summonCircle4(pl.getLocation(), 1);
			}
		}
		Location loc = player.getLocation(); //파티클
		
		new BukkitRunnable() {
			
			double t = 0;
			
			@Override
			public void run() {
				
				summonCircle3(loc, t);
				if(t>finalradius) cancel();
				t+=0.7;
				
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
		
		summonCircle(loc, finalradius);

	}
	private void tIII1(double radius) {
		for(LivingEntity lE : player.getWorld().getLivingEntities()) {
			Location lloc = lE.getLocation();
			if(lloc.distance(player.getLocation()) < radius && lE instanceof Player pl && !entitycheck.duelcheck(lE, player)) {
				if(!pl.isOnline()) continue;
				new BukkitRunnable() {
					Set<Entity> Hit = new HashSet<>();
					int time = 0;
					@Override
					public void run() {
						if(time % 4 == 0) {
							pl.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, pl.getLocation().add(0, 1, 0)
									, 5, 0.1, 0.1, 0.1, 0.5f);
							pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 1, 1);
						}
						if(time>20) {
							Location loc = pl.getLocation().clone().add(0, 1, 0);
							for(double phi = 0; phi <= Math.PI; phi += Math.PI / 8) {
								double y = 3 * Math.cos(phi) + 1.5;
								for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 8) {
									double x = 3 * Math.cos(theta) * Math.sin(phi);
									double z = 3 * Math.sin(theta) * Math.sin(phi);

									Vector v = new Vector(x, y, z);

									loc.add(x, y, z);
									loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, pl.getLocation().add(0, 1, 0), 0, v.getX()
											, v.getY(), v.getZ(), 0.1, null);

									targetBuilder tb = targetBuilder.builder(pl)
											.setDamage(() -> PlayerHealthShield.getinstance(pl).getCurrentShield())
											.setRadius(3.5)
											.setLocation(loc)
											.entityExcept(Hit).build();

									Hit.addAll(tb.getHitEntity());

									loc.subtract(x, y, z);
								}
							}
							pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1f);
							pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
							cancel();
						}
						time++;
					}
				}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
			}
		}
	}

	public void ImpulseSwitchWeapon() {

		PlayParticle playParticle = new PlayParticle(Particle.CRIT);
		playParticle.CirCleHorizontalSmallImpact(player.getLocation().add(0, 0.3, 0));
		PlayerFunction PF = PlayerFunction.getinstance(player);

		double spellrate = PF.AEImpulse / 1000 * 4 + 1;

		int FRtIII = pm.getTalent("FR", 3);
		int FRtIV = pm.getTalent("FR", 4);

		if(FRtIII == 1) {
			pm.damageTakenRate += 0.2;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
				pm.damageTakenRate -= 0.2;
			}, 30);
			spellrate = PF.AEImpulse * 3 / 1000 + 2.5;
		}
		if(FRtIV == 1) {
			if(FRtIII == 1) {
				spellrate = PF.AEImpulse * 8.5 / 1000 + 2.5;
			}
			else {
				spellrate = PF.AEImpulse * 11 / 1000;
			}
		}
		else if(FRtIV == 2) {
			PF.AEImpulse = Double.parseDouble(String.format("%.2f", PF.AEImpulse/2));
		}

		if(FRtIV != 2) {
			PF.AEImpulse = 0;
		}

		BladeStorm(spellrate);
	}
	public void ShieldSwitchCharge() {
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 0);
		}
		
		Vector dir = player.getLocation().getDirection();
		
		double x = dir.getX();
		double y = dir.getY();
		double z = dir.getZ();
		x *= 2;
		y = 0.3;
		z *= 2;
		dir = new Vector(x, y, z);
		player.setVelocity(dir);

		SpellManager Spell = new SpellManager(player, 0.1);
		Spell.setHitBoxRange(2);
		Spell.setEntityPassable(true);
		Spell.setDamageRate(1);
		Spell.addDestinationSound(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 0);
		Spell.setmultiplyDamage((double) PlayerHealthShield.getinstance(player).getCurrentShield() / (double) PlayerManager.getinstance(player).MaxShield);

		new BukkitRunnable() {
			
			int duration = 0;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation().add(0,1,0), 10, 0.5, 0.5, 0.5, 0);
				player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation().add(0,1,0), 10, 0.5, 0.5, 0.5, 0);
								
				if(duration > 3) {
					
					if(player.isOnGround()) {

						double xx = player.getLocation().getX();
						double yy = player.getLocation().getY();
						double zz = player.getLocation().getZ();
						yy -= 1;
						Location newloc = new Location(player.getWorld(), xx, yy, zz);
						player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0,1,0), 100, 1, 1, 1, newloc.getBlock().getType().createBlockData());

						cancel();
					}
					
					
				}

				if(Spell.RunRadiusRange(SpellManager.MeleeOrSpell.Spell, player.getLocation())) {

					Vector vect = new Vector(0, 0, 0);
					player.setVelocity(vect);
					double xx = player.getLocation().getX();
					double yy = player.getLocation().getY();
					double zz = player.getLocation().getZ();
					yy -= 2;
					Location newloc = new Location(player.getWorld(), xx, yy, zz);
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0,1,0), 500, 1, 1, 1, 0, newloc.getBlock().getType().createBlockData());

					PlayerHealthShield.getinstance(player).setCurrentShield(PlayerHealthShield.getinstance(player).getCurrentShield() * 95 /100);
					PlayerHealthShield.getinstance(player).setShieldRegenerateStop();

					cancel();


				}
				duration ++;

			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}

	public void SweepWeapon(Player p, int mana) {

		PlayerEnergy.getinstance(p).removeEnergy(mana);
		SweepTwice(p);
		
	}
	public void ImpulseSwitchEnergy(Player p) {

		PlayerFunction PF = PlayerFunction.getinstance(p);

		double i = Double.parseDouble(String.format("%.2f",PF.AEImpulse-100d));
		PF.AEImpulse = i;
		//PlayerEnergy.getinstance(p).setEnergy(PlayerEnergy.getinstance(p).getEnergy() + ImpulseSwitchEnergymana);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 2);
		}
	}
	
	public void DmgtoImpulse(int takendmg, Player p, Player pl) { //패시브  데미지, 충격량 채울 대상, 맞은 사람의 최대 체력

		PlayerFunction PF = PlayerFunction.getinstance(p);

		double i = Double.parseDouble(String.format("%.2f", PF.AEImpulse + PF.AEImpulseRate * (double) takendmg / (double) PlayerManager.getinstance(p).Health * 400));
		PF.AEImpulse = i;

		if (PF.AEImpulse > 1000) {
			PF.AEImpulse = 1000;
		}
	}
	
	public void summonCircle(Location location, double size) {
	    for (int d = 0; d <= 90; d += 1) {
	        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	        particleLoc.setX(location.getX() + Math.cos(d) * size);
	        particleLoc.setZ(location.getZ() + Math.sin(d) * size);
	        location.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, new Particle.DustOptions(Color.WHITE, 3));
	    }
	}
	
	public void summonCircle2(Location location, int size) {
	    for (int d = 0; d <= 30; d += 1) {
	        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	        particleLoc.setX(location.getX() + Math.cos(d) * size);
	        particleLoc.setY(location.getY() - 1);
	        particleLoc.setZ(location.getZ() + Math.sin(d) * size);
	        //location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLoc, 200, 0.2, 0,2, 0.2, 0.1);
			player.getWorld().spawnParticle(Particle.BLOCK_CRACK, particleLoc, 20, 0.2, 0.2, 0.2, 0, Material.SEA_LANTERN.createBlockData());

	    }

		player.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
		player.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
	}
	
	public void summonCircle3(Location location, double size) {
	    for (int d = 0; d <= 90; d += 1) {
	        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	        particleLoc.setX(location.getX() + Math.cos(d) * size);
	        particleLoc.setZ(location.getZ() + Math.sin(d) * size);
	        location.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, new Particle.DustOptions(Color.PURPLE, 1));
	    }
	}
	
	public void summonCircle4(Location location, double size) {
	    for (int d = 0; d <= 45; d += 1) {
	        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	        particleLoc.setX(location.getX() + Math.cos(d) * size);
	        particleLoc.setZ(location.getZ() + Math.sin(d) * size);
	        particleLoc.setY(location.getY() + d/90);
	        location.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLoc,5, 0,0,0,0, null);
	    }
	}

	private void SweepTwice(Player player) {

		double radius = 5;

		new BukkitRunnable() {

			double yaw = player.getLocation().getYaw();
			double pitch = player.getLocation().getPitch();
			double roll = Math.random() * 90 - 45;
			double roll2 = Math.random() * 50 - 25;
			double roll3 = Math.random() * 50 - 25;
			Location location = player.getEyeLocation();

			int step = 0;
			//double anglez = Math.random() * 180;
			double angley = -70;  // 140도
			double angley2 = -140;  // 280도
			double angley3 = 140;
			double x = 0;
			double y = 0;
			double z = 0;

			int t = 0;
			final List<Entity> Hit = new ArrayList<>();

			@Override
			public void run() {

				if(t<4) {

					for(int i=0; i<18; i++) {

						for(double k = 1.5; k<5; k+=0.4) {
							x = 0;
							y = 0;
							z = k;

							double yangle = Math.toRadians(angley);
							double yaxiscos = Math.cos(-yangle);
							double yaxissin = Math.sin(-yangle);

							Vector v = new Vector(x, y, z);
							v = rotateAroundAxisY(v, yaxiscos, yaxissin);
							v = transform(v, Math.toRadians(yaw), Math.toRadians(pitch), Math.toRadians(roll));
							location.add(v);
							if(k<2.2)
								location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
										new Particle.DustOptions(Color.WHITE, 1));
							else if(k>=2.2 && k<3.8)
								location.getWorld().spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
							else
								location.getWorld().spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);
							location.getWorld().spawnParticle(Particle.ASH, location, 1, 0, 0, 0, 0);


							for(LivingEntity entity : player.getWorld().getLivingEntities()) {
								if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
									Location eloc = entity.getEyeLocation();
									BoundingBox box = entity.getBoundingBox();
									if(eloc.distance(location) < 1.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
										int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 1);
										Damage.getinstance().taken(dmg, entity, player);
										Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.5);
										EntityStatusManager.getinstance(entity).KnockBack(knockvector);
										Hit.add(entity);
										player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
									}
								}
							}


							location.subtract(v);
						}
						angley += 2;
					}
				}
				else if(t>=8 && t<12){

					for(int i=0; i<12; i++) {

						for(double k = 1.5; k<4; k+=0.2) {
							x = 0;
							y = 0;
							z = k;

							double yangle = Math.toRadians(angley2);
							double yaxiscos = Math.cos(-yangle);
							double yaxissin = Math.sin(-yangle);

							Vector v = new Vector(x, y, z);
							v = rotateAroundAxisY(v, yaxiscos, yaxissin);
							v = transform(v, Math.toRadians(yaw), Math.toRadians(pitch), Math.toRadians(roll2));
							location.add(v);
							if(k<2.2)
								location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
										new Particle.DustOptions(Color.WHITE, 1));
							else if(k>=2.2 && k<3.8)
								location.getWorld().spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
							else
								location.getWorld().spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);
							location.getWorld().spawnParticle(Particle.ASH, location, 1, 0, 0, 0, 0);

							for(LivingEntity entity : player.getWorld().getLivingEntities()) {
								if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
									Location eloc = entity.getEyeLocation();
									BoundingBox box = entity.getBoundingBox();
									if(eloc.distance(location) < 1.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
										int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 0.7);
										Damage.getinstance().taken(dmg, entity, player);
										Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.5);
										EntityStatusManager.getinstance(entity).KnockBack(knockvector);
										Hit.add(entity);
										player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
									}
								}
							}

							location.subtract(v);
						}

						angley2 += 6;
					}

				}
				else if(t>=12 && t<16){

					for(int i=0; i<12; i++) {

						for(double k = 1.5; k<4; k+=0.2) {
							x = 0;
							y = 0;
							z = k;

							double yangle = Math.toRadians(angley3);
							double yaxiscos = Math.cos(-yangle);
							double yaxissin = Math.sin(-yangle);

							Vector v = new Vector(x, y, z);
							v = rotateAroundAxisY(v, yaxiscos, yaxissin);
							v = transform(v, Math.toRadians(yaw), Math.toRadians(pitch), Math.toRadians(roll3));
							location.add(v);
							if(k<2.2)
								location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
										new Particle.DustOptions(Color.WHITE, 1));
							else if(k<3.8)
								location.getWorld().spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
							else
								location.getWorld().spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);

							for(LivingEntity entity : player.getWorld().getLivingEntities()) {
								if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
									Location eloc = entity.getEyeLocation();
									BoundingBox box = entity.getBoundingBox();
									if(eloc.distance(location) < 1.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
										int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 0.7);
										Damage.getinstance().taken(dmg, entity, player);
										Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.5);
										EntityStatusManager.getinstance(entity).KnockBack(knockvector);
										Hit.add(entity);
										player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
									}
								}
							}

							location.subtract(v);
						}

						angley3 -= 6;
					}

				}
				if(t==0) {
					player.getWorld().playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
					player.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);
				}

				if(t == 8) {
					player.getWorld().playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
					player.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);
					location = player.getEyeLocation();
					Hit.clear();
				}
				if(t == 12) {
					player.getWorld().playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
					player.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);
					location = player.getEyeLocation();
					Hit.clear();
				}
				if(t == 16) cancel();
				t += 1;



			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}

	private void BladeStorm(double spellrate) {

		int FRtI = pm.getTalent("FR", 1);
		int FRtII = pm.getTalent("FR", 2);
		int FRtIII = pm.getTalent("FR", 3);
		int FRtIV = pm.getTalent("FR", 4);

		double dist = 3;

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 2);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 1, 2);

		targetBuilder tb = targetBuilder.builder(player)
				.setRadius(1.5)
				.setDamage(() -> PlayerManager.getinstance(player).spelldmgcalculate(player, spellrate));

		if(FRtI == 2) tb.addRunWhenEntityExist((e)->EntityStatusManager.getinstance(e).KnockBack(player, 1.5));
		if(FRtII == 2) dist = 4;
		if(FRtIII == 2) {
			tb.addRunWhenEntityExist((e)->player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, e.getLocation(), 1, 0, 0, 0, 0))
					.addRunWhenEntityExist((e)->player.getWorld().playSound(e.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1))
					.setDamage(() -> PlayerManager.getinstance(player).spelldmgcalculate(player, 1));
		}

		double finaldist = dist;

		new BukkitRunnable() {

			double pitch = player.getLocation().getPitch();
			double yaw = player.getLocation().getYaw();
			double rpitch = Math.toRadians(pitch);
			double ryaw = Math.toRadians(yaw);

			double roll = Math.random()	* 180 - 90;

			double rroll = Math.toRadians(roll);

			Location location = player.getEyeLocation();
			Location temploc = player.getEyeLocation();
			Vector vector = location.getDirection().normalize().multiply(0.5);

			List<Entity> Hit = new ArrayList<>();
			int t = 0;

			int a = 60;

			@Override
			public void run() {

				for(int i =0; i<finaldist; i++) {

					location.add(vector);

					double x = 0;
					double y = 0;
					double z = 2;

					for(int p = -45; p<45; p+=5) {
						double xangle = Math.toRadians(p);
						double xaxissin = Math.sin(xangle);
						double xaxiscos = Math.cos(xangle);

						Vector v = new Vector(x, y, z);
						v = rotateAroundAxisX(v, xaxiscos, xaxissin);
						v = transform(v, ryaw, rpitch, rroll);

						location.add(v);
						player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location,1, 0, 0, 0, 0);
						player.getWorld().spawnParticle(Particle.REDSTONE, location,1, 0, 0, 0, 0,
								new Particle.DustOptions(Color.RED, 0.5f));

						tb.setLocation(location);
						tb.build();

						location.subtract(v);

					}
				}
				if(t<=5) {

					for(int l=0; l<8; l++) {

						for(double k=1; k<=4; k+=0.5) {

							double x = 0;
							double y = 0;
							double z = k;

							double xangle = Math.toRadians(a);
							double xaxissin = Math.sin(xangle);
							double xaxiscos = Math.cos(xangle);

							Vector v = new Vector(x, y, z);
							v = rotateAroundAxisX(v, xaxiscos, xaxissin);
							v = transform(v, ryaw, rpitch , rroll);

							temploc.add(v);
							if(k<2.2)
								temploc.getWorld().spawnParticle(Particle.REDSTONE, temploc, 1, 0, 0, 0, 0,
										new Particle.DustOptions(Color.WHITE, 1));
							else if(k>=2.2 && k<3.8)
								location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, temploc, 1, 0, 0, 0, 0);
							else
								location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, temploc, 1, 0, 0, 0, 0);
							temploc.subtract(v);
						}
						a-=3;
					}
				}

				if(t>7) cancel();
				t++;

			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

	}
	public void MeleeMethod(Player player) {

		Location location = player.getEyeLocation();

		new BukkitRunnable() {

			double pitch = location.getPitch();
			double yaw = location.getYaw();
			double rpitch = Math.toRadians(pitch);
			double ryaw = Math.toRadians(yaw);
			double roll = Math.random() * 60 - 30;
			double rroll = Math.toRadians(roll);

			double angle = -60;
			int t = 0;

			double x =0;
			double y =0;
			double z =0;

			List<Entity> Hit = new ArrayList<>();

			@Override
			public void run() {

				for(int i=0; i<8; i++) {

					for(double k = 1.5; k<3.5; k+=0.2) {

						x = 0;
						y = 0;
						z = k;

						double yangle = Math.toRadians(angle);
						double yaxiscos = Math.cos(yangle);
						double yaxissin = Math.sin(yangle);

						Vector v = new Vector(x , y, z);
						v = rotateAroundAxisY(v, yaxiscos, yaxissin);
						v = transform(v, ryaw, rpitch, rroll);

						location.add(v);
						location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
								new Particle.DustOptions(Color.WHITE, 1));
						location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 1, 0, 0, 0, 0);
						if(k+0.3>3) {
							location.getWorld().spawnParticle(Particle.ASH, location, 1, 0, 0, 0, 0);
							location.getWorld().spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);
						}

						for(LivingEntity entity : player.getWorld().getLivingEntities()) {
							if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
								Location eloc = entity.getEyeLocation();
								BoundingBox box = entity.getBoundingBox();
								if(eloc.distance(location) < 1.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
									int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
									Damage.getinstance().taken(dmg, entity, player);
									EntityStatusManager.getinstance(entity).KnockBack(player, 0.5);
									Hit.add(entity);
									player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
								}
							}
						}

						location.subtract(v);
					}
					angle +=3;
				}

				if(t>4) cancel();
				t++;

			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

	}

	public void PassiveEffect() {

		final List<Location> location = new ArrayList<>();
		int width = 1;
		for(double i = -width; i<=width; i+=0.2) {
			location.add(new Location(Bukkit.getWorld("world"), i, 0, -width));
			location.add(new Location(Bukkit.getWorld("world"), i, 0, width));
			location.add(new Location(Bukkit.getWorld("world"), width, 0, i));
			location.add(new Location(Bukkit.getWorld("world"), -width, 0, i));
		}

		int time = 0;

		new BukkitRunnable() {

			double x = 0;
			double y = 0;
			double z = 0;

			double angle = 0;

			@Override
			public void run() {

				for(Player player : Bukkit.getOnlinePlayers()) {
					if(PlayerFunction.getinstance(player).AEImpulse < 600 || !PlayerManager.getinstance(player).CurrentClass.equals("아이테르")) continue;
					for(Location loc : location) {

						x = loc.getX();
						y = loc.getY();
						z = loc.getZ();

						double yangle = Math.toRadians(angle);
						double yaxiscos = Math.cos(yangle);
						double yaxissin = Math.sin(yangle);

						Vector v = new Vector(x, y, z);
						v = rotateAroundAxisY(v, yaxiscos, yaxissin);

						Location location = player.getLocation().add(v);
						location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0,
								new Particle.DustOptions(Color.RED, 1));
					}
				}

				angle += 3;
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);


	}

	public void SR() {
		Location loc = player.getLocation().add(0, 1, 0);

		targetBuilder tb = targetBuilder.builder(player);

		DuraAbilityHandler dah = DuraAbilityHandler.getHandler(player, "AESR");
		PlayerFunction pf = PlayerFunction.getinstance(player);
		dah.setRunnable(()->pf.AEImpulseRate+=0.5, ()->pf.AEImpulseRate-=0.5)
				.setMaximumStack(1)
				.setTick(30).run();

		tb.setRadius(8)
				.setLocation(loc)
				.setDamage(()->pm.spelldmgcalculate(player, 1.5))
				.addRunWhenEntityExist((e)->EntityStatusManager.getinstance(e).KnockBackVectorPSubE(player, -1)).build();

		new BukkitRunnable() {
			int time = 0;
			@Override
			public void run() {

				for(int k=0; k<360; k+=60) {
					for(double z =6; z>6-time*2; z-=0.2) {
						Vector v = new Vector(0, 0, z);
						v = Rotate.transform(v, Math.toRadians(k+z*30), 0, 0);
						loc.add(v);
						loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
						if(time==3) {
							loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc,
									0, -v.getX(), -v.getY(), -v.getZ(), 0.2f);
							loc.getWorld().spawnParticle(Particle.TOTEM, loc,
									0,0, 0, 0, 0.2f);
						}

						loc.subtract(v);
					}
				}
				if(time>2) cancel();
				time++;
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.5f, 2f);

		new BukkitRunnable() {
			int time =0;
			@Override
			public void run() {
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5f, 1f);
				if(time>2) cancel();
				time++;
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 3);
	}

}

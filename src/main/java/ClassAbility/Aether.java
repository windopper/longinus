package ClassAbility;

import DynamicData.Damage;
import Mob.EntityStatusManager;
import PlayParticle.PlayParticle;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static PlayParticle.Rotate.*;

public class Aether {
	
	private static Aether Aether;
	/*아이테르 스킬 */
	
	public final static int ImpulseSwitchShieldmana = 6;
	public final static int ImpulseSwitchWeaponmana = 8;
	public final static int ShieldSwitchChargemana = 6;
	public final static int WeaponModeChangemana = 3;
	public final static int ImpulseSwitchEnergymana = 3;
	
	private Aether() {
		
	}

	public static Aether getinstance()	{
		if(Aether == null) Aether = new Aether();
		return Aether;
	}
	
	@SuppressWarnings("deprecation")
	public void melee(final Player p) {


		MeleeMethod(p);
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 2);

		PlayerFunction.getinstance(p).setMeleeDelay(20);
		
	}
	
	public void ImpulseSwitchShield(Player p, int mana) {

		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction PF = PlayerFunction.getinstance(p);

		int add = (int)((double) PlayerManager.getinstance(p).Health * 5/100 * ((PF.AEImpulse+100) / 100) * (PlayerManager.getinstance(p).Shield + 100) / 100);
		
		summonCircle4(p.getLocation(), 1);
		
		PlayerHealthShield.getinstance(p).ShieldAdd(add);
		
		
		double i = Double.parseDouble(String.format("%.2f", PF.AEImpulse/2));
		PF.AEImpulse = i;
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(p.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
		}
		
		for(Entity pl : p.getNearbyEntities(6, 6, 6)) { // 근처 아군
			if(pl instanceof Player && !entitycheck.duelcheck(pl, p)) {
				Player pla = (Player) pl;
				add = (int)((double) PlayerManager.getinstance(pla).Health * 5/100 * ((PF.AEImpulse+100) / 100) * (PlayerManager.getinstance(p).Shield + 100) / 100);
				
				PlayerHealthShield.getinstance(pla).ShieldAdd(add);
				
				pl.sendMessage("§d"+p.getName()+" §d§5§l🛡§l§5§r §5"+add+"§5§d 부여§d");
				
				summonCircle4(pla.getLocation(), 1);
				
			}
		}
		
		Location loc = p.getLocation(); //파티클
		
		new BukkitRunnable() {
			
			double t = 0;
			
			@Override
			public void run() {
				
				summonCircle3(loc, t);
				if(t>7) cancel();
				t+=0.7;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		
		summonCircle(loc, 7);

	}
	public void ImpulseSwitchWeapon(final Player p, int mana) {

		PlayParticle playParticle = new PlayParticle(Particle.CRIT);
		playParticle.CirCleHorizontalSmallImpact(p);
		PlayerFunction PF = PlayerFunction.getinstance(p);
		PlayerEnergy.getinstance(p).removeEnergy(mana);

		final double spellrate = 2 * (PF.AEImpulse+100) / 200;

		if(PF.AEImpulse<300) {
			BladeStorm(p, spellrate);
		}
		if(PF.AEImpulse>=300 && PF.AEImpulse<600) {
			BladeStorm(p, spellrate);
		}
		if(PF.AEImpulse>=600) {

			BladeStorm(p, spellrate);

		}
	}
	public void ShieldSwitchCharge(final Player p, int mana) {
		
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 0);
		}
		
		Vector dir = p.getLocation().getDirection();
		
		double x = dir.getX();
		double y = dir.getY();
		double z = dir.getZ();
		x *= 2;
		y = 0.3;
		z *= 2;
		dir = new Vector(x, y, z);
		p.setVelocity(dir);

		SpellManager Spell = new SpellManager(p, 0.1);
		Spell.setHitBoxRange(2);
		Spell.setEntityPassable(true);
		Spell.setDamageRate(1);
		Spell.addDestinationSound(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 0);
		Spell.setmultiplyDamage((double) PlayerHealthShield.getinstance(p).getCurrentShield() / (double) PlayerManager.getinstance(p).ShieldRaw);

		new BukkitRunnable() {
			
			int duration = 0;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation().add(0,1,0), 10, 0.5, 0.5, 0.5, 0);
				p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation().add(0,1,0), 10, 0.5, 0.5, 0.5, 0);
								
				if(duration > 3) {
					
					if(p.isOnGround()) {

						double xx = p.getLocation().getX();
						double yy = p.getLocation().getY();
						double zz = p.getLocation().getZ();
						yy -= 1;
						Location newloc = new Location(p.getWorld(), xx, yy, zz);					
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation().add(0,1,0), 100, 1, 1, 1, newloc.getBlock().getType().createBlockData());

						cancel();
					}
					
					
				}

				if(Spell.RunRadiusRange(SpellManager.MeleeOrSpell.Spell, p.getLocation())) {

					Vector vect = new Vector(0, 0, 0);
					p.setVelocity(vect);
					double xx = p.getLocation().getX();
					double yy = p.getLocation().getY();
					double zz = p.getLocation().getZ();
					yy -= 2;
					Location newloc = new Location(p.getWorld(), xx, yy, zz);
					p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation().add(0,1,0), 500, 1, 1, 1, 0, newloc.getBlock().getType().createBlockData());

					PlayerHealthShield.getinstance(p).setCurrentShield(PlayerHealthShield.getinstance(p).getCurrentShield() * 95 /100);
					PlayerHealthShield.getinstance(p).setShieldRegenerateStop();

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
		PlayerEnergy.getinstance(p).setEnergy(PlayerEnergy.getinstance(p).getEnergy() + ImpulseSwitchEnergymana);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 2);
		}
	}
	
	public void DmgtoImpulse(int takendmg, Player p, Player pl) { //패시브  데미지, 충격량 채울 대상, 맞은 사람의 최대 체력

		PlayerFunction PF = PlayerFunction.getinstance(p);

		double i = Double.parseDouble(String.format("%.2f", PF.AEImpulse + (double) takendmg / (double) PlayerManager.getinstance(p).Health * 400));
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
	        for(Player p : Bukkit.getOnlinePlayers()) {
		        p.spawnParticle(Particle.BLOCK_CRACK, particleLoc, 20, 0.2, 0.2, 0.2, 0, Material.SEA_LANTERN.createBlockData());
	        }

	    }
	    
	    for(Player p : Bukkit.getOnlinePlayers()) {
	    	p.playSound(location, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
	    	p.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
	    }
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
										Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.2);
										entity.setVelocity(knockvector);
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
										Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.2);
										entity.setVelocity(knockvector);
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
										Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.2);
										entity.setVelocity(knockvector);
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

	private void BladeStorm(Player player, double spellrate) {

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 2);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 1, 2);

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

				for(int i =0; i<5; i++) {

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
						player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location,1, 0, 0, 0, 0);
						player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location,1, 0, 0, 0, 0);
						player.getWorld().spawnParticle(Particle.REDSTONE, location,1, 0, 0, 0, 0,
								new Particle.DustOptions(Color.RED, 1));


						for(LivingEntity entity : player.getWorld().getLivingEntities()) {
							if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
								Location eloc = entity.getEyeLocation();
								BoundingBox box = entity.getBoundingBox();
								if(eloc.distance(location) < 1.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
									int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, spellrate);
									Damage.getinstance().taken(dmg, entity, player);
									EntityStatusManager.getinstance(entity).KnockBack(player, 1);
									Hit.add(entity);
									player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
									player.getWorld().playSound(eloc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
									player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, eloc, 1, 0, 0, 0, 0);
								}
							}
						}


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

				if(t>6) cancel();
				t++;

			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);


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
		for(double i = -4; i<=4; i+=0.2) {
			location.add(new Location(Bukkit.getWorld("world"), i, 0, -4));
			location.add(new Location(Bukkit.getWorld("world"), i, 0, 4));
			location.add(new Location(Bukkit.getWorld("world"), 4, 0, i));
			location.add(new Location(Bukkit.getWorld("world"), -4, 0, i));
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
					if(PlayerFunction.getinstance(player).AEImpulse < 600 && PlayerManager.getinstance(player).CurrentClass.equals("아이테르")) continue;
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
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);


	}

}

package ClassAbility.Phlox;

import ClassAbility.Combination;
import ClassAbility.entitycheck;
import DynamicData.Damage;
import DynamicData.targetBuilder;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import Mob.*;
import com.google.common.base.Enums;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import spellinteracttest.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Phlox {

	private static Phlox Phlox;
	
//	public static final int healmana = 5;
//	public static final int annihilationmana = 3;
//	public static final int escapemana = 3;
//	public static final int interruptmana = 4;
//	public static final int robotmana = 3;
//
//	public static final int healrobot = 20;
//	public static final int annihilationrobot = 20;
//	public static final int escaperobot = 10;
//	public static final int interruptrobot = 20;

	private Player player;
	private PlayerFunction playerFunction;
	private PlayerManager pm;
	private int CurrentMana;
	private int ManaDecrease;
	private int CurrentRobot;


	public Phlox(Player player) {
		this.player = player;
		this.playerFunction = PlayerFunction.getinstance(player);
		this.CurrentMana = PlayerEnergy.getinstance(player).getEnergy();
		this.ManaDecrease = PlayerManager.getinstance(player).ManaDecrease;
		this.CurrentRobot = PlayerFunction.getinstance(player).PHNanoRobot;
		this.pm = PlayerManager.getinstance(player);
	}

	private Phlox() {

	}

	private enum ENUM {
		RR(3, 10,"§o§l긴급탈출§l§o §3§l-⚡§l"),
		RL(5, 20,"§o§l정밀치료§l§o §3§l-⚡§l"),
		FR(8, 20,"§o§l충격량전환: 블레이드오러§l§o §3§l-⚡§l"),
		SHIFTR(5, 20,"§o§l섬멸개시§l§o §3§l-⚡§l");

		private int mana;
		private String title;
		private int robot;

		ENUM(int mana, int robot, String title) {
			this.mana = mana;
			this.title = title;
			this.robot = robot;
		}

		int getMana() {
			return mana;
		}
		int getRobot() { return robot; }

		String getTitle() {
			return title;
		}
	}

	public int Skill(String combo) {

		if(!Enums.getIfPresent(ENUM.class, combo).isPresent()) return 0;


		int mana = ENUM.valueOf(combo).getMana() - ManaDecrease <= 0 ? 1 : ENUM.valueOf(combo).getMana() - ManaDecrease
				+ PlayerEnergy.getinstance(player).getEnergyOverload();

		int robot = ENUM.valueOf(combo).getRobot();

		int RLtIV = pm.getTalent("RL", 4);
		int SRtIII = pm.getTalent("SR", 3);
		int SRtIV = pm.getTalent("SR", 4);
		if(RLtIV == 2 && combo.equals("RL")) {
			mana = 1 + PlayerEnergy.getinstance(player).getEnergyOverload();
			robot /= 2;
		}
		if(SRtIII == 2 && combo.equals("SHIFTR")) {
			robot -= 10;
		}
		if(SRtIV == 3 && combo.equals("SHIFTR")) {
			robot += 30;
		}
		if(SRtIV == 2  && combo.equals("SHIFTR")) {
			mana = Math.max(mana - pm.nextManaDecrease, 1);
		}

		String title = ENUM.valueOf(combo).getTitle()+mana;

		if(robot > CurrentRobot) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
			player.sendTitle(" ", Combination.blank+Combination.robotexhaustion, 0, 20, 10);
			return 0;
		}
		if(mana <= CurrentMana) {
			PlayerEnergy.getinstance(player).useEnergy(mana);
			if(combo.equals("RR")) escape();
			if(combo.equals("RL")) heal();
			if(combo.equals("FR")) {};
			if(combo.equals("SHIFTR")) annihilation();


			Combination.getinstance().Sound(player);
			player.sendTitle(" ", Combination.blank+title, 5, 20, 10);
			Combination.getinstance().energyoverload(player, combo);
			nanorobotoverload(robot, 80);
			return mana;
		}
		else {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
			player.sendTitle(" ", Combination.blank+Combination.manaexhaustion, 0, 20, 10);
		}
		return 0;
	}
	
	public static Phlox getinstance() {
		if(Phlox == null) Phlox = new Phlox();
		return Phlox;
	}
	
	public void melee() {
		
		nanorobotoverload(1, 80);
		int meleehit = 0;

		Location loc = player.getEyeLocation();
		
		Vector dir = loc.getDirection();
		Vector vec = new Vector(0, 0, 0);
		
		
		vec = new Vector(dir.getZ(), 0, -dir.getX());
		vec.normalize();
		vec.multiply(0.3);
		
		Vector reverse = new Vector(-dir.getX(), 0, -dir.getZ());
		reverse.normalize();
		reverse.multiply(0.5);
		
		
		loc.add(vec).add(0, 0.7, 0).add(reverse);
		
		
		Location tmp = loc.clone();
		
		for(Player pl : Bukkit.getOnlinePlayers()) {
			pl.spawnParticle(Particle.SMOKE_LARGE, loc, 1, 0, 0, 0, 0, null);
			pl.playSound(loc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 2, 2);
		}

		dir.multiply(0.2);
		
		Entity target = player;
		
		for(int i=0; i<50; i++) { // 타겟 설정
			
			for(LivingEntity e : player.getWorld().getLivingEntities()) {
				Location plloc = e.getBoundingBox().getCenter().toLocation(e.getWorld());
				
				if(plloc.distance(loc)<1.5 && entitycheck.duelcheck(e, player) && entitycheck.entitycheck(e) && meleehit == 0 && e != player){
					target = e;
					meleehit = 1;
					
					int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
					Damage.getinstance().taken(dmg, e, player);
				}
				
				if(meleehit ==1) {
					break;
				}	
			}
			loc.add(dir);
			
		}
		
		if(target != player) {
			meleerobotlaser(player, target);
		}
		else {
			meleelasernontarget(player, loc);
		}

		PlayerFunction PF = PlayerFunction.getinstance(player);

		if(PF.PHMeleeRobot == null) {
			ShulkerBullet e = (ShulkerBullet) player.getWorld().spawnEntity(tmp, EntityType.SHULKER_BULLET);
			e.setGravity(false);
			e.setSilent(true);
			e.setInvulnerable(true);
			PF.PHMeleeRobot = e;
			PF.PHMeleeRobotCount = 1;
		}
		else {
			PF.PHMeleeRobotCount = 1;
		}
		
		
		PlayerFunction.getinstance(player).setMeleeDelay(10);
		
		
	}
	public void heal() {

		int tI = pm.getTalent("RL", 1);
		int tII = pm.getTalent("RL", 2);
		int tIII = pm.getTalent("RL", 3);
		int tIV = pm.getTalent("RL", 4);

		int tick = tI==1 ? 6 : 10;
		int healvalue = pm.Health / 20;
		int dist = 60;

		if(tI==2) healvalue *= 1.5;
		if(tIV==1) healvalue *= 3;
		if(tI==3) pm.addiWalkSpeed += 15;
		if(tIV==1) {
			pm.addiWalkSpeed -= 500;
		}
		if(tIII==3) dist *= 1.5;

		final int fiheal = healvalue;
		
		for(Player pl : Bukkit.getOnlinePlayers()) {
			pl.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
		}
		new BukkitRunnable() {
			
			int i=0;
			@Override
			public void run() {
				
				Location head = player.getEyeLocation().add(0, 0.5, 0);
				for(Player pl : Bukkit.getOnlinePlayers()) {
					pl.spawnParticle(Particle.HEART, head, 1, 0, 0, 0, 0, null);
				}
				
				PlayerHealthShield.getinstance(player).HealthAdd(tII == 2 ? (int) (fiheal * 1.5) : fiheal, player);
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
				if(i==3) {
					if(tIV==1) pm.addiWalkSpeed += 500;
					if(tI==3) pm.addiWalkSpeed -= 15;
					if(tII == 1) PlayerFunction.getinstance(player).removeAllAbnormalStatus();
					if(tII == 3) PlayerHealthShield.getinstance(player).ShieldAdd(0.05d,  player);
					cancel();
				}
				i++;
				
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, tick);

		int healhit = 0;
		Location loc = player.getEyeLocation();
		Vector dir = loc.getDirection();
		dir.normalize();
		dir.multiply(0.2);
		
		Player target = player;
		
		for(int i=0; i<dist; i++) { // 타겟 설정
			
			for(Player pl : Bukkit.getOnlinePlayers()) {
				
				if(player.getWorld() == pl.getWorld()) {
					Location plloc = pl.getBoundingBox().getCenter().toLocation(pl.getWorld());
					
					if(plloc.distance(loc)<2 && !entitycheck.duelcheck(pl, player) && healhit == 0 && pl != player){
						target = pl;
						healhit = 1;
						final PlayerManager pm_ = PlayerManager.getinstance(pl);
						if(tI==3) pm_.addiWalkSpeed += 15;
						if(tIV==1) {
							pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 5));
							pm_.addiWalkSpeed -= 500;
						}

						final BukkitTask runnable = new BukkitRunnable() {
							@Override
							public void run() {
								if(tIII == 1) PlayerEnergy.getinstance(pl)
										.getUsedManaFromPlayer.put(player, 0.5d);
							}
						}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
								
						new BukkitRunnable() {
							int i=0;
							@Override
							public void run() {
								
								Location head = pl.getEyeLocation().add(0, 0.5, 0);
								for(Player plll : Bukkit.getOnlinePlayers()) {
									plll.spawnParticle(Particle.HEART, head, 1, 0, 0, 0, 0, null);
								}

								PlayerHealthShield.getinstance(pl).HealthAdd(fiheal,  player);
								if(tIII == 2) healIII2Talent(pl);
								
								if(i==3) {
									if(tI==3) pm_.addiWalkSpeed -= 15;
									if(tIV==1) pm_.addiWalkSpeed += 500;
									if(tII == 1) {
										PlayerFunction.getinstance(pl).removeAllAbnormalStatus();
									}
									if(tII == 3) PlayerHealthShield.getinstance(pl).ShieldAdd(0.05d,  player);
									runnable.cancel();
									cancel();
								}
								i++;
							}
						}.runTaskTimer(Main.getPlugin(Main.class), 0, tick);
					}
					if(healhit == 1) {
						break;
					}
				}
			}
			loc.add(dir);
		}

		if(target != player)
			constantlyheal(player, target, tick * 4);

	}
	public void healIII2Talent(Player origin) {
		Location loc = origin.getLocation();
		for(Player target : loc.getWorld().getPlayers()) {
			Location tloc = target.getLocation();
			if(loc.distance(tloc)<5 && !entitycheck.duelcheck(player, target) && target != player && target != origin) {
				PlayerHealthShield.getinstance(target).HealthAdd(pm.Health / 40,  player);
				target.getWorld().spawnParticle(Particle.HEART,
						target.getEyeLocation().add(0, 0.5, 0), 1, 0, 0, 0, 0, null);
			}
		}

		for(double i =0; i<Math.PI * 2; i+=Math.PI/32) {
			double x = 5 * Math.cos(i);
			double y = 0.2;
			double z = 5 * Math.sin(i);

			player.getWorld().spawnParticle(Particle.SPELL_WITCH, origin.getLocation().add(x, y, z), 1, 0, 0, 0, 0);
		}
	}

	public void constantlyheal(Player me, Player target, int tick) {

		new BukkitRunnable() {

			int i=0;
			ShulkerBullet e;

			@Override
			public void run() {
				Location loc = me.getEyeLocation();
				Vector dir = loc.getDirection();
				dir.normalize();
				Vector vec = new Vector(dir.getZ(), 0, -dir.getX());
				vec.normalize();

				Vector reverse = new Vector(-dir.getX(), 0, -dir.getZ());
				reverse.normalize();
				reverse.multiply(0.3);

				loc.add(vec).add(0, 0.3, 0).add(reverse);

				Vector particlevec = loc.toVector(); // 파티클 떠있음

				Location targetloc = target.getBoundingBox().getCenter().toLocation(target.getWorld()); // 타겟
				Vector targetvec = targetloc.toVector();

				Vector healroad = targetvec.subtract(particlevec); // 파티클로부터 타겟 까지의 방향

				healroad.normalize();
				healroad.multiply(0.2);

				Location targethitbox = target.getBoundingBox().getCenter().toLocation(target.getWorld());

				Location robot = loc;

				if(i==0) {
					e = (ShulkerBullet) me.getWorld().spawnEntity(robot, EntityType.SHULKER_BULLET);
					e.setGravity(false);
					e.setSilent(true);
					e.setInvulnerable(true);
				}
				e.teleport(robot);

				for(Player pl : Bukkit.getOnlinePlayers()) {
					pl.spawnParticle(Particle.SPELL_WITCH, robot.clone(), 1, 0, 0, 0, 0, null);
					//pl.spawnParticle(Particle.FIREWORKS_SPARK, robot, 10, 0, 0, 0, 0, null);
				}

				for(int i=0; i<200; i++) {

					player.getWorld().spawnParticle(Particle.REDSTONE, loc.add(healroad),
							1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(160, 212, 104), 1));

					if(targethitbox.distance(loc)<1.5) {
						break;
					}
				}

				if(i>=tick) {
					e.remove();
					cancel();
				}
				i++;

			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}

	public void annihilation() {
		annihilationlaser();
	}

	public void escape() {
		Location ploc = player.getLocation();
		for(Player pl : Bukkit.getOnlinePlayers()) {
			pl.playSound(ploc, Sound.ENTITY_GHAST_SHOOT, 2, 1);
		}
		Vector pvec = ploc.toVector();
		
		player.setVelocity(new Vector(0, 1.5, 0));
		
		for(Entity e : player.getWorld().getNearbyEntities(ploc, 3, 3, 3)) {

			if(e instanceof LivingEntity) {
				if(EntityStatusManager.getinstance((LivingEntity)e).canKnockback() == false) continue;
			}

			if(entitycheck.entitycheck(e) && entitycheck.duelcheck(e, player) && e != player) {
				
				Location eloc = e.getLocation();	
				
				Vector evec = eloc.toVector();
				Vector ptoe = evec.subtract(pvec);
				ptoe.normalize();
				e.setVelocity(ptoe.multiply(1.5));	
				
			}
		}
		new BukkitRunnable() {
			
			double i=0;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				player.setFallDistance(0);

				if(player.isOnGround() && i>3) {
					cancel();
				}
				
				if(i<10) {
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.spawnParticle(Particle.FLAME, player.getLocation(), 10, 0.2, 0.2, 0.2, 0, null);
						pl.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 5, 0.2, 0.2, 0.2, 0, null);
					}
					summonCircle(ploc, 0.3 * i);
				}
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

		
		
	}
	public void interrupt() {
		nanorobotoverload(ENUM.FR.robot, 80);
		
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2, 1);
		
		for(Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5)) {
			if(entitycheck.entitycheck(e) && entitycheck.duelcheck(e, player) && e != player) {
				int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 1.5);
				EntityStatusManager.getinstance((LivingEntity)e).Stun(player, 30);
				Damage.getinstance().taken(dmg, (LivingEntity) e, player);
			}
		}
		
        Location loc = player.getLocation().add(0, -1, 0);
        double r = 5;
        for(double phi = 0; phi <= Math.PI; phi += Math.PI / 15) {
            double y = r * Math.cos(phi) + 1.5;
            for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 12) {
                double x = r * Math.cos(theta) * Math.sin(phi);
                double z = r * Math.sin(theta) * Math.sin(phi);

                loc.add(x, y, z);
                loc.getWorld().spawnParticle(Particle.DRIP_LAVA, loc, 2, 0F, 0F, 0F, 0.5, null);
                loc.subtract(x, y, z);
            }
        }
        
        
        for(double j = 5; j<8; j+=0.4) {
            for(double phi = 0; phi <= Math.PI; phi += Math.PI / 5) {
                double y = j * Math.cos(phi) + 1.5;
                for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 3) {
                    double x = j * Math.cos(theta) * Math.sin(phi);
                    double z = j * Math.sin(theta) * Math.sin(phi);

                    loc.add(x, y, z);
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 5, 0F, 0F, 0F, 0.001, new Particle.DustOptions(Color.fromRGB(166, 201, 255), 2));
                    loc.subtract(x, y, z);
                }
            }
        }
        
        
        summonCircle2(loc.add(0, 1, 0), 6);
	}
	public void robot() {
		nanorobotadd(20);
	}
	
	
	public void PhloxPassive() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(PlayerManager.getinstance(p).CurrentClass.equals("플록스")) {
				if(PlayerFunction.getinstance(p).PHNanoRobot == -1) PlayerFunction.getinstance(p).PHNanoRobot = 100;
			}
			else {
				if(PlayerFunction.getinstance(p).PHNanoRobot != -1) PlayerFunction.getinstance(p).PHNanoRobot = -1;
			}
		}
		
	}
	
	
	public void nanorobotoverload(final int robotamount, final int tick) {
		
		nanorobotuse(robotamount);
		
		new BukkitRunnable() {
			int i=0;
			
			@Override
			public void run() {
				
				if(i==1) {
					if(robotamount >=10) {
						player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 2);
					}
					nanorobotadd(robotamount);
					cancel();
				}
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, tick);
	}
	
	public void nanorobotadd(int robotamount) {

		PlayerFunction PF = PlayerFunction.getinstance(player);
		
		if(PF.PHNanoRobot != -1) {
			if(PF.PHNanoRobot +robotamount>100) PF.PHNanoRobot = 100;
			else {
				PF.PHNanoRobot += robotamount;
			}
		}
		
	}
	
	public void nanorobotuse(int robotamount) {
		PlayerFunction.getinstance(player).PHNanoRobot -= robotamount;
	}

	public void meleerobotlaser(Player me, Entity target) {
		
		
				Location loc = me.getEyeLocation();
				Vector dir = loc.getDirection();
				dir.normalize();
				Vector vec = new Vector(dir.getZ(), 0, -dir.getX());
				vec.normalize();
				vec.multiply(0.5);
				
				Vector reverse = new Vector(-dir.getX(), 0, -dir.getZ());
				reverse.normalize();
				reverse.multiply(0.5);
				
				
				loc.add(vec).add(0, 0.7, 0).add(reverse);
				
				
				Vector particlevec = loc.toVector(); // 파티클 떠있음
				
				Location targetloc = target.getBoundingBox().getCenter().toLocation(target.getWorld()); // 타겟
				Vector targetvec = targetloc.toVector();
				
				Vector healroad = targetvec.subtract(particlevec); // 파티클로부터 타겟 까지의 방향
				
				healroad.normalize();
				healroad.multiply(0.2);
				
				
				
				Location targethitbox = target.getBoundingBox().getCenter().toLocation(target.getWorld());
				
				
				Location robot = loc;
				
				
				for(Player pl : Bukkit.getOnlinePlayers()) {
					pl.spawnParticle(Particle.VILLAGER_ANGRY, robot.clone(), 10, 0, 0, 0, 0, null);
					//pl.spawnParticle(Particle.FIREWORKS_SPARK, robot, 10, 0, 0, 0, 0, null);
				}
				
					
				for(int i=0; i<200; i++) {
					
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.spawnParticle(Particle.REDSTONE, loc.add(healroad), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(240, 46, 80), 1));
					}
						
					
					
					if(targethitbox.distance(loc)<1.5) {
						break;
					}
				}
					

		

		
		
	}
	
	
	public void meleelasernontarget(Player me,  Location target) {
		
		Location loc = me.getEyeLocation();
		Vector dir = loc.getDirection();
		dir.normalize();
		Vector vec = new Vector(dir.getZ(), 0, -dir.getX());
		vec.normalize();
		vec.multiply(0.5);
		
		Vector reverse = new Vector(-dir.getX(), 0, -dir.getZ());
		reverse.normalize();
		reverse.multiply(0.5);
		
		
		loc.add(vec).add(0, 0.7, 0).add(reverse);
		
		
		Vector particlevec = loc.toVector(); // 파티클 떠있음
		
		Location targetloc = target;
		Vector targetvec = targetloc.toVector();
		
		Vector healroad = targetvec.subtract(particlevec); // 파티클로부터 타겟 까지의 방향
		
		healroad.normalize();
		healroad.multiply(0.2);
		
		
		
		Location targethitbox = target;
		
		
		Location robot = loc;
		
		
		for(Player pl : Bukkit.getOnlinePlayers()) {
			pl.spawnParticle(Particle.VILLAGER_ANGRY, robot.clone(), 10, 0, 0, 0, 0, null);
			//pl.spawnParticle(Particle.FIREWORKS_SPARK, robot, 10, 0, 0, 0, 0, null);
		}
		
			
		for(int i=0; i<200; i++) {
			
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.spawnParticle(Particle.REDSTONE, loc.add(healroad), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(240, 46, 80), 1));
			}
				
			
			
			if(targethitbox.distance(loc)<1.5) {
				break;
			}
		}
	}
	
	public void annihilationlaser() {

		int SRtI = pm.getTalent("SR", 1);
		int SRtII = pm.getTalent("SR", 2);
		int SRtIII = pm.getTalent("SR", 3);
		int SRtIV = pm.getTalent("SR", 4);

		double dist = 70;
		double spellrate1 = 1.5;
		double spellrate2 = 1;
		if(SRtI == 1) dist *= 1.2;
		else if(SRtI == 2) {
			spellrate1 *= 1.1;
			spellrate2 *= 1.1;
		}
		if(SRtIV == 3) {
			spellrate1 *= 2.5;
			spellrate2 *= 2.5;
		}

		double finaldist = dist;
		double finalspellrate1 = spellrate1;
		double finalspellrate2 = spellrate2;

		HashMap<Entity, Integer> laserhit = new HashMap<>();
		HashMap<Entity, Integer> bombhit = new HashMap<>();
		List<Location> bombpoint = new ArrayList<>();

		targetBuilder tb = targetBuilder.builder(this.player)
				.setRadius(2)
				.setDamage(() -> PlayerManager.getinstance(this.player).spelldmgcalculate(this.player, finalspellrate1));

		targetBuilder tb2 = targetBuilder.builder(this.player)
				.setRadius(2)
				.setDamage(() -> PlayerManager.getinstance(this.player).spelldmgcalculate(this.player, finalspellrate2));

		if(SRtI == 3) {
			tb.addStatus((e) -> {
				if(!EntityManager.getinstance(e).isUnstoppable())
					e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 5));
			});
		}
		if(SRtII == 2) {
			tb.addStatus((e) -> {
				new BukkitRunnable() {
					int time = 0;
					@Override
					public void run() {
						Damage.getinstance().taken(PlayerManager.getinstance(player).spelldmgcalculate(player, 0.2),
								e, player);
						if(time>=2) cancel();
						time++;
					}
				}.runTaskTimer(Main.getPlugin(Main.class), 0, 15);
			});
		}

		new BukkitRunnable() {
			
			int i=0;
			ShulkerBullet e;
			Location target[] = new Location[2];

			@Override
			public void run() {

				Location ploc = player.getEyeLocation();
				Vector pdir = ploc.getDirection();
				pdir.normalize();
				pdir.multiply(0.2);
				
				for(int i=0; i<200; i++) { // 타겟 설정
					if(ploc.getBlock().getType().isSolid()) {
						break;
					}
					ploc.add(pdir);
				}
				if(i==0) target[0] = ploc;
				if(i==4) target[1] = ploc;
				//	로봇 위치
				Location loc = player.getEyeLocation();
				Vector dir = loc.getDirection();
				dir.normalize();
				Vector vec = new Vector(-dir.getZ(), 0, dir.getX());
				vec.normalize().multiply(0.5);
				loc.add(vec).add(0, 1, 0);
				Location robot = loc.clone();

				if(i>=4) {
					Vector target1 = target[0].toVector();
					Vector target2 = target[1].toVector();
					Vector tgtotg = target2.subtract(target1);
					tgtotg.normalize();
					tgtotg.multiply(0.5 * (i-4));
					
					Location firsttarget = target[0].clone().add(tgtotg);
					
					Vector particlevec = loc.toVector(); // 파티클 떠있음
					
					Location targetloc = firsttarget;
					Vector targetvec = targetloc.toVector();
					
					Vector laserroad = targetvec.subtract(particlevec); // 파티클로부터 타겟 까지의 방향
					
					laserroad.normalize();
					laserroad.multiply(0.4);
					
					for(int i=0; i<finaldist; i++) {
						
						player.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
						if(loc.getBlock().getType().isSolid()){
							player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc.clone(), 10, 1, 1, 1, 0, loc.clone().add(0, 0, 0).getBlock().getType().createBlockData());
						}
						tb.setLocation(loc).build();
						if(SRtIV == 2) {
							int nextDecrease = tb.getHitEntity().size();
							pm.nextManaDecrease = nextDecrease;
						}

						// 땅에 닿으면
						double dist = loc.distance(targetloc);
						if(dist<0.7 || !loc.getBlock().isPassable()) {
							player.getWorld().spawnParticle(Particle.SOUL, loc, 5, 0.5, 0.5, 0.5, 0, null);
							player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc.clone(), 40, 1, 1, 1, 0, loc.clone().add(0, 1, 0).getBlock().getType().createBlockData());
							player.getWorld().playSound(loc, Sound.BLOCK_LAVA_EXTINGUISH, 2, 1);
							bombpoint.add(loc);
							break;
						}			
						// 아니면
						if(i==99) {
							player.getWorld().spawnParticle(Particle.SOUL, loc, 5, 0.5, 0.5, 0.5, 0, null);
							bombpoint.add(loc);
						}
						loc.add(laserroad);
					}
					
					
				}
				// 로봇 텔포 및 소환
				
				if(i==0) {
					e = (ShulkerBullet) player.getWorld().spawnEntity(robot, EntityType.SHULKER_BULLET);
					e.setGravity(false);
					e.setSilent(true);
					e.setInvulnerable(true);
				}
				e.teleport(robot);
				
				if(i>30) {

					if(SRtII == 3) {
						for(int i=0; i<bombpoint.size(); i++) {

							player.getWorld().spawnParticle(Particle.LANDING_LAVA, bombpoint.get(i), 5, 0.5, 0.5, 0.5, 0, null);
							player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, bombpoint.get(i), 2, 0.5, 0.5, 0.5, 0, null);
							if(i==10) {
								player.getWorld().playSound(bombpoint.get(i), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
							}

							tb2.setLocation(bombpoint.get(i)).build();
						}
					}
					e.remove();
					cancel();
				}
				i++;
			}
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
	}

	public void meleerobotcountloop() {
		
		for(Player p : Bukkit.getOnlinePlayers()) {

			PlayerFunction PF = PlayerFunction.getinstance(p);
			
			if(PF.PHMeleeRobotCount != 0) {
				
				Location loc = p.getEyeLocation();
				
				Vector dir = loc.getDirection();
				Vector vec = new Vector(0, 0, 0);
				
				vec = new Vector(dir.getZ(), 0, -dir.getX());
				
				vec.normalize();

				vec.multiply(0.3);
				
				Vector reverse = new Vector(-dir.getX(), 0, -dir.getZ());
				reverse.normalize();
				reverse.multiply(0.5);
				
				
				loc.add(vec).add(0, 0.7, 0).add(reverse);
				
				for(Player pl : Bukkit.getOnlinePlayers()) {
					pl.spawnParticle(Particle.CRIT_MAGIC, loc, 10, 0, 0, 0, 0, null);
				}

				if(PF.PHMeleeRobotCount > 40) {
					PF.PHMeleeRobot.remove();
					PF.PHMeleeRobotCount = 0;
					PF.PHMeleeRobot = null;
					continue;
				}
				
				PF.PHMeleeRobot.teleport(loc);
				PF.PHMeleeRobotCount++;
				
			}
			
			
		}		

				
	}


	public void summonCircle(Location location, double size) {
	    for (int d = 0; d <= 45; d += 1) {
	        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	        particleLoc.setX(location.getX() + Math.cos(d) * size);
	        particleLoc.setZ(location.getZ() + Math.sin(d) * size);
	        location.getWorld().spawnParticle(Particle.BLOCK_DUST, particleLoc, 1, particleLoc.clone().add(0, -1, 0).getBlock().getType().createBlockData());
	    }
	}
	
	public void summonCircle2(Location location, double size) {
	    for (int d = 0; d <= 45; d += 1) {
	        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	        particleLoc.setX(location.getX() + Math.cos(d) * size);
	        particleLoc.setZ(location.getZ() + Math.sin(d) * size);
	        location.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.WHITE, 2));
	    }
	}

	private void FRSkill(Player player) {

		Location targetloc = null;

		Location loc = player.getEyeLocation();
		Vector dir = loc.getDirection().normalize().multiply(0.4);


		// location settings
		for(int i=0; i<40; i++) {
			for(LivingEntity entity : player.getWorld().getLivingEntities()) {
				if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player)) {
					Location eloc = entity.getLocation();
					BoundingBox box = entity.getBoundingBox();
					if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
						targetloc = entity.getLocation();
						break;
					}
				}
			}
			if(targetloc != null) break;
			if(loc.getBlock().getType().isSolid()) {
				targetloc = loc;
				break;
			}
			if(i==39) targetloc = loc;
			loc.add(dir);
		}





	}

}	



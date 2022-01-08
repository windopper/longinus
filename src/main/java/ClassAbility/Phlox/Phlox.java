package ClassAbility.Phlox;

import ClassAbility.Combination;
import ClassAbility.entitycheck;
import DynamicData.Damage;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import Mob.*;
import com.google.common.base.Enums;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Phlox {

	private static Phlox Phlox;
	
	public static final int healmana = 5;
	public static final int annihilationmana = 3;
	public static final int escapemana = 3;
	public static final int interruptmana = 4;
	public static final int robotmana = 3;
	
	public static final int healrobot = 20;
	public static final int annihilationrobot = 20;
	public static final int escaperobot = 10;
	public static final int interruptrobot = 20;

	private Player player;
	private PlayerFunction playerFunction;
	private int CurrentMana;
	private int ManaDecrease;
	private int CurrentRobot;


	public Phlox(Player player) {
		this.player = player;
		this.playerFunction = PlayerFunction.getinstance(player);
		this.CurrentMana = PlayerEnergy.getinstance(player).getEnergy();
		this.ManaDecrease = PlayerManager.getinstance(player).ManaDecrease;
		this.CurrentRobot = PlayerFunction.getinstance(player).PHNanoRobot;
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

	public void Skill(String combo) {

		if(!Enums.getIfPresent(ENUM.class, combo).isPresent()) return;

		int mana = ENUM.valueOf(combo).getMana() - ManaDecrease <= 0 ? 1 : ENUM.valueOf(combo).getMana() - ManaDecrease
				+ PlayerEnergy.getinstance(player).getEnergyOverload();
		String title = ENUM.valueOf(combo).getTitle()+mana;
		int robot = ENUM.valueOf(combo).getRobot();

		if(robot > CurrentRobot) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
			player.sendTitle(" ", Combination.blank+Combination.robotexhaustion, 0, 20, 10);
			return;
		}
		if(mana <= CurrentMana) {
			PlayerEnergy.getinstance(player).removeEnergy(mana);
			if(combo.equals("RR")) escape();
			if(combo.equals("RL")) heal();
			if(combo.equals("FR")) {};
			if(combo.equals("SHIFTR")) annihilation();


			Combination.getinstance().Sound(player);
			player.sendTitle(" ", Combination.blank+title, 5, 20, 10);
			Combination.getinstance().energyoverload(player, combo);
			nanorobotoverload(robot, 80);
		}
		else {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
			player.sendTitle(" ", Combination.blank+Combination.manaexhaustion, 0, 20, 10);
		}
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

		nanorobotoverload(healrobot, 80);
		
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
				
				PlayerHealthShield.getinstance(player).HealthAdd(PlayerManager.getinstance(player).Health/20);
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
				if(i==3) cancel();
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 10);

		int healhit = 0;
		
		Location loc = player.getEyeLocation();
		Vector dir = loc.getDirection();
		dir.normalize();
		dir.multiply(0.2);
		
		Player target = player;
		
		for(int i=0; i<100; i++) { // 타겟 설정
			
			for(Player pl : Bukkit.getOnlinePlayers()) {
				
				if(player.getWorld() == pl.getWorld()) {
					Location plloc = pl.getBoundingBox().getCenter().toLocation(pl.getWorld());
					
					if(plloc.distance(loc)<2 && !entitycheck.duelcheck(pl, player) && healhit == 0 && pl != player){
						target = pl;
						healhit = 1;
								
						new BukkitRunnable() {
							
							int i=0;
							@Override
							public void run() {
								
								Location head = pl.getEyeLocation().add(0, 0.5, 0);
								for(Player plll : Bukkit.getOnlinePlayers()) {
									plll.spawnParticle(Particle.HEART, head, 1, 0, 0, 0, 0, null);
								}
								
								
								PlayerHealthShield.getinstance(pl).HealthAdd(PlayerManager.getinstance(pl).Health/20);
								
								if(i==3) cancel();
								i++;
								
							}
						}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 10);
						
					}
					
					if(healhit ==1) {
						break;
					}
				}
				
			}
			
			
			loc.add(dir);
			
		}
		
		
		if(target != player)
			constantlyheal(player, target);
		
		
	}
	public void annihilation() {
		annihilationlaser(player);
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
		nanorobotoverload(interruptrobot, 80);
		
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
	
	
	
	
	public void constantlyheal(Player me, Player target) {
		
		
		
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
					pl.spawnParticle(Particle.SPELL_WITCH, robot.clone(), 10, 0, 0, 0, 0, null);
					//pl.spawnParticle(Particle.FIREWORKS_SPARK, robot, 10, 0, 0, 0, 0, null);
				}
				
					
				for(int i=0; i<200; i++) {
					
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.spawnParticle(Particle.REDSTONE, loc.add(healroad), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(160, 212, 104), 1));
					}
					
						
					
					
					if(targethitbox.distance(loc)<1.5) {
						break;
					}
				}
					

					
				
				
				if(i>=40) {
					e.remove();
					cancel();
				}
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		

		
		
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
	
	public void annihilationlaser(final Player me) {
		
		HashMap<Entity, Integer> laserhit = new HashMap<>();
		HashMap<Entity, Integer> bombhit = new HashMap<>();
		List<Location> bombpoint = new ArrayList<>();
		
		new BukkitRunnable() {
			
			int i=0;
			ShulkerBullet e;
			Location target[] = new Location[2];
			
			
			@Override
			public void run() {
				
				
				Location ploc = me.getEyeLocation();
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
				
				Location loc = me.getEyeLocation();
				Vector dir = loc.getDirection();
				dir.normalize();
				Vector vec = new Vector(-dir.getZ(), 0, dir.getX());
				vec.normalize();
				vec.multiply(0.5);
				
//				Vector reverse = new Vector(-dir.getX(), 0, -dir.getZ());
//				reverse.normalize();
//				reverse.multiply(0.5);
				
				
				loc.add(vec).add(0, 1, 0);
				
				Location robot = loc.clone();
				// 
				
				
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
					
					for(int i=0; i<100; i++) {
						
						me.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
						if(loc.getBlock().getType().isSolid()){
							me.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc.clone(), 10, 1, 1, 1, 0, loc.clone().add(0, 0, 0).getBlock().getType().createBlockData());
						}

						
						// 레이저가 엔티티를 스치는지
						for(LivingEntity e : me.getWorld().getLivingEntities()) {
							Location eloc = e.getBoundingBox().getCenter().toLocation(e.getWorld());
							BoundingBox ebox = e.getBoundingBox();
							double edist = eloc.distance(loc);
							
							
							if((edist<2 || ebox.contains(loc.getX(), loc.getY(), loc.getZ())) && entitycheck.entitycheck(e) && entitycheck.duelcheck(e, me) && e != me && !laserhit.containsKey(e)) {
								laserhit.put(e, 0);
								me.playSound(me.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
								int dmg = PlayerManager.getinstance(me).spelldmgcalculate(me, 1.5);
								Damage.getinstance().taken(dmg, e, me);
							}
						}
						
						
						// 땅에 닿으면
						double dist = loc.distance(targetloc);
						if(dist<0.7 || loc.getBlock().getType().isSolid()) {
							me.getWorld().spawnParticle(Particle.SOUL, loc, 5, 0.5, 0.5, 0.5, 0, null);
							me.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc.clone(), 40, 1, 1, 1, 0, loc.clone().add(0, 1, 0).getBlock().getType().createBlockData());
							me.getWorld().playSound(loc, Sound.BLOCK_LAVA_EXTINGUISH, 2, 1);
							
							bombpoint.add(loc);
							break;
						}			
						// 아니면
						if(i==99) {
							me.getWorld().spawnParticle(Particle.SOUL, loc, 5, 0.5, 0.5, 0.5, 0, null);
							bombpoint.add(loc);
						}
						loc.add(laserroad);
					}
					
					
				}
				
				
				
				
				

				// 로봇 텔포 및 소환
				
				if(i==0) {
					e = (ShulkerBullet) me.getWorld().spawnEntity(robot, EntityType.SHULKER_BULLET);
					e.setGravity(false);
					e.setSilent(true);
					e.setInvulnerable(true);
				}
				e.teleport(robot);
				
				// 
				
				if(i>30) {
					
					for(int i=0; i<bombpoint.size(); i++) {
						
						me.getWorld().spawnParticle(Particle.LANDING_LAVA, bombpoint.get(i), 5, 0.5, 0.5, 0.5, 0, null);
						me.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, bombpoint.get(i), 2, 0.5, 0.5, 0.5, 0, null);
						if(i==10) {
							me.getWorld().playSound(bombpoint.get(i), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
						}
						
						for(LivingEntity e : me.getWorld().getLivingEntities()) {
							Location eloc = e.getLocation();
							double dist = eloc.distance(bombpoint.get(i));
							
							
							if(dist<2 && entitycheck.entitycheck(e) && entitycheck.duelcheck(e, me) && e != me && !bombhit.containsKey(e)) {
								bombhit.put(e, 0);
								me.getWorld().playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
								int dmg = PlayerManager.getinstance(me).spelldmgcalculate(me, 3);
								Damage.getinstance().taken(dmg, (LivingEntity) e, me);
							}
							
						}
						
					}
					
					
					
					
					
					e.remove();
					cancel();
				}
				
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		
		

		
		
		
		
		
		
		
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


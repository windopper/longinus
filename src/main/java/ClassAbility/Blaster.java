package ClassAbility;

import DynamicData.Damage;
import PlayParticle.PlayParticle;
import PlayParticle.Shape;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerHealthShield;
import PlayerManager.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static PlayParticle.Rotate.*;

public class Blaster {
	
	private static Blaster Blaster;
	
	public static final int TurretDropMana = 6;
	public static final int TurretUpgradeMana = 4;
	public static final int SelfExplosion = 4;
	public static final int EnergyTransMana = 10;
	public static final int Acceleration = 12;
	
	//public static final List<Player> preheat = new ArrayList<>();

	private Blaster() {
		
	}
	
	public static Blaster getinstance() {
		if(Blaster == null) Blaster = new Blaster();
		return Blaster;
	}
	
	
//	public void removemaps(Player p) {
//		preheat.remove(p);
//	}
	
	@SuppressWarnings("deprecation")
	public void melee(final Player p) {
		
		if(PlayerFunction.getinstance(p).getMeleemode()==0) {

			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LLAMA_SPIT, 2, 0);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 2);
			RailGun(p);
			
			PlayerFunction.getinstance(p).setMeleeDelay(10);
					
		}
		else if(PlayerFunction.getinstance(p).getMeleemode()==1) { // 유탄
			
			Location ploc = p.getEyeLocation();
			p.getWorld().playSound(ploc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 0);
			Vector pvec = ploc.getDirection();
			pvec.normalize();
			pvec.multiply(1.5);
			
			Arrow ar = (Arrow) p.getWorld().spawnEntity(ploc, EntityType.ARROW);
			ar.setVelocity(pvec);
			ar.setCustomName("bomb"+":"+p.getName());
			
			PlayerFunction.getinstance(p).setMeleeDelay(40);
		}
		

		
		else if(PlayerFunction.getinstance(p).getMeleemode()==2) { // 라이플
			
			int hit = 0;

			SpellManager Spell = new SpellManager(p, 0.1);
			Spell.addDepartParticle(Particle.FLAME, 5, 0, 0, 0, 0, null);
			Spell.addDepartSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 2);
			Spell.setMaximumRange(10);
			Spell.addTrailParticle(Particle.WHITE_ASH, 1, 0, 0, 0, 0, null);
			Spell.setHitBoxRange(1.5);
			Spell.setDamageRate(0.35);
			Spell.setKnockBack(p, 0.2);
			Spell.setEntityPassable(false);
			Spell.setWallPassable(false);

			Spell.RunRayCast(SpellManager.MeleeOrSpell.Melee);

			if(!Spell.getHitLocation().getBlock().isPassable()) {
				Location BLoc = Spell.getHitLocation();
				p.getWorld().spawnParticle(Particle.BLOCK_DUST, BLoc, 30, 0.5, 0.5, 0.5, 0, BLoc.getBlock().getType().createBlockData());
				p.getWorld().playSound(BLoc, Sound.BLOCK_STONE_BREAK, 2, 1);
			}

			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1), true);
			
			PlayerFunction.getinstance(p).setMeleeDelay(1);
		}
		
		
	}
	public void railgun(final Player p, final int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		PlayerFunction.getinstance(p).setMeleemode(0);
		
	}
	public void grenadelauncher(final Player p, final int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction.getinstance(p).setMeleemode(1);
		
	}
	public void rifle(final Player p, final int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction.getinstance(p).setMeleemode(2);
		
	}
	public void energytrans(final Player p, final int mana) {
		
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		final double recoverrate = mana * 0.05;
		PlayerHealthShield.getinstance(p).ShieldAdd((int)(PlayerManager.getinstance(p).Health * recoverrate));
		
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 2, 0);

		new BukkitRunnable() {
			
			double i=0;
			
			@Override
			public void run() {
				
				final Location ploc = p.getLocation();
				Location loc = new Location(ploc.getWorld(),ploc.getX(), ploc.getY(), ploc.getZ());
				loc.setX(ploc.getX() + Math.cos(i));
				loc.setY(ploc.getY()+ i/20);
				loc.setZ(ploc.getZ() + Math.sin(i));
				
				ploc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.fromBGR(251, 51, 153), 2));
				
				if(i>30) cancel();
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		
	}
	public void magneticfield(final Player p, final int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		magneticfield2(p);
		return;

	}

	private void magneticfield2(Player player) {

		new BukkitRunnable(){
			double phi = 0;

			int angle = 0;
			List<Location> square = (new Shape()).getHollowCube(new Location(player.getWorld(), -4, 0, -4)
			, new Location(player.getWorld(), 4, 0, 4), 1);

			public void run(){
				phi = phi + Math.PI/8;
				double x, y, z;

				Location location1 = player.getLocation();
				Location loc = location1.clone().add(0, 3, 0);
				for (double t = 0; t <= 2*Math.PI; t = t + Math.PI/16){
					for (double i = 0; i <= 1; i = i + 1){
						x = 0.4*(2*Math.PI-t)*0.5*Math.cos(t + phi + i*Math.PI);
						y = 0.5*t;
						z = 0.4*(2*Math.PI-t)*0.5*Math.sin(t + phi + i*Math.PI);
						location1.add(x, y, z);
						location1.getWorld().spawnParticle(Particle.REDSTONE, location1, 1, 0, 0, 0, 0, new Particle.DustOptions(
								Color.RED, 1
						));
						//location1.getWorld().spawnParticle(Particle.SPELL_WITCH, location1, 1, 0, 0, 0, 0);
						location1.subtract(x,y,z);
					}

				}



				for(double t=0; t<Math.PI * 2; t+=Math.PI/16)  {
					double zangle = Math.toRadians(angle);
					double zAxisCos = Math.cos(zangle);
					double zAxisSin = Math.sin(zangle);

					double xangle = Math.toRadians(angle/2);
					double xAxisCos = Math.cos(xangle);
					double xAxisSin = Math.sin(xangle);

					double xx = 0.3 * Math.cos(t);
					double yy = 0;
					double zz = 0.3 * Math.sin(t);
					Vector v = new Vector(xx, 0, zz);
					v = rotateAroundAxisZ(v, zAxisCos, zAxisSin);
					v = rotateAroundAxisX(v, xAxisCos, xAxisSin);
					loc.add(v.getX(), v.getY(), v.getZ());

					loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 1, 0, 0, 0, 0);
					loc.subtract(v.getX(), v.getY(), v.getZ());

				}

				for(int i =0; i<1; i++) {
					double yangle = Math.toRadians(angle);
					double yaxiscos = Math.cos(yangle);
					double yaxissin = Math.sin(yangle);

					for(Location sq : square) {
						x = sq.getX();
						y = sq.getY();
						z = sq.getZ();
						Vector v = new Vector(x, 0, z);
						v = rotateAroundAxisY(v, yaxiscos, yaxissin);
						location1.add(v.getX(), v.getY(), v.getZ());
						location1.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0
						, new Particle.DustOptions(Color.PURPLE, 1));
						location1.subtract(v.getX(), v.getY(), v.getZ());
					}
				}



				if(phi > 10*Math.PI){
					this.cancel();
				}
				angle += 25;
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

	}

	
	public void grenadelauncherbomb(Location loc, Player me) {


		loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 15, 1.5, 1.5, 1.5, 0, null);
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);

		SpellManager Spell = new SpellManager(me);
		Spell.setDamageRate(2);
		Spell.setHitBoxRange(4);

		Spell.RunRadiusRange(SpellManager.MeleeOrSpell.Spell, loc);


		
	}

	private void RailGun(Player player)	{

		Location plocation = player.getEyeLocation();
		Vector dir = plocation.getDirection().normalize().multiply(0.7);
		boolean Out = false;

		for(int i=0; i<20; i++) {
			for(LivingEntity le : player.getWorld().getLivingEntities()) {
				if(entitycheck.entitycheck(le) && entitycheck.duelcheck(le, player) && le!=player) {
					Location eloc = le.getLocation();
					BoundingBox bb = le.getBoundingBox();
					if(eloc.distance(plocation) < 1.5 || bb.contains(plocation.getX(), plocation.getY(), plocation.getZ())) {
						Out = true;
						break;
					}
				}
			}
			if(Out) break;
			plocation.add(dir);
		}


		Location location = player.getEyeLocation();

		double pitch = Math.toRadians(location.getPitch());
		double yaw = Math.toRadians(location.getYaw());

		Vector eyevec = location.getDirection().normalize().multiply(-0.8);
		location.add(eyevec);

		new BukkitRunnable() {

			int t = 0;
			//double RandomAnglez = (int)(Math.random() * 2) == 0 ? Math.random() * 90 + 45 : -Math.random() * 90 - 45;
			double RandomAnglex = Math.random() * -90;
			double RandomAngley = (int)(Math.random() * 2) == 0 ? Math.random() * 45 + 45 : -Math.random() * 45 - 45;

			double anglex = RandomAnglex;
			double angley = RandomAngley;
			//double anglez = RandomAnglez;
			@Override
			public void run() {

				double x = 0;
				double y = 0;
				double z = 0;



				for(int i=0; i<3; i++) {

					Vector vector = location.getDirection().normalize().multiply(0.4);

					//location.add(vector);

					double yangle = Math.toRadians(angley);
					double yaxiscos = Math.cos(yangle);
					double yaxissin = Math.sin(yangle);

					double xangle = Math.toRadians(anglex);
					double xaxiscos = Math.cos(xangle);
					double xaxissin = Math.sin(xangle);

					//anglez /= 1.15;
					angley /= 1.10;
					anglex /= 1.10;

					y = 0;
					x = 0;
					if(t<10) {
						z = 0.025 * Math.pow(t, 2);
					}
					else {
						z = (double)t/4;
					}

					Vector v = new Vector(x, y, z);
					v = rotateAroundAxisY(v, yaxiscos, yaxissin);
					v = rotateAroundAxisX(v, xaxiscos, xaxissin);
					v = transform(v, yaw, pitch, 0);
					v.multiply(1);

					location.add(v);
					location.getWorld().spawnParticle(Particle.REDSTONE, location, 25, 0.05, 0.05, 0.05, 0
							, new Particle.DustOptions(Color.fromRGB(210, 4, 45), 1));

					location.getWorld().spawnParticle(Particle.SMOKE_NORMAL, location, 3, 0.1, 0.1, 0.1, 0);
					location.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 2, 0, 0, 0, 0);
					//location.getWorld().spawnParticle(Particle.FLAME, location, 2, 0.1, 0.1, 0.1, 0);

					for(LivingEntity entity : location.getWorld().getLivingEntities()) {
						Location el = entity.getLocation();
						BoundingBox bb = entity.getBoundingBox();
						if(el.distance(location) < 1.5 || bb.contains(location.getX(), location.getY(), location.getZ())) {
							if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player) {
								int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 1);
								el.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, el,1, 0d, 0d, 0d, 0d);
								el.getWorld().playSound(el, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 2);
								el.getWorld().playSound(el, Sound.ENTITY_GENERIC_BURN, 0.5f, 1);
								Damage.getinstance().taken(dmg, entity, player);
								cancel();
								break;
							}
						}
					}

					location.subtract(v);
					//location.subtract(v);

					if(t>35) cancel();
					t++;
				}


			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

	}


	public void TurretDrop(final Player p, int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		TurretDropMethod(p);

	}

	private void TurretDropMethod(final Player p) {

		Location targetloc = null;
		Location loc = p.getEyeLocation();
		Vector dir = loc.getDirection().normalize().multiply(0.3);

		for(int i=0; i<40; i++) {
			for(LivingEntity entity : p.getWorld().getLivingEntities()) {
				if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, p) && p != entity) {
					Location location = entity.getLocation();
					BoundingBox box = entity.getBoundingBox();
					if (location.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
						targetloc = entity.getLocation();
						break;
					}
				}
			}
			if(targetloc != null) break;
			if(targetloc == null && i==39) targetloc = loc;

			if(loc.getBlock().getType().isSolid()) {
				targetloc = loc;
				break;
			}
			loc.add(dir);
		}

		while(!targetloc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
			targetloc.add(0, -1, 0);
		}


		final List<Location> locs = new ArrayList<>();

		double xangle = Math.toRadians(Math.random() * 40-20);
		double yangle = Math.toRadians(Math.random() * 180 - 360);
		double yaxiscos = Math.cos(yangle);
		double yaxissin = Math.sin(yangle);
		double xaxiscos = Math.cos(xangle);
		double xaxissin = Math.sin(xangle);

		for(int i=0; i<60; i++) {

			double y = (double)i/4;
			Vector v = new Vector(0, y, 0);
			v = rotateAroundAxisX(v, xaxiscos, xaxissin);
			v = rotateAroundAxisY(v, yaxiscos, yaxissin);
			locs.add(targetloc.clone().add(v));

		}

		final List<Location> square = new ArrayList<>();
		final double halfwidth = 1.5;
		for(double i = -halfwidth; i<=halfwidth; i+=0.2) {
			square.add(new Location(Bukkit.getWorld("world"), i, 0.2, -halfwidth));
			square.add(new Location(Bukkit.getWorld("world"), i, 0.2, halfwidth));
			square.add(new Location(Bukkit.getWorld("world"), halfwidth, 0.2, i));
			square.add(new Location(Bukkit.getWorld("world"), -halfwidth, 0.2, i));
		}

		new BukkitRunnable() {

			int time = 0;

			double yaw = 0;
			double yaw2 = 45;

			double x = 0;
			double y = 0;
			double z = 0;

			int kloc = 59;

			@Override
			public void run() {

				for(Location loc : locs) {
					loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0,
							new Particle.DustOptions(Color.fromRGB(135, 206, 235), 1));
				}

				for(Location square : square) {

					x = square.getX();
					y = square.getY();
					z = square.getZ();
					Vector v = new Vector(x, y, z);
					v = transform(v, Math.toRadians(yaw), 0,0);
					p.getWorld().spawnParticle(Particle.REDSTONE, locs.get(0).clone().add(v), 1, 0, 0, 0,
							new Particle.DustOptions(Color.RED, 0.5f));
					v = transform(v, Math.toRadians(45), 0,0);
					p.getWorld().spawnParticle(Particle.REDSTONE, locs.get(0).clone().add(v), 1, 0, 0, 0,
							new Particle.DustOptions(Color.RED, 0.5f));
				}


				// 낙하
				if(time>10) {
					for(int i=0; i<3; i++) {

						loc.getWorld().spawnParticle(Particle.FLAME, locs.get(kloc), 10, 1, 1, 1, 0);

						loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, locs.get(kloc), 4, 1, 1, 1, 0);


						if(kloc>=1) kloc--;
					}
				}

				if(time>30) {
					PlayParticle playParticle = new PlayParticle(Particle.CRIT);
					playParticle.CirCleHorizontalSmallImpact(locs.get(kloc).add(0, 0.3, 0));
					p.getWorld().playSound(locs.get(kloc), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
					p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, locs.get(kloc), 1, 0, 0, 0, 0);
					cancel();
				}
				yaw += 3;
				yaw2 += 3;
				time++;
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

	}

	
	
}

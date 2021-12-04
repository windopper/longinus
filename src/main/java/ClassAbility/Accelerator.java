package ClassAbility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import Interact.Damage;
import dynamicdata.PlayerEnergy;
import dynamicdata.PlayerFunction;
import dynamicdata.PlayerHealth;
import userdata.UserManager;

public class Accelerator {
	
	private static Accelerator Accelerator;
	public static final int movehitmana = 8;
	public static final int adrenalinemana = 8;
	public static final int bombthrowmana = 12;
	public static final int randomfiremana = 12;
	public static final int particleaccelerationmana = 15;
	public static HashMap<Player, Integer> passivecooldown = new HashMap<>();
	public static HashMap<Player, Double> rate = new HashMap<>();
	
	public int particletime = 0;
	
	private Accelerator() {
		
	}
	
	public static Accelerator getinstance() {
		if(Accelerator == null) Accelerator = new Accelerator();
		return Accelerator;
	}
	
	
	public void removemaps(Player p) {
		passivecooldown.remove(p);
		rate.remove(p);
	}
	
	public void melee(final Player p) {


		SpellManager Spell = new SpellManager(p, 0.3);

		Spell.addDepartSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 1, 2);
		Spell.setEntityPassable(false);
		Spell.setMaximumRange(6);
		Spell.setHitBoxRange(1);
		Spell.setDamageRate(1);
		Spell.addTrailParticle(Particle.CRIT);
		Spell.addTrailParticle(Particle.CRIT_MAGIC);
		Spell.RunLinearSpell(SpellManager.MeleeOrSpell.Melee);

		PlayerFunction.getinstance(p).setMeleeDelay(8);
		
	}
	
	public void movehit(final Player p, int mana) {
		
		
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		
		new BukkitRunnable() {
			
			HashMap<Entity, Integer> hit = new HashMap<>();
			int i =0;
			
			
			@Override
			public void run() {
				
				hit.clear();
				
				if(i>=2) cancel();
				i++;

				SpellManager Spell = new SpellManager(p, 0.1);
				Spell.setEntityPassable(true);
				Spell.addDepartSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 1, 2);
				Spell.addTrailParticle(Particle.CRIT_MAGIC, 1, 0, 0, 0, 0, null);
				Spell.setMaximumRange(6);
				Spell.setHitBoxRange(1);
				Spell.setDamageRate(1);
				Spell.setKnockBack(p, 0.4);
				Spell.addDestinationParticle(Particle.BLOCK_CRACK, 20, 0.5d, 0.5d, 0.5d, 0d, Material.REDSTONE_BLOCK.createBlockData());

				Spell.RunLinearSpell(SpellManager.MeleeOrSpell.Spell);
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 2);
	}
	@SuppressWarnings("deprecation")
	public void adrenaline(final Player p, int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		for(Player pl : Bukkit.getOnlinePlayers()) {
			pl.playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
			pl.spawnParticle(Particle.SMOKE_NORMAL, p.getLocation(), 20, 0.5, 0.5, 0.5, 0.1, null);
		}
		
		PotionEffect potion = new PotionEffect(PotionEffectType.SPEED, 60, 9);
		p.addPotionEffect(potion, true);
		
		Location loc = p.getEyeLocation();
		Vector dir = loc.getDirection();
		dir.normalize();
		dir.multiply(1.5);
		
		p.setVelocity(dir);
		
		new BukkitRunnable() {
			
			int i=0;
			@Override
			public void run() {
				
				rate.put(p, 1.5d);
				
				if(i>=60) {
					rate.remove(p);
					cancel();
				}
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}
	public void bombthrow(final Player p, int mana) {

		PlayerEnergy.getinstance(p).removeEnergy(mana);
		HashMap<Entity, Integer> hit = new HashMap<>();
		
		for(Player pl : Bukkit.getOnlinePlayers()) {
			pl.playSound(p.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 1, 0);
		}
		
		
		Location loc = p.getEyeLocation();
		Vector dir = loc.getDirection();
		Vector plus = new Vector(0, -0.04, 0);
		
		dir.normalize();
		dir.multiply(0.7);

		new BukkitRunnable() {

			@Override
			public void run() {

				Location clonnloc = loc.clone();
				Random random = new Random();
				double x = random.nextDouble()*2-1;
				double y = random.nextDouble()*2-1;
				double z = random.nextDouble()*2-1;
				Vector vec = new Vector(x, y, z);
				vec.normalize();
				vec.multiply(0.04);

				for(int j=0; j<12; j++) {
					for(Player pl : Bukkit.getOnlinePlayers()) {
						pl.spawnParticle(Particle.CRIT_MAGIC, clonnloc.add(vec), 5, 0, 0, 0, 0, null);
					}
				}
				// 삐죽삐죽
				x = random.nextDouble()*2-1;
				y = random.nextDouble()*2-1;
				z = random.nextDouble()*2-1;
				Vector vec2 = new Vector(x, y, z);
				vec2.normalize();
				vec2.multiply(0.05);

				for(int j=0; j<6; j++) {
					for(Player pl : Bukkit.getOnlinePlayers()) {
						//pl.spawnParticle(Particle.REDSTONE, clonnloc.add(vec), 1, 0, 0, 0, 0, new DustOptions(Color.AQUA, 1));
						pl.spawnParticle(Particle.CRIT_MAGIC, clonnloc.add(vec2), 5, 0, 0, 0, 0, null);
					}
				}
				// 삐죽삐죽
				for(Player pl : Bukkit.getOnlinePlayers()) {
					pl.spawnParticle(Particle.REDSTONE, loc.add(dir), 10, 0.1, 0.1, 0.1, 0, new DustOptions(Color.PURPLE, 1));
					pl.spawnParticle(Particle.CRIT_MAGIC, loc, 10, 0.1, 0.1, 0.1, 0, null);
				}


				SpellManager Spell = new SpellManager(p, 0.1);
				Spell.setHitBoxRange(3);
				Spell.setDamageRate(2.5);
				Spell.setStun(20);
				Spell.setWallPassable(false);
				Spell.addDestinationSound(Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
				Spell.addDestinationSound(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 2, 2);
				Spell.addDestinationParticle(Particle.BLOCK_CRACK, 50, 0.5, 0.5, 0.5, 0, Material.BLUE_STAINED_GLASS.createBlockData());
				Spell.addDestinationParticle(Particle.LANDING_OBSIDIAN_TEAR, 40, 0.5, 0.5, 0.5, 0, null);
				Spell.addDestinationParticle(Particle.EXPLOSION_LARGE, 2, 0, 0, 0, 0, null);

				if(Spell.RunCircleSpell(SpellManager.MeleeOrSpell.Spell, loc)) {
					for(Player pl : Bukkit.getOnlinePlayers()) {
						for(int i=0; i<10; i++) {

							Location eclonloc = loc.clone();

							x = random.nextDouble()*2-1;
							y = random.nextDouble()*2-1;
							z = random.nextDouble()*2-1;
							vec = new Vector(x, y, z);
							vec.normalize();
							vec.multiply(0.1);
							for(int k=0; k<30; k++) {
								pl.spawnParticle(Particle.REDSTONE, eclonloc.add(vec), 1, 0, 0, 0, 0, new DustOptions(Color.PURPLE, 1));
							}


							x = random.nextDouble()*2-1;
							y = random.nextDouble()*2-1;
							z = random.nextDouble()*2-1;
							vec2 = new Vector(x, y, z);
							vec2.normalize();
							vec2.multiply(0.1);
							for(int k=0; k<10; k++) {
								pl.spawnParticle(Particle.REDSTONE, eclonloc.add(vec2), 1, 0, 0, 0, 0, new DustOptions(Color.PURPLE, 1));
							}
						}
					}
					cancel();
				}
				dir.add(plus);

			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

	}
	public void randomfire(final Player p, int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		Random random = new Random();
		
		new BukkitRunnable() {
			
			int i=0;
			@Override
			public void run() {
				
				ArrayList<Entity> elist = new ArrayList<Entity>();
				
				Location loc = p.getEyeLocation();
				Vector locvec = loc.toVector();
				double x = random.nextDouble()*2-1;
				double y = random.nextDouble()*2-1;
				double z = random.nextDouble()*2-1;
				Vector dir = new Vector(x, y, z);
				dir.normalize();
				dir.multiply(0.05);
				loc.add(dir);
				loc.add(dir);
				loc.add(dir);

				if(i%3==0) {

					
					
					for(Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 6, 6, 6)) {
						if(entitycheck.duelcheck(e, p) && entitycheck.duelcheck(e, p)) {
							elist.add(e);
						}
					}
					
					Collections.shuffle(elist);
					
					//Bukkit.broadcastMessage(Integer.toString(elist.size()));

					for(Entity e : elist) {
						
						if(entitycheck.duelcheck(e, p) && entitycheck.duelcheck(e, p)) {
							
							for(Player pl : Bukkit.getOnlinePlayers()) {
								pl.playSound(loc, Sound.BLOCK_CONDUIT_DEACTIVATE, 1, 2);
								pl.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, p.getLocation(), 5, 3, 1, 3, 0.05, null);
							}
							
							
							Vector eloc = ((LivingEntity) e).getEyeLocation().toVector();
							Vector etop = eloc.subtract(locvec);
							etop.normalize();
							etop.multiply(0.05);
							
							
							for(int i=0; i<30; i++) {
								for(Player pl : Bukkit.getOnlinePlayers()) {
									pl.spawnParticle(Particle.CRIT, loc.add(etop), 1, 0, 0, 0, 0, null);
								}
							}
							
							
							int dmg = UserManager.getinstance(p).spelldmgcalculate(p, 0.5);
							p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2);
							Damage.getinstance().taken(dmg, (LivingEntity) e, p);
							
							break;
						}
					}
				}
				if(i>36) cancel();
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		
	}
	public void particleacceleration(final Player p, int mana) {
		
		int Health = UserManager.getinstance(p).Health;
		
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		for(Player pl : Bukkit.getOnlinePlayers()) {
			pl.spawnParticle(Particle.PORTAL, p.getLocation(), 500, 0.1, 0.1, 0.1, 1.5, null);
		}
		
		if(passivecooldown.containsKey(p)) passivecooldown.remove(p);
		
		if(PlayerHealth.getinstance(p).getCurrentHealth()+(int)(Health/4) > Health) {
			PlayerHealth.getinstance(p).setCurrentHealth(Health);
		}
		else {
			PlayerHealth.getinstance(p).setCurrentHealth(PlayerHealth.getinstance(p).getCurrentHealth()+(int)(Health/4));
		}
		p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 2);
	}
	@SuppressWarnings("deprecation")
	public void Passive1() {

		for(Player p : Bukkit.getOnlinePlayers()) {

			if(passivecooldown.containsKey(p)) {
				passivecooldown.replace(p, passivecooldown.get(p)+1);

				if(passivecooldown.get(p)>80) {
					passivecooldown.remove(p);
				}

			}

			if(!passivecooldown.containsKey(p) && UserManager.getinstance(p).CurrentClass.equals("엑셀러레이터")) { // 패시브가 터졌고 엑셀러레이터일때


				PlayerEnergy.getinstance(p).setEnergyRate(2);
				PotionEffect potion = new PotionEffect(PotionEffectType.SPEED, 2, 2);
				p.addPotionEffect(potion, true);

			}
			else {
				PlayerEnergy.getinstance(p).setEnergyRate(1);
			}
		}
		
	}
}

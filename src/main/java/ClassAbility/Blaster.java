package ClassAbility;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
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

import DynamicData.EntityStatus;
import DynamicData.PlayerEnergy;
import DynamicData.PlayerFunction;
import DynamicData.PlayerHealth;
import UserData.UserManager;

public class Blaster {
	
	private static Blaster Blaster;
	
	public static final int railgunmana = 4;
	public static final int grenadelaunchermana = 4;
	public static final int riflemana = 4;
	public static final int energytransmana = 10;
	public static final int magneticfieldmana = 12;
	
	public static final List<Player> preheat = new ArrayList<>();
	private Blaster() {
		
	}
	
	public static Blaster getinstance() {
		if(Blaster == null) Blaster = new Blaster();
		return Blaster;
	}
	
	
	public void removemaps(Player p) {
		preheat.remove(p);
	}
	
	@SuppressWarnings("deprecation")
	public void melee(final Player p) {
		
		if(PlayerFunction.getinstance(p).getMeleemode()==0) {

			Location ploc = p.getEyeLocation();
			//p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LLAMA_SPIT, 2, 0);
			Vector pvec = ploc.getDirection();
			pvec.normalize();
			pvec.multiply(0.3);
			for(int i=0; i<3; i++) {
				ploc.add(pvec);
			}

			SpellManager Spell = new SpellManager(p, ploc, 0.3);
			Spell.addDepartSound(Sound.ENTITY_LLAMA_SPIT, 2, 0);
			Spell.addDepartParticle(Particle.SMOKE_LARGE, 4, 0, 0, 0, 0, null);
			Spell.setMaximumRange(9);
			Spell.setEntityPassable(true);
			Spell.addTrailParticle(Particle.VILLAGER_ANGRY, 1, 0, 0, 0, 0, null);
			Spell.setHitBoxRange(1.5);
			Spell.setDamageRate(1);
			Spell.setBurn(60, 0.1);

			Spell.RunRayCast(SpellManager.MeleeOrSpell.Melee);
			
			PlayerFunction.getinstance(p).setMeleeDelay(20);
					
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

			if(Spell.getHitLocation().getBlock().isSolid()) {
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
		PlayerHealth.getinstance(p).ShieldAdd((int)(UserManager.getinstance(p).Health * recoverrate));
		
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
		
		
		Location loc = p.getLocation();
		
		final int r = 10;
		
		new BukkitRunnable() {
			
			int i= 1;
			double theta = 0;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2, 1);
				
				double y = 0;
				for(double phi = 0; phi<=Math.PI; phi+=Math.PI/15) {
					y = r * Math.cos(phi) + 1.5;
	                double x = r * Math.cos(theta) * Math.sin(phi);
	                double z = r * Math.sin(theta) * Math.sin(phi);
	                loc.add(x, y, z);
	                loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 5, 1F, 1F, 1F, 0.5, null);
	                loc.getWorld().spawnParticle(Particle.CRIT, loc, 10, 1F, 1F, 1F, 0, null);
	                loc.subtract(x, y, z);
					y = r * Math.cos(phi) + 1.5;
	                z = r * Math.cos(theta) * Math.sin(phi);
	                x = r * Math.sin(theta) * Math.sin(phi);
	                loc.add(x, y, z);
	                loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 5, 1F, 1F, 1F, 0.5, null);
	                loc.getWorld().spawnParticle(Particle.CRIT, loc, 10, 1F, 1F, 1F, 0, null);
	                loc.subtract(x, y, z);

				}
			    for (int d = 0; d <= 45; d += 1) {
			        Location particleLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
			        particleLoc.setX(loc.getX() + Math.cos(d) * i);
			        particleLoc.setZ(loc.getZ() + Math.sin(d) * i);
			        loc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, new Particle.DustOptions(Color.TEAL, 1));
			    }

				Bukkit.getWorld(p.getWorld().toString());
	            
	            for(Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 10, 10, 10)) {
	            	if(entitycheck.entitycheck(e) && entitycheck.duelcheck(e, p)) {
	            		LivingEntity le = (LivingEntity) e;
	            		EntityStatus.getinstance(le).Stun(e, 20);
	            	}
	            }
	            
		        if(theta >= Math.PI * 5) cancel();
		        theta += Math.PI /10;
		        if(i==10) i=0;
		        i++;
				
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
	
	
	
}

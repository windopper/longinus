package ClassAbility;

import DynamicData.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ByV {
	
	private static ByV ByV;
	
	public static final int recovermana = 4;
	public static final int takendownmana = 6;
	public static final int chainmana = 6;
	public static final int punchmana = 4;
	public static final int shockwavemana = 8;
	
	public final static HashMap<FallingBlock, Integer> fallingblocks = new HashMap<>();
	public final static List<Player> whiletakedown = new ArrayList<>();
	
	private ByV() {
		
	}
	
	public static ByV getinstance() {
		if(ByV == null) ByV = new ByV();
		return ByV;
	}
	
	public void removemaps(Player p) {
		whiletakedown.remove(p);
	}
	
	public void melee(final Player p) {

		SpellManager Spell = new SpellManager(p, 0.2);
		Spell.addDepartSound(Sound.BLOCK_STONE_BREAK, 1, 1);
		Spell.addTrailParticle(Particle.SMOKE_NORMAL, 4, 0.2, 0.2, 0.2, 0, null);
		Spell.setMaximumRange(2);
		Spell.setEntityPassable(true);
		Spell.setHitBoxRange(1.5);
		Spell.setDamageRate(1);
		Spell.setKnockBack(p, 0.5);
		Spell.RunRayCast(SpellManager.MeleeOrSpell.Melee);
		
		PlayerFunction.getinstance(p).setMeleeDelay(20);

		
	}
	public void recover(final Player p, int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction.getinstance(p).essence --;
		
		PlayerHealthShield.getinstance(p).HealthAdd(PlayerHealthShield.getinstance(p).getCurrentHealth()/10);
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(p.getWorld().getName().equals(player.getWorld().getName())) {
				if(!entitycheck.duelcheck(player, p) && player != p) {
					PlayerHealthShield.getinstance(player).HealthAdd(PlayerHealthShield.getinstance(p).getCurrentHealth()/10);
				}
			}
		}
		
		
	}
	public void takedown(final Player p, int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction.getinstance(p).essence--;
		p.setVelocity(new Vector(0, 1.5, 0));
		
		if(!whiletakedown.contains(p)) {
			whiletakedown.add(p);
			new BukkitRunnable() {
				
				int i = 0;
				
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					
					p.setFallDistance(0);
					
					
					if(i==20) {
						Location ploc = p.getEyeLocation();
						Vector pvec = ploc.getDirection();
						pvec.normalize();
						pvec.multiply(3);
						p.getWorld().playSound(ploc, Sound.ENTITY_GHAST_SHOOT, 2, 1);
						p.setVelocity(pvec);
					}
					
					p.getWorld().spawnParticle(Particle.REDSTONE, p.getLocation(), 5, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.RED, 2));
					p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, p.getLocation(), 5, 0.5, 0.5, 0.5, 0, null);
					
					if(i>20) {
						riptidepacket(p, 1);
					}
					
					
					if(i>20 && p.isOnGround()) {
						
						p.setFallDistance(3);

						DataWatcher dataWatcher = ((CraftPlayer) p).getHandle().getDataWatcher();
						dataWatcher.set(new DataWatcherObject<>(8, DataWatcherRegistry.a), (byte) 0x00);

						Location ploc = p.getLocation();
						p.getWorld().playSound(ploc, Sound.BLOCK_GRASS_BREAK, 2, 1);
						p.getWorld().playSound(ploc, Sound.ENTITY_IRON_GOLEM_DEATH, 2, 2);
						
						
						for(Entity e : p.getWorld().getNearbyEntities(ploc, 5, 5, 5)) {
							if(entitycheck.entitycheck(e) && entitycheck.duelcheck(e, p)) {
								Location eloc = e.getLocation();
								Vector evec = eloc.toVector();
								Vector pvec = ploc.toVector();
								Vector etop = evec.subtract(pvec);
								etop.normalize();
								etop.multiply(1);
								if(EntityStatusManager.getinstance((LivingEntity)e).canKnockback() == true) {
									
									e.setVelocity(etop);
								}

								int dmg = PlayerData.UserManager.getinstance(p).spelldmgcalculate(p, 1.5);
								Damage.getinstance().taken(dmg, (LivingEntity) e, p);
								
								p.getWorld().playSound(ploc, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
								
							}
						}
						
						takedownparticles(p);
						//riptideoffpacket(p);
						
						whiletakedown.remove(p);
						cancel();
					}
					i++;
					
				}
			}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		}
		
		else {
			
			new BukkitRunnable() {
				
				int i=0;
				
				@Override
				public void run() {
					
					if(i==20) {
						Location ploc = p.getEyeLocation();
						Vector pvec = ploc.getDirection();
						pvec.normalize();
						pvec.multiply(3);
						p.getWorld().playSound(ploc, Sound.ENTITY_GHAST_SHOOT, 2, 1);
						p.setVelocity(pvec);
					}
					
					if(i>20 && p.isOnGround()) {
						
						p.setFallDistance(3);

						cancel();
					}
					i++;
					
				}
			}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		}

	}
	public void chain(final Player p, int mana) {
		
		PlayerEnergy.getinstance(p).removeEnergy(mana);

		
		Location ploc = p.getEyeLocation();
		p.getWorld().playSound(ploc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 2, 1);
		
		Vector pvec = ploc.getDirection();
		pvec.normalize();	
		
		pvec.multiply(0.1);
		Vector pvecclone = pvec.clone().multiply(0.2);
		int j =0;
		
		for(int i=0; i<=200; i++) {
			
			chainpiece1(ploc, pvecclone, i%18, j);
			
			for(LivingEntity e : p.getWorld().getLivingEntities()) {
				if(entitycheck.entitycheck(e) && entitycheck.duelcheck(e, p)) {
					Location eloc = e.getBoundingBox().getCenter().toLocation(p.getWorld());
					BoundingBox box = e.getBoundingBox();
					double dist = eloc.distance(ploc);
					if(dist<1.2 || box.contains(ploc.getX(), ploc.getY(), ploc.getZ())) {
						p.playSound(ploc, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
						chainparticle(p, i, e);
						chainvectorzerocc(e);
						
						int dmg = PlayerData.UserManager.getinstance(p).spelldmgcalculate(p, 0.75);
						
						Damage.getinstance().taken(dmg, e, p);
						PlayerFunction.getinstance(p).essence++;
						return;
					}
				}
			}
			ploc.add(pvec);
			
		}
		
		
		
		
		
	}
	public void punch(final Player p, int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction.getinstance(p).essence--;

		Location ploc = p.getEyeLocation();
		Vector pvec = ploc.getDirection();
		pvec.normalize();
		pvec.multiply(2);
		ploc.add(pvec);
		
		p.getWorld().playSound(ploc, Sound.ENTITY_WITHER_SHOOT, 1.5f, 1f);
		p.getWorld().playSound(ploc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

		SpellManager Spell = new SpellManager(p);
		Spell.setHitBoxRange(3);
		Spell.addDestinationSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1.5f, 0);
		Spell.setDamageRate(1.5);
		Spell.setKnockBack(p, 2);
		Spell.RunRadiusRange(SpellManager.MeleeOrSpell.Spell, ploc);

		punchparticle(p, ploc);

	}
	public void shockwave(final Player p, int mana) {
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		
		
		p.getLocation().getWorld().playSound(p.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 0);
		p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

		int rate = 1;
		if(PlayerFunction.getinstance(p).essence>8) {
			rate = 8;
		}
		else {
			rate = PlayerFunction.getinstance(p).essence;
		}

		PlayerFunction.getinstance(p).essence -= rate;

		SpellManager Spell = new SpellManager(p);
		Spell.setHitBoxRange(3);
		Spell.addDestinationSound(Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2f, 0);
		Spell.setDamageRate(rate);
		Spell.setKnockBack(p, 2);
		Spell.RunRadiusRange(SpellManager.MeleeOrSpell.Spell, p.getLocation());

		for(Entity e : Spell.getHitEntityList()) {
			e.setVelocity(new Vector(0, 1, 0));
		}
		
//		for(Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 3, 3, 3)) {
//			if(entitycheck.entitycheck(e) && entitycheck.duelcheck(e, p)) {
//				p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2, 0);
//				p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
//				int dmg = userdata.UserManager.getinstance(p).spelldmgcalculate(p, rate);
//				Damage.getinstance().taken(dmg, (LivingEntity) e, p);
//				e.setVelocity(new Vector(0, 1, 0));
//			}
//		}
		
	    for (int d = 0; d <= 45; d += 1) {
	        Location particleLoc = new Location(p.getLocation().getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
	        particleLoc.setX(p.getLocation().getX() + Math.cos(d) * 3);
	        particleLoc.setZ(p.getLocation().getZ() + Math.sin(d) * 3);
	        particleLoc.setY(p.getLocation().getY() + d/90);
	        p.getLocation().getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 20, 0,2,0,0, new Particle.DustOptions(Color.BLUE, 2));
	        p.getLocation().getWorld().spawnParticle(Particle.BLOCK_DUST, particleLoc, 20, 0,2,0,0, particleLoc.add(0, -1, 0).getBlock().getType().createBlockData());
	    }
		
		
	}
	
//	public void ByVPassive() {
//
//		for(Player p : Bukkit.getOnlinePlayers()) {
//
//			if(userdata.UserManager.getinstance(p).CurrentClass.equals("바이V")) {
//				if(!essence.containsKey(p)) essence.put(p, 0);
//			}
//			else {
//				essence.remove(p);
//			}
//
//		}
//
//	}
	
	public void takedownparticles(Player p) {
		
		final Location ploc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
		
		//Location previousloc = p.getLocation();

		
		new BukkitRunnable() {
			
			double j = 2;
			
			@Override
			public void run() {	
				summonCircle(ploc, j);
				if(j>=6) cancel();
				j+=2;
				
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 2);
		
	}
	
	
	public void summonCircle(Location location, double size) {
		Location previousloc = location;
	    for (int d = 0; d <= 90; d += 1) {
	        Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
	        particleLoc.setX((int)(location.getX() + Math.cos(d) * size));
	        particleLoc.setZ((int)(location.getZ() + Math.sin(d) * size));
	        
	        if(previousloc == particleLoc) continue;
	        else previousloc = particleLoc;
	        if(particleLoc.getBlock().getType() != Material.AIR) continue;
	        
		    FallingBlock e = location.getWorld().spawnFallingBlock(particleLoc, particleLoc.clone().add(0, -1, 0).getBlock().getType().createBlockData());
		    e.setHurtEntities(false);
		    e.setSilent(true);
		    e.setVelocity(new Vector(0, 0.1, 0));
		    e.setDropItem(false);
		    e.setInvulnerable(true);
		    location.getWorld().spawnParticle(Particle.BLOCK_DUST, particleLoc, 4, 1, 1, 1, 0, particleLoc.clone().add(0, -1, 0).getBlock().getType().createBlockData());
	    }
	}
	
	
	public void riptidepacket(Player player, int tick) {

		DataWatcher dataWatcher = ((CraftPlayer) player).getHandle().getDataWatcher();
		dataWatcher.set(new DataWatcherObject<>(8, DataWatcherRegistry.a), (byte) 0x04);

		
	}

	
	public void punchparticle(final Player p, Location cloc) {
		
		Location ploc = p.getEyeLocation();
		Vector pvec = ploc.getDirection();
		pvec.normalize();
		pvec.multiply(2);
		ploc.add(pvec);
		
		Vector minusvec = new Vector(-pvec.getX(), 0, -pvec.getZ());
		minusvec.multiply(2);
		
		
		Vector pvecright = new Vector(-pvec.getZ(), pvec.getY(), pvec.getX());
		Vector pvecleft = new Vector(pvec.getZ(), pvec.getY(), -pvec.getX());
		
		Location plocright = ploc.clone().add(pvecright).add(0, -1, 0);
		Location plocleft = ploc.clone().add(pvecleft).add(0, 1, 0);
		
		Vector rightvec = plocright.toVector();
		Vector leftvec = plocleft.toVector();
		Vector finalvec = leftvec.subtract(rightvec);
		finalvec.normalize();
		finalvec.multiply(0.3);
		
		
		for(int i=0; i<15; i++) {
			p.getWorld().spawnParticle(Particle.REDSTONE, plocright.clone().add(minusvec.multiply(0.8)), 20, 0.3, 0.3, 0.3, 0, new Particle.DustOptions(Color.RED, 1));
			p.getWorld().spawnParticle(Particle.REDSTONE, plocright.clone().add(minusvec.multiply(0.8)), 20, 0.3, 0.3, 0.3, 0, new Particle.DustOptions(Color.WHITE, 1));
			plocright.add(finalvec);
		}
		
		

		
		
	}
	
	public void chainparticle(final Player p, final int k, final LivingEntity target) {
		
		ArmorStand e = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
		
		e.teleport(new Location(p.getWorld(), p.getLocation().getX(),
				p.getLocation().getY(), 
				p.getLocation().getZ(), 
				p.getLocation().getYaw(),
				p.getLocation().getPitch()));
		e.setSilent(true);
		e.setInvulnerable(true);
		e.setInvisible(true);
		e.setGravity(false);
		
		
		
		
		new BukkitRunnable() {

			int j=0;
			
			@Override
			public void run() {
				
				
				
				
				Location ploc = e.getEyeLocation();
				Vector pvec = ploc.getDirection();
				pvec.normalize();	
				pvec.multiply(0.1);
				Vector pvecclone = pvec.clone().multiply(0.2);
				
				if(j<5) { // 파티클
					
					for(int i=0; i<=k; i++) {
						
						chainpiece1(ploc, pvecclone, i%18, j+1);
						ploc.add(pvec);
						
					}
				}	
				
				if(j>6) {
					
					p.getWorld().playSound(ploc, Sound.ENTITY_WITHER_DEATH, 1f, 2f);
					
					for(int a=0; a<9; a++) {
						
						Location rl = new Location(p.getWorld(), Math.random() * 5 -2.5, Math.random() * 3, Math.random() * 5 -2.5);
						Vector vec = new Vector(Math.random() * 2 -1, Math.random() * 2 -1, Math.random() * 2 -1);
						rl.add(target.getLocation());
						vec.normalize();
						vec.multiply(0.1);
						
						for(int l=0; l<18; l++) {
							chainpiece1(rl, vec, l%18, 0);
							rl.add(vec);
						}
								
					}
					
					
					 // 끌고 오기
					
					if(EntityStatusManager.getinstance(target).canKnockback() == false) {


						new BukkitRunnable() {

							int i=0;

							@Override
							public void run() {

								Vector pp = target.getLocation().toVector();
								Vector ee = p.getLocation().toVector();
								Vector ppee = pp.subtract(ee);
								ppee.normalize();

								ppee.multiply(1.5);

								target.setVelocity(ppee);

								if(p.getWorld().getName().equals(target.getWorld().getName())) {
									if(p.getLocation().distance(target.getLocation())<3) {
										p.setVelocity(new Vector(0, 0, 0));
										cancel();
									}
								}

								if(i>60) cancel();
								i++;
							}
						}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);


					}
					else {
						
						new BukkitRunnable() {
							
							int i=0;
							
							@Override
							public void run() {
								
								Vector pp = p.getLocation().toVector();
								Vector ee = target.getLocation().toVector();
								Vector ppee = pp.subtract(ee);
								ppee.normalize();
								
								ppee.multiply(1.5);
								
								target.setVelocity(ppee);
								
								if(p.getWorld().getName().equals(target.getWorld().getName())) {
									if(p.getLocation().distance(target.getLocation())<3) {
										target.setVelocity(new Vector(0, 0, 0));
										cancel();
									}
								}	

								if(i>60) cancel();
								i++;
							}
						}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
					}
					e.remove();
					cancel();
				}
				j++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 3);
		
	}
	
	public void chainpiece1(Location loc, Vector dir, int i, int j) {
		
		Vector rightvec = new Vector(dir.getZ(), 0, -dir.getX());
		Vector leftvec = new Vector(-dir.getZ(), 0, dir.getX());
		rightvec.normalize();
		leftvec.normalize();
		
		if(i==0 || i==9 || i==16) {
			chainpieceparticle(loc, rightvec.clone().multiply(0.1), j);
			chainpieceparticle(loc, leftvec.clone().multiply(0.1), j);	
		}
		if(i==1 || i==8 || i==17) {
			chainpieceparticle(loc, rightvec.clone().multiply(0.2), j);
			chainpieceparticle(loc, leftvec.clone().multiply(0.2), j);		
		}
		if(i==2 || i==7) {
			chainpieceparticle(loc, rightvec.clone().multiply(0.25), j);
			chainpieceparticle(loc, leftvec.clone().multiply(0.25), j);
		}
		if(i>=3 && i<=6) {
			chainpieceparticle(loc, rightvec.clone().multiply(0.25), j);
			chainpieceparticle(loc, leftvec.clone().multiply(0.25), j);
		}
		
		Vector upvec = new Vector(0, 1, 0);
		Vector downvec = new Vector(0, -1, 0);
		upvec.normalize();
		downvec.normalize();
		
		if(i==8 || i==17) {
			chainpieceparticle(loc, upvec.clone().multiply(0.1), j);
			chainpieceparticle(loc, downvec.clone().multiply(0.1), j);
		}
		if(i==9 || i==16) {
			chainpieceparticle(loc, upvec.clone().multiply(0.2), j);
			chainpieceparticle(loc, downvec.clone().multiply(0.2), j);
		}
		if(i==10 || i==15)  {
			chainpieceparticle(loc, upvec.clone().multiply(0.25), j);
			chainpieceparticle(loc, downvec.clone().multiply(0.25), j);
		}
		if(i>=11 && i<=14) {
			chainpieceparticle(loc, upvec.clone().multiply(0.25), j);
			chainpieceparticle(loc, downvec.clone().multiply(0.25), j);
		}
	}

	public void chainpieceparticle(Location loc, Vector vec, int j) {
			
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(vec), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.PURPLE, 1));
		//loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc.clone().add(vec), 1, 0, 0, 0, 0, null);
		if(j==0) {
			
			loc.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(vec), 1, 0, 0, 0, 0, null);
		}

		
	}
	
	public boolean checkentitychain(Location loc, Player p) {
		
		for(LivingEntity e : p.getWorld().getLivingEntities()) {
			if(entitycheck.entitycheck(e) && entitycheck.duelcheck(e, p) && p!=e) {
				Location eloc = e.getBoundingBox().getCenter().toLocation(p.getWorld());
				BoundingBox box = e.getBoundingBox();
				double dist = eloc.distance(loc);
				if(dist<2 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
					int dmg = PlayerData.UserManager.getinstance(p).spelldmgcalculate(p, 0.75);
					e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 20));
					Damage.getinstance().taken(dmg, e, p);
					return true;
				}
			}
		}
		return false;
	}
	
	public void chainvectorzerocc(Entity e) {
		
		if(EntityStatusManager.getinstance((LivingEntity)e).canKnockback() == false) return;
		
		new BukkitRunnable() {
			
			int i=0;
			
			@Override
			public void run() {
				
				e.setVelocity(new Vector(0, 0, 0));
				
				if(i>18) cancel();
				i++;
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}
	
}

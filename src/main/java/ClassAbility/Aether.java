package ClassAbility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import DynamicData.PlayerEnergy;
import DynamicData.PlayerFunction;
import DynamicData.PlayerHealth;
import org.jetbrains.annotations.NotNull;
import UserData.UserManager;

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
	public void melee(final @NotNull Player p) {

		HashMap<Entity, Integer> meleehit = new HashMap<>();
		Vector dir1 = p.getLocation().getDirection();
		Location loc1 = p.getEyeLocation();
		dir1.normalize();
		dir1.multiply(0.2);

		SpellManager Spell = new SpellManager(p, 0.2);
		Spell.addDepartSound(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 2);
		Spell.setEntityPassable(true);
		Spell.setHitBoxRange(2);
		Spell.setMaximumRange(3);
		Spell.setDamageRate(1);

		Spell.RunRayCast(SpellManager.MeleeOrSpell.Melee);
		
		for(int i=0; i<15; i++) {
			
			for(Player pl : Bukkit.getOnlinePlayers()) {
				
				if(i==14) {
					pl.spawnParticle(Particle.SMOKE_NORMAL, loc1, 20, 0.5, 0.5, 0.5, 0.1, null);
				}
				else if(i==12) {
					pl.spawnParticle(Particle.BLOCK_CRACK, loc1, 20, 0.5, 0.5, 0.5, 0, Material.SEA_LANTERN.createBlockData());
				}
				else if(i==13) {
					pl.spawnParticle(Particle.SWEEP_ATTACK, loc1, 10, 0.5, 0.5, 0.5, 0, null);
				}
			}
			loc1.add(dir1);
		}

		PotionEffect potion = new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 10);
		p.addPotionEffect(potion, true);
		
		PlayerFunction.getinstance(p).setMeleeDelay(20);
		
	}
	
	@SuppressWarnings("deprecation")
	public void melee2(final Player p) {
		
		for(Player pla : Bukkit.getOnlinePlayers()) {
			pla.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1, 2);
		}
		
		PotionEffect potion = new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 10);
		p.addPotionEffect(potion, true);
		
		final ArmorStand as = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
		
		float yaw = p.getEyeLocation().getYaw();
		float pitch = p.getEyeLocation().getPitch();

		if(yaw-90 <-180) {
			yaw = (yaw-90) + 360;
		}		
		else {
			yaw = yaw-90;
		}
		
		final float yaw2 = yaw;
		final float pitch2 = pitch;

		SpellManager Spell = new SpellManager(p, 0.3);
		Spell.setHitBoxRange(2);
		Spell.setEntityPassable(false);
		Spell.setDamageRate(1);
		Spell.setKnockBack(as, 1);

		new BukkitRunnable() {
			
			float yaw1 = yaw2;
			float pitch1 = pitch2;
			int i=0;
			List<Entity> meleehit = new ArrayList<>();
			
			@Override
			public void run() {
				
				as.setInvisible(true);
				as.setInvulnerable(true);
				as.setSilent(true);		
				as.setRotation(yaw1, pitch1);

				if(i<=180) {
					if(yaw1>180) {
						yaw1-=360;
					}
					
					yaw1=yaw1+36;
					i += 36;
					
					Vector asdir = as.getEyeLocation().getDirection();
					Location asloc = as.getEyeLocation();
					asloc.add(0, -1, 0);
					asdir.normalize();
					asdir.multiply(0.3);

					for(int j=0; j<=15; j++) {
						for(Player pl : Bukkit.getOnlinePlayers()) {
							
							if(j==15) {
								pl.spawnParticle(Particle.SMOKE_NORMAL, asloc, 20, 0.5, 0.5, 0.5, 0.1, null);
							}
							else if(j==12) {
								pl.spawnParticle(Particle.BLOCK_CRACK, asloc, 20, 0.5, 0.5, 0.5, 0, Material.SEA_LANTERN.createBlockData());
							}
							else if(j==14) {
								pl.spawnParticle(Particle.SWEEP_ATTACK, asloc, 10, 0.5, 0.5, 0.5, 0, null);
							}
						}
						asloc.add(asdir);

						Spell.RunRadiusRange(SpellManager.MeleeOrSpell.Melee, asloc);

					}
					as.remove();
				}
				else {
					cancel();
				}
				
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
		
		PlayerFunction.getinstance(p).setMeleeDelay(40);
		
	}
	
	public void ImpulseSwitchShield(Player p, int mana) {

		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction PF = PlayerFunction.getinstance(p);
		
		
		int add = (int)((double)UserManager.getinstance(p).Health * 5/100 * ((PF.AEImpulse+100) / 100) * (UserManager.getinstance(p).Shield + 100) / 100);
		
		summonCircle4(p.getLocation(), 1);
		
		PlayerHealth.getinstance(p).ShieldAdd(add);
		
		
		double i = Double.parseDouble(String.format("%.2f", PF.AEImpulse/2));
		PF.AEImpulse = i;
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(p.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
		}
		
		for(Entity pl : p.getNearbyEntities(6, 6, 6)) { // 근처 아군
			if(pl instanceof Player && !entitycheck.duelcheck(pl, p)) {
				Player pla = (Player) pl;
				add = (int)((double)UserManager.getinstance(pla).Health * 5/100 * ((PF.AEImpulse+100) / 100) * (UserManager.getinstance(p).Shield + 100) / 100);
				
				PlayerHealth.getinstance(pla).ShieldAdd(add);
				
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
		
		
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction PF = PlayerFunction.getinstance(p);
		
		//final int spellrate = (int)(2 * (impulse.get(p)+100) / 200);
		
		final double spellrate = 2 * (PF.AEImpulse+100) / 200;
		
		if(PF.AEImpulse<300) {
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2);
			}
			laser(p, 2, 2, 10, spellrate);
		}
		if(PF.AEImpulse>=300 && PF.AEImpulse<600) {
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2);
			}
			laser(p, 2, 3, 10, spellrate);
		}
		if(PF.AEImpulse>=600) {
			
			new BukkitRunnable() {
				
				float i = 0;
				
				@Override
				public void run() {

					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, i);

					i+=0.1;
					
					if(i>=2) {
						for(Player pl : Bukkit.getOnlinePlayers()) {
							pl.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 2);
						}
						
						laser(p, 3, 3, 20, spellrate);
						cancel();
					}
				}
			}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
			
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
		Spell.setmultiplyDamage((double)PlayerHealth.getinstance(p).getCurrentShield() / (double)UserManager.getinstance(p).ShieldRaw);
		
		
		
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

					PlayerHealth.getinstance(p).setCurrentShield(PlayerHealth.getinstance(p).getCurrentShield() * 95 /100);
					PlayerHealth.getinstance(p).setShieldRegenerateStop();

					cancel();


				}
				duration ++;

			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
	}
	public void SwitchWeapon(Player p, int mana) {
		
		PlayerEnergy.getinstance(p).removeEnergy(mana);
		PlayerFunction PF = PlayerFunction.getinstance(p);
		if(PF.getMeleemode() == 0) {
			PF.setMeleemode(1);
		}
		else {
			PF.setMeleemode(0);
		}

		
		
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

		double i = Double.parseDouble(String.format("%.2f",PF.AEImpulse +(double)takendmg/(double)UserManager.getinstance(p).Health * 400));
		PF.AEImpulse = i;
		
		if(PF.AEImpulse > 1000) {
			PF.AEImpulse = 1000;
		}
	}
	
//	public void AetherPassive() {
//
//		PlayerFunction PF = PlayerFunction.getinstance(p);
//
//		for(Player p : Bukkit.getOnlinePlayers()) {
//			if(UserManager.getinstance(p).CurrentClass.equals("아이테르")) {
//				if(!impulse.containsKey(p)) impulse.put(p, 0d);
//			}
//			else {
//				if(impulse.containsKey(p)) impulse.remove(p);
//			}
//		}
//
//	}
	
	
	public void laser(Player p, double radius, int particlesize, int particleamount, double spellrate) {

		PlayerFunction PF = PlayerFunction.getinstance(p);


		//Bukkit.broadcastMessage(Double.toString(spellrate));

		Vector dir = p.getLocation().getDirection();
		Location loc = p.getEyeLocation();
		loc.add(0, -0.7, 0);		
		dir.normalize().multiply(0.4);

		SpellManager Spell = new SpellManager(p, loc, 0.4);
		Spell.setMaximumRange(20);
		Spell.setDamageRate(spellrate);
		Spell.setWallPassable(false);
		Spell.setEntityPassable(true);
		Spell.setHitBoxRange(radius);
		Spell.setKnockBack(p, 1.5);
		Spell.addDestinationSound(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1);
		Spell.addDestinationParticle(Particle.EXPLOSION_LARGE, 5, 1, 1, 1, 0, null);

		if(PF.AEImpulse <300)
			Spell.addTrailParticle(Particle.REDSTONE, particleamount, 0.1, 0.1, 0.1, 0.5, new Particle.DustOptions(Color.RED, particlesize));
		if(PF.AEImpulse >=300 && PF.AEImpulse <600) {
			Spell.addTrailParticle(Particle.REDSTONE, particleamount, 0.2, 0.2, 0.2, 0.5, new Particle.DustOptions(Color.RED, particlesize));
			Spell.addTrailParticle(Particle.REDSTONE, particleamount, 0.2, 0.2, 0.2, 100, new Particle.DustOptions(Color.BLACK, particlesize));
		}

		if(PF.AEImpulse >=600) {
			Spell.addTrailParticle(Particle.REDSTONE, particleamount, 0.3, 0.3, 0.3, 0.5, new Particle.DustOptions(Color.RED, particlesize));
			Spell.addTrailParticle(Particle.REDSTONE, particleamount, 0.3, 0.3, 0.3, 100, new Particle.DustOptions(Color.BLACK, particlesize));
		}

		if(Spell.RunRayCast(SpellManager.MeleeOrSpell.Spell)) {

		}

		PF.AEImpulse = 0;

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

}

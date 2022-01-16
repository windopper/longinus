package Mob.Gliese581cMobs;

import Mob.EntityManager;
import Mob.MobListManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;

public class GlowingButterfly extends EntityZombie {

    Location GlowLocation;
    Location location;
    Bee bee;
    

    public GlowingButterfly(EntityTypes<? extends EntityZombie> entitytypes, World world, Location location) {

        super(entitytypes, world);

        this.location = location;
        EntityBee bat = new EntityBee(EntityTypes.g, world);
        Bee bat1 = (Bee) bat.getBukkitEntity();

        bee = bat1;

        bat.setPosition(location.getX(), location.getY()+1, location.getZ());
        bat1.setSilent(true);
        bat1.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10, true, true));
        world.addEntity(bat);

        MobListManager.MobList mobList = MobListManager.MobList.발광_나비;;
        Zombie glowingbutterfly = (Zombie) this.getBukkitEntity();


        Summon(glowingbutterfly, location, mobList, bat1);
        glowingbutterfly.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 5, true, true));
        glowingbutterfly.setInvulnerable(true);
        glowingbutterfly.setCollidable(false);
        world.addEntity(this);

    }

    public void Summon(Zombie zombie, Location location, MobListManager.MobList mobList, Bee bat) {

        Method method = null;
        Method method1 = null;
        Method method2 = null;

        try {
            Class c = this.getClass();
            method = c.getMethod("particle");
            method1 = c.getMethod("glow");
            method2 = c.getMethod("deleteGlow");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        EntityManager.getinstance(bat, mobList)
                .setTeleportToEntity(zombie)
                .setTeleportToEntityLoc(new Location(location.getWorld(), 0, 1, 0))
                .setShowNameTag(true)
                .setAmbientParticle(method, this).setAmbientAbility(method1, this)
                        .setDeathAbility(method2, this);

        EntityManager.getinstance(zombie, mobList)
                .setShowNameTag(false);
    }

    @Override
    protected void initPathfinder() {

        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 1, true));
        this.bP.a(2, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));

        this.bQ.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityLiving.class, 10, false, false, (entityliving) -> {

            if(entityliving instanceof GlowingButterfly || entityliving instanceof EntityBee) {
                return false;
            }
            return true;

        }));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.aT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.aV;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.aU;
    }

    @Override
    protected SoundEffect getSoundStep() {
        return SoundEffects.aW;
    }

    public void particle() {

        Location loc = this.getBukkitEntity().getLocation().add(0, 1.4, 0);
        loc.getWorld().spawnParticle(Particle.GLOW, loc,1, 0, 0, 0, 0d);
    }

    public void glow() {

        if(GlowLocation != null) {
            GlowLocation.getWorld().setBlockData(GlowLocation, Material.AIR.createBlockData());
            GlowLocation = null;
        }

        GlowLocation = bee.getLocation();

        Light light = (Light) Material.LIGHT.createBlockData();
        light.setLevel(7);

        if(GlowLocation.getBlock().isEmpty()) {
            GlowLocation.getWorld().setBlockData(GlowLocation, light);
        }
    }

    public void deleteGlow() {
        if(GlowLocation != null) {
            GlowLocation.getWorld().setBlockData(GlowLocation, Material.AIR.createBlockData());
        }
    }
}

package Gliese581cMobs;

import Mob.EntityManager;
import Mob.GatherLocation;
import Mob.MobListManager;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.level.World;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.Method;

public class RageEagle extends EntityBee {

    Location location;
    Bee bee;
    Entity target = null;

    public RageEagle(EntityTypes<? extends EntityBee> entitytypes, World world, Location location) {
        super(entitytypes, world);

        this.location = location;
//        EntityBee bat = new EntityBee(EntityTypes.g, world);
//        Bee bat1 = (Bee) bat.getBukkitEntity();
//
//        bee = bat1;
//
//        bat.setPosition(location.getX(), location.getY()+1, location.getZ());
//        bat1.setSilent(true);
//        bee.setAnger(10);
//
//        world.addEntity(bat);

        MobListManager.MobList mobList = MobListManager.MobList.레이지_이글;
        Bee bee = (Bee) this.getBukkitEntity();
        bee.setHive(new Location(location.getWorld(), -319,105, 210));

        world.addEntity(this);

        Method method = null;
        Method method1 = null;

        try {
            Class c = this.getClass();
            method = c.getMethod("particle");
            method1 = c.getMethod("attackAbility");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        EntityManager.getinstance(bee, mobList)
                .setShowNameTag(true)
                .setAttackAbility(method1, this)
                .setAmbientAbility(method, this);

        this.bP.a(5, new GatherLocation(this, 1, 10, new Location(location.getWorld(), -319,105, 210)));

    }

//    public void Summon(Zombie zombie, Location location, MobListManager.MobList mobList, Bee bat) {
//
//        Method method = null;
//        Method method1 = null;
//        Method method2 = null;
//
//        try {
//            Class c = this.getClass();
//            method = c.getMethod("particle");
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
//
//        EntityManager.getinstance(bat, mobList)
//                .setTeleportToEntity(zombie)
//                .setTeleportToEntityLoc(new Location(location.getWorld(), 0, 1, 0))
//                .setShowNameTag(true)
//                .setAmbientParticle(method, this);
//
//        EntityManager.getinstance(zombie, mobList)
//                .setShowNameTag(false);
//    }

    @Override
    protected void initPathfinder() {

        super.initPathfinder();

//        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 1, true));
//        this.bP.a(2, new PathfinderGoalRandomStrollLand(this, 0.6D));
//        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
//        this.bP.a(4, new PathfinderGoalRandomLookaround(this));
//
//        this.bQ.a(0, new PathfinderGoalNearestAttackableTarget<>(this, EntityLiving.class, 10, false, false, (entityliving) -> {
//
//            if(entityliving instanceof GlowingButterfly || entityliving instanceof EntityBee) {
//                return false;
//            }
//            return true;
//
//        }));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.nC;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.nG;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.nE;
    }

    public void particle() {

        Location loc = this.getBukkitEntity().getLocation();
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc,3, 0.2, 0.2, 0.2, 0d, new Particle.DustOptions(Color.RED, 1));
        ((Bee) this.getBukkitEntity()).setHasStung(false);
        this.setHasStung(false);
    }

    public void attackAbility() {

        try {
            if(this.getGoalTarget().getBukkitEntity().getLocation().distance(this.getBukkitEntity().getLocation()) > 10) {
                this.setGoalTarget(null);
            }
        }
        catch(Exception e) {

        }
        if(this.getGoalTarget() != null) {
            this.setGoalTarget(this.getGoalTarget());
            return;
        }

        for(Entity entity : this.getBukkitEntity().getNearbyEntities(10, 10, 10)) {
            if(((CraftEntity) entity).getHandle() instanceof RageEagle) {
                continue;
            }
            if(entity instanceof ArmorStand) continue;
            if(entity.isInvulnerable()) continue;
            this.setGoalTarget((EntityLiving) ((CraftEntity) entity).getHandle(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY,
                        true);
            break;
        }

    }
}

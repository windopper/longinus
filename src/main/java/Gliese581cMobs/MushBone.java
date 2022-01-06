package Gliese581cMobs;

import Mob.EntityManager;
import Mob.MobListManager;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MushBone extends EntitySlime {

    private final Location loc;

    public MushBone(EntityTypes<? extends EntitySlime> entitytypes, World world, Location loc) {
        super(entitytypes, world);
        this.loc = loc;
        Slime mushBone = (Slime) this.getBukkitEntity();
        MobListManager.MobList moblist = MobListManager.MobList.머쉬본;
        mushBone.setInvisible(true);
        mushBone.setSilent(true);
        mushBone.setAI(false);

        FallingBlock fallingBlock = world.getWorld().spawnFallingBlock(loc, Material.RED_MUSHROOM.createBlockData());
        fallingBlock.setGravity(false);
        fallingBlock.setInvulnerable(true);
        fallingBlock.setHurtEntities(false);
        fallingBlock.setTicksLived(0);

        world.addEntity(this);
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        HashMap<Entity, Location> disguises = new HashMap<Entity, Location>();
        disguises.put(fallingBlock, loc);

        Method method = null;
        try {
            Class c = this.getClass();
            method = c.getMethod("AttackAmbientAbility");
        }
        catch(Exception e) {

        }

        EntityManager.getinstance(mushBone, moblist, disguises)
                .setAmbientAbility(method, this)
                .setShowNameTag(false);
    }

    public void AttackAmbientAbility() {
        if(EntityManager.getinstance(this.getBukkitEntity()).getPatterntime() % 100 == 0) {
            for(LivingEntity lE : loc.getWorld().getLivingEntities()) {
                if(lE.getLocation().distance(loc) < 4) {
                    Vector v = loc.toVector().subtract(lE.getLocation().toVector());
                    v.normalize().multiply(0.5);
                    loc.getWorld().spawnParticle(Particle.CRIMSON_SPORE, loc, 0);
                }
            }
        }
    }
}
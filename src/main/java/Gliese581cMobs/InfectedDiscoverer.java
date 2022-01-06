package Gliese581cMobs;

import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import Mob.MobListManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InfectedDiscoverer extends EntityZombie {

    public InfectedDiscoverer(EntityTypes<? extends EntityZombie> entitytypes, World world, Location loc, String texture, String signature) {

        super(entitytypes, world);
        MobListManager.MobList mobList = MobListManager.MobList.감염된_개척자;
        Zombie infDcr = (Zombie) this.getBukkitEntity();
        infDcr.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10));
        world.addEntity(this);
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        EntityPlayer eP = EntityPlayerManager.getInstance().dummyNetworkNPC(loc.getWorld(), loc, texture, signature);
        EntityPlayerWatcher.EntityWrapper(eP, infDcr, mobList)
                .setHeight(eP.getHeight())
                .setShowNameTag(true);
    }

    @Override
    public void initPathfinder() {

        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 1D, false));
        this.bP.a(1, new PathfinderGoalFloat(this));
        this.bP.a(2, new PathfinderGoalRandomStroll(this, 0.6D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));

//        this.bQ.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityLiving.class,10, true, true, entityliving -> {
//            if(((net.minecraft.world.entity.Entity) entityliving).isInvulnerable()) return false;
//            if(entityliving instanceof Parasite || entityliving instanceof InfectedDiscoverer) return false;
//            return true;
//        }));

        this.bQ.a(2, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true, true));

    }
}

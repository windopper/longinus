package Gliese581cMobs;

import DynamicData.EntityManager;
import Mob.MobListManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.entity.Zombie;

public class HiddenOasis extends EntityZombie {


    public HiddenOasis(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        MobListManager.MobList mobList = MobListManager.MobList.히든_오아시스;
        Zombie hiddenOasis = (Zombie) this.getBukkitEntity();

        IChatBaseComponent name = IChatBaseComponent.a(mobList.getName());
        this.setCustomName(name);
        this.setCustomNameVisible(true);
        this.setSilent(true);
        this.getWorld().addEntity(this);

        hiddenOasis.setInvisible(true);


//        FallingBlock fallingBlock = hiddenOasis.getWorld().spawnFallingBlock(hiddenOasis.getLocation().clone().add(0, 1, 0), Material.water.createBlockData());
//        fallingBlock.setHurtEntities(false);
//        fallingBlock.setSilent(true);
//        fallingBlock.setDropItem(false);
//        fallingBlock.setVelocity(new Vector(0, 1, 0));
//        fallingBlock.setInvulnerable(true);
//        fallingBlock.setGravity(false);
//
//        Location location = new Location(hiddenOasis.getWorld(), 0, 1, 0);
//        HashMap<Entity, Location> map = new HashMap<>();
//        map.put(fallingBlock, location);

        EntityManager.getinstance(hiddenOasis, mobList);
    }

    @Override
    public void initPathfinder() {

        this.bP.a(5, new PathfinderGoalZombieAttack(this, 1, false));
        this.bP.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.bP.a(10, new PathfinderGoalRandomLookaround(this));
        this.bQ.a(4, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, false));
    }

    @Override
    public SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.oA;
    }
}

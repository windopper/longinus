package Gliese581cMobs;

import DynamicData.EntityHealthManager;
import Mob.MobManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import org.bukkit.entity.Zombie;

public class MouseFoot extends EntityZombie {

    public MouseFoot(World world) {
        super(world);
        MobManager.MobList mobList = MobManager.MobList.마우스풋;
        Zombie craftZombie = (Zombie) this.getBukkitEntity();
        this.setBaby(false);
        craftZombie.setMaxHealth(1000);
        this.setHealth(1000);

        IChatBaseComponent name = IChatBaseComponent.a(MobManager.MobList.마우스풋.getName());
        this.setCustomName(name);
        this.setCustomNameVisible(true);
        this.getWorld().addEntity(this);

        EntityHealthManager.getinstance(craftZombie, mobList.getHealth());

    }

    @Override
    public void initPathfinder() {
        super.initPathfinder();

        this.bP.a(0, new PathfinderGoalRandomLookaround(this));
        //this.bP.a(1, new PathfinderGoalAvoidTarget<EntityPlayer>(this, EntityPlayer.class, 5, 5, 5));

        this.bQ.a(0, new PathfinderGoalNearestAttackableTarget<EntityPig>(this, EntityPig.class, true));
        this.bQ.a(1, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
        this.bQ.a(2, new PathfinderGoalLeapAtTarget(this, 1));

    }




}

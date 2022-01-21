package Mob.Gliese581cMobs;

import Mob.EntityManager;
import Mob.MobListManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class WolfFang extends EntityWolf {

    public WolfFang(EntityTypes<? extends EntityWolf> entitytypes, World world, Location loc) {
        super(entitytypes, world);
        Wolf wolf = (Wolf) this.getBukkitEntity();
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        world.addEntity(this);

        MobListManager.MobList mobList = MobListManager.MobList.울프팽;
        EntityManager.getinstance(wolf, mobList);
    }

    @Override
    public void initPathfinder() {
        this.bP.a(0, new PathfinderGoalFloat(this));
        this.bP.a(2, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.bP.a(3, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.bP.a(8, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.bP.a(9, new PathfinderGoalBeg(this, 8.0F));
        this.bP.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        //this.bQ.a(2, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[0]));
        this.bQ.a(4, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true, true));

    }
}

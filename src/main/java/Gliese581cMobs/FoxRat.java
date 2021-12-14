package Gliese581cMobs;

import DynamicData.EntityManager;
import Mob.MobListManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.level.World;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;

public class FoxRat extends EntityFox {

    Player player;

    public FoxRat(EntityTypes<? extends EntityFox> entitytypes, World world, Player player) {
        super(entitytypes, world);
        MobListManager.MobList mobList = MobListManager.MobList.폭스랫;
        Fox craftFox = (Fox) this.getBukkitEntity();

        IChatBaseComponent name = IChatBaseComponent.a(mobList.getName());
        this.setCustomName(name);
        this.setCustomNameVisible(true);
        this.getWorld().addEntity(this);

        EntityManager.getinstance(craftFox, mobList);
    }

    @Override
    public void initPathfinder() {

        this.bP.a(1, new PathfinderGoalMeleeAttack(this, 1.5, true));
        this.bP.a(2, new PathfinderGoalRandomStroll(this, 0.6D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));

        this.bQ.a(3, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true, true));

    }
}

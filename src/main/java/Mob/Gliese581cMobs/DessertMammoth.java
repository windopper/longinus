package Mob.Gliese581cMobs;

import Mob.EntityManager;
import Mob.MobListManager;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.entity.Hoglin;

public class DessertMammoth extends EntityHoglin {
    public DessertMammoth(EntityTypes<? extends EntityHoglin> var0, World var1, Location loc) {
        super(var0, var1);
        MobListManager.MobList mobList = MobListManager.MobList.데저트맘모스;
        Hoglin hoglin = (Hoglin) this.getBukkitEntity();
        var1.addEntity(this);


        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setImmuneToZombification(true);

        EntityManager.getinstance(hoglin, mobList)
                .setShowNameTag(true);
    }

    @Override
    public void initPathfinder() {
//        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 1D, false));
//        this.bP.a(1, new PathfinderGoalFloat(this));
//        this.bP.a(2, new PathfinderGoalRandomStroll(this, 0.6D));
//        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
//        this.bP.a(4, new PathfinderGoalRandomLookaround(this));
    }

    public void ambientAbility() {

    }
}

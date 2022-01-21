package Mob.Gliese581cMobs;

import Mob.EntityManager;
import Mob.EntityStatusManager;
import Mob.MobListManager;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.entity.Skeleton;

public class SanBag extends EntitySkeleton {

    public SanBag(EntityTypes<? extends EntitySkeleton> entitytypes, World world, Location loc) {
        super(entitytypes, world);
        Skeleton skeleton = (Skeleton) this.getBukkitEntity();
        skeleton.setAI(false);

        world.addEntity(this);
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());

        MobListManager.MobList mobList = MobListManager.MobList.샌드백;
        EntityManager.getinstance(skeleton, mobList);
        EntityStatusManager.getinstance(skeleton).setStatusTag(EntityStatusManager.statusTag.knockbackImmune);
    }
}

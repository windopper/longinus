package Mob.Gliese581cMobs;

import Mob.MobListManager;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityIllagerIllusioner;
import net.minecraft.world.level.World;

public class RedBloodRoot extends EntityIllagerIllusioner {


    public RedBloodRoot(EntityTypes<? extends EntityIllagerIllusioner> entitytypes, World world) {
        super(entitytypes, world);
        MobListManager.MobList mobList = MobListManager.MobList.레드_블러드_루트;
    }
}

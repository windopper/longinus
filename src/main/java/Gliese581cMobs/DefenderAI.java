package Gliese581cMobs;

import EntityPlayerManager.EntityPlayerWatcher;
import Mob.MobListManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.level.World;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DefenderAI extends EntityVillager {

    public DefenderAI(EntityTypes<? extends EntityVillager> entitytypes, World world, EntityPlayer entityPlayer) {
        super(entitytypes, world);

        Villager defenderai = (Villager) this.getBukkitEntity();
        MobListManager.MobList mobList = MobListManager.MobList.edison1304;

        defenderai.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10, true, true));
        defenderai.setCustomName(mobList.getName());
        defenderai.setCustomNameVisible(true);

        this.getWorld().addEntity(this);
        EntityPlayerWatcher.EntityWrapper(entityPlayer, defenderai, mobList);
    }

    @Override
    public void initPathfinder() {

    }
}

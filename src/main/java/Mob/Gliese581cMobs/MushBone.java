package Mob.Gliese581cMobs;

import Items.MaterialItemBuilder;
import Mob.EntityManager;
import Mob.MobListManager;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.level.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

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
        mushBone.setSize(1);

        FallingBlock fallingBlock = world.getWorld().spawnFallingBlock(loc, Material.RED_MUSHROOM.createBlockData());
        fallingBlock.setGravity(false);
        fallingBlock.setInvulnerable(true);
        fallingBlock.setHurtEntities(false);
        fallingBlock.setPersistent(true);
        fallingBlock.setDropItem(false);

        world.addEntity(this);
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        HashMap<Entity, Location> disguises = new HashMap<>();
        disguises.put(fallingBlock, new Location(loc.getWorld(), 0, 0, 0));

        Method method = null;
        try {
            Class c = this.getClass();
            method = c.getMethod("AttackAmbientAbility");
        }
        catch(Exception e) {

        }

        ItemStack drop1 = MaterialItemBuilder.getBuilder()
                .setMaterial(Material.BROWN_MUSHROOM)
                .setGrade(MaterialItemBuilder.GRADE.일반)
                .setName("손상된 머쉬본")
                .build();

        EntityManager.getinstance(mushBone, moblist, disguises)
                .setAmbientAbility(method, this)
                .addDropTable(drop1, MaterialItemBuilder.GRADE.일반.getPercent())
                .setShowNameTag(false);
    }

    public void AttackAmbientAbility() {

        EntityManager eM = EntityManager.getinstance(this.getBukkitEntity());
        boolean spout = false;

        if(eM.getPatterntime() % 100 == 0) {
            for(LivingEntity lE : loc.getWorld().getLivingEntities()) {
                net.minecraft.world.entity.Entity cE = ((CraftEntity) lE).getHandle();
                if(!(cE instanceof MushBone)) {
                    if(lE.getLocation().distance(loc) < 4) {
                        Vector v = loc.toVector().subtract(lE.getLocation().toVector());
                        v.normalize().multiply(0.5);
                        this.attackEntity(((CraftEntity) lE).getHandle());
                        if(!spout) {
                            this.getBukkitEntity().getWorld().playSound(this.getBukkitEntity().getLocation(), Sound.ENTITY_GENERIC_BURN, 1, 2);
                            loc.getWorld().spawnParticle(Particle.CRIMSON_SPORE, loc, 200, 0, 0, 0, 1);
                        }

                        spout = true;
                    }
                }
            }
        }
    }

    public void dropTable(List<Player> playerList) {

        ItemStack itemStack = MaterialItemBuilder.getBuilder()
                .setMaterial(Material.BROWN_MUSHROOM)
                .setGrade(MaterialItemBuilder.GRADE.일반)
                .setName("손상된 머쉬본")
                .build();

        for(Player player : playerList) {
            if(Math.random() <= MaterialItemBuilder.GRADE.일반.getPercent()) {
                Item item = (Item) this.getBukkitEntity().getWorld().spawnEntity(this.getBukkitEntity().getLocation(), EntityType.DROPPED_ITEM);
                item.setItemStack(itemStack);
                item.setOwner(player.getUniqueId());
            }
        }
    }
}
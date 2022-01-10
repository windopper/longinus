package Gliese581cMobs;

import EntityPlayerManager.EntityPlayerManager;
import EntityPlayerManager.EntityPlayerWatcher;
import Mob.EntityFunctions;
import Mob.MobListManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Villager;

import java.lang.reflect.Field;
import java.util.Map;

public class Defender extends EntityVillager {

    public Defender(EntityTypes<? extends EntityVillager> entitytypes, net.minecraft.world.level.World world, Location loc) {
        super(entitytypes, world);

        Villager defenderai = (Villager) this.getBukkitEntity();
        MobListManager.MobList mobList = MobListManager.MobList.edison1304;

        //defenderai.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10, true, true));
        defenderai.setCustomName(mobList.getName());
        defenderai.setCustomNameVisible(true);

        Field attributeField = null;

        try {
            attributeField = AttributeMapBase.class.getDeclaredField("b");
            attributeField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            AttributeMapBase attributeMapBase = ((CraftLivingEntity) defenderai).getHandle().getAttributeMap();
            Map<AttributeBase, AttributeModifiable> map = (Map<AttributeBase, AttributeModifiable>) attributeField.get(attributeMapBase);
            AttributeBase attributeBase = CraftAttributeMap.toMinecraft(Attribute.GENERIC_ATTACK_DAMAGE);
            AttributeModifiable attributeModifiable = new AttributeModifiable(attributeBase, AttributeModifiable::getAttribute);
            map.put(attributeBase, attributeModifiable);
        }
        catch(Exception e) {

        }
        //this.w().a(GenericAttributes.f, 1).a(GenericAttributes.g, 1).a(GenericAttributes.h, 1);
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.getWorld().addEntity(this);

        EntityFunctions.hideFromPlayer(defenderai);


        EntityPlayer entityPlayer = EntityPlayerManager.getInstance().dummyNetworkNPC(loc.getWorld(), loc, "ewogICJ0aW1lc3RhbXAiIDogMTYxNzk1MTkyNzUzOCwKICAicHJvZmlsZUlkIiA6ICI3MzlkYzg4OThmODg0OTRmOGNkNDE4NDI4NjUxNzBkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZGlzb24xMzA0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM4MjYwNjkzNjQwYzIxYWJjZDNiYTQwYjI4N2ZmMzhiMGIxNGMyYTljM2FlMmFlOGVhYmIwZTNiNDU3YzJiMmUiCiAgICB9CiAgfQp9"
                , "N0/34BBcnfe8x2gF8xwdhQ1fpFuU5bT1y3/uh0NsSja0UwTblI0qK0UzF7EPpye+O+ZrbVAp82DuDioC6LH/Al0dQRqUFRETgQuJRSJRNavpgikDCKE7TRqFclMELvxQ5xika0HpoR6+bI80H82+9H+4ePrhL8W9JVacCDiq9m8/TEG9SlUKsHxbg0cjXKi7xfOouk6LvIZl68PtdZlkVCmOzgTDZgX3fJ6lXjl0gSmu+afLZ7bKumoKBFWYddacwlBLIqnuxHK+byd9wb5Kg45Lle0CH2edcNxVydcPgEG9wSwf8aHJbryQQFtJMjRooZQgGBn/aFFM7hpo+CuG7w2B2kZ6YPMyTzRhJoEvJDdyeweAPssyTqTkLn32/cJ2Mot18PJHJSnekp/CFJaqIKRbGkNBkYZIzuy/IuC5noAftI41J4Ty3IumEIeLyRRD4w2Bh68pIBSwOe5rxmGrkF4USfumdejUtHo4C6AxhQ/N9kbvv6Yn/Z8+wX7srIhDuqBtYBv/31q30G/cGI0aq3MIFR2dueTNO3Oj2+4XVlp7Dpz5g2K5Cg0UhS8xdHsj38SjLJA+TWaT9fnXAkBUnNLoQ8McXUOe9WKwqleDmSszQiMBR66t3zWE17XeGvOznIMYmBfW//GN1VYQPNjUyGr3T2vPgmQF8AMV72YlLYQ=");

        EntityPlayerWatcher.EntityWrapper(entityPlayer, defenderai, mobList)
                .setHeight(entityPlayer.getHeight())
                .setShowNameTag(true);
    }

    @Override
    protected void initPathfinder() {

        //super.initPathfinder();
        this.bP.a(0, new PathfinderGoalMeleeAttack(this, 0.7, true));
        this.bP.a(2, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F));
        this.bP.a(4, new PathfinderGoalRandomLookaround(this));

        this.bQ.a(0, new PathfinderGoalNearestAttackableTarget(this, EntityLiving.class, 10, false, false, (entityliving) -> {

            if(entityliving instanceof EntityHuman || entityliving instanceof Defender) {
                return false;
            }
            return true;

//            if(((EntityLiving) entityliving).getBukkitEntity().getHandle() instanceof LivingEntity) {
//                LivingEntity lE = (LivingEntity) ((EntityLiving) entityliving).getBukkitEntity().getHandle();
//                MobListManager.MobList mobList = EntityManager.getinstance(lE).getMobList();
//                if(mobList == null) return true;
//                else {
//                    String name = mobList.getName();
//                    if(Arrays.stream(MobListManager.MobList.values()).filter(value -> name.equals(value.getName()))
//                            .filter(MobListManager.MobList::isScannable).toArray().length >= 1) return true;
//
//                }
//            }
//            return false;
        }));

    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ot;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.os;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.fi;
    }


//    public static EntityPlayer createNPC(Player p, World world , Location location) {
//
//        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
//        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
//        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
//        DummyEntityPlayer entityPlayer = new DummyEntityPlayer(nmsServer, nmsWorld, profile);
//        entityPlayer.setInvulnerable(true);
//
//        entityPlayer.b = new PlayerConnection(nmsServer, new DummyNetworkManager(EnumProtocolDirection.a), entityPlayer);
//
//        (new EntityPlayerSummon()).dummyNetworkNPC(world, location, "ewogICJ0aW1lc3RhbXAiIDogMTYxNzk1MTkyNzUzOCwKICAicHJvZmlsZUlkIiA6ICI3MzlkYzg4OThmODg0OTRmOGNkNDE4NDI4NjUxNzBkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJlZGlzb24xMzA0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM4MjYwNjkzNjQwYzIxYWJjZDNiYTQwYjI4N2ZmMzhiMGIxNGMyYTljM2FlMmFlOGVhYmIwZTNiNDU3YzJiMmUiCiAgICB9CiAgfQp9"
//                , "N0/34BBcnfe8x2gF8xwdhQ1fpFuU5bT1y3/uh0NsSja0UwTblI0qK0UzF7EPpye+O+ZrbVAp82DuDioC6LH/Al0dQRqUFRETgQuJRSJRNavpgikDCKE7TRqFclMELvxQ5xika0HpoR6+bI80H82+9H+4ePrhL8W9JVacCDiq9m8/TEG9SlUKsHxbg0cjXKi7xfOouk6LvIZl68PtdZlkVCmOzgTDZgX3fJ6lXjl0gSmu+afLZ7bKumoKBFWYddacwlBLIqnuxHK+byd9wb5Kg45Lle0CH2edcNxVydcPgEG9wSwf8aHJbryQQFtJMjRooZQgGBn/aFFM7hpo+CuG7w2B2kZ6YPMyTzRhJoEvJDdyeweAPssyTqTkLn32/cJ2Mot18PJHJSnekp/CFJaqIKRbGkNBkYZIzuy/IuC5noAftI41J4Ty3IumEIeLyRRD4w2Bh68pIBSwOe5rxmGrkF4USfumdejUtHo4C6AxhQ/N9kbvv6Yn/Z8+wX7srIhDuqBtYBv/31q30G/cGI0aq3MIFR2dueTNO3Oj2+4XVlp7Dpz5g2K5Cg0UhS8xdHsj38SjLJA+TWaT9fnXAkBUnNLoQ8McXUOe9WKwqleDmSszQiMBR66t3zWE17XeGvOznIMYmBfW//GN1VYQPNjUyGr3T2vPgmQF8AMV72YlLYQ=");
//
//        //Defender defender = new Defender(EntityTypes.aV, entityPlayer.getWorld(), entityPlayer);
//        //defender.setPosition(location.getX(), location.getY(), location.getZ());
//
//        return entityPlayer;
//    }
}

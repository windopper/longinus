package Gliese581cMobs;

import DynamicData.EntityManager;
import DynamicData.EntityStatusManager;
import Mob.MobListManager;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.level.World;
import org.bukkit.*;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BloodRoot extends EntitySkeleton {


    public BloodRoot(EntityTypes<? extends EntitySkeleton> entitytypes, World world) {
        super(entitytypes, world);
        Skeleton bloodroot = (Skeleton) this.getBukkitEntity();
        MobListManager.MobList mobList = MobListManager.MobList.블러드루트;

        IChatBaseComponent name = IChatBaseComponent.a(mobList.getName());
        this.setCustomName(name);
        this.setCustomNameVisible(true);
        this.getWorld().addEntity(this);
        this.setSilent(true);
        this.setInvulnerable(true);
        this.addEffect(new MobEffect(MobEffectList.fromId(2), 99999, 100 ,true, true));
        EntityManager.getinstance(bloodroot, mobList);
        EntityStatusManager.getinstance(bloodroot).setCanKnockback(false);
    }

    @Override
    public void initPathfinder() {

        //this.bP.a(1, new PathfinderGoalArrowAttack(this, 1, 2, 1));
        this.bP.a(3, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 8.0F, 3, false));

        //this.bQ.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.bQ.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityPlayer.class, true));
    }

    public static void BloodRootSkill1(LivingEntity entity, final Player target) {

        Location location = entity.getLocation();
        Location targetlocation = target.getLocation();

        final org.bukkit.World world = location.getWorld();
        final Vector vector = targetlocation.toVector().subtract(location.toVector()).normalize().multiply(0.5);
        vector.multiply(new Vector(1, 0, 1));

        new BukkitRunnable() {

            double distance = location.distance(targetlocation);
            int count = 0;

            boolean attacked = false;
            int reachcount = 0;

            Location dripstone1;
            Location dripstone2;

            Boolean dripstone1isAir = false;
            Boolean dripstone2isAir = false;

            @Override
            public void run() {

                if(((distance < 1 && reachcount == 0) || location.distance(target.getLocation()) < 1) && !attacked) {

                    world.spawnParticle(Particle.GLOW, location, 30, 0.5, 0.5, 0.5, 0);
                    world.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 0);

                    if(location.distance(target.getLocation()) < 1) {

                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), true);

                        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                            Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0.1));
                        }, 0);

                    }

                    dripstone1 = location.clone().add(0, 0, 0);
                    dripstone2 = location.clone().add(0, 1, 0);
                    if(dripstone1.getBlock().isEmpty()) {

                        PointedDripstone frustum = (PointedDripstone) Material.POINTED_DRIPSTONE.createBlockData();
                        frustum.setThickness(PointedDripstone.Thickness.FRUSTUM);
                        world.setBlockData(dripstone1, frustum);
                        dripstone1isAir = true;

                    }
                    if(dripstone2.getBlock().isEmpty()) {

                        PointedDripstone tip = (PointedDripstone) Material.POINTED_DRIPSTONE.createBlockData();
                        tip.setThickness(PointedDripstone.Thickness.FRUSTUM);
                        world.setBlockData(dripstone2, tip);
                        dripstone2isAir = true;
                    }

                    attacked = !attacked;
                    reachcount = 1;
                }
                if(reachcount == 20) {
                    if(dripstone1isAir == true) {
                        world.setBlockData(dripstone1, Material.AIR.createBlockData());
                    }
                    if(dripstone2isAir == true) {
                        world.setBlockData(dripstone2, Material.AIR.createBlockData());
                    }
                    cancel();
                }
                if(count==80) {
                    cancel();
                }
                if(reachcount >= 1) {
                    reachcount ++;
                }
                else {
                    world.playSound(location, Sound.BLOCK_STONE_BREAK, 1, 1);
                    world.spawnParticle(Particle.BLOCK_CRACK, location, 7, 0.25, 0.25, 0.25, 0, location.clone().add(0, -1, 0).getBlock().getType().createBlockData());
                    world.spawnParticle(Particle.REDSTONE, location, 7, 0.25, 0.25, 0.25, 0, new Particle.DustOptions(Color.fromRGB(139, 69, 19), 2));
                    location.add(vector);
                    count++;
                }

                distance = location.distance(targetlocation);
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);



    }
}

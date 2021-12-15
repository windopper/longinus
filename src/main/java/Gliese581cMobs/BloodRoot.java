package Gliese581cMobs;

import DynamicData.EntityManager;
import DynamicData.EntityStatusManager;
import Mob.MobListManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.level.World;
import org.bukkit.*;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BloodRoot extends EntitySkeleton {

    public BloodRoot(EntityTypes<? extends EntitySkeleton> entitytypes, World world) {
        super(entitytypes, world);
        Skeleton bloodroot = (Skeleton) this.getBukkitEntity();
        MobListManager.MobList mobList = MobListManager.MobList.블러드루트;

        this.getWorld().addEntity(this);
        this.setSilent(true);
        this.setInvulnerable(true);
        bloodroot.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 100, true));
        bloodroot.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 100, true));
        bloodroot.setCustomName(mobList.getName());
        bloodroot.setCustomNameVisible(true);
        bloodroot.setCollidable(false);

        FallingBlock fallingBlock = bloodroot.getWorld().spawnFallingBlock(bloodroot.getLocation().clone().add(0, 0, 0), Material.CACTUS.createBlockData());
        FallingBlock fallingBlock2 = bloodroot.getWorld().spawnFallingBlock(bloodroot.getLocation().clone().add(0, 1, 0), Material.CACTUS.createBlockData());
        fallingBlock.setHurtEntities(false);
        fallingBlock.setSilent(true);
        fallingBlock.setPersistent(true);
        fallingBlock.setDropItem(false);
        fallingBlock.setInvulnerable(true);
        fallingBlock.setGravity(false);

        fallingBlock2.setHurtEntities(false);
        fallingBlock2.setSilent(true);
        fallingBlock2.setDropItem(false);
        fallingBlock.setPersistent(true);
        fallingBlock2.setInvulnerable(true);
        fallingBlock2.setGravity(false);

        Location location = new Location(bloodroot.getWorld(), 0, 0, 0);
        HashMap<Entity, Location> map = new HashMap<>();
        map.put(fallingBlock, location);
        map.put(fallingBlock2, location.clone().add(0, 1, 0));

        EntityManager.getinstance(bloodroot, mobList, map);
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

                if(((distance < 1.5 && reachcount == 0) || location.distance(target.getLocation()) < 1.5) || count == 40) {

                    world.spawnParticle(Particle.GLOW, location, 30, 0.5, 0.5, 0.5, 0);
                    world.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 0);

                    if(location.distance(target.getLocation()) < 1.5) {

                        for(Entity le : location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5 )) {
                            if(le instanceof Player) {
                                Player targets = (Player) le;

                                targets.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 5), true);

                                Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                                    Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(entity, targets, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0.1));
                                    EntityManager.getinstance(entity).addHealth(EntityManager.getinstance(entity).getMaxHealth()/10);

                                }, 0);
                            }
                        }

                    }

                    dripstone1 = location.clone().add(0, 0, 0);
                    dripstone2 = location.clone().add(0, 1, 0);


                    boolean isCactusNearby = false;
                    boolean isBloodRootNearby = false;

                    if(dripstone1.clone().add(1, 0, 0).getBlock().getType().equals(Material.CACTUS)) isCactusNearby = true;
                    if(dripstone1.clone().add(0, 0, 1).getBlock().getType().equals(Material.CACTUS)) isCactusNearby = true;
                    if(dripstone1.clone().add(0, 0, -1).getBlock().getType().equals(Material.CACTUS)) isCactusNearby = true;
                    if(dripstone1.clone().add(-1, 0, 0).getBlock().getType().equals(Material.CACTUS)) isCactusNearby = true;

                    if(dripstone1.getBlock().isEmpty() && !isCactusNearby) {

                        PointedDripstone frustum = (PointedDripstone) Material.POINTED_DRIPSTONE.createBlockData();
                        frustum.setThickness(PointedDripstone.Thickness.FRUSTUM);
                        world.setBlockData(dripstone1, frustum);
                        dripstone1isAir = true;

                    }
                    if(dripstone2.getBlock().isEmpty() && !isCactusNearby) {

                        PointedDripstone tip = (PointedDripstone) Material.POINTED_DRIPSTONE.createBlockData();
                        tip.setThickness(PointedDripstone.Thickness.FRUSTUM);
                        world.setBlockData(dripstone2, tip);
                        dripstone2isAir = true;
                    }


                    Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
                        if(dripstone1isAir == true) {
                            world.setBlockData(dripstone1, Material.AIR.createBlockData());
                        }
                        if(dripstone2isAir == true) {
                            world.setBlockData(dripstone2, Material.AIR.createBlockData());
                        }
                    }, 20);

                    cancel();


                }

                world.playSound(location, Sound.BLOCK_STONE_BREAK, 1, 1);
                world.spawnParticle(Particle.BLOCK_CRACK, location, 7, 0.25, 0.25, 0.25, 0, location.clone().add(0, -1, 0).getBlock().getType().createBlockData());
                world.spawnParticle(Particle.REDSTONE, location, 7, 0.25, 0.25, 0.25, 0, new Particle.DustOptions(Color.fromRGB(139, 69, 19), 2));

                if(!location.getBlock().isLiquid() && !location.clone().getBlock().isEmpty() && !location.getBlock().getType().equals(Material.POINTED_DRIPSTONE))
                    location.add(0, 1, 0);
                else if(location.clone().add(0, -1, 0).getBlock().isEmpty())
                    location.add(0, -1, 0);
                else
                    location.add(vector);

                count++;
                distance = location.distance(targetlocation);
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }
}
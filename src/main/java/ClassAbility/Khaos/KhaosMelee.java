package ClassAbility.Khaos;

import ClassAbility.entitycheck;
import DynamicData.Damage;
import Mob.EntityStatusManager;
import PlayParticle.Rotate;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static PlayParticle.Rotate.transform;

public class KhaosMelee {

    private final static ConcurrentHashMap<Player, ArmorStand> throwns = new ConcurrentHashMap<>();

    private Player player;
    private PlayerFunction playerFunction;

    public KhaosMelee(Player player) {
        this.player = player;
        playerFunction = PlayerFunction.getinstance(player);
    }

    public static ArmorStand getThrown(Player player) {
        if(throwns.containsKey(player)) return throwns.get(player);
        return null;
    }

    public void Melee(String combo) {

        int MeleeCombo = playerFunction.getMeleeCombo();

        if(playerFunction.getMeleeDelay() != 0) return;

        if(MeleeCombo == 1) {

            if(combo.equals("L")) {
                Horizon();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }
            if(combo.equals("SHIFTL")) {
                throwns.remove(player);
            }
            if(combo.equals("R")) {
                ThrowDagger();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }


        }
        else if(MeleeCombo == 2) {

            if(combo.equals("L")) {
                TripleHit();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(10);
            }
            if(combo.equals("R")) {
                if(throwns.containsKey(player)) {

                }
                else ThrowDagger();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }


        }
        else if(MeleeCombo == 3) {

            if(combo.equals("R")) {
                if(throwns.containsKey(player)) {

                }
                else ThrowDagger();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }

        }
        else if(MeleeCombo == 4) {

        }
        else if(MeleeCombo == 5) {

        }
    }

    public void Horizon() {

        final Location loc = player.getEyeLocation().add(0, -0.5, 0);
        final List<Entity> Hit = new ArrayList<>();

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

        new BukkitRunnable() {

            double rpitch = Math.toRadians(player.getLocation().getPitch());
            double ryaw = Math.toRadians(player.getLocation().getYaw());
            double rroll = Math.toRadians(Math.random() * 30 - 15);
            int time = 0;
            double angle = PlayerFunction.getinstance(player).getMeleeRot() ? -60 : 60;

            double x = 0;
            double y = 0;
            double z = 0;

            @Override
            public void run() {

                for(int i=0; i<5; i++) {

                    for(double k = 2; k<4; k+=0.2) {

                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angle);
                        double yaxiscos = Math.cos(yangle);
                        double yaxissin = Math.sin(yangle);

                        Vector v = new Vector(x, y, z);
                        v = Rotate.rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, ryaw, rpitch, rroll);

                        loc.add(v);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.ASH, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, new Particle.DustOptions(Color.WHITE, 1));

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    EntityStatusManager.getinstance(entity).KnockBack(player, 0.5);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }
                        loc.subtract(v);



                    }
                    angle = angle + (PlayerFunction.getinstance(player).getMeleeRot() ? 6 : -6);
                }

                if(time>2) cancel();
                time++;
            }

        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);


    }

    public void TripleHit() {

        final Location loc = player.getEyeLocation().add(0, -0.5, 0);

        double r1 = Math.random() * 80 - 20;
        double r2 = Math.random() * 80 + 100;
        double r3 = Math.random() * 80 + 220;

        KhaosOneofTripleHit(Math.toRadians(r1), loc);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () ->
                KhaosOneofTripleHit(Math.toRadians(r2), loc), 2);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () ->
                KhaosOneofTripleHit(Math.toRadians(r3), loc), 4);


    }

    public void Vertical() {

    }

    public void BlinkHit() {

    }

    public void ThrowDagger() {

        if(throwns.containsKey(player)) {
            throwns.get(player).remove();
        }
        throwns.remove(player);

        Location loc = player.getEyeLocation();

        player.getWorld().playSound(loc, Sound.ITEM_TRIDENT_RIPTIDE_2, 1, 1);


        final List<Entity> Hit = new ArrayList<>();
        Location yawloc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw() + 90f, loc.getPitch());

        ArmorStand dagger = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0, -0.5, 0), EntityType.ARMOR_STAND);
        dagger.setInvisible(true);
        dagger.setSilent(true);
        //dagger.setMarker(true);
        dagger.setInvulnerable(true);
        dagger.setArms(true);
        dagger.setSmall(true);
        //dagger.setGravity(false);
        dagger.getEquipment().setItem(EquipmentSlot.HAND, new ItemStack(Material.NETHERITE_SWORD));
        dagger.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        dagger.setRightArmPose(new EulerAngle(Math.toRadians(165f), Math.toRadians(180f-loc.getPitch()), Math.toRadians(90f)));

        throwns.put(player, dagger);

        final Vector vector = loc.getDirection().normalize().multiply(2);

        new BukkitRunnable() {

            int t = 0;

            @Override
            public void run() {

                if(t<11) {
                    dagger.setVelocity(vector);
                    if(dagger.getEyeLocation().add(vector.clone().normalize().multiply(0.5)).getBlock().getType().isSolid()) t = 11;


                    player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, dagger.getEyeLocation().add(0, 0.25, 0), 0, vector.clone().multiply(-1).getX()
                            , vector.clone().multiply(-1).getY(), vector.clone().multiply(-1).getZ(), 0.5f);

                    for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                        if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                            Location eloc = entity.getEyeLocation();
                            BoundingBox box = entity.getBoundingBox();
                            if(eloc.distance(dagger.getEyeLocation()) < 1.5 || box.contains(dagger.getEyeLocation().getX()
                                    , dagger.getEyeLocation().getY(), dagger.getEyeLocation().getZ())) {
                                int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                Damage.getinstance().taken(dmg, entity, player);
                                EntityStatusManager.getinstance(entity).KnockBack(dagger, 0.5);
                                Hit.add(entity);
                                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                t = 11;
                                break;
                            }
                        }
                    }
                }

                if(t==11) {
                    if(dagger.getEyeLocation().add(vector.clone().normalize().multiply(0.5)).getBlock().getType().isSolid()) {
                        dagger.teleport(dagger.getEyeLocation().subtract(vector.clone().normalize().multiply(0.5)));
                    }
                    dagger.setVelocity(new Vector(0, 0, 0));
                    dagger.setGravity(false);
                    dagger.remove();

                    ArmorStand marker = (ArmorStand) player.getWorld().spawnEntity(dagger.getLocation(), EntityType.ARMOR_STAND);
                    marker.setMarker(true);
                    marker.setSmall(true);
                    marker.setGravity(false);
                    marker.setInvulnerable(true);
                    marker.setInvisible(true);
                    throwns.replace(player, marker);
                }

                if(t>11) {
                    ArmorStand marker = throwns.get(player);

                    Location mloc = marker.getLocation();

                    double x = 1.5 * Math.cos((double)t * Math.PI / 16);
                    double y = 0;
                    double z = 1.5 * Math.sin((double)t * Math.PI / 16);

                    Vector v = new Vector(x, y, z);

                    player.getWorld().spawnParticle(Particle.CLOUD, mloc.clone().add(v), 1, 0, 0, 0, 0);
                    v = Rotate.rotateAroundAxisX(v, Math.cos(Math.toRadians(45)), Math.sin(Math.toRadians(45)));
                    player.getWorld().spawnParticle(Particle.CLOUD, mloc.clone().add(v), 1, 0, 0, 0, 0);
                    v = Rotate.rotateAroundAxisX(v, Math.cos(Math.toRadians(-90)), Math.sin(Math.toRadians(-90)));
                    player.getWorld().spawnParticle(Particle.CLOUD, mloc.clone().add(v), 1, 0, 0, 0, 0);

                }


                if(!throwns.containsKey(player)) cancel();
                t++;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"),0, 1);


    }

    public void TeleportToDagger() {

    }

    public void SpiritChain() {

    }

    public void DaggerInterruption() {

    }

    private void KhaosOneofTripleHit(Double roll, Location loc) {

        final List<Entity> Hit = new ArrayList<>();

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);

        new BukkitRunnable() {

            double rpitch = Math.toRadians(player.getLocation().getPitch());
            double ryaw = Math.toRadians(player.getLocation().getYaw());
            int time = 0;
            double angle = PlayerFunction.getinstance(player).getMeleeRot() ? -60 : 60;

            double x = 0;
            double y = 0;
            double z = 0;

            @Override
            public void run() {

                for(int i=0; i<5; i++) {

                    for(double k = 2; k<4; k+=0.2) {

                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angle);
                        double yaxiscos = Math.cos(yangle);
                        double yaxissin = Math.sin(yangle);

                        Vector v = new Vector(x, y, z);
                        v = Rotate.rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, ryaw, rpitch, roll);

                        loc.add(v);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.ASH, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, new Particle.DustOptions(Color.WHITE, 1));

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    EntityStatusManager.getinstance(entity).KnockBack(player, 0.5);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }
                        loc.subtract(v);



                    }
                    angle = angle + (PlayerFunction.getinstance(player).getMeleeRot() ? 6 : -6);
                }

                if(time>2) cancel();
                time++;
            }

        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }


}

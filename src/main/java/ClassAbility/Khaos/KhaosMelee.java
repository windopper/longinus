package ClassAbility.Khaos;

import ClassAbility.entitycheck;
import DynamicData.Damage;
import DynamicData.targetBuilder;
import Mob.EntityStatusManager;
import PlayParticle.Rotate;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import PlayerManager.PlayerHealthShield;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static PlayParticle.Rotate.*;

public class KhaosMelee {

    private final static ConcurrentHashMap<Player, ArmorStand> throwns = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Player, BukkitRunnable> tasks = new ConcurrentHashMap<>();

    private Player player;
    private PlayerFunction playerFunction;
    private PlayerManager pm;
    private final targetBuilder tb;

    public KhaosMelee(Player player) {
        this.player = player;
        playerFunction = PlayerFunction.getinstance(player);
        pm = PlayerManager.getinstance(player);
        tb = targetBuilder.builder(player);
    }

    public static ArmorStand getThrown(Player player) {
        if(throwns.containsKey(player)) return throwns.get(player);
        return null;
    }

    public void Melee(String combo) {

        if(pm.getTalent("RR", 3) == 3) {
            if(pm.dummyCount.contains("KHRRtIII3")) {
                tb.addwhenHit(() -> {
                    PlayerHealthShield.getinstance(player).HealthAdd((int) ((double)pm.Health / 100), player);
                });
            }
        }

        int MeleeCombo = playerFunction.getMeleeCombo();

        if(playerFunction.getMeleeDelay() != 0) return;

        if(MeleeCombo == 1) {

            if(combo.equals("L")) {
                Horizon();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }
            if(combo.equals("SHIFTL")) {
                ThrowDagger();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }
//            if(combo.equals("R")) {
//
//            }


        }
        else if(MeleeCombo == 2) {

            if(combo.equals("L")) {
                TripleHit();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(10);
            }
//            else if(combo.equals("R")) {
//                if(throwns.containsKey(player)) {
//
//                }
//                else ThrowDagger();
//                playerFunction.addMeleeCombo();
//                playerFunction.setMeleeDelay(5);
//            }
            else if(combo.equals("SHIFTL")) {
                Vertical();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }
        }
        else if(MeleeCombo == 3) {
            if(combo.equals("L")) {
                BlinkHit();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(10);
            }
//            else if(combo.equals("R")) {
//                if(throwns.containsKey(player)) {
//
//                }
//                else ThrowDagger();
//                playerFunction.addMeleeCombo();
//                playerFunction.setMeleeDelay(5);
//            }
            else if(combo.equals("SHIFTL")) {
                LeapVertical();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }
        }
        else if(MeleeCombo == 4) {
            if(combo.equals("L")) {
                SweepAround();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(10);
            }
        }
        else if(MeleeCombo == 5) {

        }
    }

    public void Horizon() {

        final Location loc = player.getEyeLocation().add(0, -0.5, 0);
        final Set<Entity> Hit = new HashSet<>();

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

        tb.setRadius(1.5)
                .setLocation(loc)
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(player, 0.5))
                .setDamage(() -> PlayerManager.getinstance(player).meleedmgcalculate(player, 1));

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

                        tb.setLocation(loc).build();

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

        final Location loc = player.getEyeLocation().add(0, -0.5, 0);
        final Set<Entity> Hit = new HashSet<>();

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

        tb.setRadius(1.5)
                .setLocation(loc)
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(new Vector(0, 0.3, 0)))
                .setDamage(() -> PlayerManager.getinstance(player).meleedmgcalculate(player, 1));

        new BukkitRunnable() {

            double rpitch = Math.toRadians(player.getLocation().getPitch());
            double ryaw = Math.toRadians(player.getLocation().getYaw());
            double rroll = Math.toRadians(Math.random() * 40 + 70);
            int time = 0;
            double angle = -30;

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

                        tb.setLocation(loc).build();

                        loc.subtract(v);
                    }
                    angle = angle + 6;
                }

                if(time>2) cancel();
                time++;
            }

        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

    public void BlinkHit() {

        Location loc = player.getEyeLocation();
        Location location = player.getLocation();
        Vector dir = loc.getDirection().normalize();

        tb.setRadius(1.5)
                .setLocation(loc)
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(new Vector(0, 0.3, 0)))
                .setDamage(() -> PlayerManager.getinstance(player).meleedmgcalculate(player, 1));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 2);

        double dirmultiply = 0.2;

        for(double i=0.2; i<10; i+=0.2) {
            dirmultiply = i;
            if(loc.clone().add(dir.clone().multiply(i)).getBlock().getType().isSolid()) {
                dirmultiply -= 1;
                break;
            }
        }

        for(double i = 0.5; i<dirmultiply; i+= 0.5) {

            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc.add(dir.clone().multiply(i)),
                    2, 0, 0, 0, 0);
            player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc,
                    10, 0.1, 0.1, 0.1, 0);



            tb.setLocation(loc).build();
            loc.subtract(dir.clone().multiply(i));

        }

        dir.multiply(dirmultiply);
        loc.add(dir);
        location.add(dir);


        player.getWorld().spawnParticle(Particle.CRIT, loc, 0, dir.getX(), dir.getY(), dir.getZ(), 1);
        player.teleport(new Location(player.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));

    }

    public void LeapVertical() {

        final Location loc = player.getEyeLocation().add(0, -0.5, 0);

        player.setVelocity(new Vector(loc.getDirection().normalize().multiply(0.5).getX(), 0.7
                , loc.getDirection().normalize().multiply(0.5).getZ()));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

        tb.setRadius(2.5)
                .setLocation(loc)
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(new Vector(0, 0.3, 0)))
                .setDamage(() -> PlayerManager.getinstance(player).meleedmgcalculate(player, 1));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {

            new BukkitRunnable() {

                final Location loc = player.getEyeLocation().add(0, -0.5, 0);
                final Set<Entity> Hit = new HashSet<>();
                double rpitch = Math.toRadians(player.getLocation().getPitch());
                double ryaw = Math.toRadians(player.getLocation().getYaw());
                double rroll = Math.toRadians(Math.random() * 40 + 70);
                int time = 0;
                double angle = 60;

                double x = 0;
                double y = 0;
                double z = 0;

                @Override
                public void run() {

                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

                    for(int i=0; i<15; i++) {

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
                            player.getWorld().spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1, 0, 0, 0);

                            tb.setLocation(loc).build();

                            loc.subtract(v);
                        }
                        angle = angle - 6;
                    }

                    if(time>3) cancel();
                    time++;
                }

            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

        },5);


    }

    public synchronized void ThrowDagger() {

        if(throwns.containsKey(player)) {
            if(!tasks.get(player).isCancelled()) tasks.get(player).cancel();
            tasks.remove(player);
            throwns.get(player).remove();

        }
        throwns.remove(player);

        Location loc = player.getEyeLocation();

        player.getWorld().playSound(loc, Sound.ITEM_TRIDENT_RIPTIDE_2, 1, 1);

        tb.setRadius(1.5)
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(new Vector(0, 0.3, 0)))
                .addPlaySound((e) -> player.getWorld().playSound(e.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1))
                .setDamage(() -> PlayerManager.getinstance(player).meleedmgcalculate(player, 1))
                .setHitOnlyOne(true);

            final Set<Entity> Hit = new HashSet<>();

            ArmorStand dagger = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0, -0.5, 0), EntityType.ARMOR_STAND);
            dagger.setInvisible(true);
            dagger.setSilent(true);
            dagger.setInvulnerable(true);
            dagger.setArms(true);
            dagger.setSmall(true);
            dagger.getEquipment().setItem(EquipmentSlot.HAND, new ItemStack(Material.NETHERITE_SWORD));
            dagger.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
            dagger.setRightArmPose(new EulerAngle(Math.toRadians(165f), Math.toRadians(180f-loc.getPitch()), Math.toRadians(90f)));

            throwns.put(player, dagger);

            final Vector vector = loc.getDirection().normalize().multiply(2);


            new BukkitRunnable() {

                double xa = 0;
                double za = 0;

                int t = 0;

                @Override
                public void run() {

                    tasks.put(player, this);

                    if(t<11) {
                        dagger.setVelocity(vector);
                        if(dagger.getEyeLocation().add(vector.clone().normalize().multiply(0.8)).getBlock().getType().isSolid()) t = 11;


                        player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, dagger.getEyeLocation().add(0, 0.25, 0), 0, vector.clone().multiply(-1).getX()
                                , vector.clone().multiply(-1).getY(), vector.clone().multiply(-1).getZ(), 0.5f);

                        tb.setLocation(dagger.getEyeLocation()).build();
                        if(tb.getHitEntity().size()>=1) { t=11; }

                    }

                    
                    // 단검 마커 이펙트
                    if(t==11) {
                        if(dagger.getEyeLocation().add(vector.clone().normalize().multiply(0.5)).getBlock().getType().isSolid()) {
                            dagger.teleport(dagger.getEyeLocation().subtract(vector.clone().normalize().multiply(0.5)));
                        }

                        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, dagger.getLocation(), 1, 0, 0, 0, 0);
                        player.getWorld().playSound(dagger.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
                        player.getWorld().playSound(dagger.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1, 2);

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

                        try {
                            ArmorStand marker = throwns.get(player);
                            Location mloc = marker.getLocation().add(0, 1, 0);

                            Vector betvec = mloc.toVector().subtract(player.getEyeLocation().add(0, -1, 0).toVector()).normalize().multiply(1);
                            for(int i=1; i<100; i++) {
                                player.getWorld().spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(0, -1, 0).add(betvec.clone().multiply(i)), 1, 0, 0, 0, 0
                                , new Particle.DustOptions(Color.PURPLE, 0.5f));
                                if(player.getLocation().add(betvec.clone().multiply(i)).distance(mloc)<1.5) break;
                            }

                            double x = 1.5 * Math.cos((double)t * Math.PI / 16);
                            double y = 0;
                            double z = 1.5 * Math.sin((double)t * Math.PI / 16);

                            Vector v = new Vector(x, y, z);
                            player.getWorld().spawnParticle(Particle.TOTEM, mloc.clone().add(v), 1, 0, 0, 0, 0);

                            x = -1.5 * Math.cos((double)t * Math.PI / 16);
                            y = 0;
                            z = -1.5 * Math.sin((double)t * Math.PI / 16);

                            v = new Vector(x, y, z);
                            player.getWorld().spawnParticle(Particle.TOTEM, mloc.clone().add(v), 1, 0, 0, 0, 0);

                            for(int i =0; i<10; i++) {
                                double r1 = Math.random() * 2 - 1;
                                double r2 = Math.random() * 4 - 2;
                                double r3 = Math.random() * 2 - 1;

                                player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, throwns.get(player).getLocation().add(0, 2, 0), 0, r1, r2, r3, 3);
                            }
                        }
                        catch(Exception e) {
                        }
                    }
                    // 시간이 지나거나 단검이 없어지면 종료
                    if(!throwns.containsKey(player) || t > 211) {
                        tasks.remove(player);
                        if(throwns.containsKey(player)) {
                            throwns.get(player).remove();
                            throwns.remove(player);
                        }
                        cancel();
                    }
                    t++;
                    xa += 10;
                    za += 20;

                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"),0, 1);
//        }, 1);

    }

    public void TeleportToDagger() {

    }

    public void SpiritChain() {

    }

    public void DaggerInterruption() {

    }

    private void SweepAround() {

        final Set<Entity> Hit = new HashSet<>();
        Location loc = player.getEyeLocation().add(0, -0.5, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1);

        tb.setRadius(2.5)
                .setDamage(() -> PlayerManager.getinstance(player).meleedmgcalculate(player, 2))
                .entityExcept(Hit)
                .setLocation(loc);

        new BukkitRunnable() {

            double rpitch = Math.toRadians(player.getLocation().getPitch());
            double ryaw = Math.toRadians(player.getLocation().getYaw());
            int time =0;
            double angle = 0;
            double angle2 = 180;

            @Override
            public void run() {

                for(int i=0; i<5; i++) {

                    double yangle = Math.toRadians(angle);
                    double ycos = Math.cos(yangle);
                    double ysin = Math.sin(yangle);

                    double yangle2 = Math.toRadians(angle2);
                    double ycos2 = Math.cos(yangle2);
                    double ysin2 = Math.sin(yangle2);

                    double x = 0;
                    double y = -angle / 360;

                    for (double j = 0; j < 10; j++) {
                        double z = 1 + angle / 200 + j / 6;
                        double z2 = 1 + angle2 / 200 + j / 6;

                        Vector v = new Vector(x, y, z);
                        Vector v2 = new Vector(x, y, z2);

                        v = Rotate.rotateAroundAxisY(v, ycos, ysin);
                        v = transform(v, ryaw, rpitch, 0);

                        v2 = Rotate.rotateAroundAxisY(v2, ycos2, ysin2);
                        v2 = transform(v2, ryaw, rpitch, 0);

                        loc.add(v);
                        if(j>5) {
                            player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0, 0, 0, 0);

                            tb.setLocation(loc).build();

                        }
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                        loc.subtract(v);

                        loc.add(v2);
                        if(j>5) {
                            player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 1, 0, 0, 0, 0);

                            tb.setLocation(loc).build();

                        }
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                        loc.subtract(v2);
                    }
                    angle += 6;
                    angle2 += 6;
                }

                if(time>=6) cancel();
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }
    private void KhaosOneofTripleHit(Double roll, Location loc) {

        final Set<Entity> Hit = new HashSet<>();

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1, 1);
        targetBuilder tb = targetBuilder.builder(player)
                .setRadius(1.5)
                .setDamage(() -> PlayerManager.getinstance(player).meleedmgcalculate(player, 1))
                .addStatus((e) -> EntityStatusManager.getinstance(e).KnockBack(player, 0.5));

        if(pm.getTalent("RR", 3) == 3) {
            if(pm.dummyCount.contains("KHRRtIII3")) {
                tb.addwhenHit(() -> {
                    PlayerHealthShield.getinstance(player).HealthAdd((int) ((double)pm.Health / 100), player);
                });
            }
        }

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

                    for(double k = 2; k<4; k+=0.4) {

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

                        tb.setLocation(loc).build();

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

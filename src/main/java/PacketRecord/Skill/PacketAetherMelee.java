package PacketRecord.Skill;

import ClassAbility.entitycheck;
import DynamicData.Damage;
import Mob.EntityStatusManager;
import PacketRecord.PacketPlayParticle;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static PlayParticle.Rotate.rotateAroundAxisY;
import static PlayParticle.Rotate.transform;

public class PacketAetherMelee {

    private EntityPlayer eplayer;
    private Player player;
    private Player showTo;
    private final int radius = 7;
    private boolean meleerot = false;

    public PacketAetherMelee(EntityPlayer player, Player showTo) {

        this.eplayer = player;
        this.player = player.getBukkitEntity();
        this.showTo = showTo;

    }

    public void Melee(String combo, int MeleeCombo) {

        if(MeleeCombo==1) {
            WeakHorizon();
        }
        else if(MeleeCombo==2) {
            if(combo.equals("L")) {
                WeakStabbing();
            }
            else if(combo.equals("SHIFTL")) {
                WeakHorizon();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract")
                        , () -> {
                            WeakVerticalUp();
                        }, 5);
            }
            else if(combo.equals("R")) {
                Weak240Horizon();
            }
        }
        else if(MeleeCombo==3) {
            if(combo.equals("L")) {
                StrongStabbing();
            }
            else if(combo.equals("SHIFTL")) {
                WeakStabbing();
            }
            else if(combo.equals("R")) {
                WeakLeap();
                Weak240Horizon();
            }
        }
        else if(MeleeCombo==4) {
            if(combo.equals("L")) {
                WeakStabbing();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                        this::StrongStabbing, 5);
            }
            else if(combo.equals("SHIFTL")) {
                WeakVerticalUp();
            }
            else if(combo.equals("R")) {
                ShowTraj();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                        () -> {
                            Strong240Horizon();
                        }, 5);
            }
        }
        else if(MeleeCombo==5) {
            if(combo.equals("L")) {
                WeakStabbing();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                        this::StrongStabbing, 3);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                        this::StrongStabbing, 6);
            }
            else if(combo.equals("R")) {
                StrongVerticalDown();
            }
            else if(combo.equals("SHIFTL")) {

            }
        }
    }

    private void WeakHorizon() {

        Location location = player.getEyeLocation().add(0, -0.5, 0);
        showTo.playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);

        new BukkitRunnable() {

            double pitch = location.getPitch();
            double yaw = location.getYaw();
            double rpitch = Math.toRadians(pitch);
            double ryaw = Math.toRadians(yaw);
            double roll = Math.random() * 60 - 30;
            double rroll = Math.toRadians(roll);

            double angle = PlayerFunction.getinstance(player).getMeleeRot() ? -60 : 60;
            int t = 0;

            double x =0;
            double y =0;
            double z =0;

            List<Entity> Hit = new ArrayList<>();

            @Override
            public void run() {

                for(int i=0; i<8; i++) {

                    for(double k = 1.5; k<4; k+=0.2) {

                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angle);
                        double yaxiscos = Math.cos(yangle);
                        double yaxissin = Math.sin(yangle);

                        Vector v = new Vector(x , y, z);
                        v = rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, ryaw, rpitch, rroll);

                        location.add(v);
//                        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
//                                new Particle.DustOptions(Color.WHITE, 1));
                        showTo.spawnParticle(Particle.ELECTRIC_SPARK, location, 1, 0, 0, 0, 0);
                        if(k+0.3>3) {
                            //location.getWorld().spawnParticle(Particle.ASH, location, 1, 0, 0, 0, 0);
                            showTo.spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);
                        }

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(location) < 1.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    EntityStatusManager.getinstance(entity).KnockBack(player, 0.5);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }

                        location.subtract(v);
                    }

                    angle = angle + (meleerot ? 3 : -3);
                }

                if(t>4) cancel();
                t++;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }

    private void StrongHorizon() {


        Location location = player.getEyeLocation().add(0, -0.5, 0);
        showTo.playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);

        new BukkitRunnable() {

            double pitch = location.getPitch();
            double yaw = location.getYaw();
            double rpitch = Math.toRadians(pitch);
            double ryaw = Math.toRadians(yaw);
            double roll = Math.random() * 60 - 30;
            double rroll = Math.toRadians(roll);

            double angle = PlayerFunction.getinstance(player).getMeleeRot() ? -60 : 60;
            int t = 0;

            double x =0;
            double y =0;
            double z =0;

            List<Entity> Hit = new ArrayList<>();

            @Override
            public void run() {

                for(int i=0; i<8; i++) {

                    for(double k = 1.5; k<4; k+=0.2) {

                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angle);
                        double yaxiscos = Math.cos(yangle);
                        double yaxissin = Math.sin(yangle);

                        Vector v = new Vector(x , y, z);
                        v = rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, ryaw, rpitch, rroll);

                        location.add(v);
                        showTo.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                                new Particle.DustOptions(Color.RED, 0.5f));
                        showTo.spawnParticle(Particle.ELECTRIC_SPARK, location, 1, 0, 0, 0, 0);
                        if(k+0.3>3) {
                            //location.getWorld().spawnParticle(Particle.ASH, location, 1, 0, 0, 0, 0);
                            //location.getWorld().spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);
                        }

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(location) < 1.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    EntityStatusManager.getinstance(entity).KnockBack(player, 0.5);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }

                        location.subtract(v);
                    }

                    angle = angle + (PlayerFunction.getinstance(player).getMeleeRot() ? 3 : -3);
                }

                if(t>4) cancel();
                t++;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

    private void WeakStabbing() {

        final Location loc = player.getEyeLocation().add(0, -0.5, 0);

        showTo.playSound(loc, Sound.ENTITY_WITHER_SHOOT, 1f, 2f);
        showTo.playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);

        final List<Entity> Hit = new ArrayList<>();

        new BukkitRunnable() {

            double rpitch = Math.toRadians(player.getLocation().getPitch());
            double ryaw = Math.toRadians(player.getLocation().getYaw());
            double rroll = Math.toRadians(Math.random() * 30 - 15);

            int time = 0;

            double x = 0;
            double y = 0;
            double z = 0;

            @Override
            public void run() {

                for(double j=0; j<6; j+=0.5) {

                    x = (7-j)/8;
                    y = 0;
                    z = time + j+1;

                    Vector v = new Vector(x, y, z);
                    v = transform(v, ryaw, rpitch, rroll);
                    loc.add(v);
                    showTo.spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                    //player.getWorld().spawnParticle(Particle.CLOUD, loc, 1, 0, 0, 0, 0);
                    showTo.spawnParticle(Particle.REDSTONE, loc, 1, 0.1, 0.1, 0.1, 0,
                            new Particle.DustOptions(Color.WHITE, 1));


                    for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                        if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                            Location eloc = entity.getEyeLocation();
                            BoundingBox box = entity.getBoundingBox();
                            if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                Damage.getinstance().taken(dmg, entity, player);
                                Vector knockvector = eloc.toVector().subtract(loc.toVector()).normalize().multiply(0.5);
                                EntityStatusManager.getinstance(entity).KnockBack(knockvector);
                                Hit.add(entity);
                                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                            }
                        }
                    }

                    loc.subtract(v);

                    x = -(7-j)/8;
                    y = 0;
                    z = time + j+1;

                    v = new Vector(x, y, z);
                    v = transform(v, ryaw, rpitch, rroll);
                    loc.add(v);
                    showTo.spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                    //player.getWorld().spawnParticle(Particle.GLOW, loc, 1, 0, 0, 0, 0);
                    showTo.spawnParticle(Particle.REDSTONE, loc, 1, 0.1, 0.1, 0.1, 0,
                            new Particle.DustOptions(Color.WHITE, 1));

                    for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                        if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                            Location eloc = entity.getEyeLocation();
                            BoundingBox box = entity.getBoundingBox();
                            if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1.5);
                                Damage.getinstance().taken(dmg, entity, player);
                                Vector knockvector = eloc.toVector().subtract(loc.toVector()).normalize().multiply(0.5);
                                EntityStatusManager.getinstance(entity).KnockBack(knockvector);
                                Hit.add(entity);
                                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                            }
                        }
                    }

                    loc.subtract(v);
                }

                if(time>3) cancel();
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

    private void StrongStabbing() {

        PacketPlayParticle packetplayParticle = new PacketPlayParticle(Particle.CRIT, showTo);
        packetplayParticle.CirCleHorizontalSmallImpact(player.getLocation().add(0, 0.2, 0));

        final Location loc = player.getEyeLocation().add(0, -0.5, 0);

        showTo.playSound(loc, Sound.ENTITY_WITHER_SHOOT, 1f, 2f);
        showTo.playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);

        final List<Entity> Hit = new ArrayList<>();

        new BukkitRunnable() {

            double rpitch = Math.toRadians(player.getLocation().getPitch());
            double ryaw = Math.toRadians(player.getLocation().getYaw());
            double rroll = Math.toRadians(Math.random() * 30 - 15);

            int time = 0;

            double x = 0;
            double y = 0;
            double z = 0;

            @Override
            public void run() {

                for(double j=0; j<7; j+=0.2) {

                    x = (7-j)/8;
                    y = 0;
                    z = time + j+1;

                    Vector v = new Vector(x, y, z);
                    v = transform(v, ryaw, rpitch, rroll);
                    loc.add(v);
                    showTo.spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                    //showTo.spawnParticle(Particle.CLOUD, loc, 1, 0, 0, 0, 0);
                    showTo.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0,
                            new Particle.DustOptions(Color.RED, 1));

                    for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                        if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                            Location eloc = entity.getEyeLocation();
                            BoundingBox box = entity.getBoundingBox();
                            if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1.5);
                                Damage.getinstance().taken(dmg, entity, player);
                                Vector knockvector = eloc.toVector().subtract(loc.toVector()).normalize().multiply(0.5);
                                EntityStatusManager.getinstance(entity).KnockBack(knockvector);
                                Hit.add(entity);
                                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                            }
                        }
                    }

                    loc.subtract(v);

                    x = -(7-j)/8;
                    y = 0;
                    z = time + j+1;

                    v = new Vector(x, y, z);
                    v = transform(v, ryaw, rpitch, rroll);
                    loc.add(v);
                    showTo.spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
                    //showTo.spawnParticle(Particle.CLOUD, loc, 1, 0, 0, 0, 0);
                    showTo.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0,
                            new Particle.DustOptions(Color.RED, 1));

                    for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                        if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                            Location eloc = entity.getEyeLocation();
                            BoundingBox box = entity.getBoundingBox();
                            if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1.5);
                                Damage.getinstance().taken(dmg, entity, player);
                                Vector knockvector = eloc.toVector().subtract(loc.toVector()).normalize().multiply(0.5);
                                EntityStatusManager.getinstance(entity).KnockBack(knockvector);
                                Hit.add(entity);
                                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                            }
                        }
                    }
                    loc.subtract(v);
                }

                if(time>3) cancel();
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

    private void Weak240Horizon() {

        double radius = 5;

        final Location location = player.getEyeLocation().add(0, -0.5, 0);
        showTo.playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);

        new BukkitRunnable() {


            double yaw = player.getLocation().getYaw();
            double pitch = player.getLocation().getPitch();
            double roll3 = Math.random() * 10 - 5;

            int step = 0;
            //double anglez = Math.random() * 180;
            double angley =  PlayerFunction.getinstance(player).getMeleeRot() ? -140 : 140;
            double x = 0;
            double y = 0;
            double z = 0;

            int t = 0;
            final List<Entity> Hit = new ArrayList<>();

            @Override
            public void run() {

                for(int i=0; i<12; i++) {

                    for(double k = 2.5; k<5.5; k+=0.2) {
                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angley);
                        double yaxiscos = Math.cos(-yangle);
                        double yaxissin = Math.sin(-yangle);

                        Vector v = new Vector(x, y, z);
                        v = rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, Math.toRadians(yaw), Math.toRadians(pitch), Math.toRadians(roll3));
                        location.add(v);
                        if(k<2.2) {}
//                            location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
//                                    new Particle.DustOptions(Color.WHITE, 1));
                        else if(k<4.5)
                            showTo.spawnParticle(Particle.ELECTRIC_SPARK, location, 1, 0, 0, 0, 0);
                        else
                            showTo.spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(location) < 2.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
                                    int dmg = PlayerManager.getinstance(player). meleedmgcalculate(player, 1);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.5);
                                    EntityStatusManager.getinstance(entity).KnockBack(knockvector);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }

                        location.subtract(v);
                    }

                    angley = angley + (PlayerFunction.getinstance(player).getMeleeRot() ? 6 : -6);
                }
                if(t>=3) cancel();
                showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);
                t += 1;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }
    private void Strong240Horizon() {

        PacketPlayParticle packetplayParticle = new PacketPlayParticle(Particle.CRIT, showTo);
        packetplayParticle.CirCleHorizontalSmallImpact(player.getLocation().add(0, 0.2, 0));

        double radius = 5;

        final Location location = player.getEyeLocation().add(0, -0.5, 0);
        showTo.playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);

        new BukkitRunnable() {

            double yaw = player.getLocation().getYaw();
            double pitch = player.getLocation().getPitch();
            double rpitch = Math.toRadians(Math.random() * 20 - 10);
            double roll3 = Math.random() * 10-5;

            int step = 0;
            //double anglez = Math.random() * 180;
            double angley =  PlayerFunction.getinstance(player).getMeleeRot() ? -140 : 140;
            double x = 0;
            double y = 0;
            double z = 0;

            int t = 0;
            final List<Entity> Hit = new ArrayList<>();

            @Override
            public void run() {

                for(int i=0; i<12; i++) {

                    for(double k = 2.5; k<6; k+=0.3) {
                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angley);
                        double yaxiscos = Math.cos(-yangle);
                        double yaxissin = Math.sin(-yangle);

                        Vector v = new Vector(x, y, z);
                        v = rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, Math.toRadians(yaw), Math.toRadians(pitch), Math.toRadians(roll3));
                        location.add(v);
                        if(k<2.2) {}
//                            showTo.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
//                                    new Particle.DustOptions(Color.WHITE, 1));
                        else {
                            //showTo.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
                            showTo.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
                                    new Particle.DustOptions(Color.fromRGB(139 + (int)(k*18), (int)(k*40), 255), 1));
                        }

                        if(k>5.2) showTo.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0, 0, 0, 0);

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(location) < 2.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 0.7);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    Vector knockvector = eloc.toVector().subtract(location.toVector()).normalize().multiply(0.5);
                                    EntityStatusManager.getinstance(entity).KnockBack(knockvector);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }

                        location.subtract(v);
                    }

                    angley = angley + (PlayerFunction.getinstance(player).getMeleeRot() ? 6 : -6);
                }

                if(t>=3) cancel();
                showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
                t += 1;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

    private void ShowTraj() {

        final Location location = player.getEyeLocation().add(0, -0.5, 0);

        new BukkitRunnable() {

            double yaw = player.getLocation().getYaw();
            double pitch = player.getLocation().getPitch();
            //double rpitch = Math.toRadians(Math.random() * 20 - 10);
            //double roll3 = Math.random() * 10-5;

            int step = 0;
            //double anglez = Math.random() * 180;
            double angley =  PlayerFunction.getinstance(player).getMeleeRot() ? -140 : 140;
            double x = 0;
            double y = 0;
            double z = 0;

            int t = 0;

            @Override
            public void run() {

                for(int i=0; i<12; i++) {

                    for(double k =5; k<6.3; k+=0.3) {

                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angley);
                        double yaxiscos = Math.cos(-yangle);
                        double yaxissin = Math.sin(-yangle);

                        Vector v = new Vector(x, y, z);
                        v = rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, Math.toRadians(yaw), Math.toRadians(pitch), 0);
                        location.add(v);

                        showTo.spawnParticle(Particle.ELECTRIC_SPARK, location, 1, 0, 0, 0, 0);

                        location.subtract(v);
                    }
                    angley = angley + (PlayerFunction.getinstance(player).getMeleeRot() ? 6 : -6);
                }

                if(t>=5) cancel();
                t += 1;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

    private void WeakVerticalUp() {

        Location location = player.getEyeLocation();
        showTo.playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_BLAZE_HURT, 1, 2);

        new BukkitRunnable() {

            double pitch = location.getPitch();
            double yaw = location.getYaw();
            double rpitch = Math.toRadians(pitch);
            double ryaw = Math.toRadians(yaw);
            double roll = Math.random() * 20 + 70;
            double rroll = Math.toRadians(roll);

            double angle = -30;
            int t = 0;

            double x =0;
            double y =0;
            double z =0;

            List<Entity> Hit = new ArrayList<>();

            @Override
            public void run() {

                for(int i=0; i<8; i++) {

                    for(double k = 2.5; k<5; k+=0.2) {

                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angle);
                        double yaxiscos = Math.cos(yangle);
                        double yaxissin = Math.sin(yangle);

                        Vector v = new Vector(x , y, z);
                        v = rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, ryaw, rpitch, rroll);

                        location.add(v);
//                        showTo.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
//                                new Particle.DustOptions(Color.WHITE, 1));
                        showTo.spawnParticle(Particle.ELECTRIC_SPARK, location, 1, 0, 0, 0, 0);
                        if(k+0.3>4.5) {
                            //showTo.spawnParticle(Particle.ASH, location, 1, 0, 0, 0, 0);
                            showTo.spawnParticle(Particle.GLOW, location, 1, 0, 0, 0, 0);
                        }

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(location) < 2.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    EntityStatusManager.getinstance(entity).KnockBack(new Vector(0, 0.7, 0));
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }

                        location.subtract(v);
                    }

                    angle += 3;
                }

                if(t>4) cancel();
                t++;

            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }
    private void StrongVerticalDown() {


        PacketPlayParticle packetplayParticle = new PacketPlayParticle(Particle.CRIT, showTo);
        packetplayParticle.CirCleHorizontalSmallImpact(player.getLocation().add(0, 0.2, 0));

        Location location = player.getEyeLocation();
        showTo.playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 2f);
        showTo.playSound(location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1f, 0f);

        new BukkitRunnable() {

            double pitch = location.getPitch();
            double yaw = location.getYaw();
            double rpitch = Math.toRadians(pitch);
            double ryaw = Math.toRadians(yaw);
            double roll = Math.random() * 20 + 70;
            double rroll = Math.toRadians(roll);

            double angle = 90;
            int t = 0;

            double x = 0;
            double y = 0;
            double z = 0;

            List<Entity> Hit = new ArrayList<>();

            @Override
            public void run() {

                for (int i = 0; i < 8; i++) {

                    for (double k = 2.5; k < 5.6; k += 0.3) {

                        x = 0;
                        y = 0;
                        z = k;

                        double yangle = Math.toRadians(angle);
                        double yaxiscos = Math.cos(yangle);
                        double yaxissin = Math.sin(yangle);

                        Vector v = new Vector(x, y, z);
                        v = rotateAroundAxisY(v, yaxiscos, yaxissin);
                        v = transform(v, ryaw, rpitch, rroll);

                        location.add(v);
//                        showTo.spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0,
//                                new Particle.DustOptions(Color.WHITE, 1));
                        showTo.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
                        if (k + 0.3 > 4.5) {
                            //showTo.spawnParticle(Particle.ASH, location, 1, 0, 0, 0, 0);
                            showTo.spawnParticle(Particle.SOUL, location, 1, 0, 0, 0, 0);
                        }

                        for (LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if (entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if (eloc.distance(location) < 2.5 || box.contains(location.getX(), location.getY(), location.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
                                    entity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, eloc, 1, 0, 0, 0, 0);
                                    entity.getWorld().playSound(eloc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

                                    if(!entity.isOnGround()) {
                                        EntityStatusManager.getinstance(entity).KnockBack(new Vector(0, -3, 0));
                                    }

                                    Damage.getinstance().taken(dmg, entity, player);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }

                        location.subtract(v);
                    }

                    angle -= 3;
                }

                if (t > 4) cancel();
                t++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }

    private void WeakLeap() {

        Vector vec = player.getEyeLocation().getDirection().normalize();
        vec.multiply(new Vector(1, 0, 1));
        vec.add(new Vector(0, 0.3, 0));
        player.setVelocity(vec);

    }


}

package ClassAbility.Khaos;

import ClassAbility.Cheiron.Cheiron;
import ClassAbility.Combination;
import ClassAbility.entitycheck;
import DynamicData.Damage;
import Mob.EntityStatusManager;
import PlayParticle.Rotate;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import com.google.common.base.Enums;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import spellinteracttest.Main;

import java.util.*;

public class Khaos {

    private static Khaos khaos;
    private Player player;
    private PlayerFunction playerFunction;
    private int CurrentMana;
    private int ManaDecrease;
    private static Cheiron cheiron;


    public Khaos(Player player) {
        this.player = player;
        this.playerFunction = PlayerFunction.getinstance(player);
        this.CurrentMana = PlayerEnergy.getinstance(player).getEnergy();
        this.ManaDecrease = PlayerManager.getinstance(player).ManaDecrease;
    }

    private Khaos() {

    }

    public static Khaos getInstance() {
        if(khaos == null) khaos = new Khaos();
        return khaos;
    }

    private enum ENUM {
        RL(4, "§o§l반월참§l§o §3§l-⚡§l"),
        SHIFTR(4, "§o§l공간전이§l§o §3§l-⚡§l"),
        RR(6, "§o§lRR스킬§l§o §3§l-⚡§l"),
        FR(8, "§o§lFR스킬§l§o §3§l-⚡§l");

        private int mana;
        private String title;
        private String method;

        ENUM(int mana, String title) {
            this.mana = mana;
            this.title = title;
        }

        int getMana() {
            return mana;
        }

        String getTitle() {
            return title;
        }
    }

    public void Skill(String combo) {

        if(!Enums.getIfPresent(ENUM.class, combo).isPresent()) return;

        int mana = ENUM.valueOf(combo).getMana() - ManaDecrease <= 0 ? 1 : ENUM.valueOf(combo).getMana() - ManaDecrease
                + PlayerEnergy.getinstance(player).getEnergyOverload();
        String title = ENUM.valueOf(combo).getTitle()+mana;

        if(mana <= CurrentMana) {
            PlayerEnergy.getinstance(player).removeEnergy(mana);
            if(combo.equals("SHIFTR")) SHIFTR(player);
            if(combo.equals("RL")) HalfMoon();
            if(combo.equals("FR")) FR();


            Combination.getinstance().Sound(player);
            player.sendTitle(" ", Combination.blank+title, 5, 20, 10);
            Combination.getinstance().energyoverload(player, combo);
        }
        else {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
            player.sendTitle(" ", Combination.blank+Combination.manaexhaustion, 0, 20, 10);
        }
    }

    public void SHIFTR(Player player) {

        Set<Entity> Hit = new HashSet<>();

        if(KhaosMelee.getThrown(player) == null) {

        }
        /*
        단검이 있다면 단검으로 순간이동

         */
        else if(KhaosMelee.getThrown(player) != null) {

            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0)
                    , 500, 0, 0, 0, 1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1);
            player.teleport(KhaosMelee.getThrown(player));

            Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"),
                    () -> {

                        Location loc = player.getLocation();

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(loc) < 4 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 1.5);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    EntityStatusManager.getinstance(entity).KnockBack(player, 0.5);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.5f);
                                }
                            }
                        }

                        new BukkitRunnable() {

                            double time = 0;
                            double r = 0 / 255D;
                            double g = 127 / 255D;
                            double b = 255 / 255D;

                            @Override
                            public void run() {

                                for(double j=0; j<1; j+=0.2) {
                                    for(double i = 0; i<Math.PI*2; i+=Math.PI/16) {
                                        double x = Math.cos(i) * (time + j);
                                        double y = 0;
                                        double z = Math.sin(i) * (time + j);

                                        Vector v = new Vector(x, y, z);
                                        loc.add(v);
                                        player.getWorld().spawnParticle(Particle.SPELL_INSTANT, loc, 1, 0, 0, 0, 0);
                                        loc.subtract(v);
                                    }
                                }

                                if(time >= 4) cancel();
                                time++;
                            }
                        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);


                player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0, 1, 0)
                                , 500, 0, 0, 0, 1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

                    }, 1);

        }
    }

    public void HalfMoon() {

        Location loc = player.getEyeLocation();
        PlayerEnergy pe = PlayerEnergy.getinstance(player);
        boolean isPreSkillSame = pe.getPreviousSkill().equals("RL");
        int overload = pe.getEnergyOverload();
        double rate = 1 * Math.pow(1.25, isPreSkillSame ? overload + 1 : 0);

        List<Entity> Hit = new ArrayList<>();
        player.getWorld().playSound(loc, Sound.ENTITY_IRON_GOLEM_ATTACK, 1, 1);
        player.getWorld().playSound(loc, Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1);
        player.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
        player.getWorld().playSound(loc, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1, 1);
        double rpitch = Math.toRadians(loc.getPitch());
        double ryaw = Math.toRadians(loc.getYaw());
        double rroll = Math.toRadians(Math.random() * 40 - 20);

        new BukkitRunnable() {

            int time = 0;
            double angle = -60;

            @Override
            public void run() {


                for(int i=0; i<10; i++) {

                    for(double j =1.5; j<4; j+=0.3) {

                        double yangle = Math.toRadians(angle);
                        double ycos = Math.cos(-yangle);
                        double ysin = Math.sin(-yangle);

                        double x = 0;
                        double y = 0;
                        double z = j;
                        Vector v = new Vector(x, y, z);
                        v = Rotate.rotateAroundAxisY(v, ycos, ysin);
                        v = Rotate.transform(v, ryaw, rpitch, rroll);
                        loc.add(v);
                        if(j>3.1)
                            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);

                        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !Hit.contains(entity)) {
                                Location eloc = entity.getEyeLocation();
                                BoundingBox box = entity.getBoundingBox();
                                if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                    int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, rate);
                                    Damage.getinstance().taken(dmg, entity, player);
                                    EntityStatusManager.getinstance(entity).KnockBack(player, 0.5);
                                    Hit.add(entity);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                }
                            }
                        }
                        loc.subtract(v);
                    }


                    angle+=4;
                }


                if(time >= 3) cancel();

                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);
    }

    public void RR() {
        Location loc = player.getEyeLocation();

    }

    public void FR() {
        Location loc = player.getEyeLocation();
        Location loc_ = player.getEyeLocation();
        Location loc__ = player.getEyeLocation();
        loc_.setYaw(loc.getYaw() - 20);
        loc__.setYaw(loc.getYaw() + 20);
        List<ArmorStand> list = new ArrayList<>(Arrays.asList(dagger(loc), dagger(loc_), dagger(loc__)));

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                List<ArmorStand> DBukkit = new ArrayList<>();

                for(int i=0; i<list.size(); i++) {
                    Boolean hit = false;
                    ArmorStand a = list.get(i);
                    Location loc = a.getLocation();
                    Vector v = a.getLocation().getDirection().normalize().multiply(1);
                    a.setVelocity(v);
                    a.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, a.getLocation(), 1, 0, 0, 0, 0);

                    for(LivingEntity entity : player.getWorld().getLivingEntities()) {
                        if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player) {
                            Location eloc = entity.getEyeLocation();
                            BoundingBox box = entity.getBoundingBox();
                            if(eloc.distance(loc) < 1.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                                int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 1.5);
                                Damage.getinstance().taken(dmg, entity, player);
                                EntityStatusManager.getinstance(entity).KnockBack(a, 0.5);
                                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.5f);
                                DBukkit.add(a);
                                a.remove();
                                hit = true;
                            }
                        }
                        if(hit) break;
                    }
                }

                for(int i=0; i<DBukkit.size(); i++) {
                    list.remove(DBukkit.get(i));
                }

                if(time>20) {
                    list.forEach(ArmorStand::remove);
                    cancel();
                }
                time++;
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);

    }

    private ArmorStand dagger(Location loc) {
        ArmorStand dagger = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0, -0.5, 0), EntityType.ARMOR_STAND);
        dagger.setInvisible(true);
        dagger.setSilent(true);
        dagger.setInvulnerable(true);
        dagger.setArms(true);
        dagger.setSmall(true);
        dagger.setMarker(true);
        dagger.setCollidable(true);
        dagger.getEquipment().setItem(EquipmentSlot.HAND, new ItemStack(Material.NETHERITE_SWORD));
        dagger.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        dagger.setRightArmPose(new EulerAngle(Math.toRadians(165f), Math.toRadians(180f-loc.getPitch()), Math.toRadians(90f)));
        return dagger;
    }

    private void FREffect(Location loc) {

        double rpitch = Math.toRadians(loc.getPitch());
        double ryaw = Math.toRadians(loc.getYaw());
        double rroll = Math.toRadians(Math.random() * 60 - 30);



    }


}

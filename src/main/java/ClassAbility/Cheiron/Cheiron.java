package ClassAbility.Cheiron;

import ClassAbility.Combination;
import ClassAbility.entitycheck;
import DynamicData.Damage;
import Mob.EntityStatusManager;
import PlayParticle.PlayParticle;
import PlayerManager.PlayerEnergy;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import com.google.common.base.Enums;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Vibration;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cheiron implements Listener {

    private Player player;
    private PlayerFunction playerFunction;
    private int CurrentMana;
    private int ManaDecrease;
    private static Cheiron cheiron;


    public Cheiron(Player player) {
        this.player = player;
        this.playerFunction = PlayerFunction.getinstance(player);
        this.CurrentMana = PlayerEnergy.getinstance(player).getEnergy();
        this.ManaDecrease = PlayerManager.getinstance(player).ManaDecrease;
    }

    private Cheiron() {

    }

    public static Cheiron getInstance() {
        if(cheiron == null) cheiron = new Cheiron();
        return cheiron;
    }

    private enum ENUM {
        LL(4, "§o§l방어태세§l§o §3§l-⚡§l"),
        LR(8, "§o§l전격 화살§l§o §3§l-⚡§l"),
        FL(8, "§o§l이유진 멋있다§l§o §3§l-⚡§l");

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
            if(combo.equals("LL")) KnockBack();
            if(combo.equals("LR")) ElecArrow();


            Combination.getinstance().Sound(player);
            player.sendTitle(" ", Combination.blank+title, 5, 20, 10);
            Combination.getinstance().energyoverload(player, combo);
        }
        else {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 1f);
            player.sendTitle(" ", Combination.blank+Combination.manaexhaustion, 0, 20, 10);
        }
    }

    public void KnockBack() {

        Location loc = player.getLocation().add(0, 1.2, 0);
        Vector dir = loc.getDirection().normalize().multiply(1.5);
        double pitch = Math.toRadians(loc.getPitch());
        double yaw = Math.toRadians(loc.getYaw());

        (new PlayParticle(Particle.CRIT)).CirCleHorizontalSmallImpact(loc);
        loc.add(dir);
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 5, 0, 0, 0, 0);
        player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 15, 0, 0, 0, 0);
        player.getWorld().playSound(loc, Sound.BLOCK_ANVIL_PLACE, 1, 1  );
        player.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 1);

        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player) {
                Location eloc = entity.getEyeLocation();
                BoundingBox box = entity.getBoundingBox();
                if(eloc.distance(loc) < 3.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                    int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 1);
                    Damage.getinstance().taken(dmg, entity, player);
                    EntityStatusManager.getinstance(entity).KnockBack(loc.getDirection().normalize().multiply(3).add(new Vector(0, 1, 0)));
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                }
            }
        }
    }

    public void ElecArrow() {

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(3.5);
        Vector v = loc.getDirection().normalize().multiply(3);
        Arrow arrow = (Arrow) player.getWorld().spawnArrow(loc, dir, 0, 0);
        arrow.setVelocity(dir);
        arrow.setGlowing(true);
        arrow.setShooter(player);
        arrow.setInvulnerable(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setCustomName("ElecArrow");
        int damage = PlayerManager.getinstance(player).meleedmgcalculate(player, 0.7);
        arrow.addScoreboardTag(Integer.toString(damage));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
        (new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1);
        (new PlayParticle(Particle.CRIT)).CirCleHorizontalSmallImpact(player.getLocation().add(0, 0.2, 0));
    }

    public void ElecArrowHit(Location loc) {

        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 3, 0, 0, 0, 0);
        player.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
        player.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
        List<Entity> Hit = new ArrayList<>();

        for(double i = 0; i<Math.PI; i+=Math.PI/16) {

            double y = 2 * Math.cos(i);

            for(double k = 0; k<Math.PI * 2; k+=Math.PI/16) {
                double x = 2 * Math.cos(k) * Math.sin(i);
                double z = 2 * Math.sin(k) * Math.sin(i);

                Vector v = new Vector(x, y, z);

                player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc.add(x, y, z), 0, v.getX()
                        , v.getY(), v.getZ(), 1.5d);
                Vibration vibration = new Vibration(loc.clone().subtract(x, y, z), new Vibration.Destination.BlockDestination(loc.clone().add(x, y, z)), 40);
                player.getWorld().spawnParticle(Particle.VIBRATION, loc.clone().subtract(x, y, z), 1, vibration);
                loc.subtract(x, y, z);

                //player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc,0, v.getX(), v.getY(), v.getZ(), 0.15d);

            }
        }
        for(int i=0; i<100; i++) {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;
            double z = Math.random() * 2 - 1;

            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 0, x, y, z, 0.5);
            if(i%10==0)
                player.getWorld().spawnParticle(Particle.CLOUD, loc, 0, x, y, z, 0.5);
        }

        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player) {
                Location eloc = entity.getEyeLocation();
                BoundingBox box = entity.getBoundingBox();
                if((eloc.distance(loc) < 4.5 || box.contains(loc.getX(), loc.getY(), loc.getZ())) && !Hit.contains(entity)) {
                    int dmg = PlayerManager.getinstance(player).spelldmgcalculate(player, 2.5);
                    Hit.add(entity);
                    Damage.getinstance().taken(dmg, entity, player);
                    EntityStatusManager.getinstance(entity).Stun(player, 10);
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                }
            }
        }
    }

    public void TrackingArrow() {

        Location loc = player.getEyeLocation();
        Set<Arrow> arrowSet = new HashSet<>();

        for(int i=0; i<10; i++) {
            Arrow arrow = player.getWorld().spawnArrow(loc, new Vector(0, 0, 0), 0, 0);
            arrow.setShooter(player);
            arrow.setGlowing(true);
            arrow.setInvulnerable(true);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }
}

package ClassAbility.Cheiron;

import PlayParticle.PlayParticle;
import PlayerManager.PlayerFunction;
import PlayerManager.PlayerManager;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CheironMelee implements Listener {

    /**
     *
     * 모든 화살들은 ArrowCheck.ArrowWatcher 메소드에서 관리함
     *
     *
     *
     *
     */

    private Player player;
    private PlayerFunction playerFunction;
    private static CheironMelee cheironMelee;

    public CheironMelee(Player player) {
        this.player = player;
        playerFunction = PlayerFunction.getinstance(player);
    }
    public CheironMelee() {

    }

    public static CheironMelee getInstance() {
        if(cheironMelee == null) cheironMelee = new CheironMelee();
        return cheironMelee;
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        try {
            if(!PlayerManager.getinstance(player).CurrentClass.equals("케이론")) return;
            if(event.getItem().getType() == Material.BOW && (event.getAction() == Action.RIGHT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                ItemStack bow = event.getItem();
                int slot = event.getPlayer().getInventory().getHeldItemSlot();

                event.setUseItemInHand(Event.Result.DENY);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {

                    event.setUseItemInHand(Event.Result.DENY);
                    event.setCancelled(true);
                    //(new CheironMelee(player)).Melee("R");
                }, 5);
            }
        }
        catch(Exception e) {

        }
    }


    public void Melee(String combo) {
        int MeleeCombo = playerFunction.getMeleeCombo();

        if(playerFunction.getMeleeDelay() != 0) return;

        if(MeleeCombo==1) {

            if(combo.equals("R")) {
                SingleShot();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }
        }
        else if(MeleeCombo==2) {

            if(combo.equals("R")) {
                StrongShot();
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(5);
            }

        }
        else if(MeleeCombo==3) {
            if(combo.equals("R")) {
                TripleShot();
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () ->
                        TripleShot(), 2);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () ->
                        TripleShot(), 4);
                playerFunction.addMeleeCombo();
                playerFunction.setMeleeDelay(10);
            }
        }
        else if(MeleeCombo==4) {
            MultiShot();
            playerFunction.addMeleeCombo();
            playerFunction.setMeleeDelay(10);
        }
        else if(MeleeCombo==5) {
            playerFunction.setMeleeCombo(1);
            playerFunction.setMeleeDelay(10);
        }

    }

    private void SingleShot() {

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(3.5);
        Vector v = loc.getDirection().normalize().multiply(1.5);
        Arrow arrow = (Arrow) player.getWorld().spawnArrow(loc, dir, 0, 0);
        arrow.setVelocity(dir);
        arrow.setGlowing(true);
        arrow.setShooter(player);
        arrow.setInvulnerable(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        int damage = PlayerManager.getinstance(player).meleedmgcalculate(player, 1);
        arrow.addScoreboardTag(Integer.toString(damage));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        //(new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1);

    }

    private void StrongShot() {

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(3.5);
        Vector v = loc.getDirection().normalize().multiply(3);
        Arrow arrow = (Arrow) player.getWorld().spawnArrow(loc, dir, 0, 0);
        arrow.setVelocity(dir);
        arrow.setGlowing(true);
        arrow.setShooter(player);
        arrow.setInvulnerable(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setCustomName("StrongShot");
        int damage = PlayerManager.getinstance(player).meleedmgcalculate(player, 1.5);
        arrow.addScoreboardTag(Integer.toString(damage));
        (new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1d);
        (new PlayParticle(Particle.SMOKE_NORMAL)).BowShotVerticalParticle(loc.add(v), 0.3, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

    }

    public void StrongShotParticle(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 2);
        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 2);

        for(int i=0; i<100; i++) {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;
            double z = Math.random() * 2 - 1;

            location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 0, x, y, z, 0.5);
            if(i%10==0)
                location.getWorld().spawnParticle(Particle.CLOUD, location, 0, x, y, z, 0.5);
        }

        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 2, 0.1, 0.1, 0.1, 0);
    }

    private void TripleShot() {

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(3.5);
        Vector v = loc.getDirection().normalize().multiply(3);
        Arrow arrow = (Arrow) player.getWorld().spawnArrow(loc, dir, 0, 0);
        arrow.setVelocity(dir);
        arrow.setGlowing(true);
        arrow.setShooter(player);
        arrow.setInvulnerable(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setCustomName("TripleShot");
        int damage = PlayerManager.getinstance(player).meleedmgcalculate(player, 0.7);
        arrow.addScoreboardTag(Integer.toString(damage));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        (new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1);
        (new PlayParticle(Particle.CRIT)).CirCleHorizontalSmallImpact(player.getLocation().add(0, 0.2, 0));
    }

    private void MultiShot() {

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(2.5);
        Vector v = loc.getDirection().normalize().multiply(3);

        double rY = Math.toRadians(-30);

        for(int i=0; i<9; i++) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
            double random1 = Math.random() * 1 - 0.5;
            double random2 = Math.random() * 1 - 0.5;
            double random3 = Math.random() * 1 - 0.5;

            dir.add(new Vector(random1, random2, random3));

            Arrow arrow = (Arrow) player.getWorld().spawnArrow(loc, dir, 0, 0);
            arrow.setVelocity(dir);
            arrow.setGlowing(true);
            arrow.setShooter(player);
            arrow.setInvulnerable(true);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            arrow.setCustomName("MultiShot");
            int damage = PlayerManager.getinstance(player).meleedmgcalculate(player, 0.3);
            arrow.addScoreboardTag(Integer.toString(damage));

            dir.subtract(new Vector(random1, random2, random3));
        }

        (new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1d);
        (new PlayParticle(Particle.SMOKE_NORMAL)).BowShotVerticalParticle(loc, 0.3, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            (new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1d);
            (new PlayParticle(Particle.SMOKE_NORMAL)).BowShotVerticalParticle(loc, 0.3, 1);
        }, 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            (new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1d);
            (new PlayParticle(Particle.SMOKE_NORMAL)).BowShotVerticalParticle(loc, 0.3, 1);
        }, 4);


    }

    private void PiercingShot() {

        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize().multiply(3.5);
        Vector v = loc.getDirection().normalize().multiply(3);
        Arrow arrow = (Arrow) player.getWorld().spawnArrow(loc, dir, 0, 0);
        arrow.setVelocity(dir);
        arrow.setGlowing(true);
        arrow.setShooter(player);
        arrow.setInvulnerable(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setPierceLevel(10);
        arrow.setCustomName("PiercingShot");
        int damage = PlayerManager.getinstance(player).meleedmgcalculate(player, 0.7);
        arrow.addScoreboardTag(Integer.toString(damage));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);
        (new PlayParticle(Particle.CRIT)).BowShotVerticalParticle(loc.add(v), 1, 1);
        (new PlayParticle(Particle.CRIT)).CirCleHorizontalSmallImpact(player.getLocation().add(0, 0.2, 0));
    }

}

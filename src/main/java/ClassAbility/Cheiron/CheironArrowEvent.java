package ClassAbility.Cheiron;

import ClassAbility.entitycheck;
import CustomEvents.ArrowFlyingEvent;
import CustomEvents.ArrowOnGroundEvent;
import DynamicData.Damage;
import PlayerManager.PlayerManager;
import net.minecraft.world.entity.projectile.EntityArrow;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CheironArrowEvent implements Listener {

    private static CheironArrowEvent cheironArrowEvent;

    private CheironArrowEvent() {

    }

    public static CheironArrowEvent getInstance() {
        if(cheironArrowEvent == null) cheironArrowEvent = new CheironArrowEvent();
        return cheironArrowEvent;
    }

    @EventHandler
    public void ArrowHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity taker = event.getEntity();
        if(damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            Entity shooter = (Entity) arrow.getShooter();
            if(shooter instanceof Player && taker instanceof LivingEntity) {
                if(!PlayerManager.getinstance((Player) shooter).CurrentClass.equals("케이론")) return;
                if(entitycheck.entitycheck(taker) && entitycheck.duelcheck(taker, shooter)) {
                    EntityArrow entityArrow = ((CraftArrow) arrow).getHandle();
                    try {
                        int damage = Integer.parseInt(arrow.getScoreboardTags().toArray()[0].toString());
                        Damage.getinstance().taken(damage, (LivingEntity) taker, (LivingEntity) shooter);
                        ((Player) shooter).playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f);
                    }
                    catch(Exception e) {

                    }
                    if(arrow.getCustomName() != null) {
                        if(arrow.getCustomName().equals("StrongShot")) {
                            CheironMelee.getInstance().StrongShotParticle(taker.getLocation());
                        }
                        else if(arrow.getCustomName().equals("ElecArrow")) {
                            (new Cheiron((Player) shooter)).ElecArrowHit(arrow.getLocation());
                        }
                    }


                    arrow.remove();
                }
            }
        }
    }

    @EventHandler
    public void ArrowOnGroundEvent(ArrowOnGroundEvent event) {
        Arrow arrow = event.getArrow();
        String name = event.getArrowname();
        if(arrow.getShooter() instanceof Player) {
            Player shooter = (Player) arrow.getShooter();
            if(PlayerManager.getinstance(shooter).CurrentClass.equals("케이론")) {
                if(name.equals("StrongShot")) {
                    CheironMelee.getInstance().StrongShotParticle(arrow.getLocation());
                }
                else if(name.equals("TripleShot")) {

                }
                else if(name.equals("ElecArrow")) {
                    (new Cheiron(shooter)).ElecArrowHit(arrow.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void ArrowFlyingEvent(ArrowFlyingEvent event) {
        Arrow arrow = event.getArrow();
        String name = event.getArrowname();

        if(arrow.getShooter() instanceof Player) {
            Player shooter = (Player) arrow.getShooter();
            if(PlayerManager.getinstance(shooter).CurrentClass.equals("케이론")) {
                if(name.equals("StrongShot")) {
                    arrow.getWorld().spawnParticle(Particle.SMOKE_NORMAL, arrow.getLocation(),
                            1, 0, 0, 0, 0);
                }
                else if(name.equals("TripleShot")) {
                    arrow.getWorld().spawnParticle(Particle.REDSTONE, arrow.getLocation(), 1, 0, 0, 0, 0,
                            new Particle.DustOptions(Color.RED, 1));
                }
                else if(name.equals("ElecArrow")) {
                    arrow.getWorld().spawnParticle(Particle.END_ROD, arrow.getLocation(), 1, 0, 0, 0, 0);
                }
            }
        }
    }
}

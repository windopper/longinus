package ClassAbility.Cheiron;

import ClassAbility.entitycheck;
import CustomEvents.ArrowFlyingEvent;
import CustomEvents.ArrowOnGroundEvent;
import DynamicData.Damage;
import Mob.EntityManager;
import PlayerManager.PlayerManager;
import net.minecraft.world.entity.projectile.EntityArrow;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArrow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class CheironArrowEvent implements Listener {

    private static CheironArrowEvent cheironArrowEvent;

    private CheironArrowEvent() {

    }

    public static CheironArrowEvent getInstance() {
        if(cheironArrowEvent == null) cheironArrowEvent = new CheironArrowEvent();
        return cheironArrowEvent;
    }

    private void invokeHitEvent(Arrow arrow, Entity shooter, Entity taker) {

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
                    String name = arrow.getCustomName();
                    if(name.equals("StrongShot")) {
                        CheironMelee.getInstance().StrongShotParticle(taker.getLocation());
                    }
                    else if(name.equals("ElecArrow")) {
                        (new Cheiron((Player) shooter)).ElecArrowHit(arrow.getLocation());
                    }
                    else if(name.equals("VortexArrow")) {
                        (new Cheiron((Player) shooter)).VortexArrowHit(arrow.getLocation().add(0, 0.2, 0));
                    }
                }
                arrow.remove();
            }
        }
    }


    @EventHandler
    public void ArrowHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity taker = event.getEntity();
        if(damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            Entity shooter = (Entity) arrow.getShooter();
            invokeHitEvent(arrow, shooter, taker);
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
                else if(name.equals("PiercingShot")) {

                }
                else if(name.equals("VortexArrow")) {
                    (new Cheiron(shooter)).VortexArrowHit(arrow.getLocation().add(0, 0.2, 0));
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

    /*
    FallingBlock 엔티티가 Projectile을 팅겨내서 만든 이벤트
     */
    @EventHandler
    public void ArrowBounceOff(ProjectileHitEvent event) {
        Entity hitEntity = event.getHitEntity();
        Entity shooter = (Entity) event.getEntity().getShooter();
        if(hitEntity instanceof FallingBlock) {
            Entity masterEntity = EntityManager.getDisguiseEntity(hitEntity);
            if(shooter instanceof Player && masterEntity instanceof LivingEntity && event.getEntity() instanceof Arrow) {
                invokeHitEvent((Arrow) event.getEntity(), shooter, masterEntity);
            }
        }
    }
}

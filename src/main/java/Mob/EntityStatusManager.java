package Mob;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class EntityStatusManager {

    private LivingEntity e;
    private EntityStatusManager(LivingEntity e) {
        this.e = e;
    }

    public static EntityStatusManager getinstance(LivingEntity e) {
        return new EntityStatusManager(e);
    }

    public enum statusTag {
        knockbackImmune,
        slowImmune,
        stunImmune;
    }
    public boolean canStun() {
        return !e.getScoreboardTags().contains(statusTag.stunImmune.name());
    }
    public boolean canKnockback() {
        return !e.getScoreboardTags().contains(statusTag.knockbackImmune.name());
    }
    public void setStatusTag(statusTag tag) {
        e.addScoreboardTag(tag.name());
    }
    //test
    public void KnockBack(Entity damager, double knockbackvector) {
        if(canKnockback()) {
            Vector playerdir = damager.getLocation().getDirection();
            playerdir.normalize();
            Vector knockback = playerdir.multiply(knockbackvector);
            e.setVelocity(knockback);
        }
    }

    public void KnockBack(Vector knockbackvector) {
        if(canKnockback()) {
            e.setVelocity(knockbackvector);
        }
    }

    public void KnockBackVectorPSubE(Entity affect, double power) {
        if(!canKnockback()) return;
        Vector pvec = affect.getLocation().toVector();
        Vector evec = e.getLocation().toVector();
        Vector ptoe = evec.subtract(pvec);
        ptoe.normalize();
        e.setVelocity(ptoe.multiply(power));
    }

    @SuppressWarnings("deprecation")
    public void Stun(Entity damager, int tick) {
        if(canStun()) {
            PotionEffect potion = new PotionEffect(PotionEffectType.SLOW, tick, 10);
            e.addPotionEffect(potion, true);
        }
    }
}

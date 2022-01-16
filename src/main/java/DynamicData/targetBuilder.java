package DynamicData;

import ClassAbility.entitycheck;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class targetBuilder {

    private double radius = 0;
    private boolean hitOnlyOne = false;
    private Set<Supplier<Integer>> damage = new HashSet<>();
    private Set<Consumer<LivingEntity>> status = new HashSet<>();
    private Set<BiConsumer<LivingEntity, Integer>> targetAfterDamage = new HashSet<>();
    private Set<Runnable> whenHit = new HashSet<>();
    private Set<Runnable> runOnlyOnce = new HashSet<>();
    private Set<Consumer<Entity>> playParticle = new HashSet<>();
    private Set<Runnable> playSound = new HashSet<>();
    private Set<Consumer<Entity>> playSoundAtEntityLoc = new HashSet<>();
    private Set<Entity> entityExcept = new HashSet<>();
    private Set<Entity> entityHit = new HashSet<>();
    private Player player;
    private Location loc;
    private boolean isBuilt = false;

    private targetBuilder(Player player) {
        this.player = player;
        this.loc = player.getLocation();
        this.playSound.add(() -> player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f));
    }

    public static targetBuilder builder(Player player) {
        return new targetBuilder(player);
    }

    public targetBuilder entityExcept(Entity entity) {
        this.entityExcept.add(entity);
        return this;
    }

    public targetBuilder entityExcept(Set<Entity> entities) {
        this.entityExcept.addAll(entities);
        return this;
    }

    public targetBuilder setLocation(Location loc) {
        this.loc = loc;
        return this;
    }

    public targetBuilder setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public targetBuilder setHitOnlyOne(boolean hitOnlyOne) {
        this.hitOnlyOne = hitOnlyOne;
        return this;
    }

    public targetBuilder setDamage(Supplier<Integer> damage) {
        this.damage.add(damage);
        return this;
    }

    public targetBuilder addPlaySound(Runnable playSound) {
        this.playSound.add(playSound);
        return this;
    }

    public targetBuilder addPlaySound(Consumer<Entity> playSound) {
        this.playSoundAtEntityLoc.add(playSound);
        return this;
    }

    public targetBuilder addPlayParticle(Consumer<Entity> playParticle) {
        this.playParticle.add(playParticle);
        return this;
    }

    public targetBuilder addStatus(Consumer<LivingEntity> status) {
        this.status.add(status);
        return this;
    }

    public targetBuilder addwhenHit(Runnable whenHit) {
        this.whenHit.add(whenHit);
        return this;
    }

    public targetBuilder addRunOnlyOnce(Runnable runOnlyOnce) {
        this.runOnlyOnce.add(runOnlyOnce);
        return this;
    }

    public targetBuilder addTargetAfterDamage(BiConsumer<LivingEntity, Integer> targetAfterDamage) {
        this.targetAfterDamage.add(targetAfterDamage);
        return this;
    }

    public boolean isBuilt() {
        return this.isBuilt;
    }

    public Set<Entity> getHitEntity() {
        return entityHit;
    }

    public targetBuilder build() {

        if(hitOnlyOne && entityHit.size()>=1) return this;

        for(LivingEntity entity : player.getWorld().getLivingEntities()) {
            if(entityExcept.contains(entity)) continue;
            if(entitycheck.entitycheck(entity) && entitycheck.duelcheck(entity, player) && entity != player && !entityHit.contains(entity)) {
                Location eloc = entity.getEyeLocation();
                BoundingBox box = entity.getBoundingBox();
                if(eloc.distance(loc) < radius || box.contains(loc.getX(), loc.getY(), loc.getZ())) {
                    targetAfterDamage.forEach((a)->damage.forEach((damage)->a.accept(entity, damage.get())));
                    damage.forEach((a)->Damage.getinstance().taken(a.get(), entity, player));
                    entityHit.add(entity);
                    playSound.forEach(Runnable::run);
                    whenHit.forEach(Runnable::run);
                    runOnlyOnce.forEach(Runnable::run);
                    runOnlyOnce.clear();
                    playSoundAtEntityLoc.forEach((a) -> a.accept(entity));
                    playParticle.forEach((a)->a.accept(entity));
                    status.forEach((a) -> a.accept(entity));
                    if(hitOnlyOne) return this;
                    isBuilt = true;
                }
            }
        }
        return this;
    }


}

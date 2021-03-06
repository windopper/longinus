package utils;

import ClassAbility.entitycheck;
import DynamicData.Damage;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class targetBuilder {

    private double radius = 0;
    private boolean hitOnlyOne = false;
    private Set<Supplier<Integer>> damage = new HashSet<>();
    private Set<Function<Entity, Integer>> damageFunction = new HashSet<>();
    private Set<BiConsumer<LivingEntity, Integer>> targetAfterDamage = new HashSet<>();
    private Set<Runnable> runOnlyOnce = new HashSet<>();
    private Set<Consumer<LivingEntity>> runOnlyOnceWhenEntityExist = new HashSet<>();
    private Set<Runnable> runWhenEntityHit = new HashSet<>();
    private Set<Consumer<LivingEntity>> runWhenEntityExist = new HashSet<>();
    private Set<Entity> entityExcept = new HashSet<>();
    private Set<Entity> entityHit = new HashSet<>();
    private Player player;
    private Location loc;
    private boolean isBuilt = false;

    private targetBuilder(Player player) {
        this.player = player;
        this.loc = player.getLocation();
        this.runWhenEntityHit.add(() -> player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 2f));
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

    public targetBuilder setDamage(Function<Entity, Integer> damage) {
        this.damageFunction.add(damage);
        return this;
    }

    public targetBuilder addRunWhenEntityExist(Runnable playSound) {
        this.runWhenEntityHit.add(playSound);
        return this;
    }

    public targetBuilder addRunWhenEntityExist(Consumer<LivingEntity> playSound) {
        this.runWhenEntityExist.add(playSound);
        return this;
    }

    public targetBuilder addRunOnlyOnceWhenEntityExist(Runnable runOnlyOnce) {
        this.runOnlyOnce.add(runOnlyOnce);
        return this;
    }

    public targetBuilder addRunOnlyOnceWhenEntityExist(Consumer<LivingEntity> consumer) {
        this.runOnlyOnceWhenEntityExist.add(consumer);
        return this;
    }

    public targetBuilder addTargetAfterDamage(BiConsumer<LivingEntity, Integer> targetAfterDamage) {
        this.targetAfterDamage.add(targetAfterDamage);
        return this;
    }

    public targetBuilder clone() {
        try {
            targetBuilder clonetb = targetBuilder.builder(player);

            Class fromClass = this.getClass();
            Class toClass = clonetb.getClass();
            Field[] fromField = fromClass.getDeclaredFields();
            Field[] toField = toClass.getDeclaredFields();
            Arrays.stream(fromField).forEach((fromfield)-> {
                fromfield.setAccessible(true);
                for(Field tofield : toField) {
                    tofield.setAccessible(true);
                    if(tofield.getName().equals(fromfield.getName())) {
                        try {
                            tofield.set(clonetb, fromfield.get(this));
                            tofield.setAccessible(false);
                            break;
                        }
                        catch(IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                fromfield.setAccessible(false);
            });
            return clonetb;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void deleteHitSet() {
        entityHit.clear();
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
                    damage.forEach((a)-> Damage.getinstance().taken(a.get(), entity, player));
                    damageFunction.forEach((a)->Damage.getinstance().taken(a.apply(entity), entity, player));

                    if(entityHit.size()==0) {
                        runOnlyOnceWhenEntityExist.forEach((a)->a.accept(entity));
                        runOnlyOnce.forEach(Runnable::run);
                    }
                    entityHit.add(entity);
                    runWhenEntityHit.forEach(Runnable::run);
                    runWhenEntityExist.forEach((a)->a.accept(entity));

                    if(hitOnlyOne) return this;
                    isBuilt = true;
                }
            }
        }
        return this;
    }


}

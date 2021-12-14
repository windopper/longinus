package CustomEvents;

import Mob.MobListManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomMobDeathEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    private Entity e;
    private MobListManager.MobList mobList;

    public CustomMobDeathEvent(Entity entity, MobListManager.MobList mobList) {
        this.e = entity;
        this.mobList = mobList;
    }

    public Entity getEntity() { return e; }

    public MobListManager.MobList getMobList() { return mobList; }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

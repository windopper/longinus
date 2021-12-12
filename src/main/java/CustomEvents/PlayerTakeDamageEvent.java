package CustomEvents;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerTakeDamageEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    private Entity damager;
    private Player target;
    private int damage;

    public PlayerTakeDamageEvent(Entity damager, Player target, int damage) {

        this.damager = damager;
        this.target = target;
        this.damage = damage;
    }

    public Entity getDamager() { return damager; }
    public Player getTarget() { return target; }
    public int getDamage() { return damage; }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}

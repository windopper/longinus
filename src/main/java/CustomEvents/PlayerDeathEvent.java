package CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDeathEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;

    private Player player;

    public PlayerDeathEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() { return player; }

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

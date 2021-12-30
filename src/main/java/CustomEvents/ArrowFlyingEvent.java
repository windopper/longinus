package CustomEvents;

import org.bukkit.entity.Arrow;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArrowFlyingEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    final private Arrow arrow;
    final private String arrowname;

    public ArrowFlyingEvent(Arrow arrow, String arrowname) {
        this.arrow = arrow;
        this.arrowname = arrowname;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Arrow getArrow() {
        return arrow;
    }

    public String getArrowname() {
        return arrowname;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

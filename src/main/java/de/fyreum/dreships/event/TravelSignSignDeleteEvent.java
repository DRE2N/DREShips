package de.fyreum.dreships.event;

import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TravelSignSignDeleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final TravelSign travelSign;

    public TravelSignSignDeleteEvent(TravelSign travelSign) {
        this.travelSign = travelSign;
    }

    public TravelSign getTravelSign() {
        return travelSign;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

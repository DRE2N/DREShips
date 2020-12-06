package de.fyreum.dreships.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ShipTeleportationEvent extends PlayerEvent implements Cancellable {

    private final HandlerList handlerList;
    private final Location from;
    private Location to;
    private boolean cancelled = false;

    public ShipTeleportationEvent(@NotNull Player player, Location from, Location to) {
        super(player);
        this.handlerList = new HandlerList();
        this.from = from;
        this.to = to;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public Location getLocation() {
        return from;
    }

    public Location getDestination() {
        return to;
    }

    public void setDestination(Location to) {
        this.to = to;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

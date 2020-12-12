package de.fyreum.dreships.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class TravelSignTeleportationPreparationEvent extends PlayerEvent implements Cancellable, Skippable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private boolean skipped;

    public TravelSignTeleportationPreparationEvent(@NotNull Player player) {
        this(player, false);
    }

    public TravelSignTeleportationPreparationEvent(@NotNull Player player, boolean skipped) {
        super(player);
        this.skipped = skipped;
    }

    @Override
    public boolean isSkipped() {
        return skipped;
    }

    @Override
    public void setSkipped(boolean skip) {
        this.skipped = skip;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

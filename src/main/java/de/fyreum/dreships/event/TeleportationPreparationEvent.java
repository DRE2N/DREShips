package de.fyreum.dreships.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class TeleportationPreparationEvent extends PlayerEvent implements Cancellable, Skippable {

    private final HandlerList handlerList;
    private boolean cancelled = false;
    private boolean skipped;

    public TeleportationPreparationEvent(@NotNull Player player) {
        this(player, false);
    }

    public TeleportationPreparationEvent(@NotNull Player player, boolean skipped) {
        super(player);
        this.handlerList = new HandlerList();
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
        return handlerList;
    }

}

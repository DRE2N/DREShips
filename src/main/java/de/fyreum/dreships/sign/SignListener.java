package de.fyreum.dreships.sign;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.event.TravelSignCreateEvent;
import de.fyreum.dreships.event.TravelSignSignDeleteEvent;
import de.fyreum.dreships.util.TeleportationUtil;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

    private final DREShips plugin = DREShips.getInstance();
    private final TeleportationUtil teleportationUtil = DREShips.getInstance().getTeleportationUtil();

    @EventHandler
    public void handleSignInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getPlayer().isSneaking()) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!DREShips.isSign(event.getClickedBlock())) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        TravelSign travelSign;
        try {
            travelSign = new TravelSign(sign);
        } catch (IllegalArgumentException i) {
            return;
        }
        if (travelSign.isDisabled()) {
            return;
        }
        if (!TravelSign.travelSign(travelSign.getDestination().getBlock())) {
            plugin.getSignManager().disable(sign);
            ShipMessage.WARN_DISABLED_SIGN.sendMessage(event.getPlayer());
            return;
        }
        if (!teleportationUtil.isTeleporting(event.getPlayer())) {
            teleportationUtil.teleport(event.getPlayer(), travelSign, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handleSignBreak(BlockBreakEvent event) {
        if (!DREShips.isSign(event.getBlock())) {
            return;
        }
        Sign sign = (Sign) event.getBlock().getState();
        if (!TravelSign.travelSign(sign)) {
            return;
        }
        event.setCancelled(true);
        MessageUtil.sendActionBarMessage(event.getPlayer(), ShipMessage.ERROR_BREAK_DENIED.getMessage());
    }

    @EventHandler
    public void listCreatedSign(TravelSignCreateEvent event) {
        plugin.getSignConfig().getSignContainer().add(event.getTravelSign());
    }

    @EventHandler
    public void removeListedSign(TravelSignSignDeleteEvent event) {
        plugin.getSignConfig().getSignContainer().remove(event.getTravelSign().getLocation());
    }
}

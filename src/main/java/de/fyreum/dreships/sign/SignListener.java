package de.fyreum.dreships.sign;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.function.TeleportationUtil;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

    private final TeleportationUtil teleportationUtil;

    public SignListener() {
        this.teleportationUtil = new TeleportationUtil(DREShips.getInstance());
    }

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
        TravelSign sign;
        try {
            sign = new TravelSign((Sign) event.getClickedBlock().getState());
        } catch (IllegalArgumentException i) {
            return;
        }
        if (!teleportationUtil.isTeleporting(event.getPlayer())) {
            teleportationUtil.teleport(event.getPlayer(), sign);
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
}

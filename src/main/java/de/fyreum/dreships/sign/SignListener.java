package de.fyreum.dreships.sign;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.event.TravelSignCreateEvent;
import de.fyreum.dreships.event.TravelSignSignDeleteEvent;
import de.fyreum.dreships.util.TeleportationUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

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
            teleportationUtil.teleport(event.getPlayer(), travelSign);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handleSignBreak(BlockBreakEvent event) {
        if (!DREShips.isSign(event.getBlock())) {
            if (signAttachedTo(event.getBlock())) {
                event.setCancelled(true);
                MessageUtil.sendActionBarMessage(event.getPlayer(), ShipMessage.ERROR_BREAK_DENIED.getMessage());
            }
            return;
        }
        Sign sign = (Sign) event.getBlock().getState();
        if (!TravelSign.travelSign(sign)) {
            return;
        }
        event.setCancelled(true);
        MessageUtil.sendActionBarMessage(event.getPlayer(), ShipMessage.ERROR_BREAK_DENIED.getMessage());
    }

    private static class LocationNode {
        private final Location loc;
        private final BlockFace face;

        public LocationNode(Location loc, BlockFace face) {
            this.loc = loc;
            this.face = face;
        }

        public Location getLoc() {
            return loc;
        }

        public BlockFace getFace() {
            return face;
        }
    }

    private boolean signAttachedTo(Block block) {
        Location location = block.getLocation();
        List<LocationNode> locations = Arrays.asList(
                new LocationNode(location.clone().add(1, 0, 0), BlockFace.EAST), // x -1
                new LocationNode(location.clone().add(0, 0, 1), BlockFace.NORTH), // z +1
                new LocationNode(location.clone().subtract(1, 0, 0), BlockFace.WEST), // x -1
                new LocationNode(location.clone().subtract(0, 0, 1), BlockFace.SOUTH) // z -1
        );
        Location locUp = location.clone().add(0, 1, 0);
        if (locUp.getBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign && TravelSign.travelSign(locUp.getBlock())) {
            return true;
        }
        for (LocationNode loc : locations) {
            if (!TravelSign.travelSign(loc.getLoc().getBlock())) {
                continue;
            }
            Sign sign = (Sign) loc.getLoc().getBlock().getState();
            if (sign.getBlockData() instanceof WallSign) {
                WallSign wallSign = (WallSign) sign.getBlockData();
                if (wallSign.getFacing().getOppositeFace().equals(loc.getFace())) {
                    return true;
                }
            }
        }
        return false;
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

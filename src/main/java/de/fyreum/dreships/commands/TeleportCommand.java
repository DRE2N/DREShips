package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.util.TeleportationUtil;
import de.fyreum.dreships.sign.SignManager;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand extends DRECommand {

    private final DREShips plugin = DREShips.getInstance();
    private final TeleportationUtil teleportationUtil = plugin.getTeleportationUtil();

    public TeleportCommand() {
        setCommand("teleport");
        setAliases("tp");
        setMaxArgs(5);
        setHelp("This command is for internal use only!");
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        String identifier = args[1];

        if (TeleportationUtil.verifyIdentifier(identifier)) {
            if (!TeleportationUtil.whitelistedPlayer(player.getUniqueId())) {
                ShipMessage.CMD_TP_NOT_WHITELISTED.sendMessage(player, "" + plugin.getShipConfig().getWhitelistedTeleportationTime() / 20);
                return;
            }
            tryTeleport(args, player);
        } else if (ListCommand.verifyIdentifier(identifier)) {
            dsListTeleport(args, player);
        } else {
            MessageUtil.sendMessage(player, "&cThis command is for internal use only!");
        }
    }

    private void tryTeleport(String[] args, Player player) {
        try {
            Location location = new Location(Bukkit.getWorld(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
            if (!DREShips.isSign(location.getBlock())) {
                MessageUtil.log("&cCouldn't teleport through command, sign not found. This error should not appear if only this plugin uses /ds teleport");
                MessageUtil.log("&cLocation: " + SignManager.simplify(location));
                MessageUtil.sendMessage(player, "&cSIGN NOT FOUND, please contact an Administrator.");
                return;
            }
            TravelSign sign = new TravelSign((Sign) location.getBlock().getState());

            if (!teleportationUtil.isTeleporting(player)) {
                teleportationUtil.teleport(player, sign, true, false);
            }
        } catch (IllegalArgumentException e) {
            MessageUtil.log("&cCouldn't teleport through command, sign not found or incorrect. This error should not appear if only this plugin uses /ds teleport");
            MessageUtil.sendMessage(player, "&cSIGN NOT FOUND OR INCORRECT, please contact an Administrator.");
        }
    }

    private void dsListTeleport(String[] args, Player player) {
        Location location = new Location(Bukkit.getWorld(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
        Block block = location.getBlock();
        if (block.getBlockData() instanceof WallSign) {
            WallSign signData = (WallSign) block.getState().getBlockData();
            player.teleportAsync(location.add(0.5, 0, 0.5).setDirection(signData.getFacing().getDirection()));
        } else if (location.getBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign) {
            org.bukkit.block.data.type.Sign sign = (org.bukkit.block.data.type.Sign) block.getState().getBlockData();
            player.teleportAsync(location.add(0.5, 0, 0.5).setDirection(sign.getRotation().getDirection()));
        } else {
            player.teleportAsync(location.add(0.5, 0, 0.5));
        }
    }
}

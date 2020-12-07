package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.function.TeleportationUtil;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

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
        if (!TeleportationUtil.getCommandVerifier().equals(args[1])) {
            MessageUtil.sendMessage(player, "&cThis command is for internal use only!");
            return;
        }
        try {
            Location location = new Location(Bukkit.getWorld(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
            if (!DREShips.isSign(location.getBlock())) {
                MessageUtil.log("&cCouldn't teleport through command, sign not found. This error should not appear if only this plugin uses /ds teleport");
                MessageUtil.sendMessage(player, "&cSIGN NOT FOUND, please contact an Administrator.");
                return;
            }
            TravelSign sign = new TravelSign((Sign) location.getBlock().getState());

            if (!TeleportationUtil.whitelistedPlayer(player.getUniqueId())) {
                ShipMessage.CMD_TP_NOT_WHITELISTED.sendMessage(player, "" + plugin.getShipConfig().getWhitelistedTeleportationTime()/20);
                return;
            }
            if (!teleportationUtil.isTeleporting(player)) {
                teleportationUtil.teleport(player, sign, true);
            }
        } catch (IllegalArgumentException e) {
            MessageUtil.log("&cCouldn't teleport through command, sign not found or incorrect. This error should not appear if only this plugin uses /ds teleport");
            MessageUtil.sendMessage(player, "&cSIGN NOT FOUND OR INCORRECT, please contact an Administrator.");
        }
    }
}

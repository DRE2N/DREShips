package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnableCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();

    public EnableCommand() {
        setCommand("enable");
        setAliases("en", "activate");
        setMaxArgs(0);
        setHelp("/ds enable");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.enable");
    }

    @Override
    public void onExecute(String[] strings, CommandSender commandSender) {
        Player player = (Player) commandSender;
        Block target = player.getTargetBlock(8);
        if (target == null || !DREShips.isSign(target)) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_TARGET_NO_SIGN.getMessage());
            return;
        }
        Sign sign = (Sign) target.getState();
        if (!TravelSign.travelSign(sign)) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_TARGET_NO_TRAVEL_SIGN.getMessage());
            return;
        }
        TravelSign travelSign = new TravelSign(sign);
        plugin.getSignManager().enable(travelSign);
        ShipMessage.CMD_ENABLE_SUCCESS.sendMessage(player);
    }

}

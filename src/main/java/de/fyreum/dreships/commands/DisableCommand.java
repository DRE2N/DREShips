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

public class DisableCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();

    public DisableCommand() {
        setCommand("disable");
        setAliases("dis");
        setMaxArgs(0);
        setHelp("/ds disable");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.disable");
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
        plugin.getSignManager().disable(travelSign);
        ShipMessage.CMD_DISABLE_SUCCESS.sendMessage(player);
    }
}

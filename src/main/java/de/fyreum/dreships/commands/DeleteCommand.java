package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.SignManager;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();
    SignManager signManager = plugin.getSignManager();

    public DeleteCommand() {
        setCommand("delete");
        setAliases("del", "remove");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp("No help?");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.delete");
    }

    @Override
    public void onExecute(String[] strings, CommandSender commandSender) {
        Player player = (Player) commandSender;
        Block target = player.getTargetBlock(8);
        if (target == null || !TravelSign.travelSign(target)) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_TARGET_NO_SIGN.getMessage());
            return;
        }
        if (signManager.delete(commandSender, (Sign) target.getState()) == 0) {
            ShipMessage.CMD_DELETE_SUCCESS.sendMessage(commandSender, "0");
        }
    }
}

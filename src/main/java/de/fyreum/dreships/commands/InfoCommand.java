package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand extends DRECommand {

    public InfoCommand() {
        setCommand("info");
        setAliases("i");
        setMaxArgs(0);
        setHelp("No help?");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.info");
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        Block target = player.getTargetBlock(8);
        if (target == null || !TravelSign.travelSign(target)) {
            MessageUtil.sendMessage(player, ShipMessage.CMD_INFO_NO_SIGN.getMessage());
            return;
        }
        TravelSign sign = new TravelSign((Sign) target.getState());
        MessageUtil.sendMessage(player, ShipMessage.CMD_INFO_TRAVEL_SIGN.getMessage(sign.getName(),
                sign.getDestinationName(), sign.getDestination().toString(), String.valueOf(sign.getPrice())));
    }
}

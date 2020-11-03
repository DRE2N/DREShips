package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.erethon.commons.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.CacheSignException;
import de.fyreum.dreships.sign.SignManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();
    SignManager signManager = plugin.getSignManager();

    public CreateCommand() {
        setCommand("create");
        setAliases("c", "cr");
        setMaxArgs(1);
        setHelp("No help?");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.create");
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (args[1] == null) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_MISSING_ARGUMENTS.getMessage());
            return;
        }
        try {
            double multipliedDistance = signManager.getPriceCalculation().getDistanceMultiplier(args[1]);
            if (multipliedDistance < 0) {
                int price;
                try {
                    price = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    MessageUtil.sendMessage(player, ShipMessage.ERROR_PRICE_INVALID.getMessage());
                    return;
                }
                MessageUtil.sendMessage(player, ShipMessage.CMD_CREATE_START.getMessage());
                signManager.createFromCache(player.getUniqueId(), price);
            } else {
                MessageUtil.sendMessage(player, ShipMessage.CMD_CREATE_START.getMessage());
                signManager.calculateAndCreateFromCache(player.getUniqueId(), multipliedDistance);
            }
        } catch (CacheSignException c) {
            MessageUtil.sendMessage(player, ShipMessage.CMD_CACHE_EMPTY.getMessage());
        }
    }
}

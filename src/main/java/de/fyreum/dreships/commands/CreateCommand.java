package de.fyreum.dreships.commands;

import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.SignManager;
import de.fyreum.dreships.sign.cache.CacheSignException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();
    SignManager signManager = plugin.getSignManager();

    public CreateCommand() {
        setCommand("create");
        setAliases("c", "cr");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp("/ds create [price]");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.create");
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (args[1] == null) {
            ShipMessage.ERROR_MISSING_ARGUMENTS.sendMessage(player);
            return;
        }
        try {
            double multipliedDistance = plugin.getPriceCalculationUtil().getDistanceMultiplier(args[1]);
            if (multipliedDistance < 0) {
                int price;
                try {
                    price = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    ShipMessage.ERROR_PRICE_INVALID.sendMessage(player);
                    return;
                }
                signManager.createFromCache(commandSender, player.getUniqueId(), price);
            } else {
                signManager.calculateAndCreateFromCache(commandSender, player.getUniqueId(), multipliedDistance);
            }
        } catch (CacheSignException c) {
            ShipMessage.CMD_CACHE_EMPTY.sendMessage(player);
        }
    }
}

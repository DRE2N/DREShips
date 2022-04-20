package de.fyreum.dreships.commands;

import de.erethon.bedrock.command.ECommand;
import de.erethon.bedrock.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.cache.PlayerCache;
import de.fyreum.dreships.util.PriceCalculationUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CalculateCommand extends ECommand {

    DREShips plugin = DREShips.getInstance();

    public CalculateCommand() {
        setCommand("calculate");
        setAliases("cal", "price", "p");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp("/ds calculate [multiplier]");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.calculate");
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;

        PlayerCache playerCache = plugin.getPlayerCache();
        if (!playerCache.isFull(player.getUniqueId())) {
            ShipMessage.CMD_CACHE_EMPTY.sendMessage(player);
            return;
        }

        PriceCalculationUtil priceCalculation = plugin.getPriceCalculationUtil();

        double multiplier = NumberUtil.parseDouble(args[1], priceCalculation.getDistanceMultiplier(args[1]));
        if (multiplier == -1) {
            ShipMessage.CMD_CALCULATE_MULTIPLIER_INVALID.sendMessage(player);
            return;
        }

        double distance = playerCache.getFirst(player.getUniqueId()).getSign().getLocation().distance(playerCache.getSecond(player.getUniqueId()).getSign().getLocation());
        Economy economy = plugin.getEconomy();
        String distanceString = economy == null ? priceCalculation.calculate(distance, multiplier) + "" : economy.format(priceCalculation.calculate(distance, multiplier));
        ShipMessage.CMD_CALCULATE_SUCCESS.sendMessage(player, Math.ceil(distance) + "", distanceString);
    }
}

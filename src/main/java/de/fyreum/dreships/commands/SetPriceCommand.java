package de.fyreum.dreships.commands;

import de.erethon.commons.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class SetPriceCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();

    public SetPriceCommand() {
        setCommand("setPrice");
        setAliases("price");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp("/ds setPrice [price]");
        setPermission("dreships.cmd.setPrice");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        int price = NumberUtil.parseInt(args[1]);
        plugin.getSignManager().setPrice(travelSign, price);
        ShipMessage.CMD_SET_PRICE_SUCCESS.sendMessage(player);
    }
}

package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class EnableCommand extends TravelSignCommand {

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
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        plugin.getSignManager().enable(travelSign);
        ShipMessage.CMD_ENABLE_SUCCESS.sendMessage(player);
    }

}

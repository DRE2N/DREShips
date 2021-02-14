package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class SpecificWorldCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();

    public SpecificWorldCommand() {
        setCommand("specificWorld");
        setAliases("specific");
        setMaxArgs(0);
        setHelp("/ds specificWorld");
        setPermission("dreships.cmd.specificWorld");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        plugin.getSignManager().worldSpecific(travelSign);
        ShipMessage.CMD_SPECIFIC_WORLD_SUCCESS.sendMessage(player);
    }
}

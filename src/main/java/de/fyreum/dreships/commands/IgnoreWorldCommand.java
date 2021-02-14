package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class IgnoreWorldCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();

    public IgnoreWorldCommand() {
        setCommand("ignoreWorld");
        setAliases("ignore");
        setMaxArgs(0);
        setHelp("/ds ignoreWorld");
        setPermission("dreships.cmd.ignoreWorld");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        plugin.getSignManager().ignoreWorld(travelSign);
        ShipMessage.CMD_IGNORE_WORLD_SUCCESS.sendMessage(player);
    }
}

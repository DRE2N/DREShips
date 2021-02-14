package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.SignManager;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class DeleteCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();
    SignManager signManager = plugin.getSignManager();

    public DeleteCommand() {
        setCommand("delete");
        setAliases("del", "remove");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp("/ds delete");
        setPermission("dreships.cmd.delete");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        if (signManager.delete(player, travelSign) == 0) {
            ShipMessage.CMD_DELETE_SUCCESS.sendMessage(player, "0");
        }
    }
}

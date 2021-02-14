package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class RemoveMessageCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();

    public RemoveMessageCommand() {
        setCommand("removeMessage");
        setAliases("rmsg");
        setMaxArgs(0);
        setHelp("/ds removeMessage");
        setPermission("dreships.cmd.removeMessage");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        plugin.getSignManager().removeMessage(travelSign);
        ShipMessage.CMD_REMOVE_MESSAGE_SUCCESS.sendMessage(player);
    }
}

package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class RenameCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();

    public RenameCommand() {
        setCommand("rename");
        setAliases("rn", "name");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp("/ds rename [name]");
        setPermission("dreships.cmd.rename");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        String oldName = travelSign.getName();
        String newName = args[1];
        plugin.getSignManager().rename(travelSign, newName);
        ShipMessage.CMD_RENAME_SUCCESS.sendMessage(player, oldName, newName);
    }
}

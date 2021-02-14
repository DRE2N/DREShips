package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class CheckCommand extends TravelSignCommand {

    private final DREShips plugin = DREShips.getInstance();

    public CheckCommand() {
        setCommand("check");
        setAliases("ch");
        setMaxArgs(0);
        setHelp("/ds check");
        setPermission("dreships.cmd.check");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        if (plugin.getSignManager().check(player, travelSign)) {
            ShipMessage.CMD_CHECK_SIGN_IS_CORRECT.sendMessage(player);
        }
    }
}

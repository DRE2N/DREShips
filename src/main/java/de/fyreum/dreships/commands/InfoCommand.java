package de.fyreum.dreships.commands;

import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.SignManager;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class InfoCommand extends TravelSignCommand {

    public InfoCommand() {
        setCommand("info");
        setAliases("i");
        setMaxArgs(0);
        setHelp("/ds info");
        setPermission("dreships.cmd.info");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        ShipMessage.CMD_INFO_TRAVEL_SIGN.sendMessage(
                player,
                travelSign.getName(),
                SignManager.simplify(travelSign.getLocation()),
                travelSign.getDestinationName(),
                SignManager.simplify(travelSign.getDestination()),
                String.valueOf(travelSign.getPrice()),
                booleanMessage(travelSign.isDisabled()),
                booleanMessage(travelSign.isIgnoreWorld()),
                String.valueOf(travelSign.getCooldown())
        );
    }

    private String booleanMessage(boolean bool) {
        if (bool) {
            return "&atrue";
        } else {
            return "&cfalse";
        }
    }
}

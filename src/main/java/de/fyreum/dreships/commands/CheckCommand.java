package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand extends DRECommand {

    private final DREShips plugin = DREShips.getInstance();

    public CheckCommand() {
        setCommand("check");
        setAliases("ch");
        setMaxArgs(0);
        setHelp("/ds check");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.check");
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        Block target = player.getTargetBlock(8);
        if (target == null || !DREShips.isSign(target)) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_TARGET_NO_SIGN.getMessage());
            return;
        }
        if (!TravelSign.travelSign(target)) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_TARGET_NO_TRAVEL_SIGN.getMessage());
            return;
        }
        TravelSign sign = new TravelSign((Sign) target.getState());
        boolean correct = true;

        if (!plugin.getSignConfig().getSignContainer().contains(sign)) {
            plugin.getSignConfig().getSignContainer().add(sign);
            ShipMessage.CMD_CHECK_SIGN_WAS_NOT_LISTED.sendMessage(player);
            correct = false;
        }
        if (!plugin.getSignConfig().getSignContainer().contains(sign.getDestination())) {
            Block destinationBlock = sign.getDestination().getBlock();
            if (!TravelSign.travelSign(destinationBlock)) {
                return;
            }
            plugin.getSignConfig().getSignContainer().add(new TravelSign((Sign) destinationBlock.getState()));
            ShipMessage.CMD_CHECK_DESTINATION_WAS_NOT_LISTED.sendMessage(player);
            correct = false;
        }
        if (correct) {
            ShipMessage.CMD_CHECK_SIGN_IS_CORRECT.sendMessage(player);
        }
    }
}

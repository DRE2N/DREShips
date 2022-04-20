package de.fyreum.dreships.commands;

import de.erethon.bedrock.command.ECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class TravelSignCommand extends ECommand {

    public TravelSignCommand() {
        setConsoleCommand(false);
        setPlayerCommand(true);
    }

    @Override
    public final void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        Block target = player.getTargetBlock(8);
        if (target == null || !DREShips.isSign(target)) {
            ShipMessage.ERROR_TARGET_NO_SIGN.sendMessage(player);
            return;
        }
        Sign sign = (Sign) target.getState();
        if (!TravelSign.travelSign(sign)) {
            ShipMessage.ERROR_TARGET_NO_TRAVEL_SIGN.sendMessage(player);
            return;
        }
        TravelSign travelSign = new TravelSign(sign);
        travelSign.updateWorld(player.getWorld());

        onExecute(travelSign, args, player);
    }

    public abstract void onExecute(TravelSign travelSign, String[] args, Player player);
}

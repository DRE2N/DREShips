package de.fyreum.dreships.commands;

import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import de.fyreum.dreships.sign.cache.CacheSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();

    public SaveCommand() {
        setCommand("save");
        setAliases("s", "sv");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp("/ds save [name]");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.save");
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        Sign sign;
        Block target = player.getTargetBlock(8);
        if (args[1] == null) {
            ShipMessage.ERROR_MISSING_ARGUMENTS.sendMessage(player);
            return;
        }
        if (target == null) {
            ShipMessage.ERROR_TARGET_BLOCK_INVALID.sendMessage(player);
            return;
        }
        if (DREShips.isSign(target)) {
            sign = (Sign) target.getState();
            if (TravelSign.travelSign(sign)) {
                ShipMessage.CMD_SAVE_ALREADY_SIGN.sendMessage(player);
                return;
            }
        } else {
            ShipMessage.ERROR_TARGET_NO_SIGN.sendMessage(player);
            return;
        }
        plugin.getPlayerCache().save(player.getUniqueId(), new CacheSign(sign, args[1]));
        ShipMessage.CMD_SAVE_SUCCESS.sendMessage(player);
    }
}

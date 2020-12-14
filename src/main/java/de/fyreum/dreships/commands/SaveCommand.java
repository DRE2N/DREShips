package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.SignManager;
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
            MessageUtil.sendMessage(player, ShipMessage.ERROR_MISSING_ARGUMENTS.getMessage());
            return;
        }
        if (target == null) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_TARGET_BLOCK_INVALID.getMessage());
            return;
        }
        if (DREShips.isSign(target)) {
            sign = (Sign) target.getState();
            if (TravelSign.travelSign(sign)) {
                MessageUtil.sendMessage(player, ShipMessage.CMD_SAVE_ALREADY_SIGN.getMessage());
                return;
            }
        } else {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_TARGET_NO_SIGN.getMessage());
            return;
        }
        plugin.getPlayerCache().save(player.getUniqueId(), new CacheSign(sign, args[1]));
        ShipMessage.CMD_SAVE_SUCCESS.sendMessage(player);
    }
}

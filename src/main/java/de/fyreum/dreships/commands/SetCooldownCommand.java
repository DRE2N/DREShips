package de.fyreum.dreships.commands;

import de.erethon.bedrock.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

public class SetCooldownCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();

    public SetCooldownCommand() {
        setCommand("setCooldown");
        setAliases("cd");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp("/ds setCooldown [seconds]");
        setPermission("dreships.cmd.setCooldown");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        int cooldown = NumberUtil.parseInt(args[1]);
        plugin.getSignManager().setCooldown(travelSign, cooldown);
        ShipMessage.CMD_SET_COOLDOWN_SUCCESS.sendMessage(player, String.valueOf(cooldown));
    }
}

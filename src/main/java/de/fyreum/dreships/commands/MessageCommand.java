package de.fyreum.dreships.commands;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MessageCommand extends TravelSignCommand {

    DREShips plugin = DREShips.getInstance();

    public MessageCommand() {
        setCommand("message");
        setAliases("msg");
        setMinArgs(1);
        setMaxArgs(Integer.MAX_VALUE);
        setHelp("/ds message [text]");
        setPermission("dreships.cmd.message");
    }

    @Override
    public void onExecute(TravelSign travelSign, String[] args, Player player) {
        String msg = toString(Arrays.copyOfRange(args, 1, args.length));
        plugin.getSignManager().setMessage(travelSign, msg);
        ShipMessage.CMD_MESSAGE_SUCCESS.sendMessage(player);
    }

    private String toString(String[] a) {
        int iMax = a.length - 1;

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) {
                return b.toString();
            }
            b.append(" ");
        }
    }
}

package de.fyreum.dreships.commands;

import de.erethon.bedrock.command.ECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends ECommand {

    DREShips plugin = DREShips.getInstance();

    public ReloadCommand() {
        setCommand("reload");
        setAliases("r", "rl");
        setMaxArgs(0);
        setHelp("/ds reload");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.reload");
    }

    @Override
    public void onExecute(String[] strings, CommandSender commandSender) {
        plugin.instantiateShipConfig();
        plugin.getSignConfig().reload();
        plugin.reloadMessages();
        ShipMessage.CMD_RELOAD_SUCCESS.sendMessage(commandSender);
    }
}

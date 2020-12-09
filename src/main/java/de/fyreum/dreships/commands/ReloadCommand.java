package de.fyreum.dreships.commands;

import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends DRECommand {

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
        plugin.instantiateConfig();
        ShipMessage.CMD_RELOAD_success.sendMessage(commandSender);
    }
}

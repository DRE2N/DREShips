
package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.erethon.commons.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HelpCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();

    public HelpCommand() {
        setCommand("help");
        setAliases("h", "?", "main");
        setHelp("/ds help [page]");
        setMinArgs(0);
        setMaxArgs(1);
        setPermission("dreships.cmd.help");
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Set<DRECommand> dCommandSet = plugin.getCommandCache().getCommands();
        List<DRECommand> sorted = dCommandSet.stream()
                .sorted(Comparator.comparing(DRECommand::getCommand))
                .collect(Collectors.toList());
        ArrayList<DRECommand> toSend = new ArrayList<>();

        int page = 1;
        if (args.length == 2) {
            page = NumberUtil.parseInt(args[1], 1);
        }
        int send = 0;
        int max = 0;
        int min = 0;

        int perPage = plugin.getShipConfig().getCommandsPerHelpPage();
        for (DRECommand dCommand : sorted) {
            send++;
            if (send >= page * perPage - (perPage - 1) && send <= page * perPage) {
                min = page * perPage - (perPage - 1);
                max = page * perPage;
                toSend.add(dCommand);
            }
        }

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + min + "-" + max + " &4/&6 " + send + " &4|&6 " + page + " &4&l]");

        for (DRECommand dCommand : toSend) {
            MessageUtil.sendMessage(sender, "&b" + dCommand.getCommand() + "&7 - " + dCommand.getHelp());
        }
    }

}
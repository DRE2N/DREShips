
package de.fyreum.dreships.commands;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.command.ECommand;
import de.erethon.bedrock.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HelpCommand extends ECommand {

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
        Set<ECommand> dCommandSet = plugin.getCommandCache().getCommands();
        List<ECommand> sorted = dCommandSet.stream()
                .sorted(Comparator.comparing(ECommand::getCommand))
                .collect(Collectors.toList());
        ArrayList<ECommand> toSend = new ArrayList<>();

        int page = 1;
        if (args.length == 2) {
            page = NumberUtil.parseInt(args[1], 1);
        }
        int send = 0;
        int max = 0;
        int min = 0;

        int perPage = plugin.getShipConfig().getCommandsPerHelpPage();
        for (ECommand dCommand : sorted) {
            send++;
            if (send >= page * perPage - (perPage - 1) && send <= page * perPage) {
                min = page * perPage - (perPage - 1);
                max = page * perPage;
                toSend.add(dCommand);
            }
        }

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + min + "-" + max + " &4/&6 " + send + " &4|&6 " + page + " &4&l]");

        for (ECommand dCommand : toSend) {
            MessageUtil.sendMessage(sender, "&b" + dCommand.getCommand() + "&7 - " + dCommand.getHelp());
        }
    }

}
package de.fyreum.dreships.commands;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.command.ECommand;
import de.erethon.bedrock.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.sign.ListedTravelSign;
import de.fyreum.dreships.sign.SignManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListCommand extends ECommand {

    private final DREShips plugin = DREShips.getInstance();

    private static final String commandVerifier = UUID.randomUUID().toString();

    public ListCommand() {
        setCommand("list");
        setAliases("l", "li");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp("/ds list [page]");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.list");
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;

        Set<ListedTravelSign> travelSigns = DREShips.getInstance().getSignConfig().getSignContainer().getListedTravelSigns();
        List<ListedTravelSign> sorted = travelSigns.stream()
                .sorted(Comparator.comparing(ListedTravelSign::getName))
                .collect(Collectors.toList());
        ArrayList<ListedTravelSign> toSend = new ArrayList<>();

        int page = 1;
        if (args.length == 2) {
            page = NumberUtil.parseInt(args[1], 1);
        }
        int send = 0;
        int max = 0;
        int min = 0;

        int perPage = plugin.getShipConfig().getSignsPerListPage();
        for (ListedTravelSign travelSign : sorted) {
            send++;
            if (send >= page * perPage - (perPage - 1) && send <= page * perPage) {
                min = page * perPage - (perPage - 1);
                max = page * perPage;
                toSend.add(travelSign);
            }
        }

        MessageUtil.sendCenteredMessage(player, "&8&l[ &1List &8&l]");
        MessageUtil.sendCenteredMessage(player, "&8&l[ &9" + min + "-" + max + " &8/&9 " + send + " &8|&9 " + page + " &8&l]");

        for (ListedTravelSign travelSign : toSend) {
            Component component = Component.join(JoinConfiguration.separator(Component.newline()),
                    buildMessage(travelSign.getName(), travelSign.getLocation()),
                    MessageUtil.parse("<gray> -> "),
                    buildMessage(travelSign.getDestinationName(), travelSign.getDestination()),
                    MessageUtil.parse("<dark_red> - <gold>" + (plugin.getEconomy() != null ? plugin.getEconomy().format(travelSign.getPrice()) : travelSign.getPrice() + " H")));
            player.sendMessage(component);
        }
    }

    private Component buildMessage(String name, Location location) {
        return MessageUtil.parse("<aqua>" + name)
                .hoverEvent(HoverEvent.showText(MessageUtil.parse("<aqua>" + SignManager.simplify(location))))
                .clickEvent(ClickEvent.runCommand(commandString(location)));
    }

    private String commandString(Location loc) {
        return "/ds teleport " + commandVerifier + " " + loc.getWorld().getName() + " " +  loc.getX() + " " +  loc.getY() + " " +  loc.getZ();
    }

    public static boolean verifyIdentifier(String s) {
        return commandVerifier.equals(s);
    }
}

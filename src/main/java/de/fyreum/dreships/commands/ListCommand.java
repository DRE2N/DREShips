package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.erethon.commons.misc.NumberUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.sign.SignManager;
import de.fyreum.dreships.sign.TravelSign;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class ListCommand extends DRECommand {

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
        Set<TravelSign> travelSigns = DREShips.getInstance().getSignConfig().getSignContainer().getTravelSigns();
        List<TravelSign> sorted = travelSigns.stream()
                .sorted(Comparator.comparing(TravelSign::getName))
                .collect(Collectors.toList());
        ArrayList<TravelSign> toSend = new ArrayList<>();

        int page = 1;
        if (args.length == 2) {
            page = NumberUtil.parseInt(args[1], 1);
        }
        int send = 0;
        int max = 0;
        int min = 0;

        int perPage = plugin.getShipConfig().getSignsPerListPage();
        for (TravelSign travelSign : sorted) {
            send++;
            if (send >= page * perPage - (perPage - 1) && send <= page * perPage) {
                min = page * perPage - (perPage - 1);
                max = page * perPage;
                toSend.add(travelSign);
            }
        }

        MessageUtil.sendCenteredMessage(sender, "&8&l[ &1List &8&l]");
        MessageUtil.sendCenteredMessage(sender, "&8&l[ &9" + min + "-" + max + " &8/&9 " + send + " &8|&9 " + page + " &8&l]");

        for (TravelSign travelSign : toSend) {
            MessageUtil.sendMessage(sender,
                    buildMessage(travelSign.getName(), travelSign.getLocation()),
                    new TextComponent(ChatColor.GRAY + " -> "),
                    buildMessage(travelSign.getDestinationName(), travelSign.getDestination()),
                    new TextComponent(ChatColor.DARK_RED + " - " + ChatColor.GOLD + (plugin.getEconomy() != null ? plugin.getEconomy().format(travelSign.getPrice()) : travelSign.getPrice() + " H")));
        }
    }

    private TextComponent buildMessage(String name, Location location) {
        TextComponent loc1 = new TextComponent(ChatColor.AQUA + name);
        loc1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + SignManager.simplify(location))));
        loc1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandString(location)));
        return loc1;
    }

    private String commandString(Location loc) {
        return "/ds teleport " + commandVerifier + " " + loc.getWorld().getName() + " " +  loc.getX() + " " +  loc.getY() + " " +  loc.getZ();
    }

    public static boolean verifyIdentifier(String s) {
        return commandVerifier.equals(s);
    }
}

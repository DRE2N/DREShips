package de.fyreum.dreships.commands;

import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.sign.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportCommand extends DRECommand {

    DREShips plugin = DREShips.getInstance();
    SignManager signManager = plugin.getSignManager();

    public TeleportCommand() {
        setCommand("teleport");
        setAliases("tp");
        setMaxArgs(1);
        setHelp("No help?");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.teleport");
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        Location location = new Location(Bukkit.getWorld(args[1]),
                Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), player.getLocation().getYaw(), player.getLocation().getPitch());
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}

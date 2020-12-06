package de.fyreum.dreships.commands;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.function.TeleportationUtil;
import de.fyreum.dreships.sign.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class TeleportCommand extends DRECommand {

    public TeleportCommand() {
        setCommand("teleport");
        setAliases("tp");
        setMaxArgs(5);
        setHelp("This command is for internal use only!");
        setPlayerCommand(true);
        setConsoleCommand(false);
        setPermission("dreships.cmd.teleport");
    }

    @Override
    public void onExecute(String[] args, CommandSender commandSender) {
        Player player = (Player) commandSender;
        if (!TeleportationUtil.getCommandVerifier().equals(args[1])) {
            MessageUtil.sendMessage(player, "&cThis command is for internal use only!");
            return;
        }
        try {
            Location location = new Location(Bukkit.getWorld(args[2]),
                    Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), player.getLocation().getYaw(), player.getLocation().getPitch());
            player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } catch (NumberFormatException n) {
            MessageUtil.sendMessage(player, "&cSomething went wrong. Please contact an Administrator.");
        }
    }
}

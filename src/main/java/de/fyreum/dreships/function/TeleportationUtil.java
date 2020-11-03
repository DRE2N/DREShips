package de.fyreum.dreships.function;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.TravelSign;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;

import java.util.*;

public class TeleportationUtil {

    private final Economy economy;
    private final List<UUID> currentlyTeleporting;
    private final Map<UUID, Integer> taskIDs;
    private final Map<UUID, Integer> repeats;

    public TeleportationUtil(DREShips plugin) {
        this.economy = plugin.getEconomy();
        this.currentlyTeleporting  = new ArrayList<>();
        this.taskIDs = new HashMap<>();
        this.repeats = new HashMap<>();
    }

    public void teleport(Player player, TravelSign travelSign) {
        if (!economy.has(player, travelSign.getPrice())) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_NO_MONEY.getMessage());
            return;
        }
        Location destination = travelSign.getDestination();
        if (destination.getWorld().getBlockAt((int) destination.getX(), (int) destination.getY() + 1, (int) destination.getZ()).getType().isSolid()) {
            MessageUtil.sendMessage(player, ShipMessage.WARN_SUFFOCATION.getMessage());
            if (player.hasPermission("dreships.cmd.teleport")) {
                player.sendMessage(teleportMessage(travelSign.getDestination()));
            }
            return;
        }
        currentlyTeleporting.add(player.getUniqueId());
        economy.withdrawPlayer(player, travelSign.getPrice());
        teleportAfterCheck(player, travelSign.getDestination());
        MessageUtil.sendMessage(player, ShipMessage.TP_SUCCESS.getMessage(travelSign.getName(), travelSign.getDestinationName(), String.valueOf(travelSign.getPrice())));
    }

    private void teleportAfterCheck(Player player, Location location) {
        if (player.hasPermission("dreships.cmd.teleport")) {
            teleport(player, location, player.getWalkSpeed());
            return;
        }
        float oldSpeed = player.getWalkSpeed();
        player.setWalkSpeed(0);
        UUID uuid = player.getUniqueId();
        repeats.put(uuid, 0);
        taskIDs.put(uuid, Bukkit.getScheduler().scheduleSyncRepeatingTask(DREShips.getInstance(), () -> {
            player.sendActionBar(ChatColor.GREEN + multipliedString(repeats.get(uuid)) + ChatColor.DARK_RED + multipliedString(10 - repeats.get(uuid)));
            if (repeats.get(uuid) == 10) {
                teleport(player, location, oldSpeed);
                Bukkit.getScheduler().cancelTask(taskIDs.get(uuid));
                repeats.remove(uuid);
                taskIDs.remove(uuid);
                return;
            }
            repeats.put(uuid, repeats.get(uuid) + 1);
            }, 0, 20));
    }

    private void teleport(Player player, Location location, float oldSpeed) {
        Block block = location.getBlock();
        if (block.getBlockData() instanceof WallSign) {
            WallSign signData = (WallSign) block.getState().getBlockData();
            player.teleportAsync(location.add(0.5, 0, 0.5).setDirection(signData.getFacing().getDirection()));
        } else if (location.getBlock().getBlockData() instanceof Sign) {
            Sign sign = (Sign) block.getState().getBlockData();
            player.teleportAsync(location.add(0.5, 0, 0.5).setDirection(sign.getRotation().getDirection()));
        } else {
            player.teleportAsync(location.add(0.5, 0, 0.5));
        }
        player.setWalkSpeed(oldSpeed);
        currentlyTeleporting.remove(player.getUniqueId());
    }

    private String multipliedString(int multiply) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < multiply; i++) {
            stringBuilder.append("█");
        }
        return stringBuilder.toString();
    }

    private TextComponent teleportMessage(Location loc) {
        TextComponent component = new TextComponent();
        component.setText(ShipMessage.CMD_TP_SUGGESTION.getMessage());
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandString(loc)));
        return component;
    }

    private String commandString(Location loc) {
        return "/ds teleport " + loc.getWorld().getName() + " " +  loc.getX() + " " +  loc.getY() + " " +  loc.getZ();
    }

    public boolean isTeleporting(Player player) {
        return isTeleporting(player.getUniqueId());
    }

    public boolean isTeleporting(UUID uuid) {
        return currentlyTeleporting.contains(uuid);
    }
}

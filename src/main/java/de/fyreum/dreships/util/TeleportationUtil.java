package de.fyreum.dreships.util;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.faction.Faction;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.event.TravelSignTeleportationEvent;
import de.fyreum.dreships.event.TravelSignTeleportationPreparationEvent;
import de.fyreum.dreships.sign.TravelSign;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TeleportationUtil {

    private final DREShips plugin;
    private final Economy economy;
    private final List<UUID> currentlyTeleporting;
    private static final String commandVerifier = UUID.randomUUID().toString();
    private static final Set<UUID> commandWhitelist = new HashSet<>();

    public TeleportationUtil(DREShips plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
        this.currentlyTeleporting = new ArrayList<>();
    }

    public void teleport(@NotNull Player player, @NotNull TravelSign travelSign) {
        this.teleport(player, travelSign, false);
    }

    public void teleport(@NotNull Player player, @NotNull TravelSign travelSign, boolean ignoreWarnings) {
        this.teleport(player, travelSign, ignoreWarnings, false);
    }

    public void teleport(@NotNull Player player, @NotNull TravelSign travelSign, boolean ignoreWarnings, boolean skip) {
        if (isTeleporting(player)) {
            return;
        }
        if (this.economy != null && !this.economy.has(player, travelSign.getPrice())) {
            ShipMessage.ERROR_NO_MONEY.sendMessage(player);
            return;
        }
        Location destination = travelSign.getDestination();
        if (travelSign.isIgnoreWorld()) {
            destination.setWorld(player.getWorld());
        }

        if (!ignoreWarnings && unsafeDestination(destination)) {
            whitelistPlayerTeleportCommand(player.getUniqueId());
            ShipMessage.WARN_SUFFOCATION.sendMessage(player);
            player.sendMessage(teleportMessage(travelSign.getLocation()));
            return;
        }
        this.teleport(player, destination, travelSign.getPrice(), travelSign.getMessage(), skip, travelSign.getCooldown());
    }

    private void teleport(Player player, Location destination, double price, String message, boolean skip, int seconds) {
        TravelSignTeleportationPreparationEvent preparationEvent = new TravelSignTeleportationPreparationEvent(player);
        if (player.hasPermission("dreships.bypass") | skip) {
            preparationEvent.setSkipped(true);
        }

        if (preparationEvent.callEvent()) {
            if (preparationEvent.isSkipped() | seconds <= 0) {
                TravelSignTeleportationEvent teleportationEvent = new TravelSignTeleportationEvent(player, player.getLocation(), destination);
                if (teleportationEvent.callEvent()) {
                    this.teleport(player, teleportationEvent.getDestination(), price, message);
                }
                return;
            }
            new CooldownTeleportation(player, destination, price, message).run(seconds);
        }
    }

    private void teleport(Player player, Location destination, double price, String message) {
        if (economy != null) {
            economy.withdrawPlayer(player, price);
            if (plugin.getFactionsXL() != null) {
                Faction faction = plugin.getFactionsXL().getFactionCache().getByChunk(player.getChunk());
                if (faction != null) {
                    double tax = price*plugin.getShipConfig().getTaxMultiplier();
                    faction.getAccount().deposit(tax);
                    for (Player member : faction.getMembers().getOnlinePlayers()) {
                        ShipMessage.TP_TAX_MESSAGE.sendMessage(member, player.getName(), economy.format(tax), faction.getShortName());
                    }
                }
            }
        }
        Block block = destination.getBlock();
        if (block.getBlockData() instanceof WallSign) {
            WallSign signData = (WallSign) block.getState().getBlockData();
            player.teleportAsync(destination.add(0.5, 0, 0.5).setDirection(signData.getFacing().getDirection()));
        } else if (destination.getBlock().getBlockData() instanceof Sign) {
            Sign sign = (Sign) block.getState().getBlockData();
            player.teleportAsync(destination.add(0.5, 0, 0.5).setDirection(sign.getRotation().getDirection()));
        } else {
            player.teleportAsync(destination.add(0.5, 0, 0.5));
        }
        MessageUtil.sendActionBarMessage(player, message);
    }

    private void whitelistPlayerTeleportCommand(UUID uuid) {
        commandWhitelist.add(uuid);
        Bukkit.getScheduler().runTaskLater(plugin, () -> commandWhitelist.remove(uuid), plugin.getShipConfig().getWhitelistedTeleportationTime());
    }

    private String multipliedCooldownString(int multiply) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < multiply; i++) {
            stringBuilder.append("█");
        }
        return stringBuilder.toString();
    }

    private TextComponent teleportMessage(@NotNull Location loc) {
        TextComponent component = new TextComponent();
        component.setText(ShipMessage.CMD_TP_SUGGESTION.getMessage());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ShipMessage.CMD_TP_HOVER_TEXT.getMessage())));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandString(loc))); // hate Bukkit for not adding a better way of doing this -.-
        return component;
    }

    private String commandString(@NotNull Location loc) {
        return "/ds teleport " + commandVerifier + " " + loc.getWorld().getName() + " " +  loc.getX() + " " +  loc.getY() + " " +  loc.getZ();
    }

    public boolean isTeleporting(Player player) {
        return isTeleporting(player.getUniqueId());
    }

    public boolean isTeleporting(UUID uuid) {
        return currentlyTeleporting.contains(uuid);
    }

    public boolean unsafeDestination(Location destination) {
        return destination.getWorld().getBlockAt((int) destination.getX(), (int) destination.getY() + 1, (int) destination.getZ()).getType().isSolid();
    }

    public static boolean whitelistedPlayer(UUID uuid) {
        return commandWhitelist.contains(uuid);
    }

    public static boolean verifyIdentifier(String s) {
        return commandVerifier.equals(s);
    }

    class CooldownTeleportation {

        private final Player player;
        private final Location destination;
        private final String message;
        private final Location location;
        private final double price;
        private int repeats = 0;

        public CooldownTeleportation(Player player, Location destination, double price, String message) {
            this.player = player;
            this.destination = destination;
            this.message = message;
            this.price = price;
            this.location = player.getLocation();
        }

        public void run(int seconds) {
            currentlyTeleporting.add(player.getUniqueId());
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getLocation().getBlockX() == location.getBlockX() && player.getLocation().getBlockY() ==
                            location.getBlockY() && player.getLocation().getBlockZ() == location.getBlockZ()) {
                        player.sendActionBar(ChatColor.GREEN + multipliedCooldownString(repeats) + ChatColor.DARK_RED + multipliedCooldownString(seconds - repeats));
                        if (repeats == seconds) {
                            TravelSignTeleportationEvent teleportationEvent = new TravelSignTeleportationEvent(player, player.getLocation(), destination);

                            if (teleportationEvent.callEvent()) {
                                teleport(player, teleportationEvent.getDestination(), price, message);
                            }
                            currentlyTeleporting.remove(player.getUniqueId());
                            cancel();
                            return;
                        }
                        repeats++;
                    } else {
                        currentlyTeleporting.remove(player.getUniqueId());
                        ShipMessage.TP_MOVE_CANCEL.sendActionBar(player);
                        cancel();
                    }
                }
            };
            runnable.runTaskTimer(plugin, 0, 20);
        }
    }
}

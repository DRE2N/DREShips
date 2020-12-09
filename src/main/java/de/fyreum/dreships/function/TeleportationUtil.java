package de.fyreum.dreships.function;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.factionsxl.FactionsXL;
import de.erethon.factionsxl.faction.Faction;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipConfig;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.event.ShipTeleportationEvent;
import de.fyreum.dreships.event.TeleportationPreparationEvent;
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

import java.util.*;

public class TeleportationUtil {

    private final DREShips plugin;
    private final Economy economy;
    private final FactionsXL factionsXL;
    private final List<UUID> currentlyTeleporting;
    private static final String commandVerifier = UUID.randomUUID().toString();
    private static final Set<UUID> commandWhitelist = new HashSet<>();

    public TeleportationUtil(DREShips plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
        this.factionsXL = plugin.getFactionsXL();
        this.currentlyTeleporting = new ArrayList<>();
    }

    public void teleport(@NotNull Player player, @NotNull TravelSign travelSign, boolean ignoreWarnings) {
        if (this.economy != null && !this.economy.has(player, travelSign.getPrice())) {
            MessageUtil.sendMessage(player, ShipMessage.ERROR_NO_MONEY.getMessage());
            return;
        }
        if (!ignoreWarnings && unsafeDestination(travelSign.getDestination())) {
            whitelistPlayer(player.getUniqueId());
            ShipMessage.WARN_SUFFOCATION.sendMessage(player);
            player.sendMessage(teleportMessage(travelSign.getLocation()));
            return;
        }
        String priceString = economy == null ? String.valueOf(travelSign.getPrice()) : String.valueOf(economy.format(travelSign.getPrice()));
        this.teleport(player, travelSign.getDestination(), travelSign.getPrice(),
                ShipMessage.TP_SUCCESS.getMessage(travelSign.getName(), travelSign.getDestinationName(), priceString));
    }

    private void teleport(Player player, Location destination, double price, String message) {
        TeleportationPreparationEvent preparationEvent = new TeleportationPreparationEvent(player);
        if (player.hasPermission("dreships.bypass")) {
            preparationEvent.setSkipped(true);
        }

        if (preparationEvent.callEvent()) {
            if (preparationEvent.isSkipped()) {
                ShipTeleportationEvent teleportationEvent = new ShipTeleportationEvent(player, player.getLocation(), destination);
                if (teleportationEvent.callEvent()) {
                    this.teleportPlayer(player, teleportationEvent.getDestination(), price, message);
                }
                return;
            }
            new CooldownTeleportation(player, destination, price, message).run();
        }
    }

    private void teleportPlayer(Player player, Location destination, double price, String message) {
        if (economy != null) {
            economy.withdrawPlayer(player, price);
            if (factionsXL != null) {
                Faction faction = factionsXL.getFactionCache().getByChunk(player.getChunk());
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

    private void whitelistPlayer(UUID uuid) {
        commandWhitelist.add(uuid);
        Bukkit.getScheduler().runTaskLater(plugin, () -> commandWhitelist.remove(uuid), plugin.getShipConfig().getWhitelistedTeleportationTime());
    }

    private String multipliedString(int multiply) {
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

    public static String getCommandVerifier() {
        return commandVerifier;
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

        public void run() {
            currentlyTeleporting.add(player.getUniqueId());
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getLocation().getBlockX() == location.getBlockX() && player.getLocation().getBlockY() ==
                            location.getBlockY() && player.getLocation().getBlockZ() == location.getBlockZ()) {
                        player.sendActionBar(ChatColor.GREEN + multipliedString(repeats) + ChatColor.DARK_RED + multipliedString(10 - repeats));
                        if (repeats == 10) {
                            ShipTeleportationEvent teleportationEvent = new ShipTeleportationEvent(player, player.getLocation(), destination);

                            if (teleportationEvent.callEvent()) {
                                teleportPlayer(player, teleportationEvent.getDestination(), price, message);
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

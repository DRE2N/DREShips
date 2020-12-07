package de.fyreum.dreships.sign;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.function.PriceCalculation;
import de.fyreum.dreships.persistentdata.ShipDataTypes;
import de.fyreum.dreships.sign.cache.CacheSignException;
import de.fyreum.dreships.sign.cache.PlayerCache;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SignManager {

    private final PlayerCache playerCache;
    private final PriceCalculation priceCalculation;

    public SignManager() {
        this.playerCache = new PlayerCache();
        this.priceCalculation = new PriceCalculation();
    }

    // ----------TravelSign---------

    public void createFromCache(@NotNull CommandSender sender, @NotNull UUID uuid, int price) throws CacheSignException {
        if (!playerCache.isFull(uuid)) {
            throw new CacheSignException("Couldn't create a TravelSign. Player cache is empty.");
        }
        Sign sign = playerCache.getFirst(uuid).getSign();
        String name = playerCache.getFirst(uuid).getName();
        Sign destination = playerCache.getSecond(uuid).getSign();
        String destinationName = playerCache.getSecond(uuid).getName();

        this.create(sender, sign, destination, name, destinationName, price);
        this.playerCache.clear(uuid);
    }

    public void calculateAndCreateFromCache(@NotNull CommandSender sender, @NotNull UUID uuid, double multipliedDistance) throws CacheSignException {
        if (!playerCache.isFull(uuid)) {
            throw new CacheSignException("Couldn't create a TravelSign. Player cache is empty.");
        }
        Sign sign = playerCache.getFirst(uuid).getSign();
        String name = playerCache.getFirst(uuid).getName();
        Sign destination = playerCache.getSecond(uuid).getSign();
        String destinationName = playerCache.getSecond(uuid).getName();

        int price = this.priceCalculation.calculate(sign.getLocation(), destination.getLocation(), multipliedDistance);
        this.create(sender, sign, destination, name, destinationName, price);
        this.playerCache.clear(uuid);
    }

    public void create(@NotNull CommandSender sender, @NotNull Sign sign, @NotNull Sign destination, String name, String destinationName, int price) {
        this.loadAndCreate(sender, sign, destination, name, destinationName, price);
        this.loadAndCreate(sender, destination, sign, destinationName, name, price);
    }

    private void loadAndCreate(@NotNull CommandSender sender, @NotNull Sign sign, @NotNull Sign destination, String name, String destinationName, int price) {
        this.savePersistentData(sign, destination.getLocation(), name, destinationName, price);
        this.visualizeData(sign, name, destinationName, price);
        MessageUtil.sendMessage(sender, ShipMessage.CMD_CREATE_SUCCESS.getMessage(simplify(sign.getLocation())));
        /*  // failed performance friendly concept (Sign changes won't save)
        BukkitRunnable runnable = new BukkitRunnable() {
            final CompletableFuture<Chunk> completableFuture = sign.getWorld().getChunkAtAsync(sign.getLocation());
            @Override
            public void run() {
                if (completableFuture.isDone()) {
                    try {
                        savePersistentData(sign, destination.getLocation(), name, destinationName, price);
                        visualizeData(sign, name, destinationName, price);
                        MessageUtil.broadcastMessage(ShipMessage.CMD_CREATE_SUCCESS.getMessage(simplify(sign.getLocation()),
                                 String.valueOf(completableFuture.get() == null)));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        cancel();
                    }
                    cancel();
                }
            }
        };
        runnable.runTaskTimerAsynchronously(DREShips.getInstance(), 20,20);
        */
    }

    private void savePersistentData(@NotNull Sign sign, Location destination, String name, String destName, int price) {
        sign.getPersistentDataContainer().set(TravelSign.getNameKey(), PersistentDataType.STRING, name);
        sign.getPersistentDataContainer().set(TravelSign.getDestinationNameKey(), PersistentDataType.STRING, destName);
        sign.getPersistentDataContainer().set(TravelSign.getDestinationKey(), ShipDataTypes.LOCATION, destination);
        sign.getPersistentDataContainer().set(TravelSign.getPriceKey(), PersistentDataType.INTEGER, price);
        sign.update(true);
    }

    private void visualizeData(Sign sign, String name, String destName, int price) {
        sign.setLine(0, ShipMessage.SIGN_LINE_ONE.getMessage());
        sign.setLine(1, ShipMessage.SIGN_LINE_TWO.getMessage(name));
        sign.setLine(2, ShipMessage.SIGN_LINE_THREE.getMessage(String.valueOf(price)));
        sign.setLine(3, ShipMessage.SIGN_LINE_FOUR.getMessage(destName));
        sign.update(true);
    }

    // returns the amount of deleted signs
    public int delete(CommandSender sender, Sign sign) {
        if (!TravelSign.travelSign(sign)) {
            return 0;
        }
        Block destinationBlock = new TravelSign(sign).getDestination().getBlock();
        Sign destination = DREShips.isSign(destinationBlock) ? (Sign) new TravelSign(sign).getDestination().getBlock().getState() : null;
        return loadAndDelete(sender, sign) + loadAndDelete(sender, destination);
    }

    private int loadAndDelete(@NotNull CommandSender sender, @Nullable Sign sign) {
        if (sign == null) {
            return 0;
        }
        this.deletePersistentData(sign);
        this.clearLines(sign);
        MessageUtil.sendMessage(sender, ShipMessage.CMD_DELETE_SUCCESS.getMessage(simplify(sign.getLocation())));
        return 1;
        /* // failed performance friendly concept (Sign changes won't save)
        BukkitRunnable runnable = new BukkitRunnable() {
            final CompletableFuture<Chunk> completableFuture = sign.getWorld().getChunkAtAsync(sign.getLocation());
            @Override
            public void run() {
                if (completableFuture.isDone()) {
                    try {
                        deletePersistentData(sign);
                        clearLines(sign);
                        MessageUtil.broadcastMessage(ShipMessage.CMD_DELETE_SUCCESS.getMessage(simplify(sign.getLocation()),
                                String.valueOf(completableFuture.get() == null)));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        cancel();
                    }
                    cancel();
                }
            }
        };
        runnable.runTaskTimerAsynchronously(DREShips.getInstance(), 20,20);
        return 1;
        */
    }

    private void deletePersistentData(@Nullable Sign sign) {
        if (sign == null) {
            return;
        }
        sign.getPersistentDataContainer().remove(TravelSign.getNameKey());
        sign.getPersistentDataContainer().remove(TravelSign.getDestinationNameKey());
        sign.getPersistentDataContainer().remove(TravelSign.getDestinationKey());
        sign.getPersistentDataContainer().remove(TravelSign.getPriceKey());
        sign.update(true);
    }

    private void clearLines(@Nullable Sign sign) {
        if (sign == null) {
            return;
        }
        sign.setLine(0, "");
        sign.setLine(1, "");
        sign.setLine(2, "");
        sign.setLine(3, "");
        sign.update(true);
    }

    /* getter */

    public static String simplify(Location location) {
        return "[x=" + location.getX() + ", y=" + location.getY() + ", z=" + location.getZ();
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public PriceCalculation getPriceCalculation() {
        return priceCalculation;
    }
}

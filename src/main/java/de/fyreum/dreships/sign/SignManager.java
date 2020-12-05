package de.fyreum.dreships.sign;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.function.PriceCalculation;
import de.fyreum.dreships.persistentdata.ShipDataTypes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SignManager {

    private final Map<UUID, List<CacheSign>> playerCache;
    private final PriceCalculation priceCalculation;

    public SignManager() {
        this.playerCache = new HashMap<>();
        this.priceCalculation = new PriceCalculation();
    }

    // ---------CacheSign----------

    public void saveSignInCache(@NotNull UUID uuid, Sign sign, String name) {
        if (!playerCache.containsKey(uuid)) {
            playerCache.put(uuid, new ArrayList<>());
        }
        if (playerCache.get(uuid).size() > 1) {
            playerCache.get(uuid).set(0, new CacheSign(sign, name));
        } else {
            playerCache.get(uuid).add(new CacheSign(sign, name));
        }
    }

    // ----------TravelSign---------

    public int createFromCache(@NotNull UUID uuid, int price) throws CacheSignException {
        if (playerCache.get(uuid).size() != 2) {
            throw new CacheSignException("Couldn't create a TravelSign. Player cache is empty.");
        }
        Sign sign = playerCache.get(uuid).get(0).getSign();
        Sign destination = playerCache.get(uuid).get(1).getSign();
        String name = playerCache.get(uuid).get(0).getName();
        String destinationName = playerCache.get(uuid).get(1).getName();

        this.create(sign, destination, name, destinationName, price);
        return price;
    }

    public int calculateAndCreateFromCache(@NotNull UUID uuid, double multipliedDistance) throws CacheSignException {
        if (playerCache.get(uuid).size() != 2) {
            throw new CacheSignException("Couldn't create a TravelSign. Player cache is empty.");
        }
        Sign sign = playerCache.get(uuid).get(0).getSign();
        Sign destination = playerCache.get(uuid).get(1).getSign();
        String name = playerCache.get(uuid).get(0).getName();
        String destinationName = playerCache.get(uuid).get(1).getName();

        int price = this.priceCalculation.calculate(sign.getLocation(), destination.getLocation(), multipliedDistance);
        this.create(sign, destination, name, destinationName, price);
        return price;
    }

    public void create(@NotNull Sign sign, @NotNull Sign destination, String name, String destinationName, int price) {
        this.loadAndCreate(sign, destination, name, destinationName, price);
        this.loadAndCreate(destination, sign, name, destinationName, price);
    }

    private void loadAndCreate(@NotNull Sign sign, @NotNull Sign destination, String name, String destinationName, int price) {
        this.savePersistentData(sign, destination.getLocation(), name, destinationName, price);
        this.visualizeData(sign, name, destinationName, price);
        MessageUtil.broadcastMessage(ShipMessage.CMD_CREATE_SUCCESS.getMessage(simplify(sign.getLocation())));
        /*
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
        sign.setLine(2, ShipMessage.SIGN_LINE_THREE.getMessage(destName));
        sign.setLine(3, ShipMessage.SIGN_LINE_FOUR.getMessage(String.valueOf(price)));
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
        /*
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

    private String simplify(Location location) {
        return "[x=" + location.getX() + ", y=" + location.getY() + ", z=" + location.getZ();
    }

    public boolean alreadyCached(UUID uuid, Sign sign) {
        if (!playerCache.containsKey(uuid)) {
            return false;
        }
        for (CacheSign cacheSign : playerCache.get(uuid)) {
            if (cacheSign.getSign().getLocation().equals(sign.getLocation())) {
                return true;
            }
        }
        return false;
    }

    public Map<UUID, List<CacheSign>> getPlayerCache() {
        return playerCache;
    }

    public PriceCalculation getPriceCalculation() {
        return priceCalculation;
    }
}

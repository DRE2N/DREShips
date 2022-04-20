package de.fyreum.dreships.sign;

import de.erethon.bedrock.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.event.TravelSignCreateEvent;
import de.fyreum.dreships.event.TravelSignSignDeleteEvent;
import de.fyreum.dreships.persistentdata.ShipDataTypes;
import de.fyreum.dreships.sign.cache.CacheSignException;
import de.fyreum.dreships.sign.cache.PlayerCache;
import de.fyreum.dreships.util.PriceCalculationUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SignManager {

    private final DREShips plugin= DREShips.getInstance();

    PlayerCache playerCache = plugin.getPlayerCache();
    PriceCalculationUtil priceCalculation = plugin.getPriceCalculationUtil();

    public SignManager() {

    }

    // ----------TravelSign---------

    public boolean check(Player player, TravelSign sign) {
        boolean correct = true;

        if (sign.isIgnoreWorld()) {
            return true;
        }
        if (!plugin.getSignConfig().getSignContainer().contains(sign)) {
            plugin.getSignConfig().getSignContainer().add(new ListedTravelSign(sign));
            ShipMessage.CMD_CHECK_SIGN_WAS_NOT_LISTED.sendMessage(player);
            correct = false;
        }
        if (!plugin.getSignConfig().getSignContainer().contains(sign.getDestination())) {
            Block destinationBlock = sign.getDestination().getBlock();
            if (!TravelSign.travelSign(destinationBlock)) {
                return false;
            }
            plugin.getSignConfig().getSignContainer().add(new ListedTravelSign((Sign) destinationBlock.getState()));
            ShipMessage.CMD_CHECK_DESTINATION_WAS_NOT_LISTED.sendMessage(player);
            correct = false;
        }
        return correct;
    }

    public void createFromCache(@NotNull CommandSender sender, @NotNull UUID uuid, int price, boolean ignoreWorld) throws CacheSignException {
        if (!playerCache.isFull(uuid)) {
            throw new CacheSignException("Couldn't create a TravelSign. Player cache is empty.");
        }
        Sign sign = playerCache.getFirst(uuid).getSign();
        String name = playerCache.getFirst(uuid).getName();
        Sign destination = playerCache.getSecond(uuid).getSign();
        String destinationName = playerCache.getSecond(uuid).getName();

        this.create(sender, sign, destination, name, destinationName, price, ignoreWorld);
        this.playerCache.clear(uuid);
    }

    public void calculateAndCreateFromCache(@NotNull CommandSender sender, @NotNull UUID uuid, double multipliedDistance, boolean ignoreWorld) throws CacheSignException {
        if (!playerCache.isFull(uuid)) {
            throw new CacheSignException("Couldn't create a TravelSign. Player cache is empty.");
        }
        Sign sign = playerCache.getFirst(uuid).getSign();
        String name = playerCache.getFirst(uuid).getName();
        Sign destination = playerCache.getSecond(uuid).getSign();
        String destinationName = playerCache.getSecond(uuid).getName();

        int price = this.priceCalculation.calculate(sign.getLocation(), destination.getLocation(), multipliedDistance);
        this.create(sender, sign, destination, name, destinationName, price, ignoreWorld);
        this.playerCache.clear(uuid);
    }

    public void create(@NotNull CommandSender sender, @NotNull Sign sign, @NotNull Sign destination, String name, String destinationName, int price, boolean ignoreWorld) {
        this.loadAndCreate(sender, sign, destination, name, destinationName, price, ignoreWorld);
        this.loadAndCreate(sender, destination, sign, destinationName, name, price, ignoreWorld);
    }

    private void loadAndCreate(@NotNull CommandSender sender, @NotNull Sign sign, @NotNull Sign destination, String name, String destinationName, int price, boolean ignoreWorld) {
        this.savePersistentData(sign, destination.getLocation(), name, destinationName, price, ignoreWorld);
        this.visualizeData(sign, name, destinationName, price);

        new TravelSignCreateEvent(new TravelSign(name, destinationName, sign.getLocation(), destination.getLocation(), price, ignoreWorld)).callEvent();
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

    private void savePersistentData(@NotNull Sign sign, Location destination, String name, String destName, int price, boolean ignoreWorld) {
        sign.getPersistentDataContainer().set(TravelSign.getNameKey(), PersistentDataType.STRING, name);
        sign.getPersistentDataContainer().set(TravelSign.getDestinationNameKey(), PersistentDataType.STRING, destName);
        sign.getPersistentDataContainer().set(TravelSign.getDestinationKey(), ShipDataTypes.LOCATION, destination);
        sign.getPersistentDataContainer().set(TravelSign.getPriceKey(), PersistentDataType.INTEGER, price);
        if (ignoreWorld) {
            sign.getPersistentDataContainer().set(TravelSign.getIgnoreWorldKey(), ShipDataTypes.BOOLEAN, true);
        }
        sign.update(true);
    }

    private void visualizeData(Sign sign, String name, String destName, int price) {
        sign.line(0, ShipMessage.SIGN_LINE_ONE.message());
        sign.line(1, ShipMessage.SIGN_LINE_TWO.message(name));
        sign.line(2, ShipMessage.SIGN_LINE_THREE.message(String.valueOf(price)));
        sign.line(3, ShipMessage.SIGN_LINE_FOUR.message(destName));
        sign.update(true);
    }

    // returns the amount of deleted signs
    public int delete(CommandSender sender, TravelSign travelSign) {
        Block destinationBlock = travelSign.getDestination().getBlock();
        Sign destination = TravelSign.travelSign(destinationBlock) ? (Sign) destinationBlock.getState() : null;
        return loadAndDelete(sender, travelSign.getSign()) + loadAndDelete(sender, destination);
    }

    private int loadAndDelete(@NotNull CommandSender sender, @Nullable Sign sign) {
        if (sign == null) {
            return 0;
        }
        TravelSign travelSign;
        try {
            travelSign = new TravelSign(sign);
        } catch (CacheSignException c) {
            return 0;
        }

        this.deletePersistentData(sign);
        this.clearLines(sign);

        new TravelSignSignDeleteEvent(travelSign).callEvent();
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
        sign.getPersistentDataContainer().remove(TravelSign.getDisabledKey());
        sign.getPersistentDataContainer().remove(TravelSign.getIgnoreWorldKey());
        sign.getPersistentDataContainer().remove(TravelSign.getMessageKey());
        sign.getPersistentDataContainer().remove(TravelSign.getCooldownKey());
        sign.update(true);
    }

    private void clearLines(@Nullable Sign sign) {
        if (sign == null) {
            return;
        }
        sign.line(0, Component.empty());
        sign.line(1, Component.empty());
        sign.line(2, Component.empty());
        sign.line(3, Component.empty());
        sign.update(true);
    }

    /* disable - enable */

    public void disable(@NotNull TravelSign travelSign) {
        if (travelSign.getSign() != null) {
            this.disable(travelSign.getSign());
        }
        if (DREShips.isSign(travelSign.getDestination().getBlock())) {
            this.disable((Sign) travelSign.getDestination().getBlock().getState());
        }
    }

    public void disable(@NotNull Sign sign) {
        this.visualizeDisable(sign);
        this.addDisabledPersistentData(sign);
        this.setListedDisabled(sign, true);
    }

    private void visualizeDisable(@Nullable Sign sign) {
        if (sign == null) {
            return;
        }
        sign.line(0, MessageUtil.parse("&4###############"));
        sign.line(1, MessageUtil.parse("&cDisabled"));
        sign.line(2, MessageUtil.parse("&cContact Admin"));
        sign.line(3, MessageUtil.parse("&4###############"));
        sign.update(true);
    }

    private void addDisabledPersistentData(@Nullable Sign sign) {
        if (sign == null) {
            return;
        }
        sign.getPersistentDataContainer().set(DREShips.getNamespace("disabled"), ShipDataTypes.BOOLEAN, true);
        sign.update(true);
    }

    public void enable(@NotNull TravelSign travelSign) {
        Sign sign = travelSign.getSign();
        if (sign != null) {
            this.visualizeData(sign, travelSign.getName(), travelSign.getDestinationName(), travelSign.getPrice());
            this.removeDisabledPersistentData(sign);
            this.setListedDisabled(sign, false);
        }
        if (TravelSign.travelSign(travelSign.getDestination().getBlock())) {
            this.enable((Sign) travelSign.getDestination().getBlock().getState());
        }
    }

    private void enable(@NotNull Sign sign) {
        TravelSign travelSign = new TravelSign(sign);
        this.visualizeData(sign, travelSign.getName(), travelSign.getDestinationName(), travelSign.getPrice());
        this.removeDisabledPersistentData(sign);
        this.setListedDisabled(sign, false);
    }

    private void removeDisabledPersistentData(Sign sign) {
        sign.getPersistentDataContainer().remove(TravelSign.getDisabledKey());
        sign.update(true);
    }

    private void setListedDisabled(Sign sign, boolean set) {
        for (ListedTravelSign listed : plugin.getSignConfig().getSignContainer().getListedTravelSigns()) {
            if (listed.getLocation().equals(sign.getLocation())) {
                listed.setDisabled(set);
            }
        }
    }

    /* cooldown */

    public void setCooldown(TravelSign travelSign, int cooldown) {
        Sign sign = travelSign.getSign();
        if (sign != null) {
            this.setCooldownPersistentData(sign, cooldown);
            this.setListedCooldown(sign, cooldown);
        }
        if (DREShips.isSign(travelSign.getDestination().getBlock())) {
            Sign destination = (Sign) travelSign.getDestination().getBlock().getState();
            this.setCooldownPersistentData(destination, cooldown);
            this.setListedCooldown(destination, cooldown);
        }
    }

    private void setListedCooldown(Sign sign, int cooldown) {
        for (ListedTravelSign listed : plugin.getSignConfig().getSignContainer().getListedTravelSigns()) {
            if (listed.getLocation().equals(sign.getLocation())) {
                listed.setCooldown(cooldown);
            }
        }
    }

    private void setCooldownPersistentData(Sign sign, int cooldown) {
        sign.getPersistentDataContainer().set(TravelSign.getCooldownKey(), PersistentDataType.INTEGER, cooldown);
        sign.update(true);
    }

    /* message */

    public void setMessage(@NotNull TravelSign travelSign, String msg) {
        Sign sign = travelSign.getSign();
        if (sign != null) {
            this.addMessagePersistentData(sign, msg);
        }
        if (DREShips.isSign(travelSign.getDestination().getBlock())) {
            this.addMessagePersistentData((Sign) travelSign.getDestination().getBlock().getState(), msg);
        }
    }

    private void addMessagePersistentData(Sign sign, String msg) {
        sign.getPersistentDataContainer().set(TravelSign.getMessageKey(), PersistentDataType.STRING, msg);
        sign.update(true);
    }

    public void removeMessage(@NotNull TravelSign travelSign) {
        Sign sign = travelSign.getSign();
        if (sign != null) {
            this.removeMessagePersistentData(sign);
        }
        if (DREShips.isSign(travelSign.getDestination().getBlock())) {
            this.removeMessagePersistentData((Sign) travelSign.getDestination().getBlock().getState());
        }
    }

    private void removeMessagePersistentData(Sign sign) {
        sign.getPersistentDataContainer().remove(TravelSign.getMessageKey());
        sign.update(true);
    }

    /* price */

    public void setPrice(TravelSign travelSign, int price) {
        Sign sign = travelSign.getSign();
        if (sign != null) {
            this.setPricePersistentData(sign, price);
            this.setListedPrice(sign, price);
            sign.line(2, ShipMessage.SIGN_LINE_THREE.message(String.valueOf(price)));
            sign.update(true);
        }
        if (DREShips.isSign(travelSign.getDestination().getBlock())) {
            Sign destination = (Sign) travelSign.getDestination().getBlock().getState();
            this.setPricePersistentData(destination, price);
            this.setListedPrice(destination, price);
            destination.line(2, ShipMessage.SIGN_LINE_THREE.message(String.valueOf(price)));
            destination.update(true);
        }
    }

    private void setPricePersistentData(Sign sign, int price) {
        sign.getPersistentDataContainer().set(TravelSign.getPriceKey(), PersistentDataType.INTEGER, price);
        sign.update(true);
    }

    private void setListedPrice(Sign sign, int price) {
        for (ListedTravelSign listed : plugin.getSignConfig().getSignContainer().getListedTravelSigns()) {
            if (listed.getLocation().equals(sign.getLocation())) {
                listed.setPrice(price);
            }
        }
    }

    /* rename */

    public void rename(TravelSign travelSign, String name) {
        Sign sign = travelSign.getSign();
        if (sign != null) {
            this.setNamePersistentData(sign, name);
            this.setListedName(sign, name);
            sign.line(1, ShipMessage.SIGN_LINE_TWO.message(name));
            sign.update(true);
        }
        if (DREShips.isSign(travelSign.getDestination().getBlock())) {
            Sign destination = (Sign) travelSign.getDestination().getBlock().getState();
            this.setDestinationNamePersistentData(destination, name);
            this.setListedDestinationName(destination, name);
            destination.line(3, ShipMessage.SIGN_LINE_FOUR.message(name));
            destination.update(true);
        }
    }

    private void setNamePersistentData(Sign sign, String name) {
        sign.getPersistentDataContainer().set(TravelSign.getNameKey(), PersistentDataType.STRING, name);
        sign.update(true);
    }

    private void setDestinationNamePersistentData(Sign sign, String name) {
        sign.getPersistentDataContainer().set(TravelSign.getDestinationNameKey(), PersistentDataType.STRING, name);
        sign.update(true);
    }

    private void setListedName(Sign sign, String name) {
        for (ListedTravelSign listed : plugin.getSignConfig().getSignContainer().getListedTravelSigns()) {
            if (listed.getLocation().equals(sign.getLocation())) {
                listed.setName(name);
            }
        }
    }

    private void setListedDestinationName(Sign sign, String name) {
        for (ListedTravelSign listed : plugin.getSignConfig().getSignContainer().getListedTravelSigns()) {
            if (listed.getDestination().equals(sign.getLocation())) {
                listed.setDestinationName(name);
            }
        }
    }

    /* getter */

    public static String simplify(Location location) {
        return "[x=" + location.getX() + ", y=" + location.getY() + ", z=" + location.getZ() + "]";
    }
}

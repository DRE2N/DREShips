package de.fyreum.dreships.sign;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.persistentdata.ShipDataTypes;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class TravelSign {

    public static final int DEFAULT_COOLDOWN = 10;

    private final String name, destinationName;
    private final Location location, destination;
    private final int price;
    private final String message;
    private final int cooldown; // seconds
    private final boolean disabled;
    private final boolean ignoreWorld;

    private static final NamespacedKey nameKey = DREShips.getNamespace("name");
    private static final NamespacedKey destinationNameKey = DREShips.getNamespace("destinationName");
    private static final NamespacedKey destinationKey = DREShips.getNamespace("destination");
    private static final NamespacedKey priceKey = DREShips.getNamespace("price");
    private static final NamespacedKey disabledKey = DREShips.getNamespace("disabled");
    private static final NamespacedKey cooldownKey = DREShips.getNamespace("cooldown");
    private static final NamespacedKey messageKey = DREShips.getNamespace("message");
    private static final NamespacedKey ignoreWorldKey = DREShips.getNamespace("ignoreWorld");

    public TravelSign(String name, String destinationName, Location location, Location destination, int price) {
        this(name, destinationName, location, destination, price, false);
    }

    public TravelSign(String name, String destinationName, Location location, Location destination, int price, boolean disabled) {
        this(name, destinationName, location, destination, price, disabled, DEFAULT_COOLDOWN);
    }

    public TravelSign(String name, String destinationName, Location location, Location destination, int price, boolean disabled, int cooldown) {
        this(name, destinationName, location, destination, price, disabled, cooldown, "");
    }

    public TravelSign(String name, String destinationName, Location location, Location destination,
                      int price, boolean disabled, int cooldown, String message) {
        this(name, destinationName, location, destination, price, disabled, cooldown, message, false);
    }

    public TravelSign(String name, String destinationName, Location location, Location destination,
                      int price, boolean disabled, int cooldown, String message, boolean ignoreWorld) {
        this.name = name;
        this.destinationName = destinationName;
        this.location = location;
        this.destination = destination;
        this.price = price;
        this.disabled = disabled;
        this.cooldown = cooldown;
        this.message = message.isEmpty() ? getDefaultMessage() : message;
        this.ignoreWorld = ignoreWorld;
    }

    public TravelSign(Sign sign) throws IllegalArgumentException {
        if (!travelSign(sign)) {
            throw new IllegalArgumentException("The given sign doesn't contain the required TravelSign data");
        }
        PersistentDataContainer container = sign.getPersistentDataContainer();

        this.name = container.get(nameKey, PersistentDataType.STRING);
        this.destinationName = container.get(destinationNameKey, PersistentDataType.STRING);
        this.location = sign.getLocation();
        this.destination = container.get(destinationKey, ShipDataTypes.LOCATION);
        this.price = container.getOrDefault(priceKey, PersistentDataType.INTEGER, 0);
        this.cooldown = container.getOrDefault(cooldownKey, PersistentDataType.INTEGER, DEFAULT_COOLDOWN);
        this.message = container.getOrDefault(messageKey, PersistentDataType.STRING, getDefaultMessage());
        this.disabled = container.has(disabledKey, ShipDataTypes.BOOLEAN);
        this.ignoreWorld = container.has(ignoreWorldKey, ShipDataTypes.BOOLEAN);
    }

    public static boolean travelSign(Sign sign) {
        if (sign == null) {
            return false;
        }
        return sign.getPersistentDataContainer().has(nameKey, PersistentDataType.STRING) &&
                sign.getPersistentDataContainer().has(destinationNameKey, PersistentDataType.STRING) &&
                sign.getPersistentDataContainer().has(destinationKey, ShipDataTypes.LOCATION) &&
                sign.getPersistentDataContainer().has(priceKey, PersistentDataType.INTEGER);
    }

    public static boolean travelSign(Block block) {
        if (!DREShips.isSign(block)) {
            return false;
        }
        return travelSign((Sign) block.getState());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TravelSign other = (TravelSign) obj;
        return this.getLocation().equals(other.getLocation());
    }

    public TravelSign updateWorld(World world) {
        if (ignoreWorld) {
            this.location.setWorld(world);
            this.destination.setWorld(world);
        }
        return this;
    }

    /* getter and setter */

    public boolean isDisabled() {
        return disabled;
    }

    public boolean hasCooldown() {
        return cooldown != 0;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isIgnoreWorld() {
        return ignoreWorld;
    }

    public String getMessage() {
        return message;
    }

    public String getDefaultMessage() {
        Economy economy = DREShips.getInstance().getEconomy();
        String priceString = economy == null ? String.valueOf(price) : String.valueOf(economy.format(price));
        return ShipMessage.TP_SUCCESS.getMessage(name, destinationName, priceString);
    }

    public String getName() {
        return name;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public Location getLocation() {
        return location;
    }

    public Location getDestination() {
        return destination;
    }

    public int getPrice() {
        return price;
    }

    @Nullable
    public Sign getSign() {
        Block block = getLocation().getBlock();
        if (!DREShips.isSign(block)) {
            return null;
        }
        return (Sign) block.getState();
    }

    public static NamespacedKey getNameKey() {
        return nameKey;
    }

    public static NamespacedKey getDestinationNameKey() {
        return destinationNameKey;
    }

    public static NamespacedKey getDestinationKey() {
        return destinationKey;
    }

    public static NamespacedKey getPriceKey() {
        return priceKey;
    }

    public static NamespacedKey getCooldownKey() {
        return cooldownKey;
    }

    public static NamespacedKey getMessageKey() {
        return messageKey;
    }

    public static NamespacedKey getDisabledKey() {
        return disabledKey;
    }

    public static NamespacedKey getIgnoreWorldKey() {
        return ignoreWorldKey;
    }
}

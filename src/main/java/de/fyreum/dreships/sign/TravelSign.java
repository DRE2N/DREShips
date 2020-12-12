package de.fyreum.dreships.sign;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.serialization.SerializableLocation;
import de.fyreum.dreships.persistentdata.ShipDataTypes;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class TravelSign implements Serializable {

    private static final long serialVersionUID = -3006306973059990308L;
    private final String name, destinationName;
    private final SerializableLocation location, destination;
    private final int price;
    private transient boolean disabled = false;

    private static final NamespacedKey nameKey = DREShips.getNamespace("name");
    private static final NamespacedKey destinationNameKey = DREShips.getNamespace("destinationName");
    private static final NamespacedKey destinationKey = DREShips.getNamespace("destination");
    private static final NamespacedKey priceKey = DREShips.getNamespace("price");
    private static final NamespacedKey disabledKey = DREShips.getNamespace("disabled");

    public TravelSign(String name, String destinationName, Location location, Location destination, int price) {
        this.name = name;
        this.destinationName = destinationName;
        this.location = new SerializableLocation(location);
        this.destination = new SerializableLocation(destination);
        this.price = price;
    }

    public TravelSign(String name, String destinationName, SerializableLocation location, SerializableLocation destination, int price, boolean disabled) {
        this.name = name;
        this.destinationName = destinationName;
        this.location = location;
        this.destination = destination;
        this.price = price;
        this.disabled = disabled;
    }

    public TravelSign(Sign sign) throws IllegalArgumentException {
        if (!travelSign(sign)) {
            throw new IllegalArgumentException("The given sign doesn't contain the required TravelSign data so it's no TravelSign");
        }
        this.name = sign.getPersistentDataContainer().get(nameKey, PersistentDataType.STRING);
        this.destinationName = sign.getPersistentDataContainer().get(destinationNameKey, PersistentDataType.STRING);
        this.location = new SerializableLocation(sign.getLocation());
        this.destination = new SerializableLocation(sign.getPersistentDataContainer().get(destinationKey, ShipDataTypes.LOCATION));
        this.price = sign.getPersistentDataContainer().getOrDefault(priceKey, PersistentDataType.INTEGER, 0);
        this.disabled = sign.getPersistentDataContainer().has(disabledKey, ShipDataTypes.BOOLEAN);
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
        if (!(obj instanceof TravelSign)) {
            return false;
        }
        TravelSign other = (TravelSign) obj;
        return this.getLocation().equals(other.getLocation());
    }

    /* getter and setter */

    public boolean isDisabled() {
        return disabled;
    }



    public String getName() {
        return name;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public Location getLocation() {
        return location.getLocation();
    }

    public Location getDestination() {
        return destination.getLocation();
    }

    public SerializableLocation getSerializableLocation() {
        return location;
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

    public static NamespacedKey getDisabledKey() {
        return disabledKey;
    }
}

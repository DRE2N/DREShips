package de.fyreum.dreships.sign;

import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.persistentdata.ShipDataTypes;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataType;

public final class TravelSign {

    private final String name, destinationName;
    private final Location destination;
    private final int price;

    private static final NamespacedKey nameKey = DREShips.getNamespace("name");
    private static final NamespacedKey destinationNameKey = DREShips.getNamespace("destinationName");
    private static final NamespacedKey destinationKey = DREShips.getNamespace("destination");
    private static final NamespacedKey priceKey = DREShips.getNamespace("price");

    public TravelSign(String name, String destinationName, Location destination, int price) {
        this.name = name;
        this.destinationName = destinationName;
        this.destination = destination;
        this.price = price;
    }

    public TravelSign(Sign sign) {
        if (!travelSign(sign)) {
            throw new IllegalArgumentException("The given sign doesn't contain the required TravelSign data so it's no TravelSign");
        }
        this.name = sign.getPersistentDataContainer().get(nameKey, PersistentDataType.STRING);
        this.destinationName = sign.getPersistentDataContainer().get(destinationNameKey, PersistentDataType.STRING);
        this.destination = sign.getPersistentDataContainer().get(destinationKey, ShipDataTypes.LOCATION);
        this.price = sign.getPersistentDataContainer().get(priceKey, PersistentDataType.INTEGER);
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

    /* getter */

    public String getName() {
        return name;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public Location getDestination() {
        return destination;
    }

    public int getPrice() {
        return price;
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
}

package de.fyreum.dreships.function;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.DREShips;
import de.fyreum.dreships.config.ShipConfig;
import org.bukkit.Location;

public class PriceCalculation {

    DREShips plugin = DREShips.getInstance();
    ShipConfig config = plugin.getShipConfig();

    public int calculate(double distance, double distanceMultipliedPrice) {
        return (int) Math.ceil((distance * distanceMultipliedPrice) + config.getStartPrice());
    }

    public int calculate(Location loc1, Location loc2, double distanceMultipliedPrice) {
        double distance = loc1.distance(loc2);
        return this.calculate(distance, distanceMultipliedPrice);
    }

    public double getDistanceMultiplier(String name) {
        if (name.equalsIgnoreCase("AIRSHIP")) {
            return config.getAirshipDistanceMultiplier();
        }
        if (name.equalsIgnoreCase("SHIP")) {
            return config.getShipDistanceMultiplier();
        }
        if (name.equalsIgnoreCase("LAND")) {
            return config.getLandDistanceMultiplier();
        }
        return -1;
    }

}

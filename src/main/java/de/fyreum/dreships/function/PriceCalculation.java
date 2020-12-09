package de.fyreum.dreships.function;

import de.fyreum.dreships.DREShips;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class PriceCalculation {

    DREShips plugin = DREShips.getInstance();

    public int calculate(double distance, double distanceMultipliedPrice) {
        return (int) Math.ceil((distance * distanceMultipliedPrice) + plugin.getShipConfig().getStartPrice());
    }

    public int calculate(Location loc1, Location loc2, double distanceMultipliedPrice) {
        double distance = loc1.distance(loc2);
        return this.calculate(distance, distanceMultipliedPrice);
    }

    public double getDistanceMultiplier(String name) {
        if (name.equalsIgnoreCase("AIRSHIP")) {
            return plugin.getShipConfig().getAirshipDistanceMultiplier();
        }
        if (name.equalsIgnoreCase("SHIP")) {
            return plugin.getShipConfig().getShipDistanceMultiplier();
        }
        if (name.equalsIgnoreCase("LAND")) {
            return plugin.getShipConfig().getLandDistanceMultiplier();
        }
        return -1;
    }

    public static List<String> getTravelTypes() {
        return Arrays.asList("airship", "ship", "land");
    }

}

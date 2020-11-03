package de.fyreum.dreships.config;

import de.erethon.commons.config.DREConfig;

import java.io.File;

public class ShipConfig extends DREConfig  {

    public static final int CONFIG_VERSION = 1;

    private double airshipDistanceMultiplier = 1.00;
    private double landDistanceMultiplier = 1.00;
    private double shipDistanceMultiplier = 1.00;
    private double startPrice = 10.00;

    public ShipConfig(File file) {
        super(file, CONFIG_VERSION);
        if (initialize) {
            initialize();
        }
        load();
    }

    public double getAirshipDistanceMultiplier() {
        return airshipDistanceMultiplier;
    }

    public double getLandDistanceMultiplier() {
        return landDistanceMultiplier;
    }

    public double getShipDistanceMultiplier() {
        return shipDistanceMultiplier;
    }

    public double getStartPrice() {
        return startPrice;
    }

    @Override
    public void initialize() {
        if (!config.contains("multiplier.airship")) {
            config.set("multiplier.airship", airshipDistanceMultiplier);
        }
        if (!config.contains("multiplier.land")) {
            config.set("multiplier.land", landDistanceMultiplier);
        }
        if (!config.contains("multiplier.ship")) {
            config.set("multiplier.ship", shipDistanceMultiplier);
        }
        if (!config.contains("startPrice")) {
            config.set("startPrice", startPrice);
        }
        save();
    }

    @Override
    public void save() {
        config.set("multiplier.airship", airshipDistanceMultiplier);
        config.set("multiplier.land", landDistanceMultiplier);
        config.set("multiplier.ship", shipDistanceMultiplier);
        config.set("startPrice", startPrice);
        super.save();
    }

    @Override
    public void load() {
        airshipDistanceMultiplier = config.getDouble("multiplier.airship");
        landDistanceMultiplier = config.getDouble("multiplier.land");
        shipDistanceMultiplier = config.getDouble("multiplier.ship");
        startPrice = config.getDouble("startPrice");
    }
}

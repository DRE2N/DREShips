package de.fyreum.dreships.config;

import de.erethon.bedrock.config.EConfig;

import java.io.File;

public class ShipConfig extends EConfig  {

    public static final int CONFIG_VERSION = 1;

    private String language = "german";
    private double airshipDistanceMultiplier = 0.05;
    private double landDistanceMultiplier = 0.01;
    private double shipDistanceMultiplier = 0.03;
    private double startPrice = 10.00;
    private double taxMultiplier = 0.10;
    private long whitelistedTeleportationTime = 100;
    private int commandsPerHelpPage = 5;
    private int signsPerListPage = 7;

    public ShipConfig(File file) {
        super(file, CONFIG_VERSION);
        initialize();
        load();
    }

    @Override
    public void initialize() {
        if (!config.contains("language")) {
            config.set("language", language);
        }
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
        if (!config.contains("taxMultiplier")) {
            config.set("taxMultiplier", taxMultiplier);
        }
        if (!config.contains("whitelistedTeleportationTime")) {
            config.set("whitelistedTeleportationTime", whitelistedTeleportationTime);
        }
        if (!config.contains("commandsPerHelpPage")) {
            config.set("commandsPerHelpPage", commandsPerHelpPage);
        }
        if (!config.contains("signsPerListPage")) {
            config.set("signsPerListPage", signsPerListPage);
        }
        save();
    }

    @Override
    public void save() {
        config.set("language", language);
        config.set("multiplier.airship", airshipDistanceMultiplier);
        config.set("multiplier.land", landDistanceMultiplier);
        config.set("multiplier.ship", shipDistanceMultiplier);
        config.set("startPrice", startPrice);
        config.set("taxMultiplier", taxMultiplier);
        config.set("whitelistedTeleportationTime", whitelistedTeleportationTime);
        config.set("commandsPerHelpPage", commandsPerHelpPage);
        config.set("signsPerListPage", signsPerListPage);
        super.save();
    }

    @Override
    public void load() {
        language = config.getString("language", language);
        airshipDistanceMultiplier = config.getDouble("multiplier.airship", airshipDistanceMultiplier);
        landDistanceMultiplier = config.getDouble("multiplier.land", landDistanceMultiplier);
        shipDistanceMultiplier = config.getDouble("multiplier.ship", shipDistanceMultiplier);
        startPrice = config.getDouble("startPrice", startPrice);
        taxMultiplier = config.getDouble("taxMultiplier", taxMultiplier);
        whitelistedTeleportationTime = config.getLong("whitelistedTeleportationTime", whitelistedTeleportationTime);
        commandsPerHelpPage = config.getInt("commandsPerHelpPage") == 0 ? commandsPerHelpPage : config.getInt("commandsPerHelpPage");
        signsPerListPage = config.getInt("signsPerListPage") == 0 ? signsPerListPage : config.getInt("signsPerListPage");
    }

    public String getLanguage() {
        return language;
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

    public double getTaxMultiplier() {
        return taxMultiplier;
    }

    public long getWhitelistedTeleportationTime() {
        return whitelistedTeleportationTime;
    }

    public int getCommandsPerHelpPage() {
        return commandsPerHelpPage;
    }

    public int getSignsPerListPage() {
        return signsPerListPage;
    }
}

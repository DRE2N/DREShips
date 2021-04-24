package de.fyreum.dreships;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.javaplugin.DREPlugin;
import de.erethon.commons.javaplugin.DREPluginSettings;
import de.erethon.factionsxl.FactionsXL;
import de.fyreum.dreships.commands.ShipCommandCache;
import de.fyreum.dreships.config.ShipConfig;
import de.fyreum.dreships.config.SignConfig;
import de.fyreum.dreships.sign.SignListener;
import de.fyreum.dreships.sign.SignManager;
import de.fyreum.dreships.sign.TravelSign;
import de.fyreum.dreships.sign.cache.PlayerCache;
import de.fyreum.dreships.util.PriceCalculationUtil;
import de.fyreum.dreships.util.TeleportationUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.WEST;

public final class DREShips extends DREPlugin {

    private static DREShips plugin;
    private Economy economy = null;
    private ShipConfig shipConfig;
    private PlayerCache playerCache;
    private PriceCalculationUtil priceCalculation;
    private SignManager signManager;
    private TeleportationUtil teleportationUtil;
    private ShipCommandCache commandCache;
    private FactionsXL factionsXL = null;
    private SignConfig signConfig;

    public DREShips() {
        settings = DREPluginSettings.builder()
                .paper(true)
                .economy(true)
                .internals(Internals.v1_16_R1)
                .build();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // fxl integration
        if (Bukkit.getPluginManager().isPluginEnabled("FactionsXL") && Bukkit.getPluginManager().getPlugin("FactionsXL") != null) {
            factionsXL = (FactionsXL) Bukkit.getPluginManager().getPlugin("FactionsXL");
            MessageUtil.log("&aSuccessfully found FactionsXL on the server!");
        } else {
            MessageUtil.log("&4Couldn't find FactionsXL on the server -> some features may not work");
        }
        // instantiation
        plugin = this;
        this.instantiateShipConfig();
        this.instantiateSignConfig();
        this.loadMessages();
        economy = getEconomyProvider();
        playerCache = new PlayerCache();
        priceCalculation = new PriceCalculationUtil();
        signManager = new SignManager();
        teleportationUtil = new TeleportationUtil(plugin);
        commandCache = new ShipCommandCache(plugin);
        // setup
        this.setCommandCache(commandCache);
        commandCache.register(plugin);
        this.getServer().getPluginManager().registerEvents(new SignListener(), getInstance());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.signConfig.save();
    }

    public void instantiateShipConfig() {
        shipConfig = new ShipConfig(new File(getDataFolder(), "config.yml"));
    }

    public void instantiateSignConfig() {
        signConfig = new SignConfig(plugin);
    }

    public void loadMessages() {
        this.attemptToSaveResource("languages/german.yml", false);
        this.getMessageHandler().setDefaultLanguage(shipConfig.getLanguage());
    }

    public void reloadMessages() {
        this.reloadMessageHandler();
        this.getMessageHandler().setDefaultLanguage(shipConfig.getLanguage());
    }

    /* getter */

    public Economy getEconomy() {
        return economy;
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public PriceCalculationUtil getPriceCalculationUtil() {
        return priceCalculation;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public TeleportationUtil getTeleportationUtil() {
        return teleportationUtil;
    }

    @Override
    public ShipCommandCache getCommandCache() {
        return commandCache;
    }

    public ShipConfig getShipConfig () {
        return shipConfig;
    }

    public SignConfig getSignConfig() {
        return signConfig;
    }

    @Nullable
    public FactionsXL getFactionsXL() {
        return factionsXL;
    }

    public static DREShips getInstance() {
        return plugin;
    }

    public static NamespacedKey getNamespace(String key) {
        return new NamespacedKey(plugin, key);
    }

    public static boolean isSign(Block block) {
        return block.getState() instanceof Sign;
    }

    public static boolean isWallSign(Block block) {
        return block.getBlockData() instanceof WallSign;
    }

    public static boolean isSignAttachedTo(Block block) {
        Location location = block.getLocation();
        Location[] locations = new Location[]{
                location.clone().add(1, 0, 0),
                location.clone().add(0, 0, 1),
                location.clone().subtract(1, 0, 0),
                location.clone().subtract(0, 0, 1)
        };
        BlockFace[] faces = new BlockFace[]{WEST, NORTH, EAST, SOUTH};

        Location locUp = location.clone().add(0, 1, 0);
        if (TravelSign.travelSign(locUp.getBlock())) {
            if (!isWallSign(locUp.getBlock())) {
                return true;
            }
        }
        for (int i = 0; i < locations.length; i++) {
            Block b = locations[i].getBlock();
            if (TravelSign.travelSign(b) && isWallSign(b)) {
                WallSign wallSign = (WallSign) b.getBlockData();
                if (wallSign.getFacing().getOppositeFace().equals(faces[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}

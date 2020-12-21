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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static final Set<Material> SIGNS = new HashSet<>(Arrays.asList(
            Material.OAK_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.BIRCH_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.JUNGLE_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.ACACIA_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.DARK_OAK_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.CRIMSON_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.WARPED_SIGN,
            Material.WARPED_WALL_SIGN
    ));

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
        economy = getEconomyProvider();
        playerCache = new PlayerCache();
        priceCalculation = new PriceCalculationUtil();
        signManager = new SignManager();
        teleportationUtil = new TeleportationUtil(plugin);
        commandCache = new ShipCommandCache(plugin);
        // setup
        this.setCommandCache(commandCache);
        commandCache.register(plugin);
        this.getCommand(ShipCommandCache.LABEL).setTabCompleter(commandCache);
        this.getServer().getPluginManager().registerEvents(new SignListener(), getInstance());
        this.attemptToSaveResource("languages/german.yml", false);
        this.getMessageHandler().setDefaultLanguage(shipConfig.getLanguage());
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
        return SIGNS.contains(block.getType());
    }

    private static class LocationNode {
        private final Location loc;
        private final BlockFace face;

        public LocationNode(Location loc, BlockFace face) {
            this.loc = loc;
            this.face = face;
        }

        public Location getLoc() {
            return loc;
        }

        public BlockFace getFace() {
            return face;
        }
    }

    public static boolean signAttachedTo(Block block) {
        Location location = block.getLocation();
        List<LocationNode> locations = Arrays.asList(
                new LocationNode(location.clone().add(1, 0, 0), BlockFace.EAST), // x -1
                new LocationNode(location.clone().add(0, 0, 1), BlockFace.NORTH), // z +1
                new LocationNode(location.clone().subtract(1, 0, 0), BlockFace.WEST), // x -1
                new LocationNode(location.clone().subtract(0, 0, 1), BlockFace.SOUTH) // z -1
        );
        Location locUp = location.clone().add(0, 1, 0);
        if (locUp.getBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign && TravelSign.travelSign(locUp.getBlock())) {
            return true;
        }
        for (LocationNode loc : locations) {
            if (!TravelSign.travelSign(loc.getLoc().getBlock())) {
                continue;
            }
            Sign sign = (Sign) loc.getLoc().getBlock().getState();
            if (sign.getBlockData() instanceof WallSign) {
                WallSign wallSign = (WallSign) sign.getBlockData();
                if (wallSign.getFacing().getOppositeFace().equals(loc.getFace())) {
                    return true;
                }
            }
        }
        return false;
    }
}

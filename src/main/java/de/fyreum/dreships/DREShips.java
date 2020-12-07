package de.fyreum.dreships;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.javaplugin.DREPlugin;
import de.erethon.commons.javaplugin.DREPluginSettings;
import de.erethon.factionsxl.FactionsXL;
import de.fyreum.dreships.commands.ShipCommandCache;
import de.fyreum.dreships.config.ShipConfig;
import de.fyreum.dreships.function.TeleportationUtil;
import de.fyreum.dreships.sign.SignListener;
import de.fyreum.dreships.sign.SignManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class DREShips extends DREPlugin {

    private static DREShips plugin;
    private Economy economy = null;
    private ShipConfig shipConfig;
    private SignManager signManager;
    private TeleportationUtil teleportationUtil;
    private ShipCommandCache commandCache;
    private FactionsXL factionsXL = null;

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
        // instantiation
        plugin = this;
        shipConfig = new ShipConfig(new File(getDataFolder(), "config.yml"));
        economy = getEconomyProvider();
        signManager = new SignManager();
        teleportationUtil = new TeleportationUtil(this);
        commandCache = new ShipCommandCache(this);
        // fxl integration
        if (Bukkit.getPluginManager().isPluginEnabled("FactionsXL") && Bukkit.getPluginManager().getPlugin("FactionsXL") != null) {
            factionsXL = (FactionsXL) Bukkit.getPluginManager().getPlugin("FactionsXL");
            MessageUtil.log("&aSuccessfully found FactionsXL on the server!");
        } else {
            MessageUtil.log("&4Couldn't find FactionsXL on the server -> some features may not work");
        }
        // setup
        this.setCommandCache(commandCache);
        commandCache.register(this);
        this.getCommand("dreships").setTabCompleter(commandCache);
        this.getServer().getPluginManager().registerEvents(new SignListener(), getInstance());
        this.attemptToSaveResource("languages/german.yml", false);
        this.getMessageHandler().setDefaultLanguage("german");
    }

    /* getter */

    public static DREShips getInstance() {
        return plugin;
    }

    public static NamespacedKey getNamespace(String key) {
        return new NamespacedKey(plugin, key);
    }

    public Economy getEconomy() {
        return economy;
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

    public FactionsXL getFactionsXL() {
        return factionsXL;
    }

    public static boolean isSign(Block block) {
        return SIGNS.contains(block.getType());
    }
}

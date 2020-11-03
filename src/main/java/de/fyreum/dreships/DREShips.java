package de.fyreum.dreships;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.javaplugin.DREPlugin;
import de.erethon.commons.javaplugin.DREPluginSettings;
import de.fyreum.dreships.commands.ShipCommandCache;
import de.fyreum.dreships.config.ShipConfig;
import de.fyreum.dreships.config.ShipMessage;
import de.fyreum.dreships.sign.SignListener;
import de.fyreum.dreships.sign.SignManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class DREShips extends DREPlugin {

    private static DREShips plugin;
    private Economy economy = null;
    private SignManager signManager;
    private ShipCommandCache commandCache;
    private ShipConfig shipConfig;

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
        plugin = this;
        shipConfig = new ShipConfig(new File(getDataFolder(), "config.yml"));
        economy = getEconomyProvider();
        signManager = new SignManager();
        commandCache = new ShipCommandCache(this);
        setCommandCache(commandCache);
        commandCache.register(this);
        getCommand("dreships").setTabCompleter(commandCache);
        getServer().getPluginManager().registerEvents(new SignListener(), getInstance());
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

    public ShipConfig getShipConfig () {
        MessageUtil.log("Looking for ship config..." + shipConfig.toString());
        return shipConfig;
    }

    public static boolean isSign(Block block) {
        return SIGNS.contains(block.getType());
    }
}

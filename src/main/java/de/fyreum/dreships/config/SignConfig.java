package de.fyreum.dreships.config;

import de.erethon.commons.chat.MessageUtil;
import de.fyreum.dreships.serialization.Serialization;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SignConfig {

    private final File file;
    private FileConfiguration config;
    private SignContainer signContainer;
    private final String path = "stored.signs";
    private final String configName = "signs.yml";

    public SignConfig(JavaPlugin plugin) {
        MessageUtil.log("Loading " + configName + "...");
        this.file = new File(plugin.getDataFolder(), configName);
        if (!file.exists()) {
            MessageUtil.log("&cFile not found");
            try {
                this.file.createNewFile();
                MessageUtil.log("&aNew " + configName + " file created");
            } catch (IOException i) {
                MessageUtil.log("&cCouldn't create the " + configName + " file");
            }
        } else {
            MessageUtil.log("&aSuccessfully loaded " + configName + "!");
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.load();
    }

    private void load() {
        byte[] bytes = this.config.getObject(path, byte[].class);
        if (bytes == null) {
            this.signContainer = new SignContainer();
            return;
        }
        Object deserialized = Serialization.deserialize(bytes);
        if (!(deserialized instanceof SignContainer)) {
            MessageUtil.log("&cCouldn't identify " + path + " of the " + configName + " file, overwriting it");
            this.signContainer = new SignContainer();
            return;
        }
        this.signContainer = (SignContainer) deserialized;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void save() {
        byte[] serialized = Serialization.serialize(this.signContainer);
        if (serialized != null) {
            this.config.set(this.path, serialized);
        }
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            MessageUtil.log("Could not save config file " + this.file.getName());
        }
    }

    public void reload() {
        save();
        this.config = YamlConfiguration.loadConfiguration(this.file);
        load();
    }

    public String getPath() {
        return path;
    }

    public SignContainer getSignContainer() {
        return signContainer;
    }
}

package com.derongan.minecraft.mineinabyss;

import com.derongan.minecraft.mineinabyss.Ascension.AscensionData;
import com.derongan.minecraft.mineinabyss.Database.DatabaseUtils;
import com.derongan.minecraft.mineinabyss.Layer.Layer;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Stores context for the plugin, such as the plugin instance
 */
public class AbyssContext {
    private Map<UUID, AscensionData> playerAcensionDataMap = new HashMap<>();
    private Map<String, Layer> layerMap = new HashMap<>();
    private Plugin plugin;
    private Logger logger;
    private Configuration config;
    private int tickTime;
    private Connection connection;

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, AscensionData> getPlayerAcensionDataMap() {
        return playerAcensionDataMap;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public Map<String, Layer> getLayerMap() {
        return layerMap;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public Connection getConnection() {
        if(plugin == null){
            throw new IllegalStateException("Plugin must be set before attempting to get database");
        } else {
            String dBPath = plugin.getDataFolder().getPath() + config.getString("storage.sqlitefile");

            if(connection == null){
                connection = DatabaseUtils.getDBConnection(dBPath);
            }
            return connection;
        }
    }
}

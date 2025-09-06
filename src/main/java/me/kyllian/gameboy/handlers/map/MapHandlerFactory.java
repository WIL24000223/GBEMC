package me.kyllian.gameboy.handlers.map;

import me.kyllian.gameboy.GameboyPlugin;
import org.bukkit.Bukkit;

public class MapHandlerFactory {

    private GameboyPlugin plugin;

    public MapHandlerFactory(GameboyPlugin plugin) {
        this.plugin = plugin;
    }

    public MapHandler getMapHandler() {
        // Check if text display mode is enabled in config
        String displayMode = plugin.getConfig().getString("display_mode", "map");
        if ("text".equalsIgnoreCase(displayMode)) {
            // Check if server version supports TextDisplay entities (1.19.4+)
            if (supportsTextDisplays()) {
                return new MapHandlerTextDisplay(plugin);
            } else {
                plugin.getLogger().warning("Text display mode requested but server version doesn't support TextDisplay entities. Falling back to map display.");
            }
        }
        
        // Default to map-based display
        String minecraftVersion = Bukkit.getVersion();
        String mainVerString = minecraftVersion.split("\\.")[1];
        mainVerString = mainVerString.replace(")", "");
        mainVerString = mainVerString.replace("(", "");
        int mainVer = Integer.parseInt(mainVerString);
        return mainVer >= 13 ? new MapHandlerNew(plugin) : new MapHandlerOld(plugin);
    }
    
    private boolean supportsTextDisplays() {
        try {
            Class.forName("org.bukkit.entity.TextDisplay");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

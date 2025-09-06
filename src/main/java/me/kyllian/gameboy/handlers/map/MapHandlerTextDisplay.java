package me.kyllian.gameboy.handlers.map;

import me.kyllian.gameboy.GameboyPlugin;
import me.kyllian.gameboy.data.Pocket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapHandlerTextDisplay implements MapHandler {

    private final GameboyPlugin plugin;
    private final Map<Player, List<TextDisplay>> playerDisplays;
    private final Map<Player, BukkitRunnable> playerTasks;
    
    // Unicode block characters for pixel representation  
    // Using different unicode characters for better visual representation
    private static final String FULL_BLOCK = "█";
    private static final String DARK_SHADE = "▓";
    private static final String MEDIUM_SHADE = "▒";
    private static final String LIGHT_SHADE = "░";
    private static final String EMPTY_BLOCK = " ";
    
    // Screen dimensions
    private static final int SCREEN_WIDTH = 128;
    private static final int SCREEN_HEIGHT = 128;
    
    // Display spacing - configurable values
    private final double ROW_HEIGHT;
    private final double DISPLAY_SCALE; 
    private final double DISPLAY_OFFSET_Y;
    private final double DISPLAY_OFFSET_Z;
    private final int SAMPLING_RATE;

    public MapHandlerTextDisplay(GameboyPlugin plugin) {
        this.plugin = plugin;
        this.playerDisplays = new HashMap<>();
        this.playerTasks = new HashMap<>();
        
        // Load configuration values with defaults
        this.DISPLAY_OFFSET_Z = plugin.getConfig().getDouble("text_display.offset_z", 3.0);
        this.DISPLAY_OFFSET_Y = plugin.getConfig().getDouble("text_display.offset_y", 1.5);
        this.ROW_HEIGHT = plugin.getConfig().getDouble("text_display.row_height", 0.08);
        this.DISPLAY_SCALE = plugin.getConfig().getDouble("text_display.scale", 0.5);
        this.SAMPLING_RATE = plugin.getConfig().getInt("text_display.sampling_rate", 4);
        
        plugin.getLogger().info("Text Display mode enabled with settings: offset_z=" + DISPLAY_OFFSET_Z + 
                               ", offset_y=" + DISPLAY_OFFSET_Y + ", sampling_rate=" + SAMPLING_RATE);
    }

    @Override
    public void loadData() {
        // No persistent data needed for text displays
    }

    @Override
    public void sendMap(Player player) {
        // Clean up any existing displays
        cleanupPlayer(player);
        
        Location playerLocation = player.getLocation();
        Location displayLocation = playerLocation.clone().add(0, DISPLAY_OFFSET_Y, DISPLAY_OFFSET_Z);
        
        List<TextDisplay> displays = new ArrayList<>();
        
        // Create text displays for each row
        for (int row = 0; row < SCREEN_HEIGHT; row++) {
            Location rowLocation = displayLocation.clone().add(0, -row * ROW_HEIGHT, 0);
            TextDisplay textDisplay = player.getWorld().spawn(rowLocation, TextDisplay.class);
            
            // Configure the text display
            textDisplay.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
            
            // Use a more compatible transformation setup
            try {
                textDisplay.setTransformation(new org.bukkit.util.Transformation(
                    new org.joml.Vector3f(0, 0, 0),
                    new org.joml.Quaternionf(),
                    new org.joml.Vector3f((float) DISPLAY_SCALE, (float) DISPLAY_SCALE, (float) DISPLAY_SCALE),
                    new org.joml.Quaternionf()
                ));
            } catch (Exception e) {
                // Fallback if transformation fails
                plugin.getLogger().warning("Failed to set text display transformation: " + e.getMessage());
            }
            
            // Set initial properties
            textDisplay.setText(""); // Start with empty text
            textDisplay.setVisibleByDefault(true);
            
            displays.add(textDisplay);
        }
        
        playerDisplays.put(player, displays);
        
        // Start the update task
        int tickDelay = Math.max(1, plugin.getConfig().getInt("tick_update_delay", 1));
        
        BukkitRunnable updateTask = new BukkitRunnable() {
            final Pocket pocket = plugin.getPlayerHandler().getPocket(player);
            
            @Override
            public void run() {
                if (pocket.getEmulator() == null || !player.isOnline()) {
                    cleanupPlayer(player);
                    cancel();
                    return;
                }
                
                updateDisplays(player, pocket);
            }
        };
        
        updateTask.runTaskTimer(plugin, tickDelay, tickDelay);
        playerTasks.put(player, updateTask);
        
        // Give player a simple item to represent the gameboy
        ItemStack gameboyItem = new ItemStack(org.bukkit.Material.COMPASS);
        org.bukkit.inventory.meta.ItemMeta meta = gameboyItem.getItemMeta();
        meta.setDisplayName("§aGameboy Text Display");
        meta.setLore(java.util.Arrays.asList(
            "§7Look ahead to see the screen",
            "§7Each row is a text display",
            "§7Use controls to play"
        ));
        gameboyItem.setItemMeta(meta);
        player.getInventory().setItemInMainHand(gameboyItem);
        
        // Send message to player
        player.sendMessage("§aGameboy screen is now displayed using text displays! Look ahead to see it.");
    }

    private void updateDisplays(Player player, Pocket pocket) {
        List<TextDisplay> displays = playerDisplays.get(player);
        if (displays == null || displays.isEmpty()) return;
        
        byte[] pixels = pocket.getEmulator().lcd.freeBufferArrayByte.clone();
        
        // Update each row
        for (int row = 0; row < Math.min(SCREEN_HEIGHT, displays.size()); row++) {
            TextDisplay display = displays.get(row);
            if (display == null || !display.isValid()) continue;
            
            StringBuilder rowText = new StringBuilder();
            
            // Convert each pixel in the row to a character
            // Use configurable sampling rate to make it more readable
            for (int col = 0; col < SCREEN_WIDTH; col += SAMPLING_RATE) {
                int pixelIndex = col + (row * SCREEN_WIDTH);
                if (pixelIndex < pixels.length) {
                    // Convert pixel value to character
                    // Minecraft map palette values are from 0-127
                    byte pixelValue = pixels[pixelIndex];
                    char character = getCharacterForPixel(pixelValue);
                    rowText.append(character);
                } else {
                    rowText.append(EMPTY_BLOCK);
                }
            }
            
            // Update the display text
            display.setText(rowText.toString());
        }
    }
    
    private char getCharacterForPixel(byte pixelValue) {
        // Convert byte to unsigned value (0-255)
        int value = pixelValue & 0xFF;
        
        // Map to different characters based on intensity
        if (value < 32) {
            return FULL_BLOCK.charAt(0);
        } else if (value < 64) {
            return DARK_SHADE.charAt(0);
        } else if (value < 96) {
            return MEDIUM_SHADE.charAt(0);
        } else if (value < 128) {
            return LIGHT_SHADE.charAt(0);
        } else {
            return EMPTY_BLOCK.charAt(0);
        }
    }

    @Override
    public void resetMap(ItemStack map) {
        // Find player with this item and cleanup
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand != null && mainHand.isSimilar(map)) {
                cleanupPlayer(player);
                break;
            }
        }
    }
    
    public void cleanupPlayer(Player player) {
        // Remove text displays
        List<TextDisplay> displays = playerDisplays.get(player);
        if (displays != null) {
            for (TextDisplay display : displays) {
                if (display != null && display.isValid()) {
                    display.remove();
                }
            }
            playerDisplays.remove(player);
        }
        
        // Cancel update task
        BukkitRunnable task = playerTasks.get(player);
        if (task != null) {
            task.cancel();
            playerTasks.remove(player);
        }
    }
    
    public void cleanupAllPlayers() {
        List<Player> players = new ArrayList<>(playerDisplays.keySet());
        for (Player player : players) {
            cleanupPlayer(player);
        }
    }
}
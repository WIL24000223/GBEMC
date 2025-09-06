package me.kyllian.gameboy.listeners;

import me.kyllian.gameboy.GameboyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.util.Vector;

public class PlayerInputListener implements Listener {

    private GameboyPlugin plugin;

    public PlayerInputListener(GameboyPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInput(PlayerInputEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getInputHandler().hasActiveGameboy(player)) return;
        
        // Get input vector from the PlayerInputEvent
        Vector inputVector = event.getInputVector();
        
        // Map input vector to directional buttons
        // InputVector: X = sideways movement (-1 = left, 1 = right)
        // InputVector: Z = forward/backward movement (-1 = forward, 1 = backward)
        boolean left = inputVector.getX() < 0;
        boolean right = inputVector.getX() > 0;
        boolean forward = inputVector.getZ() < 0;  // forward = up in game context
        boolean backward = inputVector.getZ() > 0; // backward = down in game context
        
        plugin.getInputHandler().handleMovementInput(player, 
            left,     // left
            right,    // right  
            forward,  // up (forward movement)
            backward  // down (backward movement)
        );
    }

}

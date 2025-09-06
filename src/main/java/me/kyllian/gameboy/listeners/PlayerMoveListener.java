package me.kyllian.gameboy.listeners;

import me.kyllian.gameboy.GameboyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private GameboyPlugin plugin;

    public PlayerMoveListener(GameboyPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getInputHandler().hasActiveGameboy(player)) return;
        
        double diffX = event.getTo().getX() - event.getFrom().getX();
        double diffZ = event.getTo().getZ() - event.getFrom().getZ();
        
        plugin.getInputHandler().handleMovementInput(player, 
            diffX > 0.01,   // left
            diffX < -0.01,  // right  
            diffZ > 0.01,   // up
            diffZ < -0.01   // down
        );
        
        event.setTo(event.getFrom());
    }

}

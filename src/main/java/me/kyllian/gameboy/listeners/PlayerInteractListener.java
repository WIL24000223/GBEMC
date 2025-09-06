package me.kyllian.gameboy.listeners;

import me.kyllian.gameboy.GameboyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private GameboyPlugin plugin;

    public PlayerInteractListener(GameboyPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getInputHandler().hasActiveGameboy(player)) return;
        
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            plugin.getInputHandler().handleBButton(player, true);
        }
        event.setCancelled(true);
    }
}

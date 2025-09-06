package me.kyllian.gameboy.listeners;

import me.kyllian.gameboy.GameboyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    private GameboyPlugin plugin;

    public PlayerDropItemListener(GameboyPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getInputHandler().hasActiveGameboy(player)) {
            plugin.getInputHandler().handleSelectButton(player, true);
            event.setCancelled(true);
        }
    }
}

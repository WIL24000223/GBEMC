package me.kyllian.gameboy.listeners;

import me.kyllian.gameboy.GameboyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerItemHeldListener implements Listener {

    private GameboyPlugin plugin;

    public PlayerItemHeldListener(GameboyPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this,  plugin);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (plugin.getInputHandler().hasActiveGameboy(player)) {
            event.setCancelled(true);
        }
    }
}

package me.kyllian.gameboy.handlers;

import me.kyllian.gameboy.GameboyPlugin;
import me.kyllian.gameboy.data.Button;
import me.kyllian.gameboy.data.Pocket;
import org.bukkit.entity.Player;

/**
 * Centralized input handler for GameBoy emulator controls.
 * Consolidates all input processing logic in one place.
 */
public class InputHandler {

    private GameboyPlugin plugin;

    public InputHandler(GameboyPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle directional movement input from player movement or steering packets
     * @param player The player
     * @param leftPressed Left direction pressed
     * @param rightPressed Right direction pressed
     * @param upPressed Up direction pressed
     * @param downPressed Down direction pressed
     */
    public void handleMovementInput(Player player, boolean leftPressed, boolean rightPressed, 
                                   boolean upPressed, boolean downPressed) {
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        if (pocket.isEmpty()) return;

        pocket.getButtonToggleHelper().press(Button.BUTTONLEFT, leftPressed);
        pocket.getButtonToggleHelper().press(Button.BUTTONRIGHT, rightPressed);
        pocket.getButtonToggleHelper().press(Button.BUTTONUP, upPressed);
        pocket.getButtonToggleHelper().press(Button.BUTTONDOWN, downPressed);
    }

    /**
     * Handle A button input
     * @param player The player
     * @param pressed Button state
     */
    public void handleAButton(Player player, boolean pressed) {
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        if (pocket.isEmpty()) return;
        pocket.getButtonToggleHelper().press(Button.BUTTONA, pressed);
    }

    /**
     * Handle B button input (typically from right-click)
     * @param player The player
     * @param pressed Button state
     */
    public void handleBButton(Player player, boolean pressed) {
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        if (pocket.isEmpty()) return;
        pocket.getButtonToggleHelper().press(Button.BUTTONB, pressed);
    }

    /**
     * Handle START button input
     * @param player The player
     * @param pressed Button state
     */
    public void handleStartButton(Player player, boolean pressed) {
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        if (pocket.isEmpty()) return;
        pocket.getButtonToggleHelper().press(Button.BUTTONSTART, pressed);
    }

    /**
     * Handle SELECT button input
     * @param player The player
     * @param pressed Button state
     */
    public void handleSelectButton(Player player, boolean pressed) {
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        if (pocket.isEmpty()) return;
        pocket.getButtonToggleHelper().press(Button.BUTTONSELECT, pressed);
    }

    /**
     * Check if player has an active gameboy
     * @param player The player
     * @return true if player has an active gameboy
     */
    public boolean hasActiveGameboy(Player player) {
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        return !pocket.isEmpty();
    }

    /**
     * Handle gameboy stop request
     * @param player The player
     */
    public void handleStopGameboy(Player player) {
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        if (!pocket.isEmpty()) {
            pocket.stopEmulator(player);
            player.sendMessage(plugin.getMessageHandler().getMessage("stopped"));
        }
    }
}
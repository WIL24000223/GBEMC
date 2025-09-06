package me.kyllian.gameboy.listeners.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.kyllian.gameboy.GameboyPlugin;
import org.bukkit.entity.Player;

public class SteerVehicleListener {

    public SteerVehicleListener(GameboyPlugin gameboyPlugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(gameboyPlugin, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (!gameboyPlugin.getInputHandler().hasActiveGameboy(player)) return;
                
                PacketContainer container = event.getPacket();
                float sideways = container.getFloat().read(0);
                float forward = container.getFloat().read(1);
                
                gameboyPlugin.getInputHandler().handleMovementInput(player,
                    sideways > 0,   // left
                    sideways < 0,   // right
                    forward > 0,    // up
                    forward < 0     // down
                );
                
                gameboyPlugin.getInputHandler().handleAButton(player, container.getBooleans().read(0));
                
                if (container.getBooleans().read(1)) {
                    gameboyPlugin.getInputHandler().handleStopGameboy(player);
                    return;
                }
            }
        });
    }
}

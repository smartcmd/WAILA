package me.daoge.waila;

import lombok.Getter;
import me.daoge.waila.info.BlockInfoBuilder;
import me.daoge.waila.util.RayCastResult;
import me.daoge.waila.util.RayCastUtil;
import org.allaymc.api.block.type.BlockTypes;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.server.Server;

/**
 * WAILA (What Am I Looking At) plugin for AllayMC.
 * Displays information about the block the player is looking at in the action bar.
 */
public class WAILA extends Plugin {

    @Getter
    private static WAILA instance;

    private static final int UPDATE_INTERVAL_TICKS = 2; // 100ms refresh rate
    private static final double MAX_DISTANCE = 10.0;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Server.getInstance().getScheduler().scheduleRepeating(this, this::updateAllPlayers, UPDATE_INTERVAL_TICKS);
        getPluginLogger().info("WAILA enabled! Looking at blocks will show info in action bar.");
    }

    private void updateAllPlayers() {
        Server.getInstance().getPlayerManager().forEachPlayer(player -> {
            EntityPlayer entityPlayer = player.getControlledEntity();
            // Skip players who are not fully spawned
            if (entityPlayer == null || !entityPlayer.isSpawned()) {
                return;
            }

            // Perform raycast to find target block
            RayCastResult result = RayCastUtil.raycastBlock(entityPlayer, MAX_DISTANCE);

            if (result == null || result.blockState().getBlockType() == BlockTypes.AIR) {
                return; // No block in sight, don't send empty action bar
            }

            // Build info string
            String info = BlockInfoBuilder.buildInfo(result, player);

            // Send to player's action bar
            player.sendActionBar(info);
        });
    }

    @Override
    public void onDisable() {
        getPluginLogger().info("WAILA disabled.");
    }
}

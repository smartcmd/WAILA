package me.daoge.waila.util;

import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.blockentity.BlockEntity;
import org.allaymc.api.math.position.Position3i;
import org.allaymc.api.world.Dimension;

/**
 * Result of a block raycast operation.
 *
 * @param blockState The block state at the hit position
 * @param position   The position of the hit block
 * @param dimension  The dimension containing the block
 * @param distance   The distance from the player's eye to the hit point
 */
public record RayCastResult(
        BlockState blockState,
        Position3i position,
        Dimension dimension,
        double distance
) {
    /**
     * Gets the block entity at this position, if any.
     *
     * @return The block entity, or null if none exists
     */
    public BlockEntity getBlockEntity() {
        return dimension.getBlockEntity(position.x(), position.y(), position.z());
    }
}

package me.daoge.waila.util;

import org.allaymc.api.block.type.BlockTypes;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.math.MathUtils;
import org.allaymc.api.math.position.Position3i;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.joml.primitives.Rayd;

/**
 * Utility class for performing raycasts to find blocks a player is looking at.
 */
public final class RayCastUtil {

    private RayCastUtil() {
        // Utility class
    }

    /**
     * Performs a raycast from the player's eye position in their look direction.
     *
     * @param player      The player to raycast from
     * @param maxDistance Maximum distance to check
     * @return The raycast result, or null if no block was hit
     */
    public static RayCastResult raycastBlock(EntityPlayer player, double maxDistance) {
        var location = player.getLocation();
        var dimension = player.getDimension();

        // Calculate eye position
        var eyePos = new Vector3d(location.x(), location.y() + player.getEyeHeight(), location.z());

        // Calculate direction vector from yaw and pitch
        var direction = MathUtils.getDirectionVector(location.yaw(), location.pitch());

        // Calculate end position
        var endPos = new Vector3d(eyePos).add(direction.mul(maxDistance, new Vector3d()));

        // Create AABB that encompasses the entire ray path
        var aabb = new AABBd(
                Math.min(eyePos.x, endPos.x) - 0.5,
                Math.min(eyePos.y, endPos.y) - 0.5,
                Math.min(eyePos.z, endPos.z) - 0.5,
                Math.max(eyePos.x, endPos.x) + 0.5,
                Math.max(eyePos.y, endPos.y) + 0.5,
                Math.max(eyePos.z, endPos.z) + 0.5
        );

        // Create ray for intersection testing
        var ray = new Rayd(eyePos, direction);

        // Track the closest hit
        final class HitResult {
            double nearestT = Double.MAX_VALUE;
            int hitX, hitY, hitZ;
            org.allaymc.api.block.type.BlockState hitState = null;
        }
        var hitResult = new HitResult();

        // Iterate through all blocks in the AABB
        dimension.forEachBlockStates(aabb, 0, (x, y, z, blockState) -> {
            // Skip air blocks
            if (blockState.getBlockType() == BlockTypes.AIR) {
                return;
            }

            // Get the collision shape and test intersection
            var collisionShape = blockState.getBlockStateData().computeOffsetCollisionShape(x, y, z);
            if (collisionShape.getSolids().isEmpty()) {
                // For blocks without collision (like flowers), use a simple 1x1x1 box
                var simpleBox = new AABBd(x, y, z, x + 1, y + 1, z + 1);
                var result = new Vector2d();
                if (simpleBox.intersectsRay(ray, result) && result.x >= 0 && result.x < hitResult.nearestT) {
                    hitResult.nearestT = result.x;
                    hitResult.hitX = x;
                    hitResult.hitY = y;
                    hitResult.hitZ = z;
                    hitResult.hitState = blockState;
                }
            } else {
                var result = new Vector2d();
                if (collisionShape.intersectsRay(ray, result) && result.x >= 0 && result.x < hitResult.nearestT) {
                    hitResult.nearestT = result.x;
                    hitResult.hitX = x;
                    hitResult.hitY = y;
                    hitResult.hitZ = z;
                    hitResult.hitState = blockState;
                }
            }
        });

        // Check if we found a hit within max distance
        if (hitResult.hitState != null && hitResult.nearestT <= maxDistance) {
            return new RayCastResult(
                    hitResult.hitState,
                    new Position3i(hitResult.hitX, hitResult.hitY, hitResult.hitZ, dimension),
                    dimension,
                    hitResult.nearestT
            );
        }

        return null;
    }
}

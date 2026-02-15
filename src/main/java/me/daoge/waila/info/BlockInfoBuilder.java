package me.daoge.waila.info;

import me.daoge.waila.TrKeys;
import me.daoge.waila.util.RayCastResult;
import org.allaymc.api.block.property.type.BlockPropertyTypes;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.blockentity.BlockEntity;
import org.allaymc.api.blockentity.component.*;
import org.allaymc.api.blockentity.interfaces.*;
import org.allaymc.api.item.ItemStack;
import org.allaymc.api.item.interfaces.ItemAirStack;
import org.allaymc.api.message.I18n;
import org.allaymc.api.message.LangCode;
import org.allaymc.api.player.Player;

/**
 * Builds information strings for blocks to display in the action bar.
 */
public final class BlockInfoBuilder {

    private BlockInfoBuilder() {
        // Utility class
    }

    /**
     * Builds a complete information string for a block.
     *
     * @param result The raycast result containing block info
     * @param player The player viewing the block
     * @return Formatted information string
     */
    public static String buildInfo(RayCastResult result, Player player) {
        var sb = new StringBuilder();
        var blockState = result.blockState();
        var blockEntity = result.getBlockEntity();
        var langCode = player.getLoginData().getLangCode();

        var blockName = I18n.get().tr(langCode, blockState.getBlockStateData().translationKey());
        sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_NAME, blockName));

        // Hardness and harvest info (only if block is breakable)
        var blockStateData = blockState.getBlockStateData();
        float hardness = blockStateData.hardness();
        var controlledEntity = player.getControlledEntity();
        if (hardness >= 0 && controlledEntity != null) {
            // Calculate destroy time using Allay API (considers player's held item)
            ItemStack handItem = controlledEntity.getItemInHand();
            var blockBehavior = blockState.getBlockType().getBlockBehavior();
            double destroyTime = blockBehavior.calculateBreakTime(blockState, handItem, controlledEntity);
            sb.append("\n").append(I18n.get().tr(langCode, TrKeys.BLOCK_DESTROY_TIME, String.format("%.1f", destroyTime)));

            // Can harvest check
            // If block doesn't require correct tool for drops, any tool works (including bare hands)
            // Otherwise, check if current tool is correct
            boolean canHarvest = !blockStateData.requiresCorrectToolForDrops() || handItem.isCorrectToolFor(blockState);
            String harvestIcon = canHarvest ? "§a✔" : "§c✘";  // ✔ or ✘
            sb.append("\n").append(I18n.get().tr(langCode, TrKeys.BLOCK_CAN_HARVEST, harvestIcon));
        } else if (hardness < 0) {
            // Unbreakable block
            sb.append("\n").append(I18n.get().tr(langCode, TrKeys.BLOCK_UNBREAKABLE));
        }

        // Special block info based on block type
        String specialInfo = buildSpecialInfo(blockState, blockEntity, langCode);
        if (!specialInfo.isEmpty()) {
            sb.append("\n").append(specialInfo);
        }

        // Distance and position footer
        sb.append("\n").append(buildFooterInfo(result, langCode));

        return sb.toString();
    }

    /**
     * Builds footer information (distance and coordinates).
     */
    private static String buildFooterInfo(RayCastResult result, LangCode langCode) {
        var pos = result.position();
        String distance = String.format("%.1f", result.distance());
        return I18n.get().tr(langCode, TrKeys.DISTANCE, distance) + " " +
                I18n.get().tr(langCode, TrKeys.POS, pos.x(), pos.y(), pos.z());
    }

    /**
     * Builds special information based on block type.
     */
    private static String buildSpecialInfo(BlockState blockState, BlockEntity blockEntity, LangCode langCode) {
        var sb = new StringBuilder();
        var blockType = blockState.getBlockType();

        // Cake
        if (blockType.hasProperty(BlockPropertyTypes.BITE_COUNTER)) {
            Integer biteCounter = blockState.getPropertyValue(BlockPropertyTypes.BITE_COUNTER);
            if (biteCounter != null) {
                int remaining = 7 - biteCounter;
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_CAKE_STATUS, remaining));
            }
        }

        // Crops (wheat, carrots, potatoes, beetroot)
        if (blockType.hasProperty(BlockPropertyTypes.GROWTH)) {
            Integer growth = blockState.getPropertyValue(BlockPropertyTypes.GROWTH);
            if (growth != null) {
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_CROP_GROWTH, growth));
            }
        }

        // Redstone wire
        if (blockType.hasProperty(BlockPropertyTypes.REDSTONE_SIGNAL)) {
            Integer signal = blockState.getPropertyValue(BlockPropertyTypes.REDSTONE_SIGNAL);
            if (signal != null) {
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_REDSTONE_LEVEL, signal));
            }
        }

        // Door/trapdoor open state
        if (blockType.hasProperty(BlockPropertyTypes.OPEN_BIT)) {
            Boolean openBit = blockState.getPropertyValue(BlockPropertyTypes.OPEN_BIT);
            if (openBit != null) {
                String key = openBit ? TrKeys.BLOCK_OPEN_TRUE : TrKeys.BLOCK_OPEN_FALSE;
                sb.append(I18n.get().tr(langCode, key));
            }
        }

        // Composter
        if (blockType.hasProperty(BlockPropertyTypes.COMPOSTER_FILL_LEVEL)) {
            Integer fillLevel = blockState.getPropertyValue(BlockPropertyTypes.COMPOSTER_FILL_LEVEL);
            if (fillLevel != null) {
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_COMPOSTER_LEVEL, fillLevel));
            }
        }

        // Block entity specific info
        if (blockEntity != null) {
            String beInfo = buildBlockEntityInfo(blockEntity, langCode);
            if (!beInfo.isEmpty()) {
                if (!sb.isEmpty()) {
                    sb.append("\n");
                }
                sb.append(beInfo);
            }
        }

        return sb.toString();
    }

    /**
     * Builds information from block entities.
     */
    private static String buildBlockEntityInfo(BlockEntity blockEntity, LangCode langCode) {
        var sb = new StringBuilder();

        // Furnace family
        if (blockEntity instanceof BlockEntityFurnace furnace) {
            int storedXP = furnace.getStoredXP();
            if (storedXP > 0) {
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_FURNACE_XP, storedXP));
            }
            int burnTime = furnace.getBurnTime();
            if (burnTime > 0) {
                int burnDuration = furnace.getBurnDuration();
                if (!sb.isEmpty()) sb.append("\n");
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_FURNACE_FUEL, burnTime / 20, burnDuration / 20));
            }
            int cookTime = furnace.getCookTime();
            if (cookTime > 0) {
                if (!sb.isEmpty()) sb.append("\n");
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_FURNACE_PROGRESS, cookTime / 2));
            }
        }

        // Brewing stand
        if (blockEntity instanceof BlockEntityBrewingStand brewingStand) {
            int brewTime = brewingStand.getBrewTime();
            if (brewTime > 0) {
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_BREWING_TIME, String.format("%.1f", brewTime / 20.0)));
            }
            int fuelAmount = brewingStand.getFuelAmount();
            int fuelTotal = brewingStand.getFuelTotal();
            if (fuelAmount > 0) {
                if (!sb.isEmpty()) sb.append("\n");
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_BREWING_FUEL, fuelAmount, fuelTotal));
            }
        }

        // Beacon
        if (blockEntity instanceof BlockEntityBeacon beacon) {
            var primary = beacon.getPrimaryEffect();
            var secondary = beacon.getSecondaryEffect();
            if (primary != null) {
                // Use effect identifier for display
                String primaryName = primary.getIdentifier().path();
                String secondaryName = secondary != null ? secondary.getIdentifier().path() : "";
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_BEACON_EFFECTS, primaryName, secondaryName));
            }
        }

        // Container (chests, barrels, etc.)
        if (blockEntity instanceof BlockEntityContainerHolderComponent containerHolder) {
            var container = containerHolder.getContainer();
            if (container != null) {
                var itemStacks = container.getItemStackArray();
                int usedSlots = 0;
                int totalItems = 0;
                for (var item : itemStacks) {
                    if (item != ItemAirStack.AIR_STACK) {
                        usedSlots++;
                        totalItems += item.getCount();
                    }
                }
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_CONTAINER_SIZE, usedSlots, itemStacks.length, totalItems));
            }
        }

        // Jukebox
        if (blockEntity instanceof BlockEntityJukebox jukebox) {
            var record = jukebox.getMusicDiscItem();
            if (record != null && record != ItemAirStack.AIR_STACK) {
                String recordName = record.getItemType().getIdentifier().path();
                sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_JUKEBOX_RECORD, recordName));
            }
        }

        // Comparator
        if (blockEntity instanceof BlockEntityComparator comparator) {
            int signal = comparator.getOutputSignal();
            sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_COMPARATOR_SIGNAL, signal));
        }

        // Noteblock
        if (blockEntity instanceof BlockEntityNoteblock noteblock) {
            int pitch = noteblock.getPitch();
            sb.append(I18n.get().tr(langCode, TrKeys.BLOCK_NOTEBLOCK_NOTE, pitch));
        }

        return sb.toString();
    }
}

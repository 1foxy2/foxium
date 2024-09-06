package me.jellysquid.mods.lithium.common.compat.fabric_transfer_api_v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.LoadingModList;

public class FabricTransferApiCompat {
    public static final boolean FABRIC_TRANSFER_API_V_1_PRESENT;

    static {
        FABRIC_TRANSFER_API_V_1_PRESENT = LoadingModList.get().getModFileById("fabric_transfer_api_v1") != null;
    }

    public static boolean canHopperInteractWithApiInventory(HopperBlockEntity hopperBlockEntity, BlockState hopperState, boolean extracting) {
        Direction direction = extracting ? Direction.UP : hopperState.getValue(HopperBlock.FACING);
        BlockPos targetPos = hopperBlockEntity.getBlockPos().relative(direction);

        //noinspection UnstableApiUsage
        Object target = null; // ItemStorage.SIDED.find(hopperBlockEntity.getWorld(), targetPos, direction.getOpposite());
        return target != null;
    }
}

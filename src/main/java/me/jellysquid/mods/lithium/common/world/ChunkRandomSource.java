package me.jellysquid.mods.lithium.common.world;

import net.minecraft.core.BlockPos;

public interface ChunkRandomSource {
    /**
     * Alternative implementation of {@link net.minecraft.world.level.Level#getBlockRandomPos(int, int, int, int)} which does not allocate
     * a new {@link net.minecraft.core.BlockPos}.
     */
    void lithium$getRandomPosInChunk(int x, int y, int z, int mask, BlockPos.MutableBlockPos out);
}

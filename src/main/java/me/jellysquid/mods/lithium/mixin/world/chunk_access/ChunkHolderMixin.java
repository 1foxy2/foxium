package me.jellysquid.mods.lithium.mixin.world.chunk_access;

import me.jellysquid.mods.lithium.common.world.chunk.ChunkHolderExtended;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin extends GenerationChunkHolder implements ChunkHolderExtended {
    @Unique
    private long lastRequestTime;

    public ChunkHolderMixin(ChunkPos pos) {
        super(pos);
    }

    @Override
    public boolean lithium$updateLastAccessTime(long time) {
        long prev = this.lastRequestTime;
        this.lastRequestTime = time;

        return prev != time;
    }

    @Override
    public LevelChunk getCurrentlyLoading() {
        return this.currentlyLoading;
    }
}

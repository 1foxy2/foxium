package me.jellysquid.mods.lithium.common.world.chunk;

import net.minecraft.world.level.chunk.LevelChunk;

public interface ChunkHolderExtended {
    /**
     * Updates the last accessed timestamp for this chunk. This is used to determine if a ticket was recently
     * created for it.
     *
     * @param time The current time
     * @return True if the chunk needs a new ticket to be created in order to retain it, otherwise false
     */
    boolean lithium$updateLastAccessTime(long time);
}

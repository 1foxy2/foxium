package me.jellysquid.mods.lithium.mixin.entity.collisions.fluid;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import me.jellysquid.mods.lithium.common.block.BlockCountingSection;
import me.jellysquid.mods.lithium.common.block.BlockStateFlags;
import me.jellysquid.mods.lithium.common.entity.FluidCachingEntity;
import me.jellysquid.mods.lithium.common.util.Pos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class EntityMixin implements FluidCachingEntity {

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    private Level level;

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow
    protected Object2DoubleMap<FluidType> forgeFluidTypeHeight;

    @Inject(
            method = "updateFluidHeightAndDoFluidPushing()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;isPushedByFluid()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void tryShortcutFluidPushing(CallbackInfo cir, AABB box, int x1, int x2, int y1, int y2, int z1, int z2, double zero) {
        int chunkX1 = x1 >> 4;
        int chunkZ1 = z1 >> 4;
        int chunkX2 = ((x2 - 1) >> 4);
        int chunkZ2 = ((z2 - 1) >> 4);
        int chunkYIndex1 = Math.max(Pos.SectionYIndex.fromBlockCoord(this.level, y1), Pos.SectionYIndex.getMinYSectionIndex(this.level));
        int chunkYIndex2 = Math.min(Pos.SectionYIndex.fromBlockCoord(this.level, y2 - 1), Pos.SectionYIndex.getMaxYSectionIndexInclusive(this.level));
        for (int chunkX = chunkX1; chunkX <= chunkX2; chunkX++) {
            for (int chunkZ = chunkZ1; chunkZ <= chunkZ2; chunkZ++) {
                LevelChunk chunk = this.level.getChunk(chunkX, chunkZ);
                for (int chunkYIndex = chunkYIndex1; chunkYIndex <= chunkYIndex2; chunkYIndex++) {
                    LevelChunkSection section = chunk.getSections()[chunkYIndex];
                    if (((BlockCountingSection) section).lithium$mayContainAny(BlockStateFlags.ANY)) {
                        //fluid found, cannot skip code
                        return;
                    }
                }
            }
        }

        //side effects of not finding a fluid:
        if(!this.forgeFluidTypeHeight.isEmpty()) {
            // only call clear() if map contains anything, because array maps use naive clear
            this.forgeFluidTypeHeight.clear();
        }

        cir.cancel();
    }
}

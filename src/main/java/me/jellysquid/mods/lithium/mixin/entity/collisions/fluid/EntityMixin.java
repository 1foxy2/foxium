package me.jellysquid.mods.lithium.mixin.entity.collisions.fluid;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import me.jellysquid.mods.lithium.common.block.BlockCountingSection;
import me.jellysquid.mods.lithium.common.block.BlockStateFlags;
import me.jellysquid.mods.lithium.common.entity.FluidCachingEntity;
import me.jellysquid.mods.lithium.common.util.Pos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

@Mixin(value = Entity.class, priority = 900)
public abstract class EntityMixin implements FluidCachingEntity {

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    private Level level;

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow
    protected Object2DoubleMap<FluidType> forgeFluidTypeHeight;

    @Shadow
    abstract void updateInWaterStateAndDoWaterCurrentPushing();

    @Shadow
    @Nullable
    public abstract Entity getVehicle();

    @Shadow
    public abstract void extinguishFire();

    @Shadow
    public float fallDistance;

    @Shadow
    public abstract boolean isInFluidType();

    private boolean foxium$isInModdedFluid;

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
                    if (((BlockCountingSection) section).lithium$mayContainAny(BlockStateFlags.ANY_FLUID)) {
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

    /**
     * @author embeddedt
     * @reason Track when the entity is in a non-vanilla fluid type. This flag is used to skip looping through
     * fluid types when entities are only in vanilla fluids.
     */
    @Inject(method = "setFluidTypeHeight", at = @At("RETURN"))
    private void markInModdedFluid(FluidType type, double height, CallbackInfo ci) {
        if(!type.isAir() && !type.isVanilla()) {
            this.foxium$isInModdedFluid = true;
        }
    }

    /**
     * @author embeddedt
     * @reason Early-exit when not in a modded fluid, avoid streams & allocations for other calculations
     */
    @Overwrite
    protected boolean updateInWaterStateAndDoFluidPushing() {
        this.fluidHeight.clear();
        this.forgeFluidTypeHeight.clear();
        this.foxium$isInModdedFluid = false;
        this.updateInWaterStateAndDoWaterCurrentPushing();

        if (this.foxium$isInModdedFluid && !(this.getVehicle() instanceof Boat)) {
            float fallDistanceModifier = Float.MAX_VALUE;
            boolean canExtinguish = false;

            for(FluidType type : this.forgeFluidTypeHeight.keySet()) {
                if(!type.isAir() && !type.isVanilla()) {
                    fallDistanceModifier = Math.min(((Entity) (Object)this).getFluidFallDistanceModifier(type), fallDistanceModifier);
                    canExtinguish |= ((Entity) (Object)this).canFluidExtinguish(type);
                }
            }

            if (fallDistanceModifier != Float.MAX_VALUE) {
                this.fallDistance *= fallDistanceModifier;
            }

            if (canExtinguish) {
                this.extinguishFire();
            }
        }

        return this.isInFluidType();
    }

    /**
     * @author embeddedt
     * @reason early-exit for entities with no fluids (the likely case), avoid streams
     */
    @Overwrite
    public final boolean isInFluidType(BiPredicate<FluidType, Double> predicate, boolean forAllTypes) {
        if(this.forgeFluidTypeHeight.isEmpty()) {
            return false;
        } else {
            ObjectIterator<Object2DoubleMap.Entry<FluidType>> it = Object2DoubleMaps.fastIterator(this.forgeFluidTypeHeight);
            if(forAllTypes) {
                // Check if all fluids match
                while (it.hasNext()) {
                    var entry = it.next();
                    if(!predicate.test(entry.getKey(), entry.getDoubleValue())) {
                        return false;
                    }
                }
                return true;
            } else {
                // Check if any fluid matches
                while (it.hasNext()) {
                    var entry = it.next();
                    if(predicate.test(entry.getKey(), entry.getDoubleValue())) {
                        return true;
                    }
                }
                return false;
            }

        }
    }
}

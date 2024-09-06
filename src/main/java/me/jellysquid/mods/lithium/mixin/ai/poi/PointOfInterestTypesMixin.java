package me.jellysquid.mods.lithium.mixin.ai.poi;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

/**
 * Replaces the backing map type with a faster collection type which uses reference equality.
 */
@Mixin(targets = {"net.neoforged/neoforge.registries.NeoForgeRegistryCallbacks$PoiTypeCallbacks"})
public class PointOfInterestTypesMixin {
    @Shadow
    @Final
    @Mutable
    static Map<BlockState, Holder<PoiType>> BLOCKSTATE_TO_POI_TYPE_MAP;

    static {
        BLOCKSTATE_TO_POI_TYPE_MAP = new Reference2ReferenceOpenHashMap<>(BLOCKSTATE_TO_POI_TYPE_MAP);
    }
}


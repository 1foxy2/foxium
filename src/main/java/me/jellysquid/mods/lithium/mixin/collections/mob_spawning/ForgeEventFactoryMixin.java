package me.jellysquid.mods.lithium.mixin.collections.mob_spawning;

import me.jellysquid.mods.lithium.common.world.PotentialSpawnsExtended;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EventHooks.class)
public class ForgeEventFactoryMixin {
    /**
     * @author embeddedt
     * @reason Avoid the overhead of re-creating a pool in the event that the spawn list was not changed.
     */
    @Inject(method = "getPotentialSpawns",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/random/WeightedRandomList;create(Ljava/util/List;)Lnet/minecraft/util/random/WeightedRandomList;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void reusePoolIfPossible(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList, CallbackInfoReturnable<WeightedRandomList<MobSpawnSettings.SpawnerData>> cir, LevelEvent.PotentialSpawns event) {
        if(!((PotentialSpawnsExtended)event).radium$wasListModified()) {
            cir.setReturnValue(oldList);
        }
    }
}

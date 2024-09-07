package me.jellysquid.mods.lithium.common;

import me.jellysquid.mods.lithium.common.ai.pathing.BlockStatePathingCache;
import me.jellysquid.mods.lithium.common.config.LithiumConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.callback.BakeCallback;

@Mod(value = LithiumMod.MOD_ID)
public class LithiumMod {
    public static LithiumConfig CONFIG;
    public static final String MOD_ID = "foxium";

    public LithiumMod(IEventBus modEventBus) {
        if (CONFIG == null) {
            throw new IllegalStateException("The mixin plugin did not initialize the config! Did it not load?");
        }
        modEventBus.addListener(EventPriority.LOWEST, this::modifyRegistries);

    }

    private void modifyRegistries(ModifyRegistriesEvent event) {
        if(BlockStatePathingCache.class.isAssignableFrom(BlockState.class)) {
            BuiltInRegistries.BLOCK.addCallback((BakeCallback<Block>) registry -> {
                for(Block block : registry) {
                    for(BlockState state : block.getStateDefinition().getPossibleStates()) {
                        ((BlockStatePathingCache)state).lithium$initPathCache();
                    }
                }
            });
        }
    }
}

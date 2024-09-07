package me.jellysquid.mods.lithium.api.inventory;

public interface LithiumCooldownReceivingInventory {

    /**
     * To be implemented by modded inventories that want to receive hopper-like transfer cooldowns from foxium's (!)
     * item transfers. Hopper-like transfer cooldown means a cooldown that is only set if the hopper was empty before
     * the transfer.
     * NOTE: foxium does not replace all of vanilla's item transfers. Mod authors still need to implement
     * their own hooks for vanilla code even when they require users to install foxium.
     *
     * @param currentTime tick time of the item transfer.
     */
    default void setTransferCooldown(long currentTime) {
    }


    /**
     * To be implemented by modded inventories that want to receive hopper-like transfer cooldowns from foxium's (!)
     * item transfers. Hopper-like transfer cooldown means a cooldown that is only set if the hopper was empty before
     * the transfer.
     * NOTE: foxium does not replace all of vanilla's item transfers. Mod authors still need to implement
     * their own hooks for vanilla code even when they require users to install foxium.
     *
     * @return Whether this inventory wants to receive transfer cooldowns from foxium's code
     */
    default boolean canReceiveTransferCooldown() {
        return false;
    }
}


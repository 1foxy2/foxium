package me.jellysquid.mods.lithium.mixin.util.item_type_tracking;

import me.jellysquid.mods.lithium.common.util.change_tracking.ChangePublisher;
import me.jellysquid.mods.lithium.common.util.change_tracking.ChangeSubscriber;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements ChangePublisher<ItemEntity>, ChangeSubscriber<ItemStack> {

    @Shadow
    public abstract ItemStack getStack();

    @Unique
    private ChangeSubscriber<ItemEntity> subscriber;

    @Unique
    private boolean startTrackingChanges() {
        ItemStack stack = this.getStack();
        if (!stack.isEmpty()) {
            //noinspection unchecked
            return ((ChangePublisher<ItemStack>) (Object) stack).lithium$subscribe(this);
        }
        return true;
    }

    @Override
    public boolean lithium$subscribe(ChangeSubscriber<ItemEntity> subscriber) {
        if (this.subscriber == null) {
            boolean b = this.startTrackingChanges();
            if (!b) {
                return false;
            }
        }
        this.subscriber = ChangeSubscriber.add(this.subscriber, subscriber);
        return false;
    }

    @Override
    public void lithium$unsubscribe(ChangeSubscriber<ItemEntity> subscriber) {
        this.subscriber = ChangeSubscriber.remove(this.subscriber, subscriber);
        if (this.subscriber == null) {
            ItemStack stack = this.getStack();
            if (!stack.isEmpty()) {
                //noinspection unchecked
                ((ChangePublisher<ItemStack>) (Object) stack).lithium$unsubscribe(this);
            }
        }
    }

    @Override
    public void lithium$notify(ItemStack publisher) {
        if (publisher != this.getStack()) {
            throw new IllegalStateException("Received notification from an unexpected publisher");
        }

        if (this.subscriber != null) {
            this.subscriber.lithium$notify((ItemEntity) (Object) this);
        }
    }

    @Override
    public void lithium$forceUnsubscribe(ItemStack publisher) {
        if (this.subscriber != null) {
            this.subscriber.lithium$forceUnsubscribe((ItemEntity) (Object) this);
        }
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    private void beforeSetStack(ItemStack newStack, CallbackInfo ci) {
        if (this.subscriber != null) {
            ItemStack oldStack = this.getStack();
            if (oldStack != newStack) {
                if (!oldStack.isEmpty()) {
                    //noinspection unchecked
                    ((ChangePublisher<ItemStack>) (Object) oldStack).lithium$unsubscribe(this);
                }

                if (!newStack.isEmpty()) {
                    //noinspection unchecked
                    ((ChangePublisher<ItemStack>) (Object) newStack).lithium$subscribe(this);
                    this.subscriber.lithium$notify((ItemEntity) (Object) this);
                } else {
                    this.subscriber.lithium$forceUnsubscribe((ItemEntity) (Object) this);
                }
            }
        }
    }
}

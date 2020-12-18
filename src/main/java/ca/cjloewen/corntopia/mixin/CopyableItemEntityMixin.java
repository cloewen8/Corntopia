package ca.cjloewen.corntopia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.ItemEntity;

@Mixin(ItemEntity.class)
public abstract interface CopyableItemEntityMixin {
	@Accessor("pickupDelay")
	public int getPickupDelay();
}

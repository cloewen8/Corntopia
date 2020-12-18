package ca.cjloewen.corntopia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.ItemEntity;

@Mixin(ItemEntity.class)
public abstract interface ItemEntityMixin {
	@Accessor("pickupDelay")
	public int getPickupDelay();
	
	@Accessor("health")
	public int getHealth();
	
	@Accessor("health")
	public void setHealth(int health);
}

package ca.cjloewen.corntopia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ca.cjloewen.corntopia.item.Items;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Shadow
	public abstract ItemStack getStack();
	
	@Inject(method = "canMerge", at = @At("HEAD"), cancellable = true)
	public void cannotMergePopcorn(CallbackInfoReturnable<Boolean> info) {
		if (getStack().getItem().equals(Items.POPCORN))
			info.setReturnValue(false);
	}
}

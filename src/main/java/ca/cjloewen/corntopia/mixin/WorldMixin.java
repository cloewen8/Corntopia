package ca.cjloewen.corntopia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ca.cjloewen.corntopia.CorntopiaMod;
import ca.cjloewen.corntopia.entity.CornItemEntity;
import ca.cjloewen.corntopia.item.Items;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ModifiableWorld;

@Mixin(ServerWorld.class)
public abstract class WorldMixin implements ModifiableWorld {
	@Shadow
	public abstract boolean spawnEntity(Entity entity);
	
	@Inject(at = @At("HEAD"), method = "addEntity", cancellable = true)
	public void replaceCornItemEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if (entity.getClass().equals(ItemEntity.class) && ((ItemEntity)entity).getStack().getItem().equals(Items.CORN)) {
			CorntopiaMod.LOGGER.info("Corn item entity detected (replacing)!");
			CornItemEntity replacement = new CornItemEntity((ItemEntity)entity);
			entity.remove();
			info.setReturnValue(spawnEntity(replacement));
		}
	}
}

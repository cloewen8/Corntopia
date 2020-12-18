package ca.cjloewen.corntopia.entity;

import ca.cjloewen.corntopia.mixin.CopyableItemEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;

public class CornItemEntity extends ItemEntity {
	public CornItemEntity(ItemEntity originalEntity) {
		super(EntityType.ITEM, originalEntity.world);
		setStack(originalEntity.getStack());
		updateTrackedPosition(originalEntity.getTrackedPosition());
		refreshPositionAndAngles(originalEntity.getX(), originalEntity.getY(), originalEntity.getZ(), originalEntity.getYaw(1f), originalEntity.getPitch(1f));
		setVelocity(originalEntity.getVelocity());
		setEntityId(originalEntity.getEntityId());
		setPickupDelay(((CopyableItemEntityMixin)originalEntity).getPickupDelay());
		setUuid(originalEntity.getUuid());
	}
}

package ca.cjloewen.corntopia.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.cjloewen.corntopia.mixin.ItemEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;

public class CornItemEntity extends ItemEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public CornItemEntity(ItemEntity originalEntity) {
		super(EntityType.ITEM, originalEntity.world);
		setStack(originalEntity.getStack());
		updateTrackedPosition(originalEntity.getTrackedPosition());
		refreshPositionAndAngles(originalEntity.getX(), originalEntity.getY(), originalEntity.getZ(), originalEntity.getYaw(1f), originalEntity.getPitch(1f));
		setVelocity(originalEntity.getVelocity());
		setEntityId(originalEntity.getEntityId());
		setPickupDelay(((ItemEntityMixin)originalEntity).getPickupDelay());
		setUuid(originalEntity.getUuid());
	}
}

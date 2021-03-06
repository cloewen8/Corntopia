package ca.cjloewen.corntopia.entity;

import ca.cjloewen.corntopia.CorntopiaMod;
import ca.cjloewen.corntopia.item.Items;
import ca.cjloewen.corntopia.mixin.IItemEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class CornItemEntity extends ItemEntity {
	public static final Identifier STAT = new Identifier(CorntopiaMod.NAMESPACE, "corn_popped");
	private double explosionX;
	private double explosionY;
	private double explosionZ;
	private float explosionPower;
	
	public CornItemEntity(ItemEntity originalEntity) {
		super(EntityType.ITEM, originalEntity.world);
		explosionX = Double.POSITIVE_INFINITY;
		setStack(originalEntity.getStack());
		refreshPositionAndAngles(originalEntity.getX(), originalEntity.getY(), originalEntity.getZ(), originalEntity.getYaw(1f), originalEntity.getPitch(1f));
		setVelocity(originalEntity.getVelocity());
		setPickupDelay(((IItemEntityMixin)originalEntity).getPickupDelay());
		setEntityId(originalEntity.getEntityId());
		setUuid(originalEntity.getUuid());
	}
	
	public void setExplosion(double x, double y, double z, float power) {
		explosionX = x;
		explosionY = y;
		explosionZ = z;
		explosionPower = power;
	}
	
	public boolean damage(DamageSource source, float amount) {
		if (source.isExplosive()) {
			IItemEntityMixin thisMixin = (IItemEntityMixin)this;
			thisMixin.setHealth((int) (thisMixin.getHealth() - amount));
			if (thisMixin.getHealth() <= 0) {
				// 50 damage = 8 popcorn
				double spawnAmount = Math.min(Math.ceil(amount*0.16d), 8d)*getStack().getCount();
				World world = getEntityWorld();
				ItemEntity popcorn;
				CorntopiaMod.LOGGER.info("Spawning " + spawnAmount + " popcorn!");
				if (source instanceof EntityDamageSource) {
					Entity entity = ((EntityDamageSource)source).getAttacker();
					if (entity instanceof PlayerEntity)
						((PlayerEntity)entity).increaseStat(STAT, getStack().getCount());
				}
				for (float i = 0; i < spawnAmount; i++) {
					popcorn = new ItemEntity(world, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.POPCORN));
					// Set the velocity if the explosion position is available.
					if (explosionX != Double.POSITIVE_INFINITY) {
						Vec3d explosionPos = new Vec3d(explosionX, explosionY, explosionZ);
						// Unit vector of force direction (with 0.5 random scatter).
						double x = popcorn.getX() + random.nextDouble() - 0.5 - explosionX;
						double y = popcorn.getY() + random.nextDouble() - 0.5 - explosionY;
						double z = popcorn.getZ() + random.nextDouble() - 0.5 - explosionZ;
						double len = (double)MathHelper.sqrt(x*x + y*y + z*z);
						x /= len;
						y /= len;
						z /= len;
						double force = (double)(MathHelper.sqrt(popcorn.squaredDistanceTo(explosionPos))/(explosionPower*2f));
						double forceExposed = (1.0D - force)*Explosion.getExposure(explosionPos, popcorn);
						popcorn.setVelocity(popcorn.getVelocity().add(x*forceExposed, y*forceExposed, z*forceExposed));
					}
					world.spawnEntity(popcorn);
				}
				
				this.remove();
			}

			return false;
		} else
			return super.damage(source, amount);
	}
}

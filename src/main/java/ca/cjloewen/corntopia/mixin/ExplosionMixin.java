package ca.cjloewen.corntopia.mixin;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import ca.cjloewen.corntopia.entity.CornItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@Accessor("x")
	public abstract double getX();
	
	@Accessor("y")
	public abstract double getY();
	
	@Accessor("z")
	public abstract double getZ();
	
	@Accessor("power")
	public abstract float getPower();
	
	@SuppressWarnings("rawtypes")
	@Inject(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void addExplosion(CallbackInfo info, Set set, float q, int  r, int  s, int  t, int  u, int  v, int  w, List  list, Vec3d  vec3d,  int  x, Entity entity) {
		if (entity instanceof CornItemEntity) {
			((CornItemEntity)entity).setExplosion(getX(), getY(), getZ(), getPower());
		}
	}
}

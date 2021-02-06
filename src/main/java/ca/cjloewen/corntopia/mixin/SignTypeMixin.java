package ca.cjloewen.corntopia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.SignType;

@Mixin(SignType.class)
public interface SignTypeMixin {
	@Invoker("register")
	public static SignType invokeRegister(SignType type) {
		throw new AssertionError();
	}
}

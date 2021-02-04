package ca.cjloewen.corntopia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.loot.BinomialLootTableRange;

@Mixin(BinomialLootTableRange.class)
public interface IBinomialLootTableRange {
	@Accessor("n")
	int getN();
	
	@Accessor("p")
	float getP();
}

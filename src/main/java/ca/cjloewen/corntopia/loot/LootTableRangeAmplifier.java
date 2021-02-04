package ca.cjloewen.corntopia.loot;

import java.util.Random;

import ca.cjloewen.corntopia.CorntopiaMod;
import ca.cjloewen.corntopia.mixin.IBinomialLootTableRange;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class LootTableRangeAmplifier implements LootTableRange {
	// This loot table range can not be used in a data pack currently, sorry!
	// FIXME Add this loot table range to the LootTableRanges.TYPES
	private static final Identifier AMPLIFIER = new Identifier(CorntopiaMod.NAMESPACE, "amplifier");
	private final LootTableRange original;
	private final int amplitude;
	
	public LootTableRangeAmplifier(LootTableRange original, int amplitude) {
		this.original = original;
		this.amplitude = amplitude;
	}

	@Override
	public int next(Random random) {
		if (original instanceof UniformLootTableRange) {
			UniformLootTableRange uniformRange = (UniformLootTableRange)original;
			return MathHelper.nextInt(random, MathHelper.floor(uniformRange.getMinValue()*amplitude), MathHelper.floor(uniformRange.getMaxValue()*amplitude));
		} else if (original instanceof BinomialLootTableRange) {
			IBinomialLootTableRange binomialRange = (IBinomialLootTableRange)original;
			int n = binomialRange.getN();
			float pa = binomialRange.getP();
			int i = 0;

			for (int j = 0; j < n; ++j) {
				if (random.nextFloat() < pa) {
					++i;
				}
			}

			return i;
		} else
			return original.next(random)*amplitude;
	}

	@Override
	public Identifier getType() {
		return AMPLIFIER;
	}
}

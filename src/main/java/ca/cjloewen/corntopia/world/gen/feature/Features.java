package ca.cjloewen.corntopia.world.gen.feature;

import net.minecraft.world.gen.feature.Feature;

public class Features {
	public static final Feature<FillHorizontalFeatureConfig> FILL_HORIZONTAL;
	
	static {
		FILL_HORIZONTAL = new FillHorizontalFeature(FillHorizontalFeatureConfig.CODEC);
	}
}

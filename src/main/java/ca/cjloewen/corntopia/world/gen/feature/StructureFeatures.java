package ca.cjloewen.corntopia.world.gen.feature;

import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureFeatures {
	public static final StructureFeature<DefaultFeatureConfig> BARN = new BarnFeature(DefaultFeatureConfig.CODEC);
}

package ca.cjloewen.corntopia.world.gen.feature;

import ca.cjloewen.corntopia.block.Blocks;
import ca.cjloewen.corntopia.world.biome.BiomeKeys;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;

public class ConfiguredFeatures {
	public static final ConfiguredFeature<?, ?> CORN_STALK;
	public static final ConfiguredStructureFeature<?, ?> BARN;
	
	static {
		CORN_STALK = Features.FILL_HORIZONTAL
			.configure(ConfiguredFeatures.Configs.CORN_STALK_CONFIG);
		BARN = StructureFeatures.BARN.configure(DefaultFeatureConfig.DEFAULT);
	}
	
	public static final class Configs {
		public static final FillHorizontalFeatureConfig CORN_STALK_CONFIG;
		
		static {
			CORN_STALK_CONFIG = new FillHorizontalFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.CORN_STALK), new DoublePlantPlacer()).biome(BiomeKeys.CORN_FIELD).build();
		}
	}
	
	public static final class States {
		protected static final BlockState CORN_STALK;
		
		static {
			CORN_STALK = Blocks.CORN_STALK.getDefaultState();
		}
	}
}

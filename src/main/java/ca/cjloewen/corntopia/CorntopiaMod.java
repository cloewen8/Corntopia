package ca.cjloewen.corntopia;

import ca.cjloewen.corntopia.block.Blocks;
import ca.cjloewen.corntopia.item.Items;
import ca.cjloewen.corntopia.world.biome.BiomeKeys;
import ca.cjloewen.corntopia.world.biome.Biomes;
import ca.cjloewen.corntopia.world.gen.feature.ConfiguredFeatures;
import ca.cjloewen.corntopia.world.gen.feature.Features;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap; 
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;

@SuppressWarnings("deprecation")
public class CorntopiaMod implements ModInitializer {
	public static final String NAMESPACE = "corntopia";
	
	@Override
	public void onInitialize() {
		Identifier cornStalkId = new Identifier(NAMESPACE, "corn_stalk");
		// Corn Stalk
		Registry.register(Registry.BLOCK, cornStalkId, Blocks.CORN_STALK);
		Registry.register(Registry.ITEM, cornStalkId, Items.KERNELS);
		FlammableBlockRegistry.getDefaultInstance().add(Blocks.CORN_STALK, new FlammableBlockRegistry.Entry(80, 100));
		// Corn
		Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "corn"), Items.CORN);
		// Corn Field
		Registry.register(BuiltinRegistries.BIOME, BiomeKeys.CORN_FIELD.getValue(), Biomes.CORN_FIELD);
		Registry.register(Registry.FEATURE, new Identifier(NAMESPACE, "fill_horizontal"), Features.FILL_HORIZONTAL);
		// Corn Stalk Feature
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, cornStalkId, ConfiguredFeatures.CORN_STALK);
		// FIXME Says Deprecated but does not say what to use instead. If anyone knows, please tell me.
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION,
			RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, cornStalkId));
		OverworldBiomes.addContinentalBiome(BiomeKeys.CORN_FIELD, OverworldClimate.TEMPERATE, 1);
		
		if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
			onInitializeClient();
	}
	
	@Environment(EnvType.CLIENT)
	private void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(Blocks.CORN_STALK, RenderLayer.getCutout());
	}
}

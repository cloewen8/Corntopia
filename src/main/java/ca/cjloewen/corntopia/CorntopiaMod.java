package ca.cjloewen.corntopia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.cjloewen.corntopia.block.Blocks;
import ca.cjloewen.corntopia.entity.CornItemEntity;
import ca.cjloewen.corntopia.item.Items;
import ca.cjloewen.corntopia.structure.StructurePieceType;
import ca.cjloewen.corntopia.structure.processor.BrokenStructureProcessor;
import ca.cjloewen.corntopia.structure.processor.LevelBuildingStructureProcessor;
import ca.cjloewen.corntopia.structure.processor.OffsetYStructureProcessor;
import ca.cjloewen.corntopia.world.biome.BiomeKeys;
import ca.cjloewen.corntopia.world.biome.Biomes;
import ca.cjloewen.corntopia.world.gen.feature.ConfiguredFeatures;
import ca.cjloewen.corntopia.world.gen.feature.Features;
import ca.cjloewen.corntopia.world.gen.feature.StructureFeatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;

@SuppressWarnings("deprecation")
public class CorntopiaMod implements ModInitializer {
	public static final String NAMESPACE = "corntopia";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize() {
		Identifier cornStalkId = new Identifier(NAMESPACE, "corn_stalk");
		Identifier barnId = new Identifier(NAMESPACE, "barn");
		// Barn planks (no signs, trapdoors or buttons)
		// Going to try adding signs once they work properly.
		BuildingBlocks.register("barn", 0b01110000);
		// Barn Roof Planks
		BuildingBlocks.register("barn_roof", 0b01111000);
		// Corn Stalk
		Registry.register(Registry.BLOCK, cornStalkId, Blocks.CORN_STALK);
		Registry.register(Registry.ITEM, cornStalkId, Items.KERNELS);
		FlammableBlockRegistry.getDefaultInstance().add(Blocks.CORN_STALK, new FlammableBlockRegistry.Entry(80, 100));
		// Corn
		Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "corn"), Items.CORN);
		CompostingChanceRegistry.INSTANCE.add(Items.CORN, 0.65F);
		// Popcorn
		Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "popcorn"), Items.POPCORN);
		Registry.register(Registry.CUSTOM_STAT, CornItemEntity.STAT.getPath(), CornItemEntity.STAT);
		Stats.CUSTOM.getOrCreateStat(CornItemEntity.STAT);
		// Corn Field
		Registry.register(BuiltinRegistries.BIOME, BiomeKeys.CORN_FIELD.getValue(), Biomes.CORN_FIELD);
		Registry.register(Registry.FEATURE, new Identifier(NAMESPACE, "fill_horizontal"), Features.FILL_HORIZONTAL);
		// Corn Stalk Feature
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, cornStalkId, ConfiguredFeatures.CORN_STALK);
		// Structure Processors
		Registry.register(Registry.STRUCTURE_PROCESSOR, new Identifier(NAMESPACE, "offset_y"),
				OffsetYStructureProcessor.OFFSETY);
		Registry.register(Registry.STRUCTURE_PROCESSOR, new Identifier(NAMESPACE, "broken"),
				BrokenStructureProcessor.BROKEN);
		Registry.register(Registry.STRUCTURE_PROCESSOR, new Identifier(NAMESPACE, "level_building"),
				LevelBuildingStructureProcessor.LEVELBUILDING);
		// Barn
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier(NAMESPACE, "barn_base"), StructurePieceType.BARN);
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier(NAMESPACE, "barn_path"),
				StructurePieceType.BARN_PATH);
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier(NAMESPACE, "barn_house"),
				StructurePieceType.BARN_HOUSE);
		FabricStructureBuilder.create(barnId, StructureFeatures.BARN).step(GenerationStep.Feature.SURFACE_STRUCTURES)
				.defaultConfig(32, 2, 1404)
				.register();
		BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, barnId, ConfiguredFeatures.BARN);
		// FIXME Says Deprecated but does not say what to use instead. If anyone knows,
		// please tell me.
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.VEGETAL_DECORATION,
				RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, cornStalkId));
		BiomeModifications.addStructure(BiomeSelectors.includeByKey(BiomeKeys.CORN_FIELD),
				RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, barnId));
		OverworldBiomes.addContinentalBiome(BiomeKeys.CORN_FIELD, OverworldClimate.TEMPERATE, 1);

		if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
			onInitializeClient();
	}

	@Environment(EnvType.CLIENT)
	private void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(Blocks.CORN_STALK, RenderLayer.getCutout());
	}
}

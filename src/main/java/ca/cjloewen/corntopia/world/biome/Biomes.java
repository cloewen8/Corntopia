package ca.cjloewen.corntopia.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class Biomes {
	public static final Biome CORN_FIELD;
	
	private static Biome createCornField() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addPlainsMobs(builder);
		builder.playerSpawnFriendly();
		
		GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
		generationSettings.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(generationSettings);
		DefaultBiomeFeatures.addLandCarvers(generationSettings);
		DefaultBiomeFeatures.addDungeons(generationSettings);
		
		DefaultBiomeFeatures.addMineables(generationSettings);
	    DefaultBiomeFeatures.addDefaultOres(generationSettings);
	    
	    DefaultBiomeFeatures.addDefaultMushrooms(generationSettings);
		
	    return new Biome.Builder()
	    	.precipitation(Biome.Precipitation.RAIN)
	    	.category(Biome.Category.PLAINS)
	    	.depth(0.1F)
	    	.scale(0.01F)
	    	.temperature(0.8F)
	    	.downfall(0.4F)
	    	.effects(new BiomeEffects.Builder()
	    		.waterColor(0x3F76E4)
	    		.waterFogColor(0x50533)
	    		.fogColor(0xFFE099)
	    		.skyColor(getSkyColor(0.8F))
	    		.grassColor(0xF6DA51)
	    		.foliageColor(0xE0A152)
	    		.moodSound(BiomeMoodSound.CAVE)
	    		.build())
	    	.spawnSettings(builder.build())
	    	.generationSettings(generationSettings.build()).build();
	}
	
	private static int getSkyColor(float temperature) {
	    float f = temperature / 3.0F;
	    f = MathHelper.clamp(f, -1.0F, 1.0F);
	    return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	 }
	
	static {
		CORN_FIELD = createCornField();
	}
}

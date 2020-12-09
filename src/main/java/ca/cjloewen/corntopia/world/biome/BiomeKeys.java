package ca.cjloewen.corntopia.world.biome;

import ca.cjloewen.corntopia.CorntopiaMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class BiomeKeys {
	public static final RegistryKey<Biome> CORN_FIELD;
	
	static {
		CORN_FIELD = RegistryKey.of(Registry.BIOME_KEY, new Identifier(CorntopiaMod.NAMESPACE, "corn_field"));
	}
}

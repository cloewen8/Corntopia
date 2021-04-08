package ca.cjloewen.corntopia.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import ca.cjloewen.corntopia.structure.BarnGenerator;
import ca.cjloewen.corntopia.structure.IBarnBuilding;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class BarnFeature extends StructureFeature<DefaultFeatureConfig> {
	public BarnFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return Start::new;
	}
	
	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> feature, int chunkX, int chunkZ, BlockBox box, int references, long seed) {
			super(feature, chunkX, chunkZ, box, references, seed);
		}

		@Override
		public void init(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, StructureManager manager, int chunkX, int chunkZ, Biome biome, DefaultFeatureConfig config) {
			int x = chunkX*16;
			int z = chunkZ*16;
			BlockPos pos = new BlockPos(x, chunkGenerator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG), z);
			BlockRotation rotation = BlockRotation.random(this.random);
			BarnGenerator.addPieces(manager, pos, rotation, this.children, this.random);
			this.setBoundingBoxFromChildren();
		}
		
		@Override
		public void generateStructure(StructureWorldAccess world, StructureAccessor structureAccessor,
				ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos) {
			super.generateStructure(world, structureAccessor, chunkGenerator, random, box, chunkPos);
			// Adds a fancy base underneath all buildings.
			for (StructurePiece piece : this.children) {
				if (piece instanceof IBarnBuilding) {
					BlockBox bounds = ((IBarnBuilding)piece).getBuildingBase(world);
					for (int x = bounds.minX; x <= bounds.maxX; ++x) {
						for (int z = bounds.minZ; z <= bounds.maxZ; ++z) {
							for (int y = bounds.minY - 1; y > 1; --y) {
								BlockPos downwardPos = new BlockPos(x, y, z);
								if (!world.isAir(downwardPos) &&
									!world.getBlockState(downwardPos).getMaterial().isLiquid() &&
									world.getBlockState(downwardPos).isSolidBlock(world, downwardPos))
										break;
	
								if ((x == bounds.minX || x == bounds.maxX) && (z == bounds.minZ || z == bounds.maxZ))
									world.setBlockState(downwardPos, Blocks.OAK_LOG.getDefaultState(), 2);
								else
									world.setBlockState(downwardPos, random.nextFloat() < 0.4f ?
										Blocks.COBBLESTONE.getDefaultState() :
										Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
							}
						}
					}
				}
			}
		}
	}
}

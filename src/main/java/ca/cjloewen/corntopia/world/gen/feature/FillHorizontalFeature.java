package ca.cjloewen.corntopia.world.gen.feature;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class FillHorizontalFeature extends Feature<FillHorizontalFeatureConfig> {
	public FillHorizontalFeature(Codec<FillHorizontalFeatureConfig> codec) {
	    super(codec);
	}

	public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, FillHorizontalFeatureConfig fillHorizontalFeatureConfig) {
		boolean placedAny = false;
		int xOffset;
		int zOffset;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Optional<RegistryKey<Biome>> biomeKey;
		BlockPos groundTopBlockPos;
		BlockState groundTopBlockState;
		BlockPos groundBlockPos;
		// O(n1024)
		for (xOffset = 0; xOffset < 32; ++xOffset) {
			for (zOffset = 0; zOffset < 32; ++zOffset) {
				mutable.set(blockPos, xOffset, 0, zOffset);
				if (fillHorizontalFeatureConfig.project) {
			    	groundTopBlockPos = structureWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, mutable);
			    } else {
			    	groundTopBlockPos = mutable;
			    }
				biomeKey = structureWorldAccess.method_31081(groundTopBlockPos);
				if (fillHorizontalFeatureConfig.biomeKey == null || biomeKey.isPresent() && biomeKey.get().getValue().equals(fillHorizontalFeatureConfig.biomeKey.getValue())) {
				    groundTopBlockState = fillHorizontalFeatureConfig.stateProvider.getBlockState(random, groundTopBlockPos);
			        groundBlockPos = groundTopBlockPos.down();
			        if ((structureWorldAccess.isAir(groundTopBlockPos) ||
			        	fillHorizontalFeatureConfig.canReplace &&
			        	structureWorldAccess.getBlockState(groundTopBlockPos).getMaterial().isReplaceable()) &&
			        		groundTopBlockState.canPlaceAt(structureWorldAccess, groundTopBlockPos) &&
			        	(fillHorizontalFeatureConfig.whitelist.isEmpty() ||
			        		fillHorizontalFeatureConfig.whitelist.contains(groundTopBlockState.getBlock())) &&
			        		!fillHorizontalFeatureConfig.blacklist.contains(groundTopBlockState) &&
			        		(!fillHorizontalFeatureConfig.needsWater ||
			        			structureWorldAccess.getFluidState(groundBlockPos.west()).isIn(FluidTags.WATER) ||
			        			structureWorldAccess.getFluidState(groundBlockPos.east()).isIn(FluidTags.WATER) ||
			        			structureWorldAccess.getFluidState(groundBlockPos.north()).isIn(FluidTags.WATER) ||
			        			structureWorldAccess.getFluidState(groundBlockPos.south()).isIn(FluidTags.WATER))) {
			        	
			        	fillHorizontalFeatureConfig.blockPlacer.generate(structureWorldAccess, groundTopBlockPos, groundTopBlockState, random);
			        	placedAny = true;
			        }
		        }
			}
		}

	    return placedAny;
	}
}

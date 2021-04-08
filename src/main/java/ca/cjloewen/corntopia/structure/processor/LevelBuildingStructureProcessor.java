package ca.cjloewen.corntopia.structure.processor;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;

public class LevelBuildingStructureProcessor extends StructureProcessor {
	public static final Codec<LevelBuildingStructureProcessor> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(BlockPos.CODEC.fieldOf("offset").orElse(BlockPos.ORIGIN).forGetter((levelBuildingStructureProcessor) -> {
			return levelBuildingStructureProcessor.offset;
		}), Codec.INT.fieldOf("across").orElse(Direction.NORTH.getId()).forGetter((levelBuildingStructureProcessor) -> {
			return levelBuildingStructureProcessor.across.getId();
		}), Codec.INT.fieldOf("length").orElse(1).forGetter((levelBuildingStructureProcessor) -> {
			return levelBuildingStructureProcessor.length;
		})).apply(instance, LevelBuildingStructureProcessor::new);
	});
	public static final StructureProcessorType<LevelBuildingStructureProcessor> LEVELBUILDING = () -> {
		return CODEC;
    };
	private final BlockPos offset;
	private final Direction across;
	private final int length;

	public LevelBuildingStructureProcessor(BlockPos offset, Direction across, int length) {
		this.offset = offset;
		this.across = across;
		this.length = length;
	}
	
	public LevelBuildingStructureProcessor(BlockPos offset, int across, int length) {
		this(offset, Direction.byId(across), length);
	}
	
	public int getOffset(WorldView worldView, BlockPos pos) {
		BlockPos start = pos.offset(this.across, this.offset.getX())
				.offset(this.across.rotateYClockwise(), this.offset.getZ())
				.offset(Direction.UP, this.offset.getY());
		Heightmap.Type heightmap = worldView instanceof ServerWorld ?
			Heightmap.Type.WORLD_SURFACE :
			Heightmap.Type.WORLD_SURFACE_WG;
		int entranceHeight = worldView.getTopY(heightmap, start.getX(), start.getZ());
		BlockPos entrancePos;
		for (int acrossOffset = 1; acrossOffset < this.length; acrossOffset++) {
			entrancePos = start.offset(this.across.getOpposite(), acrossOffset);
			entranceHeight = Math.min(entranceHeight, worldView.getTopY(heightmap, entrancePos.getX(), entrancePos.getZ()));
		}
		return start.getY() - entranceHeight;
	}

	@Nullable
	public Structure.StructureBlockInfo process(WorldView worldView, BlockPos pieceCorner, BlockPos structureCenter,
			Structure.StructureBlockInfo offsetBlockInfo, Structure.StructureBlockInfo blockInfo,
			StructurePlacementData structurePlacementData) {
		return new Structure.StructureBlockInfo(
			new BlockPos(blockInfo.pos.getX(), blockInfo.pos.getY() - getOffset(worldView, pieceCorner), blockInfo.pos.getZ()),
			blockInfo.state,
			blockInfo.tag);
	}

	protected StructureProcessorType<?> getType() {
		return LEVELBUILDING;
	}
}

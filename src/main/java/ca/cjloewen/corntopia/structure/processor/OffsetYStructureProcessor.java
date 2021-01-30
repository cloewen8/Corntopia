package ca.cjloewen.corntopia.structure.processor;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class OffsetYStructureProcessor extends StructureProcessor {
	public static final Codec<OffsetYStructureProcessor> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(Codec.INT.fieldOf("offset").orElse(0).forGetter((offsetYStructureProcessor) -> {
			return offsetYStructureProcessor.offset;
		})).apply(instance, OffsetYStructureProcessor::new);
	});
	public static final StructureProcessorType<OffsetYStructureProcessor> OFFSETY = () -> {
		return CODEC;
    };
	private final int offset;

	public OffsetYStructureProcessor(int offset) {
		this.offset = offset;
	}

	@Nullable
	public Structure.StructureBlockInfo process(WorldView worldView, BlockPos pieceCorner, BlockPos structureCenter,
			Structure.StructureBlockInfo offsetBlockInfo, Structure.StructureBlockInfo blockInfo,
			StructurePlacementData structurePlacementData) {
		return new Structure.StructureBlockInfo(
			new BlockPos(blockInfo.pos.getX(), blockInfo.pos.getY() + this.offset, blockInfo.pos.getZ()),
			blockInfo.state,
			blockInfo.tag);
	}

	protected StructureProcessorType<?> getType() {
		return OFFSETY;
	}
}

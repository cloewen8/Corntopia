package ca.cjloewen.corntopia.structure.processor;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;

public class BrokenStructureProcessor extends StructureProcessor {
	public static final Codec<BrokenStructureProcessor> CODEC;
	public static final StructureProcessorType<BrokenStructureProcessor> BROKEN;
	private final BlockPos size;
	private final ImmutableList<Rule> rules;

	public BrokenStructureProcessor(BlockPos size, List<Rule> rules) {
		this.size = size;
		this.rules = ImmutableList.copyOf(rules);
	}

	@Nullable
	public Structure.StructureBlockInfo process(WorldView worldView, BlockPos pieceCorner, BlockPos structureCenter,
			Structure.StructureBlockInfo offsetBlockInfo, Structure.StructureBlockInfo blockInfo,
			StructurePlacementData structurePlacementData) {
		Random random = new Random(MathHelper.hashCode(blockInfo.pos));
		UnmodifiableIterator<Rule> ruleIter = this.rules.iterator();
		Rule rule;

		do {
			if (!ruleIter.hasNext())
				return blockInfo;

			rule = ruleIter.next();
		} while (!(blockInfo.state.isOf(rule.targetBlock) && test(
			(double)offsetBlockInfo.pos.getX()/(double)size.getX(), (double)offsetBlockInfo.pos.getY()/(double)size.getY(), (double)offsetBlockInfo.pos.getZ()/(double)size.getZ(),
			rule.probabilityOffset, random)));

		return new Structure.StructureBlockInfo(blockInfo.pos, rule.getOutput(), null);
	}
	
	private boolean test(double xPercentage, double yPercentage, double zPercentage, float probabilityOffset, Random random) {
		// Threshold that increases near the center (sine arc) and decreases near the bottom.
		return random.nextDouble() < Math.min(Math.min(Math.sin(xPercentage*Math.PI), Math.sin(zPercentage*Math.PI)), yPercentage) + probabilityOffset;
	}

	protected StructureProcessorType<?> getType() {
		return BROKEN;
	}
	
	public static class Rule {
		public static final Codec<Rule> CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(Registry.BLOCK.fieldOf("target_block").forGetter((rule) -> {
				return rule.targetBlock;
			}), Codec.FLOAT.fieldOf("probability_offset").forGetter((rule) -> {
				return rule.probabilityOffset;
			}), BlockState.CODEC.fieldOf("output_state").forGetter((rule) -> {
				return rule.outputState;
			})).apply(instance, Rule::new);
		});
		private final Block targetBlock;
		private final float probabilityOffset;
		private final BlockState outputState;
		
		public Rule(Block targetBlock, float probability, BlockState outputState) {
			this.targetBlock = targetBlock;
			this.probabilityOffset = probability;
			this.outputState = outputState;
		}
		
		public Block getTarget() {
			return targetBlock;
		}
		
		public float getOffset() {
			return probabilityOffset;
		}
		
		public BlockState getOutput() {
			return outputState;
		}
	}

	static {
		CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(BlockPos.CODEC.fieldOf("size").forGetter((brokenStructureProcessor) -> {
				return brokenStructureProcessor.size;
			}), Rule.CODEC.listOf().fieldOf("rules").forGetter((brokenStructureProcessor) -> {
				return brokenStructureProcessor.rules;
			})).apply(instance, BrokenStructureProcessor::new);
		});
		BROKEN = () -> {
			return CODEC;
	    };
	}
}

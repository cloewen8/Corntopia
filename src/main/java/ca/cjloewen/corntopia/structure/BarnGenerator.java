package ca.cjloewen.corntopia.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import ca.cjloewen.corntopia.CorntopiaMod;
import ca.cjloewen.corntopia.structure.processor.BrokenStructureProcessor;
import ca.cjloewen.corntopia.structure.processor.OffsetYStructureProcessor;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;

public class BarnGenerator {
	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random) {
		List<StructurePiece> extra = new ArrayList<StructurePiece>();
		if (random.nextBoolean())
			extra.add(new Piece(manager, "pile_front", pos.add(new BlockPos(8, 1, 2).rotate(rotation)), rotation, false, false));
		if (random.nextBoolean())
			extra.add(new Piece(manager, "pile_front", pos.add(new BlockPos(3, 1, 2).rotate(rotation)), rotation, true, false));
		if (random.nextBoolean())
			extra.add(new Piece(manager, "pile_back", pos.add(new BlockPos(7, 1, 10).rotate(rotation)), rotation, false, false));
		if (random.nextBoolean())
			extra.add(new Piece(manager, "pile_back", pos.add(new BlockPos(4, 1, 10).rotate(rotation)), rotation, true, false));
		if (random.nextBoolean()) {
			extra.add(new Piece(manager, "platform", pos.add(new BlockPos(6, 0, 2).rotate(rotation)), rotation, false, false));
			if (random.nextBoolean())
				extra.add(new Piece(manager, "lamp_tall", pos.add(new BlockPos(8, 3, 8).rotate(rotation)), rotation, false, false));
			else if (random.nextBoolean())
				extra.add(new Piece(manager, "lamp_short", pos.add(new BlockPos(8, 3, 8).rotate(rotation)), rotation, false, false));
		}
		if (random.nextBoolean()) {
			extra.add(new Piece(manager, "platform", pos.add(new BlockPos(5, 0, 2).rotate(rotation)), rotation, true, false));
			if (random.nextBoolean())
				extra.add(new Piece(manager, "lamp_tall", pos.add(new BlockPos(3, 3, 8).rotate(rotation)), rotation, true, false));
			else if (random.nextBoolean())
				extra.add(new Piece(manager, "lamp_short", pos.add(new BlockPos(3, 3, 8).rotate(rotation)), rotation, true, false));
		}
		pieces.add(new Piece(manager, "base", pos, rotation, false, extra.size() == 0));
		pieces.addAll(extra);
	}
	
	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final BlockRotation rotation;
		private final boolean flip;
		private boolean broken;

		public Piece(StructureManager manager, String template, BlockPos pos, BlockRotation rotation, boolean flip, boolean broken) {
			super(StructurePieceType.BARN, 0);
			this.template = template;
			this.pos = pos;
			this.rotation = rotation;
			this.flip = flip;
			this.broken = broken;
			this.initializeStructureData(manager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.BARN, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.flip = compoundTag.getBoolean("Flip");
			this.broken = compoundTag.getBoolean("Broken");
			this.initializeStructureData(structureManager);
		}

		private void initializeStructureData(StructureManager structureManager) {
			Structure structure = structureManager
				.getStructureOrBlank(new Identifier(CorntopiaMod.NAMESPACE, "barn/" + this.template));
			StructurePlacementData placementData = new StructurePlacementData()
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS)
				.addProcessor(new OffsetYStructureProcessor(-1))
				.setRotation(this.rotation)
				.setMirror(this.flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE)
				.setMirror(this.flip ? BlockMirror.FRONT_BACK : BlockMirror.NONE);
			if (this.broken) {
				float probability = 0.2F;
				placementData.addProcessor(new BrokenStructureProcessor(structure.getSize(), ImmutableList.of(
					new BrokenStructureProcessor.Rule(Blocks.OAK_PLANKS, probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(Blocks.OAK_LOG, probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(Blocks.CRIMSON_SLAB, probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(Blocks.CRIMSON_STAIRS, probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(Blocks.GRASS_PATH, 0.33F, Blocks.GRASS_BLOCK.getDefaultState()),
					new BrokenStructureProcessor.Rule(Blocks.GRASS_PATH, 0.66F, Blocks.COARSE_DIRT.getDefaultState()))));
			}
			this.setStructureData(structure, this.pos, placementData);
		}
		
		protected void toNbt(CompoundTag tag) {
			   super.toNbt(tag);
			   tag.putString("Template", this.template);
			   tag.putString("Rot", this.rotation.name());
			   tag.putBoolean("Flip", this.flip);
			   tag.putBoolean("Broken", this.broken);
			}
		
		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random, BlockBox boundingBox) { }
	}
}

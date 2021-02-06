package ca.cjloewen.corntopia.structure;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import ca.cjloewen.corntopia.BuildingBlocks;
import ca.cjloewen.corntopia.CorntopiaMod;
import ca.cjloewen.corntopia.loot.LootTables;
import ca.cjloewen.corntopia.structure.processor.BrokenStructureProcessor;
import ca.cjloewen.corntopia.structure.processor.OffsetYStructureProcessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HayBlock;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;

// TODO Add Barn Planks and replace the barn walls with it.
// TODO Add Barn Roof and replace the barn roof with it.
public class BarnGenerator {
	enum BarnType {
		USED, ABANDONED, BROKEN
	}
	
	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random) {
		BarnType barnType;
		boolean[] toggles = new boolean[10];
		BlockState pileState = Blocks.HAY_BLOCK.getDefaultState().with(HayBlock.AXIS, Direction.Axis.Y);
		for (int i = 0; i < toggles.length; i++) {
			toggles[i] = random.nextBoolean();
		}
		// Left or right platform
		if (toggles[4] || toggles[7])
			barnType = BarnType.USED;
		// Everything (except lamps, which are not included anyway if platforms aren't).
		else if (toggles[0] || toggles[1] || toggles[2] || toggles[3] || toggles[4] || toggles[7])
			barnType = BarnType.ABANDONED;
		else
		// Nothing
			barnType = BarnType.BROKEN;
		
		pieces.add(new Piece(manager, "base", pos, rotation, false, BuildingBlocks.PLANKS_BLOCKS.get("barn").getDefaultState(), barnType));
		if (toggles[0])
			pieces.add(new Piece(manager, "pile_front", pos.add(new BlockPos(8, 1, 2).rotate(rotation)), rotation, false, pileState, barnType));
		if (toggles[1])
			pieces.add(new Piece(manager, "pile_front", pos.add(new BlockPos(3, 1, 2).rotate(rotation)), rotation, true, pileState, barnType));
		if (toggles[2])
			pieces.add(new Piece(manager, "pile_back", pos.add(new BlockPos(7, 1, 10).rotate(rotation)), rotation, false, pileState, barnType));
		if (toggles[3])
			pieces.add(new Piece(manager, "pile_back", pos.add(new BlockPos(4, 1, 10).rotate(rotation)), rotation, true, pileState, barnType));
		if (toggles[4]) {
			pieces.add(new Piece(manager, "platform", pos.add(new BlockPos(6, 0, 2).rotate(rotation)), rotation, false, BuildingBlocks.PLANKS_BLOCKS.get("barn").getDefaultState(), barnType));
			if (toggles[5])
				pieces.add(new Piece(manager, "lamp_tall", pos.add(new BlockPos(8, 3, 8).rotate(rotation)), rotation, false, Blocks.AIR.getDefaultState(), barnType));
			else if (toggles[6])
				pieces.add(new Piece(manager, "lamp_short", pos.add(new BlockPos(8, 3, 8).rotate(rotation)), rotation, false, Blocks.AIR.getDefaultState(), barnType));
		}
		if (toggles[7]) {
			pieces.add(new Piece(manager, "platform", pos.add(new BlockPos(5, 0, 2).rotate(rotation)), rotation, true, BuildingBlocks.PLANKS_BLOCKS.get("barn").getDefaultState(), barnType));
			if (toggles[8])
				pieces.add(new Piece(manager, "lamp_tall", pos.add(new BlockPos(3, 3, 8).rotate(rotation)), rotation, true, Blocks.AIR.getDefaultState(), barnType));
			else if (toggles[9])
				pieces.add(new Piece(manager, "lamp_short", pos.add(new BlockPos(3, 3, 8).rotate(rotation)), rotation, true, Blocks.AIR.getDefaultState(), barnType));
		}
	}
	
	public static class Piece extends SimpleStructurePiece {
		private final String template;
		private final BlockRotation rotation;
		private final boolean flip;
		private final BlockState chestReplacement;
		private final BarnType barnType;

		public Piece(StructureManager structureManager, String template, BlockPos pos, BlockRotation rotation, boolean flip, BlockState chestReplacement, BarnType barnType) {
			super(StructurePieceType.BARN, 0);
			this.template = template;
			this.pos = pos;
			this.rotation = rotation;
			this.flip = flip;
			this.chestReplacement = chestReplacement;
			this.barnType = barnType;
			this.initializeStructureData(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.BARN, compoundTag);
			this.template = compoundTag.getString("Template");
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.flip = compoundTag.getBoolean("Flip");
			this.chestReplacement = NbtHelper.toBlockState(compoundTag.getCompound("ChestReplacement"));
			this.barnType = BarnType.valueOf(compoundTag.getString("BarnType"));
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
			if (this.barnType.equals(BarnType.BROKEN)) {
				float probability = 0.2F;
				placementData.addProcessor(new BrokenStructureProcessor(structure.getSize(), ImmutableList.of(
					new BrokenStructureProcessor.Rule(BuildingBlocks.PLANKS_BLOCKS.get("barn"), probability, Blocks.AIR.getDefaultState()),
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
		   tag.put("ChestReplacement", NbtHelper.fromBlockState(this.chestReplacement));
		   tag.putString("BarnType", this.barnType.name());
		}
		
		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random, BlockBox boundingBox) {
			BlockPos chestPos;
			switch(metadata) {
				case "chest":
					chestPos = pos.down(2);
					break;
				case "chest2":
					chestPos = pos.down(3);
					break;
				default:
					chestPos = null;
			}
			if (chestPos != null &&  boundingBox.contains(chestPos)) {
				if (random.nextFloat() < 0.9F)
					serverWorldAccess.setBlockState(chestPos, this.chestReplacement, 3);
				else if (this.barnType.equals(BarnType.ABANDONED))
					LootableContainerBlockEntity.setLootTable(serverWorldAccess, random, chestPos, LootTables.BARN_ABANDONED);
				else if (this.barnType.equals(BarnType.USED))
					LootableContainerBlockEntity.setLootTable(serverWorldAccess, random, chestPos, LootTables.BARN_USED);
			}
		}
	}
}

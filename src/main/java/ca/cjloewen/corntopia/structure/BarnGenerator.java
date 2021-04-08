package ca.cjloewen.corntopia.structure;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import ca.cjloewen.corntopia.BuildingBlocks;
import ca.cjloewen.corntopia.CorntopiaMod;
import ca.cjloewen.corntopia.loot.LootTables;
import ca.cjloewen.corntopia.structure.processor.BrokenStructureProcessor;
import ca.cjloewen.corntopia.structure.processor.LevelBuildingStructureProcessor;
import ca.cjloewen.corntopia.structure.processor.OffsetYStructureProcessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HayBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BarnGenerator {
	public enum BarnType {
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
		// Everything (except lamps, which are not included anyway if platforms aren't)
		else if (toggles[0] || toggles[1] || toggles[2] || toggles[3] || toggles[4] || toggles[7])
			barnType = BarnType.ABANDONED;
		else
		// Nothing
			barnType = BarnType.BROKEN;
		
		pieces.add(new BarnBasePiece(manager, pos, rotation, BuildingBlocks.BLOCKS.get("barn").getDefaultState(), barnType));
		if (toggles[0])
			pieces.add(new BarnPiece(manager, "pile_front", pos.add(new BlockPos(8, 1, 2).rotate(rotation)), rotation, false, pileState, barnType));
		if (toggles[1])
			pieces.add(new BarnPiece(manager, "pile_front", pos.add(new BlockPos(3, 1, 2).rotate(rotation)), rotation, true, pileState, barnType));
		if (toggles[2])
			pieces.add(new BarnPiece(manager, "pile_back", pos.add(new BlockPos(7, 1, 10).rotate(rotation)), rotation, false, pileState, barnType));
		if (toggles[3])
			pieces.add(new BarnPiece(manager, "pile_back", pos.add(new BlockPos(4, 1, 10).rotate(rotation)), rotation, true, pileState, barnType));
		if (toggles[4]) {
			pieces.add(new BarnPiece(manager, "platform", pos.add(new BlockPos(6, 0, 2).rotate(rotation)), rotation, false, BuildingBlocks.BLOCKS.get("barn").getDefaultState(), barnType));
			if (toggles[5])
				pieces.add(new BarnPiece(manager, "lamp_tall", pos.add(new BlockPos(8, 3, 8).rotate(rotation)), rotation, false, Blocks.AIR.getDefaultState(), barnType));
			else if (toggles[6])
				pieces.add(new BarnPiece(manager, "lamp_short", pos.add(new BlockPos(8, 3, 8).rotate(rotation)), rotation, false, Blocks.AIR.getDefaultState(), barnType));
		}
		if (toggles[7]) {
			pieces.add(new BarnPiece(manager, "platform", pos.add(new BlockPos(5, 0, 2).rotate(rotation)), rotation, true, BuildingBlocks.BLOCKS.get("barn").getDefaultState(), barnType));
			if (toggles[8])
				pieces.add(new BarnPiece(manager, "lamp_tall", pos.add(new BlockPos(3, 3, 8).rotate(rotation)), rotation, true, Blocks.AIR.getDefaultState(), barnType));
			else if (toggles[9])
				pieces.add(new BarnPiece(manager, "lamp_short", pos.add(new BlockPos(3, 3, 8).rotate(rotation)), rotation, true, Blocks.AIR.getDefaultState(), barnType));
		}
		
		if (barnType.equals(BarnType.USED)) {
			// Place a path
			BlockRotation houseRot = getHouseRotation(rotation, random);
			pieces.add(new CenterPiece(manager, pos.add(new BlockPos(4, 0, 0).rotate(rotation)), rotation, houseRot));
			// Calculate the barn house position
			BlockPos housePos;
			Direction barnDir = rotation.rotate(Direction.NORTH);
			Direction barnAcross = barnDir.rotateYClockwise();
			Direction houseDir = houseRot.rotate(Direction.NORTH);
			Direction houseAcrossDir = houseDir.rotateYCounterclockwise();
			int barnAcrossI = CenterPiece.ENTRANCE_WIDTH/2 + 4;
			// Fixes parallel offset (same calculation the CenterPiece does)
			if (barnAcross.getDirection().equals(Direction.AxisDirection.NEGATIVE))
				barnAcrossI--;
			// Find the path center
			housePos = pos.offset(barnDir, CenterPiece.LENGTH).offset(barnAcross, barnAcrossI);
			// Find the path end
			housePos = housePos.offset(houseDir.getOpposite(), CenterPiece.LENGTH - 1);
			// Horizontally offset the house
			housePos = housePos.offset(houseAcrossDir, 4);
			if (houseDir.equals(Direction.NORTH) || houseDir.equals(Direction.EAST))
				housePos = housePos.offset(houseAcrossDir, 1);
			// Place the barn house
			pieces.add(new HousePiece(manager, housePos, houseRot));
		}
	}
	
	private static BlockRotation getHouseRotation(BlockRotation barnRotation, Random random) {
		EnumSet<BlockRotation> houseRots = EnumSet.allOf(BlockRotation.class);
		houseRots.remove(barnRotation);
		int houseI = random.nextInt(houseRots.size());
		int i = 0;
		for (BlockRotation houseRot : houseRots) {
			if (i == houseI) {
				return houseRot;
			}
			i++;
		}
		// Let's hope this never happens.
		throw new IllegalStateException("The laws of mathematics failed!");
	}
	
	public static class BarnBasePiece extends BarnPiece implements IBarnBuilding {
		public BarnBasePiece(StructureManager structureManager, BlockPos pos, BlockRotation rotation, BlockState chestReplacement, BarnType barnType) {
			super(structureManager, "base", pos, rotation, false, chestReplacement, barnType);
		}

		public BarnBasePiece(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag);
		}

		@Override
		public BlockBox getBuildingBase(WorldView world) {
			BlockBox bounds = new BlockBox(this.boundingBox.minX + 1, this.boundingBox.minY - 1, this.boundingBox.minZ + 1,
				this.boundingBox.maxX - 1, this.boundingBox.maxY - 1, this.boundingBox.maxZ - 1);
			bounds.move(0, -levelProcessor.getOffset(world, this.pos), 0);
			return bounds;
		}
	}
	
	public static class BarnPiece extends SimpleStructurePiece {
		private final String template;
		private final BlockRotation rotation;
		private final boolean flip;
		private final BlockState chestReplacement;
		private final BarnType barnType;
		protected LevelBuildingStructureProcessor levelProcessor;

		public BarnPiece(StructureManager structureManager, String template, BlockPos pos, BlockRotation rotation, boolean flip, BlockState chestReplacement, BarnType barnType) {
			super(StructurePieceType.BARN, 0);
			this.template = template;
			this.pos = pos;
			this.rotation = rotation;
			this.flip = flip;
			this.chestReplacement = chestReplacement;
			this.barnType = barnType;
			this.initializeStructureData(structureManager);
		}

		public BarnPiece(StructureManager structureManager, CompoundTag compoundTag) {
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
			levelProcessor = new LevelBuildingStructureProcessor(new BlockPos(4, 0, 1), this.rotation.rotate(Direction.NORTH).rotateYClockwise(), 4);
			StructurePlacementData placementData = new StructurePlacementData()
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS)
				.addProcessor(new OffsetYStructureProcessor(-1))
				.setRotation(this.rotation)
				.setMirror(this.flip ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE)
				.setMirror(this.flip ? BlockMirror.FRONT_BACK : BlockMirror.NONE);
			if (this.barnType.equals(BarnType.BROKEN)) {
				float probability = 0.2F;
				placementData.addProcessor(new BrokenStructureProcessor(structure.getSize(), ImmutableList.of(
					new BrokenStructureProcessor.Rule(BuildingBlocks.BLOCKS.get("barn"), probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(Blocks.OAK_LOG, probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(BuildingBlocks.BLOCKS.get("barn_roof"), probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(BuildingBlocks.SLAB_BLOCKS.get("barn_roof"), probability, Blocks.AIR.getDefaultState()),
					new BrokenStructureProcessor.Rule(BuildingBlocks.STAIRS_BLOCKS.get("barn_roof"), probability, Blocks.AIR.getDefaultState()),
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
	
	public static class CenterPiece extends StructurePiece {
		public static final int CENTER_DIAMETER = 12;
		public static final int ENTRANCE_WIDTH = 4;
		public static final int LENGTH = 16;
		public static final int WIDTH = 4;
		public static final int MARGIN = 5;
		
		private final BlockPos pos;
		private final Direction barnDir;
		private final Direction houseDir;
		
		public CenterPiece(StructureManager structureManager, BlockPos pos, BlockRotation rotation, BlockRotation houseRotation) {
			super(StructurePieceType.BARN_PATH, 0);
			this.pos = pos;
			this.barnDir = rotation.rotate(Direction.NORTH);
			this.houseDir = houseRotation.rotate(Direction.NORTH);
			calculateBoundingBox();
		}

		public CenterPiece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.BARN_PATH, compoundTag);
			this.pos = new BlockPos(compoundTag.getInt("TPX"), compoundTag.getInt("TPY"), compoundTag.getInt("TPZ"));
			this.barnDir = Direction.byId(compoundTag.getInt("Direction"));
			this.houseDir = Direction.byId(compoundTag.getInt("HouseDirection"));
			calculateBoundingBox();
		}
		
		private void calculateBoundingBox() {
			int diameter = LENGTH*2;
			int acrossOffset = ENTRANCE_WIDTH/2 - diameter/2;
			// Horizontal
			BlockBox blockBox = new BlockBox(0, 0, 0, diameter, 1, diameter);
			blockBox.move(this.pos.getX(), this.pos.getY(), this.pos.getZ());
			blockBox.move(this.barnDir.rotateYClockwise().getOffsetX()*acrossOffset, 0, this.barnDir.rotateYClockwise().getOffsetZ()*acrossOffset);
			// Vertical
			blockBox.minY--;
			blockBox.maxY = blockBox.minY;
			if (this.barnDir.getAxis().equals(Direction.Axis.X)) {
				if (this.barnDir.getDirection().equals(Direction.AxisDirection.NEGATIVE))
					blockBox.move(-blockBox.getBlockCountX() + 1, 0, -blockBox.getBlockCountZ() + 1);
			} else { // Z
				if (this.barnDir.getDirection().equals(Direction.AxisDirection.NEGATIVE))
					blockBox.move(0, 0, -blockBox.getBlockCountZ() + 1);
				else
					blockBox.move(-blockBox.getBlockCountX() + 1, 0, 0);
			}
			this.boundingBox = blockBox;
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			tag.putInt("TPX", this.pos.getX());
		    tag.putInt("TPY", this.pos.getY());
		    tag.putInt("TPZ", this.pos.getZ());
		    tag.putInt("Direction", this.barnDir.getId());
		    tag.putInt("HouseDirection", this.houseDir.getId());
		}

		@Override
		public boolean generate(StructureWorldAccess structureWorldAccess, StructureAccessor structureAccessor,
				ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos,
				BlockPos blockPos) {
			Direction across = this.barnDir.rotateYClockwise();
			int acrossI = ENTRANCE_WIDTH/2;
			// Fixes parallel offset.
			if (across.getDirection().equals(Direction.AxisDirection.NEGATIVE))
				acrossI--;
			BlockPos middle = this.pos.offset(this.barnDir, LENGTH).offset(across, acrossI);
			// Generate the path.
			generateCenter(structureWorldAccess, boundingBox, random, middle);
			generatePath(structureWorldAccess, boundingBox, random, middle, this.barnDir.getOpposite());
			generatePath(structureWorldAccess, boundingBox, random, middle, this.houseDir.getOpposite());
			return true;
		}
		
		private void generateBlock(StructureWorldAccess structureWorldAccess, BlockBox boundingBox, BlockPos pos) {
			int x = pos.getX();
			int z = pos.getZ();
			int y = structureWorldAccess.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z) - 1;
			this.addBlock(structureWorldAccess, net.minecraft.block.Blocks.GRASS_PATH.getDefaultState(),
				x, y, z, boundingBox);
			this.addBlock(structureWorldAccess, net.minecraft.block.Blocks.AIR.getDefaultState(),
				x, y + 1, z, boundingBox);
			this.addBlock(structureWorldAccess, net.minecraft.block.Blocks.AIR.getDefaultState(),
				x, y + 2, z, boundingBox);
		}
		
		private void generateCenter(StructureWorldAccess structureWorldAccess, BlockBox boundingBox, Random random, BlockPos middle) {
			int centerRadius = Math.floorDiv(CENTER_DIAMETER, 2);
			float centerOffsetX;
			float centerOffsetZ;
			float magnitudeXZ;
			float chance;
			for (float x = -centerRadius - MARGIN; x < centerRadius + MARGIN; x++) {
				for (float z = -centerRadius - MARGIN; z < centerRadius + MARGIN; z++) {
					centerOffsetX = x + 0.5f;
					centerOffsetZ = z + 0.5f;
					magnitudeXZ = (float)Math.sqrt(centerOffsetX*centerOffsetX + centerOffsetZ*centerOffsetZ);
					// Falloff
					chance = 1f - Math.min((magnitudeXZ - centerRadius)/(float)MARGIN, 1f);
					if (random.nextFloat() <= chance)
						generateBlock(structureWorldAccess, boundingBox, middle.add(x, 0, z));
				}
			}
		}
		
		private void generatePath(StructureWorldAccess structureWorldAccess, BlockBox boundingBox, Random random, BlockPos middle, Direction way) {
			Direction wayAcross = way.rotateYClockwise();
			int halfWidth = WIDTH/2;
			int halfRadius = halfWidth + MARGIN;
			float chance;
			// Keep paths parallel.
			if (wayAcross.getDirection().equals(Direction.AxisDirection.POSITIVE))
				wayAcross = wayAcross.getOpposite();
			for (int across = -halfRadius + 1; across <= halfRadius; across++) {
				// Falloff
				chance = 1f - (Math.abs(across > 0 ? across : across - 1) - halfWidth)/(float)MARGIN;
				// Path
				if (across > -halfWidth && across <= halfWidth)
					chance = 1f;
				for (int toward = 1; toward <= LENGTH; toward++) {
					if (random.nextFloat() <= chance)
						generateBlock(structureWorldAccess, boundingBox, middle.offset(wayAcross, across).offset(way, toward));
				}
			}
		}
	}
	
	public static class HousePiece extends SimpleStructurePiece implements IBarnBuilding {
		private final BlockRotation rotation;
		private LevelBuildingStructureProcessor levelProcessor;

		public HousePiece(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
			super(StructurePieceType.BARN_HOUSE, 0);
			this.pos = pos;
			this.rotation = rotation;
			this.initializeStructureData(structureManager);
		}

		public HousePiece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.BARN_HOUSE, compoundTag);
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.initializeStructureData(structureManager);
		}

		private void initializeStructureData(StructureManager structureManager) {
			Structure structure = structureManager
				.getStructureOrBlank(new Identifier(CorntopiaMod.NAMESPACE, "barn/house"));
			levelProcessor = new LevelBuildingStructureProcessor(new BlockPos(4, 0, 0), this.rotation.rotate(Direction.NORTH).rotateYClockwise(), 4);
			StructurePlacementData placementData = new StructurePlacementData()
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS)
				.addProcessor(levelProcessor)
				.addProcessor(new RuleStructureProcessor(ImmutableList.of(
					new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.4F),
						AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))
				.setRotation(this.rotation);
			this.setStructureData(structure, this.pos, placementData);
		}
		
		@Override
		public BlockBox getBuildingBase(WorldView world) {
			BlockBox bounds = this.getBoundingBox();
			switch (this.rotation) {
			case NONE:
				bounds = new BlockBox(bounds.minX + 1, bounds.minY, bounds.minZ + 2,
					bounds.maxX - 1, bounds.maxY, bounds.maxZ - 1);
				break;
			case CLOCKWISE_90:
				bounds = new BlockBox(bounds.minX + 1, bounds.minY, bounds.minZ + 1,
					bounds.maxX - 2, bounds.maxY, bounds.maxZ - 1);
				break;
			case CLOCKWISE_180:
				bounds = new BlockBox(bounds.minX + 1, bounds.minY, bounds.minZ + 1,
					bounds.maxX - 1, bounds.maxY, bounds.maxZ - 2);
				break;
			case COUNTERCLOCKWISE_90:
				bounds = new BlockBox(bounds.minX + 2, bounds.minY, bounds.minZ + 1,
					bounds.maxX - 1, bounds.maxY, bounds.maxZ - 1);
				break;
			}
			bounds.move(0, -levelProcessor.getOffset(world, this.pos), 0);
			return bounds;
		}
		
		protected void toNbt(CompoundTag tag) {
		   super.toNbt(tag);
		   tag.putString("Rot", this.rotation.name());
		}
		
		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random, BlockBox boundingBox) {
			pos = pos.down(this.levelProcessor.getOffset(serverWorldAccess, this.pos));
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
			if (chestPos != null) {
				LootableContainerBlockEntity.setLootTable(serverWorldAccess, random, chestPos, LootTables.BARN_ABANDONED);
			} else if ("villager".equals(metadata)) {
				VillagerEntity villagerEntity = EntityType.VILLAGER.create(serverWorldAccess.toServerWorld());
				villagerEntity.setPersistent();
				villagerEntity.refreshPositionAndAngles((double)pos.getX() + 0.5d, (double)pos.getY() + 1d, (double)pos.getZ() + 0.5d, 0.0f, 0.0f);
				villagerEntity.initialize(serverWorldAccess,
					serverWorldAccess.getLocalDifficulty(pos), SpawnReason.STRUCTURE,
					(EntityData) null, (CompoundTag) null);
				serverWorldAccess.setBlockState(pos, net.minecraft.block.Blocks.OAK_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP), 2);
				serverWorldAccess.spawnEntityAndPassengers(villagerEntity);
			}
			
		}
	}
}

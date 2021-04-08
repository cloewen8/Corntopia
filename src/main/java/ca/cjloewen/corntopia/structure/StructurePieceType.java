package ca.cjloewen.corntopia.structure;

public class StructurePieceType {
	public static final net.minecraft.structure.StructurePieceType BARN = BarnGenerator.BarnPiece::new;
	public static final net.minecraft.structure.StructurePieceType BARN_PATH = BarnGenerator.CenterPiece::new;
	public static final net.minecraft.structure.StructurePieceType BARN_HOUSE = BarnGenerator.HousePiece::new;
}

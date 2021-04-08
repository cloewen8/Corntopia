package ca.cjloewen.corntopia.structure;

import net.minecraft.util.math.BlockBox;
import net.minecraft.world.WorldView;

public interface IBarnBuilding {
	public BlockBox getBuildingBase(WorldView worldView);
}

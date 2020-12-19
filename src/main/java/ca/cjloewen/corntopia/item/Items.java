package ca.cjloewen.corntopia.item;

import ca.cjloewen.corntopia.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.TallBlockItem;

public class Items {
	public static final Item CORN;
	public static final Item POPCORN;
	public static final Item KERNELS;
	
	static {
		CORN = new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.CORN_FOOD));
		POPCORN = new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.POPCORN));
		KERNELS = new TallBlockItem(Blocks.CORN_STALK, new Item.Settings().group(ItemGroup.DECORATIONS));
	}
}

package ca.cjloewen.corntopia.item;

import net.minecraft.item.FoodComponent;

public class FoodComponents {
	public static final FoodComponent CORN_FOOD;
	
	static {
		CORN_FOOD = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
	}
}

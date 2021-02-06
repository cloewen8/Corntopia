package ca.cjloewen.corntopia;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.cjloewen.corntopia.mixin.SignTypeMixin;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodenButtonBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SignItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

/**
 * Building block utilities.
 */
public class BuildingBlocks {
	public enum ExcludeMask {
		PLANKS(        0b00000001),
		SLAB(          0b00000010),
		STAIRS(        0b00000100),
		PRESSURE_PLATE(0b00001000),
		SIGN(          0b00010000),
		TRAPDOOR(      0b00100000),
		BUTTON(        0b01000000);
		public final long mask;
		
		private ExcludeMask(long mask) {
			this.mask = mask;
		}
		
		public boolean contains(long mask) {
			return (mask&this.mask) != 0;
		}
	}
	
	private static final Logger LOGGER = LogManager.getLogger("Corntopia");
	// Blocks
	public static final Map<String, Block> PLANKS_BLOCKS = new HashMap<>();
	public static final Map<String, Block> STAIRS_BLOCKS = new HashMap<>();
	public static final Map<String, Block> SIGN_BLOCKS = new HashMap<>();
	public static final Map<String, Block> WALL_SIGN_BLOCKS = new HashMap<>();
	public static final Map<String, Block> PRESSURE_PLATE_BLOCKS = new HashMap<>();
	public static final Map<String, Block> TRAPDOOR_BLOCKS = new HashMap<>();
	public static final Map<String, Block> BUTTON_BLOCKS = new HashMap<>();
	public static final Map<String, Block> SLAB_BLOCKS = new HashMap<>();
	// Items
	public static final Map<String, Item> PLANKS_ITEMS = new HashMap<>();
	public static final Map<String, Item> STAIRS_ITEMS = new HashMap<>();
	public static final Map<String, Item> SIGN_ITEMS = new HashMap<>();
	public static final Map<String, Item> PRESSURE_PLATE_ITEMS = new HashMap<>();
	public static final Map<String, Item> TRAPDOOR_ITEMS = new HashMap<>();
	public static final Map<String, Item> BUTTON_ITEMS = new HashMap<>();
	public static final Map<String, Item> SLAB_ITEMS = new HashMap<>();
	
	/**
	 * Registers building blocks, items and related types.
	 * 
	 * @param name The prefix for identifiers.
	 * @param exclude A bitmask of blocks to exclude (see {@code ExcludeMask}).
	 * @implNote Only a name can be provided currently. Once a basic implementation is done, I'll add additional parameters to {@code register}.
	 * @implNote Resources are not created automatically. If this method is used, create the following (where * is {@code name}):<br />
	 * <b>Planks</b><br />
	 * - assets.corntopia.blockstates.*_planks.json
	 * <pre><code>
{
	"variants": {
		"": {
			"model": "corntopia:block/*_planks"
		}
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_planks.json
	 * <pre><code>
{
	"parent": "minecraft:block/cube_all",
	"textures": {
		"all": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.item.*_planks.json
	 * <pre><code>
{
	"parent": "corntopia:block/*_planks"
}
	 * </code></pre>
	 * <b>Stairs</b><br />
	 * - assets.corntopia.blockstates.*_stairs.json
	 * <pre><code>
{
	"variants": {
		"facing=east,half=bottom,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner",
			"y": 270,
			"uvlock": true
		},
		"facing=east,half=bottom,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner"
		},
		"facing=east,half=bottom,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer",
			"y": 270,
			"uvlock": true
		},
		"facing=east,half=bottom,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer"
		},
		"facing=east,half=bottom,shape=straight": {
			"model": "corntopia:block/*_stairs"
		},
		"facing=east,half=top,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"uvlock": true
		},
		"facing=east,half=top,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"y": 90,
			"uvlock": true
		},
		"facing=east,half=top,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"uvlock": true
		},
		"facing=east,half=top,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"y": 90,
			"uvlock": true
		},
		"facing=east,half=top,shape=straight": {
			"model": "corntopia:block/*_stairs",
			"x": 180,
			"uvlock": true
		},
		"facing=north,half=bottom,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner",
			"y": 180,
			"uvlock": true
		},
		"facing=north,half=bottom,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner",
			"y": 270,
			"uvlock": true
		},
		"facing=north,half=bottom,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer",
			"y": 180,
			"uvlock": true
		},
		"facing=north,half=bottom,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer",
			"y": 270,
			"uvlock": true
		},
		"facing=north,half=bottom,shape=straight": {
			"model": "corntopia:block/*_stairs",
			"y": 270,
			"uvlock": true
		},
		"facing=north,half=top,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"y": 270,
			"uvlock": true
		},
		"facing=north,half=top,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"uvlock": true
		},
		"facing=north,half=top,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"y": 270,
			"uvlock": true
		},
		"facing=north,half=top,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"uvlock": true
		},
		"facing=north,half=top,shape=straight": {
			"model": "corntopia:block/*_stairs",
			"x": 180,
			"y": 270,
			"uvlock": true
		},
		"facing=south,half=bottom,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner"
		},
		"facing=south,half=bottom,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner",
			"y": 90,
			"uvlock": true
		},
		"facing=south,half=bottom,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer"
		},
		"facing=south,half=bottom,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer",
			"y": 90,
			"uvlock": true
		},
		"facing=south,half=bottom,shape=straight": {
			"model": "corntopia:block/*_stairs",
			"y": 90,
			"uvlock": true
		},
		"facing=south,half=top,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"y": 90,
			"uvlock": true
		},
		"facing=south,half=top,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"y": 180,
			"uvlock": true
		},
		"facing=south,half=top,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"y": 90,
			"uvlock": true
		},
		"facing=south,half=top,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"y": 180,
			"uvlock": true
		},
		"facing=south,half=top,shape=straight": {
			"model": "corntopia:block/*_stairs",
			"x": 180,
			"y": 90,
			"uvlock": true
		},
		"facing=west,half=bottom,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner",
			"y": 90,
			"uvlock": true
		},
		"facing=west,half=bottom,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner",
			"y": 180,
			"uvlock": true
		},
		"facing=west,half=bottom,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer",
			"y": 90,
			"uvlock": true
		},
		"facing=west,half=bottom,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer",
			"y": 180,
			"uvlock": true
		},
		"facing=west,half=bottom,shape=straight": {
			"model": "corntopia:block/*_stairs",
			"y": 180,
			"uvlock": true
		},
		"facing=west,half=top,shape=inner_left": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"y": 180,
			"uvlock": true
		},
		"facing=west,half=top,shape=inner_right": {
			"model": "corntopia:block/*_stairs_inner",
			"x": 180,
			"y": 270,
			"uvlock": true
		},
		"facing=west,half=top,shape=outer_left": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"y": 180,
			"uvlock": true
		},
		"facing=west,half=top,shape=outer_right": {
			"model": "corntopia:block/*_stairs_outer",
			"x": 180,
			"y": 270,
			"uvlock": true
		},
		"facing=west,half=top,shape=straight": {
			"model": "corntopia:block/*_stairs",
			"x": 180,
			"y": 180,
			"uvlock": true
		}
	}
}
	 * </code></pre>
	 * - assets.models.block.*_stairs.json
	 * <pre><code>
{
	"parent": "minecraft:block/stairs",
	"textures": {
		"bottom": "corntopia:block/*_planks",
		"top": "corntopia:block/*_planks",
		"side": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.models.block.*_stairs_inner.json
	 * <pre><code>
{
	"parent": "minecraft:block/inner_stairs",
	"textures": {
		"bottom": "corntopia:block/*_planks",
		"top": "corntopia:block/*_planks",
		"side": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.models.block.*_stairs_outer.json
	 * <pre><code>
{
	"parent": "minecraft:block/outer_stairs",
	"textures": {
		"bottom": "corntopia:block/*_planks",
		"top": "corntopia:block/*_planks",
		"side": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.item.*_stairs.json
	 * <pre><code>
{
	"parent": "corntopia:block/*_stairs"
}
	 * </code></pre>
	 * <b>Pressure Plate</b><br />
	 * - assets.corntopia.blockstates.*_pressure_plate.json
	 * <pre><code>
{
	"variants": {
		"powered=false": {
			"model": "corntopia:block/*_pressure_plate"
		},
		"powered=true": {
			"model": "corntopia:block/*_pressure_plate_down"
		}
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_pressure_plate.json
	 * <pre><code>
{
	"parent": "minecraft:block/pressure_plate_up",
	"textures": {
		"texture": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_pressure_plate_down.json
	 * <pre><code>
{
	"parent": "minecraft:block/pressure_plate_down",
	"textures": {
		"texture": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.item.*_pressure_plate.json
	 * <pre><code>
{
	"parent": "corntopia:block/*_pressure_plate"
}
	 * </code></pre>
	 * <b>Sign</b><br />
	 * - assets.corntopia.blockstates.*_sign.json
	 * <pre><code>
{
	"variants": {
		"": {
			"model": "corntopia:block/*_sign"
		}
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_sign.json
	 * <pre><code>
{
	"textures": {
		"particle": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.blockstates.*_wall_sign.json
	 * <pre><code>
{
	"variants": {
		"": {
			"model": "corntopia:block/*_sign"
		}
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.item.*_sign.json
	 * <pre><code>
{
	"parent": "minecraft:item/generated",
	"textures": {
		"layer0": "corntopia:item/*_sign"
	}
}
	 * </code></pre>
	 * <b>Trapdoor</b><br />
	 * - assets.corntopia.blockstates.*_trapdoor.json
	 * <pre><code>
{
	"variants": {
		"facing=east,half=bottom,open=false": {
			"model": "corntopia:block/*_trapdoor_bottom"
		},
		"facing=east,half=bottom,open=true": {
			"model": "corntopia:block/*_trapdoor_open",
			"y": 90
		},
		"facing=east,half=top,open=false": {
			"model": "corntopia:block/*_trapdoor_top"
		},
		"facing=east,half=top,open=true": {
			"model": "corntopia:block/*_trapdoor_open",
			"y": 90
		},
		"facing=north,half=bottom,open=false": {
			"model": "corntopia:block/*_trapdoor_bottom"
		},
		"facing=north,half=bottom,open=true": {
			"model": "corntopia:block/*_trapdoor_open"
		},
		"facing=north,half=top,open=false": {
			"model": "corntopia:block/*_trapdoor_top"
		},
		"facing=north,half=top,open=true": {
			"model": "corntopia:block/*_trapdoor_open"
		},
		"facing=south,half=bottom,open=false": {
			"model": "corntopia:block/*_trapdoor_bottom"
		},
		"facing=south,half=bottom,open=true": {
			"model": "corntopia:block/*_trapdoor_open",
			"y": 180
		},
		"facing=south,half=top,open=false": {
			"model": "corntopia:block/*_trapdoor_top"
		},
		"facing=south,half=top,open=true": {
			"model": "corntopia:block/*_trapdoor_open",
			"y": 180
		},
		"facing=west,half=bottom,open=false": {
			"model": "corntopia:block/*_trapdoor_bottom"
		},
		"facing=west,half=bottom,open=true": {
			"model": "corntopia:block/*_trapdoor_open",
			"y": 270
		},
		"facing=west,half=top,open=false": {
			"model": "corntopia:block/*_trapdoor_top"
		},
		"facing=west,half=top,open=true": {
			"model": "corntopia:block/*_trapdoor_open",
			"y": 270
		}
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_trapdoor_bottom.json
	 * <pre><code>
{
	"parent": "minecraft:block/template_trapdoor_bottom",
	"textures": {
		"texture": "corntopia:block/*_trapdoor"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_trapdoor_open.json
	 * <pre><code>
{
	"parent": "minecraft:block/template_trapdoor_open",
	"textures": {
		"texture": "corntopia:block/*_trapdoor"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_trapdoor_top.json
	 * <pre><code>
{
	"parent": "minecraft:block/template_trapdoor_top",
	"textures": {
		"texture": "corntopia:block/*_trapdoor"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.item.*_trapdoor.json
	 * <pre><code>
{
	"parent": "corntopia:block/*_trapdoor_bottom"
}
	 * </code></pre>
	 * <b>Button</b><br />
	 * - assets.corntopia.blockstates.*_button.json
	 * <pre><code>
{
	"variants": {
		"face=ceiling,facing=east,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 270,
			"x": 180
		},
		"face=ceiling,facing=east,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 270,
			"x": 180
		},
		"face=ceiling,facing=north,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 180,
			"x": 180
		},
		"face=ceiling,facing=north,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 180,
			"x": 180
		},
		"face=ceiling,facing=south,powered=false": {
			"model": "corntopia:block/*_button",
			"x": 180
		},
		"face=ceiling,facing=south,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"x": 180
		},
		"face=ceiling,facing=west,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 90,
			"x": 180
		},
		"face=ceiling,facing=west,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 90,
			"x": 180
		},
		"face=floor,facing=east,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 90
		},
		"face=floor,facing=east,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 90
		},
		"face=floor,facing=north,powered=false": {
			"model": "corntopia:block/*_button"
		},
		"face=floor,facing=north,powered=true": {
			"model": "corntopia:block/*_button_pressed"
		},
		"face=floor,facing=south,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 180
		},
		"face=floor,facing=south,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 180
		},
		"face=floor,facing=west,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 270
		},
		"face=floor,facing=west,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 270
		},
		"face=wall,facing=east,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 90,
			"x": 90,
			"uvlock": true
		},
		"face=wall,facing=east,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 90,
			"x": 90,
			"uvlock": true
		},
		"face=wall,facing=north,powered=false": {
			"model": "corntopia:block/*_button",
			"x": 90,
			"uvlock": true
		},
		"face=wall,facing=north,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"x": 90,
			"uvlock": true
		},
		"face=wall,facing=south,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 180,
			"x": 90,
			"uvlock": true
		},
		"face=wall,facing=south,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 180,
			"x": 90,
			"uvlock": true
		},
		"face=wall,facing=west,powered=false": {
			"model": "corntopia:block/*_button",
			"y": 270,
			"x": 90,
			"uvlock": true
		},
		"face=wall,facing=west,powered=true": {
			"model": "corntopia:block/*_button_pressed",
			"y": 270,
			"x": 90,
			"uvlock": true
		}
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_button.json
	 * <pre><code>
{
	"parent": "minecraft:block/button",
	"textures": {
		"texture": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_button_pressed.json
	 * <pre><code>
{
	"parent": "minecraft:block/button_pressed",
	"textures": {
		"texture": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.item.*_button.json
	 * <pre><code>
{
	"parent": "corntopia:block/*_button_inventory"
}
	 * </code></pre>
	 * <b>Slab</b><br />
	 * - assets.corntopia.blockstates.*_slab.json
	 * <pre><code>
{
	"variants": {
		"type=bottom": {
			"model": "corntopia:block/*_slab"
		},
		"type=double": {
			"model": "corntopia:block/*_planks"
		},
		"type=top": {
			"model": "corntopia:block/*_slab_top"
		}
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_slab.json
	 * <pre><code>
{
	"parent": "minecraft:block/slab",
	"textures": {
		"bottom": "corntopia:block/*_planks",
		"top": "corntopia:block/*_planks",
		"side": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_slab_top.json
	 * <pre><code>
{
	"parent": "minecraft:block/slab_top",
	"textures": {
		"bottom": "corntopia:block/*_planks",
		"top": "corntopia:block/*_planks",
		"side": "corntopia:block/*_planks"
	}
}
	 * </code></pre>
	 * - assets.corntopia.models.block.*_planks.json (see <b>Planks</b>)<br />
	 * - assets.corntopia.models.item.*_slab.json
	 * <pre><code>
{
	"parent": "corntopia:block/*_slab"
}
	 * </code></pre>
	 */
	public static void register(String name, long exclude) {
		Block planks = null;
		StairsBlock stairs;
		PressurePlateBlock pressurePlate;
		SignBlock sign;
		WallSignBlock wallSign;
		TrapdoorBlock trapdoor;
		AbstractButtonBlock button;
		SlabBlock slab;
		SignType signType;
		
		if (!ExcludeMask.PLANKS.contains(exclude)) {
			planks = createPlanksBlock();
			PLANKS_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_planks"), planks));
			PLANKS_ITEMS.put(name, Registry.register(Registry.ITEM, new Identifier(CorntopiaMod.NAMESPACE, name + "_planks"), createBuildingBlockItem(planks)));
		}
		if (!ExcludeMask.SLAB.contains(exclude)) {
			slab = createSlab();
			SLAB_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_slab"), slab));
			SLAB_ITEMS.put(name, Registry.register(Registry.ITEM, new Identifier(CorntopiaMod.NAMESPACE, name + "_slab"), createBuildingBlockItem(slab)));
		}
		if (!ExcludeMask.STAIRS.contains(exclude) && planks != null) {
			stairs = createStairsBlock(planks);
			STAIRS_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_stairs"), stairs));
			STAIRS_ITEMS.put(name, Registry.register(Registry.ITEM, new Identifier(CorntopiaMod.NAMESPACE, name + "_stairs"), createBuildingBlockItem(stairs)));
		} else if (planks == null)
			LOGGER.warn("Stairs was not made for the %s building block type (missing Planks).", name);
		if (!ExcludeMask.PRESSURE_PLATE.contains(exclude) && planks != null) {
			pressurePlate = createPressurePlate(planks);
			PRESSURE_PLATE_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_pressure_plate"), pressurePlate));
			PRESSURE_PLATE_ITEMS.put(name, Registry.register(Registry.ITEM, new Identifier(CorntopiaMod.NAMESPACE, name + "_pressure_plate"), createRedstoneBlockItem(pressurePlate)));
		} else if (planks == null)
			LOGGER.warn("A pressure plate was not made for the %s building block type (missing Planks).", name);
		if (!ExcludeMask.SIGN.contains(exclude)) {
			signType = new CustomSignType(name);
			sign = createSignBlock(signType);
			wallSign = createWallSignBlock(signType, sign);
			SignTypeMixin.invokeRegister(signType);
			SIGN_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_sign"), sign));
			WALL_SIGN_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_wall_sign"), wallSign));
			SIGN_ITEMS.put(name, Registry.register(Registry.ITEM, new Identifier(CorntopiaMod.NAMESPACE, name + "_sign"), createSignBlockItem(sign, wallSign)));
		}
		if (!ExcludeMask.TRAPDOOR.contains(exclude)) {
			trapdoor = createTrapdoor();
			TRAPDOOR_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_trapdoor"), trapdoor));
			TRAPDOOR_ITEMS.put(name, Registry.register(Registry.ITEM, new Identifier(CorntopiaMod.NAMESPACE, name + "_trapdoor"), createRedstoneBlockItem(trapdoor)));
		}
		if (!ExcludeMask.BUTTON.contains(exclude)) {
			button = createButton();
			BUTTON_BLOCKS.put(name, Registry.register(Registry.BLOCK, new Identifier(CorntopiaMod.NAMESPACE, name + "_button"), button));
			BUTTON_ITEMS.put(name, Registry.register(Registry.ITEM, new Identifier(CorntopiaMod.NAMESPACE, name + "_button"), createRedstoneBlockItem(button)));
		}
	}
	
	/** @see #register(String, long) */
	public static void register(String name) {
		register(name, 0);
	}
	
	private static Block createPlanksBlock() {
		return new Block(FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
	}
	
	private static StairsBlock createStairsBlock(Block planks) {
		return new CustomStairsBlock(planks.getDefaultState(), FabricBlockSettings.copyOf(planks));
	}
	
	private static PressurePlateBlock createPressurePlate(Block planks) {
		return new CustomPressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, FabricBlockSettings.of(Material.WOOD, planks.getDefaultMaterialColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	}
	
	private static SignBlock createSignBlock(SignType type) {
		return new SignBlock(FabricBlockSettings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), type);
	}
	
	private static WallSignBlock createWallSignBlock(SignType type, SignBlock sign) {
		return new WallSignBlock(FabricBlockSettings.of(Material.WOOD).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(sign), type);
	}
	
	private static TrapdoorBlock createTrapdoor() {
		return new CustomTrapdoorBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(BuildingBlocks::never));
	}
	
	private static AbstractButtonBlock createButton() {
		return new CustomWoodenButtonBlock(FabricBlockSettings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD));
	}
	
	private static SlabBlock createSlab() {
		return new SlabBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
	}
	
	private static Item createBuildingBlockItem(Block block) {
		return new BlockItem(block, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));
	}
	
	private static Item createRedstoneBlockItem(Block block) {
		return new BlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE));
	}
	
	private static Item createSignBlockItem(SignBlock sign, WallSignBlock wallSign) {
		return new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), sign, wallSign);
	}
	
	private static Boolean never(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return false;
	}
	
	private static class CustomStairsBlock extends StairsBlock {
		public CustomStairsBlock(BlockState baseBlockState, Settings settings) {
			super(baseBlockState, settings);
		}
	}
	
	private static class CustomSignType extends SignType {
		public CustomSignType(String name) {
			super(name);
		}
	}
	
	private static class CustomPressurePlateBlock extends PressurePlateBlock {
		protected CustomPressurePlateBlock(ActivationRule type, Settings settings) {
			super(type, settings);
		}
	}
	
	private static class CustomTrapdoorBlock extends TrapdoorBlock {
		protected CustomTrapdoorBlock(Settings settings) {
			super(settings);
		}
	}
	
	private static class CustomWoodenButtonBlock extends WoodenButtonBlock {
		protected CustomWoodenButtonBlock(Settings settings) {
			super(settings);
		}
	}
}

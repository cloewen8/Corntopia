package ca.cjloewen.corntopia.world.gen.feature;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class FillHorizontalFeatureConfig implements FeatureConfig {
	public static final Codec<FillHorizontalFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> {
	    return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("state_provider").forGetter((fillHorizontalFeatureConfig) -> {
	        return fillHorizontalFeatureConfig.stateProvider;
	    }), BlockPlacer.TYPE_CODEC.fieldOf("block_placer").forGetter((fillHorizontalFeatureConfig) -> {
	        return fillHorizontalFeatureConfig.blockPlacer;
	    }), BlockState.CODEC.listOf().fieldOf("whitelist").forGetter((fillHorizontalFeatureConfig) -> {
	        return fillHorizontalFeatureConfig.whitelist.stream().map(Block::getDefaultState).collect(Collectors.toList());
	    }), BlockState.CODEC.listOf().fieldOf("blacklist").forGetter((fillHorizontalFeatureConfig) -> {
	        return ImmutableList.copyOf(fillHorizontalFeatureConfig.blacklist);
	    }), Codec.BOOL.fieldOf("can_replace").orElse(false).forGetter((fillHorizontalFeatureConfig) -> {
	        return fillHorizontalFeatureConfig.canReplace;
	    }), Codec.BOOL.fieldOf("project").orElse(true).forGetter((fillHorizontalFeatureConfig) -> {
	        return fillHorizontalFeatureConfig.project;
	    }), Codec.BOOL.fieldOf("need_water").orElse(false).forGetter((fillHorizontalFeatureConfig) -> {
	        return fillHorizontalFeatureConfig.needsWater;
	    }), Codec.STRING.fieldOf("biome_key").orElse(null).forGetter((fillHorizontalFeatureConfig) -> {
	    	return fillHorizontalFeatureConfig.biomeKey.getValue().toString();
	    })).apply(instance, FillHorizontalFeatureConfig::new);
	});
	public final BlockStateProvider stateProvider;
	public final BlockPlacer blockPlacer;
	public final Set<Block> whitelist;
	public final Set<BlockState> blacklist;
	public final boolean canReplace;
	public final boolean project;
	public final boolean needsWater;
	public final RegistryKey<Biome> biomeKey;

	private FillHorizontalFeatureConfig(BlockStateProvider stateProvider, BlockPlacer blockPlacer, List<BlockState> whitelist, List<BlockState> blacklist, boolean canReplace, boolean project, boolean needsWater, String biomeId) {
		this(stateProvider,
	    	blockPlacer,
	    	whitelist.stream().map(AbstractBlock.AbstractBlockState::getBlock).collect(Collectors.toSet()),
	    	ImmutableSet.copyOf(blacklist),
	    	canReplace,
	    	project,
	    	needsWater,
	    	biomeId != null ? RegistryKey.of(Registry.BIOME_KEY, new Identifier(biomeId)) : null);
	}

	private FillHorizontalFeatureConfig(BlockStateProvider stateProvider, BlockPlacer blockPlacer, Set<Block> whitelist, Set<BlockState> blacklist, boolean canReplace, boolean project, boolean needsWater, RegistryKey<Biome> biomeKey) {
	    this.stateProvider = stateProvider;
	    this.blockPlacer = blockPlacer;
	    this.whitelist = whitelist;
	    this.blacklist = blacklist;
	    this.canReplace = canReplace;
	    this.project = project;
	    this.needsWater = needsWater;
	    this.biomeKey = biomeKey;
	}

	public static class Builder {
	    private final BlockStateProvider stateProvider;
	    private final BlockPlacer blockPlacer;
	    private Set<Block> whitelist = ImmutableSet.of();
	    private Set<BlockState> blacklist = ImmutableSet.of();
	    private boolean canReplace;
	    private boolean project = true;
	    private boolean needsWater = false;
	    private RegistryKey<Biome> biomeKey = null;

	    public Builder(BlockStateProvider stateProvider, BlockPlacer blockPlacer) {
	        this.stateProvider = stateProvider;
	        this.blockPlacer = blockPlacer;
	    }

	    public FillHorizontalFeatureConfig.Builder whitelist(Set<Block> whitelist) {
	        this.whitelist = whitelist;
	        return this;
	    }

	    public FillHorizontalFeatureConfig.Builder blacklist(Set<BlockState> blacklist) {
	        this.blacklist = blacklist;
	        return this;
	    }

	    public FillHorizontalFeatureConfig.Builder canReplace() {
	        this.canReplace = true;
	        return this;
	    }

	    public FillHorizontalFeatureConfig.Builder cannotProject() {
	        this.project = false;
	        return this;
	    }

	    public FillHorizontalFeatureConfig.Builder needsWater() {
	        this.needsWater = true;
	        return this;
	    }
	    
	    /** The biome to fill. */
	    public FillHorizontalFeatureConfig.Builder biome(RegistryKey<Biome> biomeKey) {
	    	this.biomeKey = biomeKey;
	    	return this;
	    }

	    public FillHorizontalFeatureConfig build() {
	        return new FillHorizontalFeatureConfig(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.canReplace, this.project, this.needsWater, this.biomeKey);
	    }
	}
}

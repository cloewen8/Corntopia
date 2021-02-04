package ca.cjloewen.corntopia.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import ca.cjloewen.corntopia.item.Items;
import ca.cjloewen.corntopia.loot.LootTableRangeAmplifier;
import ca.cjloewen.corntopia.loot.LootTables;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPool;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(LootManager.class)
public class LootManagerMixin {
	@Shadow
	private Map<Identifier, LootTable> tables;
	
	@Inject(method = "apply", at = @At("RETURN"))
	private void apply(Map<Identifier, JsonObject> objectMap, ResourceManager manager, Profiler profiler, CallbackInfo info) {
		Map<Identifier, LootTable> newSuppliers = new HashMap<>(tables);
		// Add loot
		modifyLoot(newSuppliers, net.minecraft.loot.LootTables.VILLAGE_SHEPARD_CHEST, (builder) -> {
			builder.pool(FabricLootPoolBuilder.builder()
					.rolls(UniformLootTableRange.between(1.0F, 5.0F))
				.with(ItemEntry.builder(Items.CORN).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 16.0F)))));
		});
		modifyLoot(newSuppliers, net.minecraft.loot.LootTables.WOODLAND_MANSION_CHEST, (builder) -> {
			builder.pool(FabricLootPoolBuilder.builder()
					.rolls(UniformLootTableRange.between(1.0F, 5.0F))
				.with(ItemEntry.builder(Items.CORN).weight(30).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 32.0F)))));
		});
		// Create amplified loot tables
		newSuppliers.put(LootTables.BARN_ABANDONED, amplifyLoot(newSuppliers.get(net.minecraft.loot.LootTables.VILLAGE_SHEPARD_CHEST), 4));
		tables = ImmutableMap.copyOf(newSuppliers);
	}
	
	private void modifyLoot(Map<Identifier, LootTable> suppliers, Identifier id, Consumer<FabricLootSupplierBuilder> modifier) {
		FabricLootSupplierBuilder builder = FabricLootSupplierBuilder.of(suppliers.get(id));
		modifier.accept(builder);
		suppliers.replace(id, builder.build());
	}
	
	private LootTable amplifyLoot(LootTable supplier, int rollsMult) {
		LootTable.Builder tableBuilder = LootTable.builder();
		FabricLootSupplier extendedSupplier = (FabricLootSupplier)supplier;
		extendedSupplier.getPools().stream().map((pool) -> {
			FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder();
			poolBuilder.copyFrom(pool);
			poolBuilder.rolls(new LootTableRangeAmplifier(((FabricLootPool)pool).getRolls(), rollsMult));
			return poolBuilder;
		}).forEach(tableBuilder::pool);
		tableBuilder.type(extendedSupplier.getType());
		extendedSupplier.getFunctions().stream().map(DoneBuilder::new).forEach(tableBuilder::apply);
		return tableBuilder.build();
	}
	
	// I agree object factories (builders) look nice and they allow fields to be made final, but is this really worth it?!
	// At least it is done.
	private static class DoneBuilder implements LootFunction.Builder {
		private final LootFunction done;
		
		public DoneBuilder(LootFunction done) {
			this.done = done;
		}
		
		@Override
		public LootFunction build() {
			return done;
		}
	}
}

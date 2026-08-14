package com.mrcrayfish.guns.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LootTableGen extends LootTableProvider
{
    public LootTableGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, Collections.emptySet(), ImmutableList.of(new LootTableProvider.SubProviderEntry(BlockProvider::new, LootContextParamSets.BLOCK)), lookupProvider);
    }

    @Override
    protected void validate(WritableRegistry<LootTable> map, ValidationContext context, ProblemReporter.Collector reporter) {}

    private static class BlockProvider extends BlockLootSubProvider
    {
        protected BlockProvider(HolderLookup.Provider lookupProvider)
        {
            super(ImmutableSet.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate()
        {
            this.dropSelf(ModBlocks.WORKBENCH.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return BuiltInRegistries.BLOCK.stream().filter(block -> Reference.MOD_ID.equals(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getNamespace())).collect(Collectors.toSet());
        }
    }
}

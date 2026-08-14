package com.mrcrayfish.guns.datagen;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.ModTags;
import com.mrcrayfish.guns.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagGen extends ItemTagsProvider
{
    public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, blockTagProvider, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(ModTags.Items.GUNS)
                .add(ModItems.PISTOL.getKey())
                .add(ModItems.SHOTGUN.getKey())
                .add(ModItems.RIFLE.getKey())
                .add(ModItems.GRENADE_LAUNCHER.getKey())
                .add(ModItems.BAZOOKA.getKey())
                .add(ModItems.MINI_GUN.getKey())
                .add(ModItems.ASSAULT_RIFLE.getKey())
                .add(ModItems.MACHINE_PISTOL.getKey())
                .add(ModItems.HEAVY_RIFLE.getKey());
    }
}

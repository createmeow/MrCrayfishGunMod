package com.mrcrayfish.guns.datagen;

import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.init.ModEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

/**
 * Generates enchantment tag files for the gun mod's enchantments.
 */
public class EnchantmentTagGen extends EnchantmentTagsProvider
{
    public EnchantmentTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // Add all gun enchantments to the in_enchanting_table tag so they appear in the enchanting table
        var tag = this.tag(EnchantmentTags.IN_ENCHANTING_TABLE);
        for(DeferredHolder<Enchantment, ? extends Enchantment> enchantment : ModEnchantments.REGISTER.getEntries())
        {
            tag.add(enchantment.getKey());
        }
    }
}

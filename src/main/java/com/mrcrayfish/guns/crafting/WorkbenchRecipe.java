package com.mrcrayfish.guns.crafting;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.guns.init.ModRecipeSerializers;
import com.mrcrayfish.guns.init.ModRecipeTypes;
import com.mrcrayfish.guns.util.InventoryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Author: MrCrayfish
 */
public class WorkbenchRecipe implements Recipe<RecipeInput>
{
    private final ResourceLocation id;
    private final ItemStack item;
    private final ImmutableList<WorkbenchIngredient> materials;

    public WorkbenchRecipe(ResourceLocation id, ItemStack item, ImmutableList<WorkbenchIngredient> materials)
    {
        this.id = id;
        this.item = item;
        this.materials = materials;
    }

    public ItemStack getItem()
    {
        return this.item.copy();
    }

    public ImmutableList<WorkbenchIngredient> getMaterials()
    {
        return this.materials;
    }

    @Override
    public boolean matches(RecipeInput input, Level worldIn)
    {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider access)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access)
    {
        return this.item.copy();
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return ModRecipeSerializers.WORKBENCH.get();
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeType<?> getType()
    {
        return ModRecipeTypes.WORKBENCH.get();
    }

    public boolean hasMaterials(Player player)
    {
        for(WorkbenchIngredient ingredient : this.getMaterials())
        {
            if(!InventoryUtil.hasWorkstationIngredient(player, ingredient))
            {
                return false;
            }
        }
        return true;
    }

    public void consumeMaterials(Player player)
    {
        for(WorkbenchIngredient ingredient : this.getMaterials())
        {
            InventoryUtil.removeWorkstationIngredient(player, ingredient);
        }
    }
}

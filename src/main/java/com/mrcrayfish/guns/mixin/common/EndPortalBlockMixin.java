package com.mrcrayfish.guns.mixin.common;

import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.util.ItemStackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin
{
    @Inject(method = "entityInside", at = @At("HEAD"))
    private void beforeChangeDimension(BlockState state, Level worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci)
    {
        if(worldIn.dimension() == Level.END && entityIn instanceof ItemEntity)
        {
            ItemStack stack = ((ItemEntity) entityIn).getItem();
            if(stack.getItem() instanceof GunItem)
            {
                ItemStack gun = stack.copy();
                CompoundTag tag = ItemStackUtil.getOrCreateTag(gun);
                tag.putFloat("Scale", 2.0F);
                ItemStackUtil.setTag(gun, tag);
                ((ItemEntity) entityIn).setItem(gun);
            }
        }
    }
}


package com.mrcrayfish.guns.compat;

import com.mrcrayfish.guns.GunMod;
import com.mrcrayfish.guns.common.AmmoContext;
import com.mrcrayfish.guns.common.Gun;
import dev.xkmc.l2backpack.content.common.BaseBagItem;
import dev.xkmc.l2backpack.content.remote.player.EnderBackpackItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class L2BackpackHelper {
    private static final Predicate<ItemStack> BASE_BAG = s -> s.getItem() instanceof BaseBagItem;
    private static final Predicate<ItemStack> ENDER_BAG = s -> s.getItem() instanceof EnderBackpackItem;

    public static AmmoContext findAmmo(Player player, ResourceLocation id)
    {
        AmmoContext ctx = searchFor(player, id, BASE_BAG, (p, s, i) -> handleBaseBagItem(s, i));
        if(!ctx.equals(AmmoContext.NONE)) return ctx;
        return searchFor(player, id, ENDER_BAG, (p, s, i) -> handleEnderBackpackItem(p, i));
    }

    private static AmmoContext searchFor(Player player, ResourceLocation id, Predicate<ItemStack> filter, SearchResult result) {
        final AtomicReference<AmmoContext> ctx = new AtomicReference<>(AmmoContext.NONE);
        if (GunMod.curiosLoaded) CuriosHelper.runOnCurios(player, h -> h.findFirstCurio(filter)
                .ifPresent(r -> ctx.set(result.get(player, r.stack(), id))));
        if (!ctx.get().equals(AmmoContext.NONE)) return ctx.get();
        final ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (filter.test(chest)) ctx.set(result.get(player, chest, id));
        return ctx.get();
    }

    interface SearchResult {
        AmmoContext get(Player player, ItemStack stack, ResourceLocation id);
    }

    private static AmmoContext handleBaseBagItem(ItemStack bagStack, ResourceLocation id) {
        if (!((bagStack.getItem()) instanceof BaseBagItem)) return AmmoContext.NONE;
        final List<ItemStack> inv = BaseBagItem.getItems(bagStack);
        if (inv.isEmpty()) return AmmoContext.NONE;
        for (int i = 0; i < inv.size(); i++) {
            final ItemStack stack = inv.get(i);
            if(!Gun.isAmmo(stack, id)) continue;
            return new AmmoContext(stack, s -> BaseBagItem.setItems(bagStack, inv));
        }
        return AmmoContext.NONE;
    }

    private static AmmoContext handleEnderBackpackItem(Player player, ResourceLocation id) {
        final PlayerEnderChestContainer ender = player.getEnderChestInventory();
        for (int i = 0; i < ender.getContainerSize(); i++) {
            ItemStack stack = ender.getItem(i);
            if(Gun.isAmmo(stack, id))
            {
                return new AmmoContext(stack, ender);
            }
        }
        return AmmoContext.NONE;
    }
}

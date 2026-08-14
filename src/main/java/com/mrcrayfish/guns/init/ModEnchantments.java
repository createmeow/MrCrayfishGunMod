package com.mrcrayfish.guns.init;

import net.minecraft.core.registries.Registries;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.ModTags;
import com.mrcrayfish.guns.enchantment.AcceleratorEnchantment;
import com.mrcrayfish.guns.enchantment.CollateralEnchantment;
import com.mrcrayfish.guns.enchantment.FireStarterEnchantment;
import com.mrcrayfish.guns.enchantment.LightweightEnchantment;
import com.mrcrayfish.guns.enchantment.OverCapacityEnchantment;
import com.mrcrayfish.guns.enchantment.PuncturingEnchantment;
import com.mrcrayfish.guns.enchantment.QuickHandsEnchantment;
import com.mrcrayfish.guns.enchantment.ReclaimedEnchantment;
import com.mrcrayfish.guns.enchantment.TriggerFingerEnchantment;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Author: MrCrayfish
 */
public class ModEnchantments
{
    public static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(Registries.ENCHANTMENT, Reference.MOD_ID);

    private static HolderSet<Item> getSupportedGuns()
    {
        return BuiltInRegistries.ITEM.getOrCreateTag(ModTags.Items.GUNS);
    }

    public static final DeferredHolder<Enchantment, Enchantment> QUICK_HANDS = REGISTER.register("quick_hands", () -> new QuickHandsEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "quick_hands"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> TRIGGER_FINGER = REGISTER.register("trigger_finger", () -> new TriggerFingerEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "trigger_finger"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> LIGHTWEIGHT = REGISTER.register("lightweight", () -> new LightweightEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "lightweight"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> COLLATERAL = REGISTER.register("collateral", () -> new CollateralEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "collateral"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> OVER_CAPACITY = REGISTER.register("over_capacity", () -> new OverCapacityEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "over_capacity"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> RECLAIMED = REGISTER.register("reclaimed", () -> new ReclaimedEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "reclaimed"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> ACCELERATOR = REGISTER.register("accelerator", () -> new AcceleratorEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "accelerator"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> PUNCTURING = REGISTER.register("puncturing", () -> new PuncturingEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "puncturing"), getSupportedGuns()));
    public static final DeferredHolder<Enchantment, Enchantment> FIRE_STARTER = REGISTER.register("fire_starter", () -> new FireStarterEnchantment().build(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fire_starter"), getSupportedGuns()));
    //Gravity Impulse (3 levels) - nearby entities will get knocked away from the target location
}


package com.mrcrayfish.guns.init;

import net.minecraft.core.registries.Registries;
import com.mrcrayfish.guns.Reference;
import com.mrcrayfish.guns.common.Attachments;
import com.mrcrayfish.guns.common.GunModifiers;
import com.mrcrayfish.guns.item.AmmoItem;
import com.mrcrayfish.guns.item.BarrelItem;
import com.mrcrayfish.guns.item.GrenadeItem;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.ScopeItem;
import com.mrcrayfish.guns.item.StockItem;
import com.mrcrayfish.guns.item.StunGrenadeItem;
import com.mrcrayfish.guns.item.UnderBarrelItem;
import com.mrcrayfish.guns.item.attachment.impl.Barrel;
import com.mrcrayfish.guns.item.attachment.impl.Stock;
import com.mrcrayfish.guns.item.attachment.impl.UnderBarrel;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItems
{
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, Reference.MOD_ID);

    public static final DeferredHolder<Item, ? extends Item> PISTOL = REGISTER.register("pistol", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> SHOTGUN = REGISTER.register("shotgun", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> RIFLE = REGISTER.register("rifle", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> GRENADE_LAUNCHER = REGISTER.register("grenade_launcher", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> BAZOOKA = REGISTER.register("bazooka", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> MINI_GUN = REGISTER.register("mini_gun", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> ASSAULT_RIFLE = REGISTER.register("assault_rifle", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> MACHINE_PISTOL = REGISTER.register("machine_pistol", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));
    public static final DeferredHolder<Item, ? extends Item> HEAVY_RIFLE = REGISTER.register("heavy_rifle", () -> new GunItem(new Item.Properties().stacksTo(1).durability(1000)));

    public static final DeferredHolder<Item, ? extends Item> BASIC_BULLET = REGISTER.register("basic_bullet", () -> new AmmoItem(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> ADVANCED_AMMO = REGISTER.register("advanced_bullet", () -> new AmmoItem(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> SHELL = REGISTER.register("shell", () -> new AmmoItem(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> MISSILE = REGISTER.register("missile", () -> new AmmoItem(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> GRENADE = REGISTER.register("grenade", () -> new GrenadeItem(new Item.Properties(), 20 * 4));
    public static final DeferredHolder<Item, ? extends Item> STUN_GRENADE = REGISTER.register("stun_grenade", () -> new StunGrenadeItem(new Item.Properties(), 72000));

    /* Scope Attachments */
    public static final DeferredHolder<Item, ? extends Item> SHORT_SCOPE = REGISTER.register("short_scope", () -> new ScopeItem(Attachments.SHORT_SCOPE, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ? extends Item> MEDIUM_SCOPE = REGISTER.register("medium_scope", () -> new ScopeItem(Attachments.MEDIUM_SCOPE, new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ? extends Item> LONG_SCOPE = REGISTER.register("long_scope", () -> new ScopeItem(Attachments.LONG_SCOPE, new Item.Properties().stacksTo(1)));

    /* Barrel Attachments */
    public static final DeferredHolder<Item, ? extends Item> SILENCER = REGISTER.register("silencer", () -> new BarrelItem(Barrel.create(8.0F, GunModifiers.SILENCED, GunModifiers.REDUCED_DAMAGE), new Item.Properties().stacksTo(1)));

    /* Stock Attachments */
    public static final DeferredHolder<Item, ? extends Item> LIGHT_STOCK = REGISTER.register("light_stock", () -> new StockItem(Stock.create(GunModifiers.BETTER_CONTROL), new Item.Properties().stacksTo(1), false));
    public static final DeferredHolder<Item, ? extends Item> TACTICAL_STOCK = REGISTER.register("tactical_stock", () -> new StockItem(Stock.create(GunModifiers.STABILISED), new Item.Properties().stacksTo(1), false));
    public static final DeferredHolder<Item, ? extends Item> WEIGHTED_STOCK = REGISTER.register("weighted_stock", () -> new StockItem(Stock.create(GunModifiers.SUPER_STABILISED), new Item.Properties().stacksTo(1)));

    /* Under Barrel Attachments */
    public static final DeferredHolder<Item, ? extends Item> LIGHT_GRIP = REGISTER.register("light_grip", () -> new UnderBarrelItem(UnderBarrel.create(GunModifiers.LIGHT_RECOIL), new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ? extends Item> SPECIALISED_GRIP = REGISTER.register("specialised_grip", () -> new UnderBarrelItem(UnderBarrel.create(GunModifiers.REDUCED_RECOIL), new Item.Properties().stacksTo(1)));
}

package com.mrcrayfish.guns;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.guns.client.ClientHandler;
import com.mrcrayfish.guns.client.CustomGunManager;
import com.mrcrayfish.guns.client.KeyBinds;
import com.mrcrayfish.guns.client.MetaLoader;
import com.mrcrayfish.guns.client.handler.CrosshairHandler;
import com.mrcrayfish.guns.common.BoundingBoxManager;
import com.mrcrayfish.guns.common.NetworkGunManager;
import com.mrcrayfish.guns.common.ProjectileManager;
import com.mrcrayfish.guns.compat.SimplePlanesHelper;
import com.mrcrayfish.guns.datagen.*;
import com.mrcrayfish.guns.entity.GrenadeEntity;
import com.mrcrayfish.guns.entity.MissileEntity;
import com.mrcrayfish.guns.init.*;
import com.mrcrayfish.guns.network.PacketHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(Reference.MOD_ID)
public class GunMod
{
    public static boolean debugging = false;
    public static boolean curiosLoaded = false;
    public static boolean backpackedLoaded = false;
    public static boolean playerReviveLoaded = false;
    public static boolean sopLoaded = false;
    public static boolean travelersBackpackLoaded = false;
    public static boolean l2BackpackLoaded = false;
    public static boolean cmdCamLoaded = false;
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public GunMod(IEventBus bus, ModContainer container)
    {
        container.registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        container.registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
        container.registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        ModBlocks.REGISTER.register(bus);
        ModContainers.REGISTER.register(bus);
        ModEffects.REGISTER.register(bus);
        ModEnchantments.REGISTER.register(bus);
        ModEntities.REGISTER.register(bus);
        ModItems.REGISTER.register(bus);
        ModParticleTypes.REGISTER.register(bus);
        ModRecipeSerializers.REGISTER.register(bus);
        ModRecipeTypes.REGISTER.register(bus);
        ModSounds.REGISTER.register(bus);
        ModTileEntities.REGISTER.register(bus);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onGatherData);
        if(FMLEnvironment.dist.isClient())
        {
            FrameworkClientAPI.registerDataLoader(MetaLoader.getInstance());
            ClientHandler.registerCreativeTab(bus);
            bus.addListener(KeyBinds::registerKeyMappings);
            bus.addListener(CrosshairHandler::onConfigReload);
            bus.addListener(ClientHandler::onRegisterReloadListener);
            bus.addListener(ClientHandler::registerAdditional);
            bus.addListener(ClientHandler::registerScreenFactories);
        }
        curiosLoaded = ModList.get().isLoaded("curios");
        backpackedLoaded = ModList.get().isLoaded("backpacked");
        playerReviveLoaded = ModList.get().isLoaded("playerrevive");
        sopLoaded = ModList.get().isLoaded("sophisticatedbackpacks");
        travelersBackpackLoaded = ModList.get().isLoaded("travelersbackpack");
        l2BackpackLoaded = ModList.get().isLoaded("l2backpack");
        cmdCamLoaded = ModList.get().isLoaded("cmdcam");
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            PacketHandler.init();
            try { FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.AIMING); } catch(IllegalArgumentException ignored) {}
            try { FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.RELOADING); } catch(IllegalArgumentException ignored) {}
            try { FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.SHOOTING); } catch(IllegalArgumentException ignored) {}
            ProjectileManager.getInstance().registerFactory(ModItems.GRENADE.get(), (worldIn, entity, weapon, item, modifiedGun) -> new GrenadeEntity(ModEntities.GRENADE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.MISSILE.get(), (worldIn, entity, weapon, item, modifiedGun) -> new MissileEntity(ModEntities.MISSILE.get(), worldIn, entity, weapon, item, modifiedGun));
            if(Config.COMMON.gameplay.improvedHitboxes.get())
            {
                NeoForge.EVENT_BUS.register(new BoundingBoxManager());
            }
            if(ModList.get().isLoaded("simpleplanes")) SimplePlanesHelper.init();
        });
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(ClientHandler::setup);
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        BlockTagGen blockTagGen = new BlockTagGen(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), new RecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new LootTableGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), blockTagGen);
        generator.addProvider(event.includeServer(), new ItemTagGen(output, lookupProvider, blockTagGen.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new GunGen(output, lookupProvider));
        //generator.addProvider(event.includeServer(), new EnchantmentTagGen(output, lookupProvider, existingFileHelper));
        //generator.addProvider(event.includeServer(), new DamageTypeGen(output, lookupProvider, existingFileHelper));
    }

    public static boolean isDebugging()
    {
        return false;//!FMLEnvironment.production;
    }
}


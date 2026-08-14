package com.mrcrayfish.guns.client;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import java.nio.file.Path;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class PackHandler {
    @SubscribeEvent
    public static void onAddPackFindersEvent(AddPackFindersEvent event) {
        if (!event.getPackType().equals(PackType.CLIENT_RESOURCES)) return;
        final IModFile modFile = ModList.get().getModFileById("cgm").getFile();
        final Path resourcePath = modFile.findResource("packs/cgm_pbr");
        final PathPackResources pack = new PathPackResources(
                new PackLocationInfo(modFile.getFileName() + ":" + resourcePath, Component.translatable("pack.cgm.pbr.title"), PackSource.FEATURE, Optional.empty()),
                resourcePath
        );
        final Pack.ResourcesSupplier sup = new Pack.ResourcesSupplier()
        {
            @Override
            public PackResources openPrimary(PackLocationInfo location)
            {
                return pack;
            }

            @Override
            public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata)
            {
                return pack;
            }
        };
        final String name = "feature/cgm_pbr_textures";
        event.addRepositorySource(c -> {
            c.accept(Pack.readMetaAndCreate(
                    new PackLocationInfo(name, Component.translatable("pack.cgm.pbr.title"), PackSource.FEATURE, Optional.empty()),
                    sup,
                    PackType.CLIENT_RESOURCES,
                    new PackSelectionConfig(false, Pack.Position.TOP, false)
            ));
        });
        pack.close();
    }
}

package com.mrcrayfish.guns.mixin.common;

import net.neoforged.neoforge.fluids.CauldronFluidContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Workaround for NeoForge 1.21.1 bug where CauldronFluidContent.init() is called
 * multiple times during startup (resource reload), causing duplicate cauldron
 * registration errors because the static BY_BLOCK map is never cleared.
 */
@Mixin(CauldronFluidContent.class)
public class CauldronFluidContentMixin
{
    @Unique
    private static boolean cgm$initialized = false;

    @Inject(method = "init()V", at = @At("HEAD"), cancellable = true)
    private static void cgm$skipDuplicateInit(CallbackInfo ci)
    {
        if(cgm$initialized)
        {
            ci.cancel();
        }
        cgm$initialized = true;
    }
}

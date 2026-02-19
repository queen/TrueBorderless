package dev.queen.mixins;

import dev.queen.TrueBorderless;
import dev.queen.events.VanillaFullscreenEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 100)
public class MixinMinecraft {
    @Inject(method = "updateDisplayMode", at = @At("HEAD"), cancellable = true)
    private void onToggleFullscreen(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new VanillaFullscreenEvent())) ci.cancel();
    }
}

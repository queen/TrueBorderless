package dev.queen

import dev.queen.events.VanillaFullscreenEvent
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.logging.log4j.LogManager

@Mod(modid = "borderless", version = "{{ version }}", name = "True Borderless")
class TrueBorderless {

    companion object {
        val Logger = LogManager.getLogger("True Borderless")
    }

    private var modEnabled = true


    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onVanillaFullscreen(event: VanillaFullscreenEvent) {
        if (!modEnabled) return

        event.isCanceled = true
        ApplyBorderless.applyBorderless()
    }
}
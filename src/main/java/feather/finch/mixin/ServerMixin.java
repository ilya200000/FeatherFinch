package feather.finch.mixin;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("FeatherFinch");

    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void featherfinch$onLoadLevel(CallbackInfo info) {
        LOGGER.info("FeatherFinch: Очистка памяти перед загрузкой мира...");
        System.gc(); 
    }
}

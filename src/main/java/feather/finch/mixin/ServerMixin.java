package feather.finch.mixin;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(MinecraftServer.class)
public class ServerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("FeatherFinch");
    private static final ScheduledExecutorService CLEANER_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static boolean isRunning = false;

    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void featherfinch$onLoadLevel(CallbackInfo info) {
        if (!isRunning) {
            LOGGER.info("FeatherFinch: Система мягкой очистки RAM запущена!");
            
            // Первая очистка перед тяжелой загрузкой
            System.gc();

            // Запускаем фоновую задачу: чистить память каждые 60 секунд
            CLEANER_SERVICE.scheduleAtFixedRate(() -> {
                // LOGGER.info("FeatherFinch: Фоновая уборка мусора..."); 
                System.gc();
            }, 1, 1, TimeUnit.MINUTES);

            isRunning = true;
        }
    }
}

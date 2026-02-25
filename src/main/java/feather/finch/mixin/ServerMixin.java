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
    private long lastCleanTime = 0;

    @Inject(at = @At("HEAD"), method = "tickServer")
    private void featherfinch$criticalMemoryCheck(CallbackInfo info) {
        Runtime runtime = Runtime.getRuntime();
        
        long maxMemory = runtime.maxMemory(); // Максимум (твои 5000МБ)
        long allocatedMemory = runtime.totalMemory(); // Сколько Java уже зарезервировала
        long freeMemory = runtime.freeMemory(); // Свободно внутри зарезервированного
        
        // Считаем реально свободное место
        long actualFree = freeMemory + (maxMemory - allocatedMemory);
        double freePercent = (double) actualFree / maxMemory * 100.0;

        // Если осталось МЕНЬШЕ 2% памяти (Нагрузка > 98%)
        if (freePercent < 2.0) {
            // Проверка, чтобы не чистить чаще чем раз в 10 секунд (иначе упадет FPS)
            if (System.currentTimeMillis() - lastCleanTime > 10000) {
                LOGGER.warn("FeatherFinch: КРИТИЧЕСКИЙ УРОВЕНЬ RAM! Свободно: {}%. Очистка...", String.format("%.2f", freePercent));
                
                System.gc(); // Принудительный сбор мусора
                
                lastCleanTime = System.currentTimeMillis();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void featherfinch$onLoad(CallbackInfo info) {
        LOGGER.info("FeatherFinch: Система 'Аварийный выключатель 2%' активирована.");
    }
}

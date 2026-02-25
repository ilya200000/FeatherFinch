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
    private void featherfinch$aggressiveMemoryCheck(CallbackInfo info) {
        Runtime runtime = Runtime.getRuntime();
        
        long maxMemory = runtime.maxMemory(); 
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        // Считаем занятую память
        long usedMemory = allocatedMemory - freeMemory;
        double usedPercent = (double) usedMemory / maxMemory * 100.0;

        // ЕСЛИ ЗАНЯТО БОЛЬШЕ 2% (Нагрузка > 2%)
        if (usedPercent > 2.0) {
            // Чистим не чаще раза в 3 секунды, чтобы не превратить игру в слайд-шоу
            if (System.currentTimeMillis() - lastCleanTime > 3000) {
                // LOGGER.info("FeatherFinch: Нагрузка {}% - Чистка RAM!", String.format("%.1f", usedPercent));
                
                System.gc(); // Вызываем сборщик мусора
                
                lastCleanTime = System.currentTimeMillis();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void featherfinch$onLoad(CallbackInfo info) {
        LOGGER.info("FeatherFinch: Режим 'Стерильность' (Чистка при нагрузке > 2%) запущен!");
    }
}

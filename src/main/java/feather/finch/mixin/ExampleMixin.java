package feather.finch.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Миксин сразу на два класса: Сервер (для RAM) и Сущности (для FPS)
@Mixin(value = {MinecraftServer.class, Entity.class})
public class ExampleMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("FeatherFinch");

    // ЧАСТЬ 1: Очистка памяти при загрузке мира
    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void featherfinch$onLoadLevel(CallbackInfo info) {
        LOGGER.info("FeatherFinch: Расправляем крылья... Оптимизация RAM!");
        System.gc(); 
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        LOGGER.info("FeatherFinch: Доступно памяти: {} MB.", maxMemory);
    }

    // ЧАСТЬ 2: Умный пропуск тиков для мобов вдали (Разгружаем процессор)
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void featherfinch$smartEntityTick(CallbackInfo ci) {
        // Проверяем, является ли текущий объект сущностью (Entity)
        if ((Object) this instanceof Entity entity) {
            // Работаем только на сервере и только если это не игрок
            if (!entity.getWorld().isClient && entity.getWorld() instanceof ServerWorld serverWorld) {
                // Если рядом нет игроков в радиусе 64 блоков
                if (!serverWorld.isPlayerInRange(entity.getX(), entity.getY(), entity.getZ(), 64.0)) {
                    // Обновляем моба только каждый 5-й тик (экономим 80% ресурсов на мобах)
                    if (entity.getWorld().getTime() % 5 != 0) {
                        ci.cancel(); 
                    }
                }
            }
        }
    }
}

